package com.yuanze31.splatooninfo.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class WebImgHandler {

    private final List<String> targetSites;
    private final List<String> allowedExtensions;
    private final List<String> specialFiles;
    private final ExecutorService executor;
    private final Context context;

    private final boolean tempAllowAllSites = false;
    private final boolean tempAllowAllExtensions = false;

    public WebImgHandler(Context context, List<String> targetSites, List<String> allowedExtensions, List<String> specialFiles) {
        this.context = context.getApplicationContext();
        this.targetSites = targetSites;
        this.allowedExtensions = allowedExtensions;
        this.specialFiles = specialFiles;
        this.executor = ExecutorServiceManager.getInstance(context).getDownloadExecutor();
    }

    public String getImagePath(Context context, String originalUrl) {
        if (isSpecialFile(originalUrl)) {
            return handleSpecialFile(context, originalUrl);
        }

        if (!isTargetSite(originalUrl) || !isAllowedFileType(originalUrl)) {
            return originalUrl;
        }

        String fileType = CachePathUtils.getFileType(originalUrl);
        
        String assetPath = CachePathUtils.urlToAssetPath(originalUrl, fileType);
        if (isFileExistInAssets(context, assetPath)) {
            return "file:///android_asset/" + assetPath;
        }

        String cachePath = CachePathUtils.urlToCachePath(originalUrl, fileType);
        File dataFile = new File(CachePathUtils.getExternalCacheDir(context), 
                                 cachePath.substring(CachePathUtils.CACHE_DIR_NAME.length() + 1));

        if (dataFile.exists()) {
            return "file://" + dataFile.getAbsolutePath();
        }

        downloadAndSaveImage(originalUrl, dataFile);
        return originalUrl;
    }

    private boolean isTargetSite(String url) {
        for (String site : targetSites) {
            if (tempAllowAllSites) {
                return true;
            } else if (url.contains(site)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAllowedFileType(String url) {
        String extension = getFileExtension(url);
        for (String ext : allowedExtensions) {
            if (tempAllowAllExtensions) {
                return true;
            } else if (extension.equalsIgnoreCase(ext)) {
                return true;
            }
        }
        return false;
    }

    private boolean isSpecialFile(String url) {
        for (String file : specialFiles) {
            if (url.contains(file)) {
                return true;
            }
        }
        return false;
    }

    private String handleSpecialFile(Context context, String url) {
        String fileType = CachePathUtils.getFileType(url);
        String cachePath = CachePathUtils.urlToCachePath(url, fileType);
        File dataFile = new File(CachePathUtils.getExternalCacheDir(context), 
                                 cachePath.substring(CachePathUtils.CACHE_DIR_NAME.length() + 1));
        if (dataFile.exists()) {
            return "file://" + dataFile.getAbsolutePath();
        }
        return url;
    }

    private boolean isFileExistInAssets(Context context, String localPath) {
        try {
            AssetManager assetManager = context.getAssets();
            String directory = localPath.substring(0,
                                                   localPath.lastIndexOf('/'));
            String fileName = localPath.substring(localPath.lastIndexOf('/') + 1);
            String[] assetFiles = assetManager.list(directory);
            if (assetFiles != null) {
                for (String assetFile : assetFiles) {
                    if (assetFile.equals(fileName)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String getFileExtension(String url) {
        return CachePathUtils.getFileExtension(url);
    }

    private void downloadAndSaveImage(String url, File file) {
        executor.execute(() -> {
            try {
                File parentDir = file.getParentFile();
                if (!parentDir.exists() && !parentDir.mkdirs()) {
                    Log.e("WebImgHandler", "Failed to create directory: " + parentDir.getAbsolutePath());
                    return;
                }
                
                URL imageUrl = new URL(url);
                try (InputStream input = imageUrl.openStream(); 
                     FileOutputStream output = new FileOutputStream(file)) {
                    byte[] buffer = new byte[8192];
                    int len;
                    while ((len = input.read(buffer)) != -1) {
                        output.write(buffer, 0, len);
                    }
                    Log.d("WebImgHandler", "Downloaded: " + url + " to " + file.getAbsolutePath());
                }
            } catch (IOException e) {
                Log.e("WebImgHandler", "Failed to download image: " + url, e);
            }
        });
    }
}
