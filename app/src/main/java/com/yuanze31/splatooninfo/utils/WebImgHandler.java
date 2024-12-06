package com.yuanze31.splatooninfo.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class WebImgHandler {

    private final List<String> targetSites;
    private final List<String> allowedExtensions;
    private final List<String> specialFiles;

    private final boolean tempAllowAllSites = false;
    private final boolean tempAllowAllExtensions = false;

    public WebImgHandler(List<String> targetSites, List<String> allowedExtensions, List<String> specialFiles) {
        this.targetSites = targetSites;
        this.allowedExtensions = allowedExtensions;
        this.specialFiles = specialFiles;
    }

    public String getImagePath(Context context, String originalUrl) {
//        System.out.println("！目标站点" + targetSites);
//        System.out.println("！允许后缀" + allowedExtensions);
//        System.out.println("！特殊文件" + specialFiles);
        if (isSpecialFile(originalUrl)) {
            return handleSpecialFile(context,
                                     originalUrl);
        }

        if (!isTargetSite(originalUrl) || !isAllowedFileType(originalUrl)) {
            return originalUrl;
        }

        String localPath = convertUrlToLocalPath(originalUrl);
        File dataFile = new File(context.getExternalFilesDir(null),
                                 "web_img/" + localPath);

        if (isFileExistInAssets(context,
                                "web_img/" + localPath)) {
            return "file:///android_asset/web_img/" + localPath;
        }

        if (dataFile.exists()) {
            return "file://" + dataFile.getAbsolutePath();
        }

        downloadAndSaveImage(originalUrl,
                             dataFile);
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
        String localPath = convertUrlToLocalPath(url);
        File dataFile = new File(context.getExternalFilesDir(null),
                                 "web_img/" + localPath);
        if (dataFile.exists()) {
//            System.out.println("特殊文件存在" + "file://" + dataFile.getAbsolutePath());
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

    private String convertUrlToLocalPath(String url) {
        return url.replace("https://",
                           "")
                  .replace("http://",
                           "");
    }

    private String getFileExtension(String url) {
        int dotIndex = url.lastIndexOf('.');
        return (dotIndex != -1) ? url.substring(dotIndex + 1) : "";
    }

    private void downloadAndSaveImage(String url, File file) {
        new DownloadImageTask(url,
                              file).execute();
    }

    private static class DownloadImageTask extends AsyncTask<Void, Void, Void> {
        private final String imageUrl;
        private final File localFile;

        public DownloadImageTask(String imageUrl, File localFile) {
            this.imageUrl = imageUrl;
            this.localFile = localFile;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                File parentDir = localFile.getParentFile();
                if (!parentDir.exists() && !parentDir.mkdirs()) {
                    throw new IOException("Failed to create directory: " + parentDir.getAbsolutePath());
                }
                java.net.URL url = new java.net.URL(imageUrl);
                try (java.io.InputStream input = url.openStream(); FileOutputStream output = new FileOutputStream(localFile)) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = input.read(buffer)) != -1) {
                        output.write(buffer,
                                     0,
                                     len);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
