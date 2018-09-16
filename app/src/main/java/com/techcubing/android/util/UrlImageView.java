package com.techcubing.android.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;

import com.jakewharton.disklrucache.DiskLruCache;
import com.techcubing.android.BuildConfig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class UrlImageView extends AppCompatImageView {
    Uri uri = null;

    private static final String TAG = "TCUrlImageView";
    public UrlImageView(Context context) {
        super(context);
    }
    public UrlImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public UrlImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private static DiskLruCache getCache(Context context) throws IOException {
        return DiskLruCache.open(
                new File(context.getFilesDir(), "imgcache"),
                BuildConfig.VERSION_CODE, 1, 8 * 1024 * 1024);
    }

    public void setUri(Uri uri, String cacheKey, Activity activity) {
        if (uri == this.uri) {
            return;
        }
        this.uri = uri;
        AsyncTask.execute(() -> {
            // First try to read the image from cache.
            try {
                DiskLruCache cache = getCache(activity);
                DiskLruCache.Snapshot snapshot = cache.get(cacheKey);
                if (snapshot != null) {
                    Log.i(TAG, "Cache hit for image " + cacheKey);
                    byte[] cachedImage =
                            Base64.decode(snapshot.getString(0), Base64.URL_SAFE);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(
                            cachedImage, 0, cachedImage.length);
                    if (bitmap != null) {
                        activity.runOnUiThread(() -> {
                            UrlImageView.this.setImageBitmap(
                                    Bitmap.createScaledBitmap(
                                            bitmap, this.getMeasuredWidth(), this.getMeasuredHeight(),
                                            false));
                        });
                        return;
                    } else {
                        Log.i(TAG, "Failed to decode image " + cacheKey);
                        cache.remove(cacheKey);
                    }
                } else {
                    Log.i(TAG, "Cache miss for image " + cacheKey);
                }
                cache.close();
            } catch (Exception e) {
                Log.e(TAG, "Failed to read image from cache", e);
            }
            // Next try to read the image from the network.
            try {
                InputStream inputStream = new java.net.URL(uri.toString()).openStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                activity.runOnUiThread(() -> {
                    UrlImageView.this.setImageBitmap(
                            Bitmap.createScaledBitmap(
                                    bitmap, this.getMeasuredWidth(), this.getMeasuredHeight(),
                                    false));
                });

                // Try to write the image to cache.
                try {
                    DiskLruCache cache = getCache(activity);
                    DiskLruCache.Editor editor = cache.edit(cacheKey);
                    editor.set(0, Base64.encodeToString(byteArray, Base64.URL_SAFE));
                    editor.commit();
                    cache.close();
                } catch (IOException e) {
                    Log.e(TAG, "Failed to write image to cache", e);
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to read image from URI", e);
            }
        });
    }
}
