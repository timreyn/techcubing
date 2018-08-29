package com.techcubing.android.util;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TimerListener {
    public static class TimerState {
        private char statusCharacter;
        private String time;

        TimerState(char[] status) {
            statusCharacter = status[0];
            time = status[1] + ":" + status[2] + status[3] + ":" + status[4] + status[5];
        }

        public String getTime() {
            return time;
        }

        public char getStatusCharacter() {
            return statusCharacter;
        }
    }

    public static abstract class TimerCallback {
        public abstract void onTimerUpdate(TimerState state);
    }

    private static final String TAG = "TCTimerListener";

    private enum State {
        LISTENING, NOT_LISTENING;
    }
    private State state;
    private final String stateMutex = "";
    private TimerListenerAsyncTask asyncTask;
    private List<TimerCallback> callbacks;

    public TimerListener() {
        callbacks = new ArrayList<>();
        state = State.NOT_LISTENING;
    }

    public void registerCallback(TimerCallback callback) {
        callbacks.add(callback);
        if (asyncTask != null) {
            asyncTask.addCallback(callback);
        }
    }

    public void start() {
        synchronized (stateMutex) {
            if (state != State.NOT_LISTENING) {
                throw new RuntimeException("Listener is already listening!");
            }
        }
        asyncTask = new TimerListenerAsyncTask();
        for (TimerCallback callback : callbacks) {
            asyncTask.addCallback(callback);
        }
        asyncTask.execute();
        state = State.LISTENING;
    }

    public void stop() {
        synchronized (stateMutex) {
            if (state != State.LISTENING) {
                throw new RuntimeException("Listener is not listening!");
            }
            asyncTask.cancel(true);
            state = State.NOT_LISTENING;
        }
    }

    static class TimerListenerAsyncTask extends AsyncTask<Void, TimerState, Void> {
        private AudioRecord recorder;
        private List<TimerCallback> callbacks;

        private TimerListenerAsyncTask() {
            callbacks = new ArrayList<>();
        }

        private void addCallback(TimerCallback callback) {
            callbacks.add(callback);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            int bufferSize = AudioRecord.getMinBufferSize(
                    44100, AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);
            recorder = new AudioRecord.Builder()
                    .setAudioSource(MediaRecorder.AudioSource.UNPROCESSED)
                    .setAudioFormat(new AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(44100)
                            .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                            .build())
                    .setBufferSizeInBytes(bufferSize)
                    .build();
            recorder.startRecording();

            final int BUFFER_SIZE = 512;
            final int DATA_FRAME_SIZE = 4096;
            short[] inData = new short[BUFFER_SIZE];
            short[] alignmentData = new short[BUFFER_SIZE];
            short[] dataFrame = new short[DATA_FRAME_SIZE];

            // First look for zeros, so that we can align to the beginning of an input signal.
            boolean foundZeros = false;
            while (!isCancelled() && !foundZeros) {
                recorder.read(inData, 0, BUFFER_SIZE);
                foundZeros = true;
                for (short val : inData) {
                    if (val > 20 || val < -20) {
                        foundZeros = false;
                        break;
                    }
                }
            }

            while (!isCancelled()) {
                // We're looking for the beginning of the signal.  This is indicated by a value
                // around -100.
                recorder.read(inData, 0, BUFFER_SIZE);
                int i = 0;
                boolean foundStart = false;
                for (; i < BUFFER_SIZE; i++) {
                    if (inData[i] < -50) {
                        foundStart = true;
                        break;
                    }
                }
                if (!foundStart) {
                    continue;
                }
                // We've found the beginning of the signal, let's align more precisely
                // with the most-negative value.
                // Move the bytes into a new array for easier processing.
                System.arraycopy(inData, i + 1, alignmentData, 0, BUFFER_SIZE - i - 1);
                recorder.read(alignmentData, BUFFER_SIZE - i, i);
                int minIndex = 0;
                for (i = 0; i < 20; i++) {
                    if (alignmentData[i] < alignmentData[minIndex]) {
                        minIndex = i;
                    }
                }
                // We have our starting point.  We'll start processing 18 indices after.
                // Copy what we have left over into dataFrame, and read data into the rest of
                // dataFrame.
                int valuesSkipped = minIndex + 17;
                System.arraycopy(alignmentData, valuesSkipped + 1, dataFrame, 0,
                        BUFFER_SIZE - valuesSkipped - 1);
                recorder.read(dataFrame, BUFFER_SIZE - valuesSkipped,
                        DATA_FRAME_SIZE - (BUFFER_SIZE - valuesSkipped));

                final int BITS_PER_BYTE = 10;
                final int BYTES_PER_FRAME = 9;

                // Now we process dataFrame.
                boolean[] bitsRead = new boolean[BITS_PER_BYTE * BYTES_PER_FRAME];
                short[] rawValues = new short[BITS_PER_BYTE * BYTES_PER_FRAME];
                boolean lastPeakPositive = false;
                int lastPeakIndex = -1;

                // Process the current frame to extract the bits.
                for (int bitNumber = 0; bitNumber < BITS_PER_BYTE * BYTES_PER_FRAME; bitNumber++) {
                    int startIndex = (int) (36.75 * bitNumber);
                    int endIndex = (int) (36.75 * (bitNumber + 1));

                    // First look for the maximum value in this part of the frame.
                    // Drop the 10 outermost bits, since peaks will be closer to the middle.
                    short rawValue = 0;
                    for (int index = startIndex + 5; index < endIndex - 5; index++) {
                        short nextValue = dataFrame[index];
                        if (Math.abs(nextValue) > Math.abs(rawValue)) {
                            rawValue = nextValue;
                        }
                    }
                    rawValues[bitNumber] = rawValue;

                    // Next figure out if this is a peak.  Peaks alternate between positive and
                    // negative.
                    boolean isPeak = false;
                    if (lastPeakPositive) {
                        if (rawValue < -60) {
                            isPeak = true;
                            lastPeakPositive = false;
                        } else if (rawValue > rawValues[lastPeakIndex]) {
                            isPeak = true;
                            bitsRead[lastPeakIndex] = false;
                        }
                    } else {
                        if (rawValue > 60) {
                            isPeak = true;
                            lastPeakPositive = true;
                        } else if (lastPeakIndex > -1 && rawValue < rawValues[lastPeakIndex]) {
                            isPeak = true;
                            bitsRead[lastPeakIndex] = false;
                        }
                    }
                    if (isPeak) {
                        bitsRead[bitNumber] = true;
                        lastPeakIndex = bitNumber;
                    }
                }
                Log.i(TAG, Arrays.toString(rawValues));
                Log.i(TAG, Arrays.toString(bitsRead));

                // We have our bits, now map them to bytes.
                char[] chars = new char[BYTES_PER_FRAME];
                boolean lastBit = false;
                for (int byteNum = 0; byteNum < BYTES_PER_FRAME; byteNum++) {
                    short bitValue = 1;
                    for (int bitNum = 0; bitNum < BITS_PER_BYTE; bitNum++) {
                        lastBit ^= bitsRead[byteNum * BITS_PER_BYTE + bitNum];
                        if (bitNum < 8) {
                            if (lastBit) {
                                chars[byteNum] += bitValue;
                            }
                            bitValue *= 2;
                        }
                    }
                }
                Log.i(TAG, "Received signal from stackmat: " + Arrays.toString(chars));
                publishProgress(new TimerState(chars));
            }
            return null;
        }

        @Override
        protected void onCancelled(Void v) {
            recorder.stop();
        }

        @Override
        protected void onProgressUpdate(TimerState... states) {
            for (TimerState state : states) {
                for (TimerCallback callback : callbacks) {
                    callback.onTimerUpdate(state);
                }
            }
        }
    }
}
