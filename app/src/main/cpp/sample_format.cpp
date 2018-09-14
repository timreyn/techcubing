#include "sample_format.h"

void GetSampleFormat(SLAndroidDataFormat_PCM_EX* format) {
    format->formatType = SL_DATAFORMAT_PCM;
    format->numChannels = 1;
    format->channelMask = SL_SPEAKER_FRONT_LEFT;
    format->sampleRate = static_cast<SLmilliHertz>(44100000);
    format->endianness = SL_BYTEORDER_LITTLEENDIAN;
    format->bitsPerSample = SL_PCMSAMPLEFORMAT_FIXED_16;
    format->containerSize = SL_PCMSAMPLEFORMAT_FIXED_16;
    format->formatType = SL_ANDROID_DATAFORMAT_PCM_EX;
    format->representation = SL_ANDROID_PCM_REPRESENTATION_SIGNED_INT;
}