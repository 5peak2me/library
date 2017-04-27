package com.lvmama.base.core.cache;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;

import okhttp3.internal.Util;
import okhttp3.internal.cache.DiskLruCache;
import okhttp3.internal.io.FileSystem;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Sink;
import okio.Source;

/**
 * Created by J!nl!n on 2017/1/11.
 * Copyright © 1990-2017 J!nl!n™ Inc. All rights reserved.
 * <p>
 * ━━━━━━神兽出没━━━━━━
 * 　　　┏┓　　　┏┓
 * 　　┏┛┻━━━┛┻┓
 * 　　┃　　　　　　　┃
 * 　　┃　　　━　　　┃
 * 　　┃　┳┛　┗┳　┃
 * 　　┃　　　　　　　┃
 * 　　┃　　　┻　　　┃
 * 　　┃　　　　　　　┃
 * 　　┗━┓　　　┏━┛Code is far away from bug with the animal protecting
 * 　　　　┃　　　┃    神兽保佑,代码无bug
 * 　　　　┃　　　┃
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 * 　　　　　┗┻┛　┗┻┛
 * ━━━━━━感觉萌萌哒━━━━━━
 */
public class DiskCache {

    private static boolean initialised = false;
    private static final String CACHE_DISK_DIR = "cache";
    private static DiskLruCache sDiskLruCache;
    private static File cacheDir;
    private static Gson sGson;

    /**
     * Initialize DiskCache
     *
     * @param context context.
     * @param maxSize the maximum size in bytes.
     * @throws IOException thrown if the cache cannot be initialized.
     */
    public static synchronized void init(final Context context, final long maxSize) throws IOException {
        init(context, maxSize, new Gson());
    }

    /**
     * Initialize DiskCache
     *
     * @param context context.
     * @param maxSize the maximum size in bytes.
     * @param gson    the Gson instance.
     * @throws IOException thrown if the cache cannot be initialized.
     */
    public static synchronized void init(final Context context, final long maxSize, final Gson gson) throws IOException {
        //Create a directory inside the application specific cache directory. This is where all
        // the key-value pairs will be stored.
        cacheDir = getDiskCacheDir(context, CACHE_DISK_DIR);
        createCache(cacheDir, maxSize);
        sGson = gson;
        initialised = true;
    }

    /**
     * Checks if init method has been called and throws an IllegalStateException if it hasn't.
     *
     * @throws IllegalStateException thrown if init method hasn't been called.
     */
    private static void failIfNotInitialised() {
        if (!initialised) {
            throw new IllegalStateException("Init hasn't been called! You need to initialise " +
                    "DiskCache before you call any other methods.");
        }
    }

    /**
     * Creates the cache.
     *
     * @param cacheDir the directory where the cache is to be created.
     * @param maxSize  the maximum cache size in bytes.
     * @throws IOException thrown if the cache cannot be created.
     */
    private static synchronized void createCache(final File cacheDir, final long maxSize) throws
            IOException {
        boolean success = true;
        if (!cacheDir.exists()) {
            success = cacheDir.mkdirs();
        }
        if (!success) {
            throw new IOException("Failed to create cache directory!");
        }
        sDiskLruCache = DiskLruCache.create(FileSystem.SYSTEM, cacheDir, 1, 1, maxSize);
    }

    private static void abortQuietly(DiskLruCache.Editor editor) {
        // Give up because the cache cannot be written.
        try {
            if (editor != null) {
                editor.abort();
            }
        } catch (IOException ignored) {
        }
    }

    /**
     * Put an object into Reservoir with the given key. This a blocking IO operation. Previously
     * stored object with the same
     * key (if any) will be overwritten.
     *
     * @param key    the key string.
     * @param object the object to be stored.
     * @throws IllegalStateException thrown if init method hasn't been called.
     */
    public static void put(final String key, final Object object) {
        failIfNotInitialised();
        BufferedSink bufferedSink = null;
        DiskLruCache.Editor editor = null;
        try {
            String json = sGson.toJson(object);
            editor = sDiskLruCache.edit(md5(key));
            Sink sink = editor.newSink(0);
            bufferedSink = Okio.buffer(sink);
            bufferedSink.writeUtf8(json);
            editor.commit();
        } catch (IOException e) {
            abortQuietly(editor);
            e.printStackTrace();
        } finally {
            Util.closeQuietly(bufferedSink);
        }
    }

    /**
     * Put an object into Reservoir with the given key asynchronously. Previously
     * stored object with the same
     * key (if any) will be overwritten.
     *
     * @param key      the key string.
     * @param object   the object to be stored.
     * @param callback a callback of type {@link DiskCacheCallback}
     *                 which is called upon completion.
     * @throws IllegalStateException thrown if init method hasn't been called.
     */
    public static void putAsync(final String key, final Object object,
                                final DiskCacheCallback callback) {
        failIfNotInitialised();
        new HandleTask(callback) {
            @Override
            protected void handle() {
                DiskCache.put(key, object);
            }
        }.execute();
    }

