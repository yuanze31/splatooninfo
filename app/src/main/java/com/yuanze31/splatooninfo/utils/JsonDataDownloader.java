package com.yuanze31.splatooninfo.utils;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class JsonDataDownloader {

    private final OkHttpClient client = new OkHttpClient();
    private static final String TIMESTAMP_FILE = "last_fetch_time"; // 文件名

    public void dlJsonFiles(Context context, File saveDirectory, List<String> urls, JsonDownloadCallback callback) {
        long lastFetchTime = getLastFetchTime(context);
        long currentTime = System.currentTimeMillis();

        if (!isNextHour(lastFetchTime, currentTime)) {
            callback.onDownloadCompleted();
            return;
        }

        AtomicInteger remainingFiles = new AtomicInteger(urls.size());

        for (String url : urls) {
            Request request = new Request.Builder().url(url).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    callback.onDownloadFailed("下载失败：" + url);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonResponse = response.body().string();
                        String fileName = extractFileName(url);
                        
                        String cachePath = CachePathUtils.urlToCachePath(url, "data");
                        File cacheFile = new File(CachePathUtils.getExternalCacheDir(context), 
                                                 cachePath.substring(CachePathUtils.CACHE_DIR_NAME.length() + 1));
                        saveJsonData(cacheFile, jsonResponse);

                        if (remainingFiles.decrementAndGet() == 0) {
                            saveFetchTime(context);
                            callback.onDownloadCompleted();
                        }
                    } else {
                        callback.onDownloadFailed("下载失败：" + url);
                    }
                }
            });
        }
    }

    private void saveJsonData(File file, String jsonData) {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(jsonData.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String extractFileName(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }


    private void saveFetchTime(Context context) {
        File file = new File(CachePathUtils.getExternalCacheDir(context), TIMESTAMP_FILE);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            String currentTime = String.valueOf(System.currentTimeMillis());
            fos.write(currentTime.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private long getLastFetchTime(Context context) {
        File file = new File(CachePathUtils.getExternalCacheDir(context), TIMESTAMP_FILE);
        if (!file.exists()) {
            return 0;
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[(int) file.length()];
            fis.read(buffer);
            return Long.parseLong(new String(buffer).trim());
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private boolean isNextHour(long lastFetchTime, long currentTime) {
        if (lastFetchTime == 0) {
            return true; // 如果没有记录时间，则需要重新获取
        }

        // 判断是否超过 1 小时
        long oneHourMillis = 60 * 60 * 1000;
        if ((currentTime - lastFetchTime) > oneHourMillis) {
            return true;
        }

        // 判断是否跨越整点
        long lastHour = (lastFetchTime / oneHourMillis) % 24; // 获取上次下载的小时数
        long currentHour = (currentTime / oneHourMillis) % 24; // 获取当前的小时数
        return lastHour != currentHour;
    }

    public interface JsonDownloadCallback {
        void onDownloadCompleted();

        void onDownloadFailed(String errorMessage);
    }
}