    /**
     * Get an object from Reservoir with the given key. This a blocking IO operation.
     *
     * @param <T>      the type of the object to get.
     * @param key      the key string.
     * @param classOfT the class type of the expected return object.
     * @return the object of the given type if it exists.
     * @throws IllegalStateException thrown if init method hasn't been called.
     */
    public static <T> T get(String key, final Class<T> classOfT) {
        failIfNotInitialised();
        BufferedSource bufferedSource = null;
        try (DiskLruCache.Snapshot snapshot = sDiskLruCache.get(md5(key))) {
            if (snapshot != null) {
                Source source = snapshot.getSource(0);
                bufferedSource = Okio.buffer(source);
                String content = bufferedSource.readUtf8();
                if (!TextUtils.isEmpty(content)) {
                    T value = sGson.fromJson(content, classOfT);
                    if (value == null)
                        throw new NullPointerException();
                    snapshot.close();
                    return value;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Util.closeQuietly(bufferedSource);
        }
        return null;
    }

    /**
     * Get an object from Reservoir with the given key asynchronously.
     *
     * @param <T>      the type of the object to get.
     * @param key      the key string.
     * @param object   the type of the expected return object.
     * @param callback a callback of type {@link DiskCacheCallback}
     *                 which is called upon completion.
     * @throws IllegalStateException thrown if init method hasn't been called.
     */
    public static <T> void getAsync(final String key, final Object object,
                                    final DiskCacheCallback<T> callback) {
        failIfNotInitialised();
        new HandleTask<T>(callback) {
            @Override
            void handle() throws IOException {
                DiskCache.get(key, object.getClass());
            }
        }.execute();
    }

    /**
     * Delete an object from Reservoir with the given key. This a blocking IO operation. Previously
     * stored object with the same
     * key (if any) will be deleted.
     *
     * @param key the key string.
     * @throws IllegalStateException thrown if init method hasn't been called.
     * @throws IOException           thrown if cache cannot be accessed.
     */
    public static void remove(String key) throws IOException {
        failIfNotInitialised();
        sDiskLruCache.remove(md5(key));
    }

    /**
     * Delete an object into Reservoir with the given key asynchronously. Previously
     * stored object with the same
     * key (if any) will be deleted.
     *
     * @param key      the key string.
     * @param callback a callback of type {@link DiskCacheCallback}
     *                 which is called upon completion.
     * @throws IllegalStateException thrown if init method hasn't been called.
     */
    public static void removeAsync(final String key, final DiskCacheCallback callback) {
        failIfNotInitialised();
        new HandleTask(callback) {
            @Override
            void handle() throws IOException {
                DiskCache.remove(key);
            }
        }.execute();
    }

    /**
     * Check if an object with the given key exists in the Reservoir.
     *
     * @param key the key string.
     * @return true if object with given key exists.
     * @throws IllegalStateException thrown if init method hasn't been called.
     * @throws IOException           thrown if cache cannot be accessed.
     */
    public static boolean contains(String key) throws IOException {
        failIfNotInitialised();
        DiskLruCache.Snapshot snapshot = sDiskLruCache.get(md5(key));
        if (snapshot == null) {
            return false;
        }
        snapshot.close();
        return true;
    }

    /**
     * Clears the cache. Deletes all the stored key-value pairs synchronously.
     *
     * @throws IllegalStateException thrown if init method hasn't been called.
     * @throws IOException           thrown if cache cannot be accessed.
     */
    public static void clear() throws IOException {
        failIfNotInitialised();
        long maxSize = sDiskLruCache.getMaxSize();
        sDiskLruCache.delete();
        createCache(cacheDir, maxSize);
    }

    /**
     * Clears the cache. Deletes all the stored key-value pairs asynchronously.
     *
     * @param callback a callback of type {@link DiskCacheCallback}
     *                 which is called upon completion.
     * @throws IllegalStateException thrown if init method hasn't been called.
     */
    public static void clearAsync(final DiskCacheCallback callback) {
        failIfNotInitialised();
        new HandleTask(callback) {
            @Override
            void handle() throws IOException {
                DiskCache.clear();
            }
        }.execute();
    }

    public long size() throws IOException {
        return sDiskLruCache.size();
    }

    private static String md5(String key) {
        return Util.md5Hex(key);
    }

    private static File getDiskCacheDir(Context context, String dirName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath, dirName);
    }

    public interface DiskCacheCallback<T> {
        void onSuccess(T t);

        void onFailure(Exception e);
    }

    /**
     * AsyncTask to perform put operation in a background thread.
     */
    private abstract static class HandleTask<T> extends AsyncTask<Void, Void, T> {
        private Exception e;
        private final DiskCacheCallback callback;

        private HandleTask(DiskCacheCallback callback) {
            this.callback = callback;
            this.e = null;
        }

        @Override
        protected T doInBackground(Void... params) {
            try {
                handle();
            } catch (Exception e) {
                this.e = e;
            }
            return null;
        }

        abstract void handle() throws IOException;

        @Override
        protected void onPostExecute(T t) {
            if (callback != null) {
                if (e == null) {
                    callback.onSuccess(t);
                } else {
                    callback.onFailure(e);
                }
            }
        }
    }

}
