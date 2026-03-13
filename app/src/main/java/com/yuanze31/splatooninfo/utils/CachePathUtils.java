package com.yuanze31.splatooninfo.utils;

import android.content.Context;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class CachePathUtils {
    
    public static final String CACHE_DIR_NAME = "webcache";
    
    public static final String DATA_DIR = "data";
    public static final String IMAGES_DIR = "images";
    public static final String FONTS_DIR = "fonts";
    
    public static File getExternalCacheDir(Context context) {
        File cacheDir = context.getExternalCacheDir();
        if (cacheDir == null) {
            cacheDir = context.getCacheDir();
        }
        return new File(cacheDir, CACHE_DIR_NAME);
    }
    
    public static String urlToCachePath(String url, String resourceType) {
        try {
            URI uri = new URI(url);
            
            String host = uri.getHost();
            int port = uri.getPort();
            String hostWithPort = (port != -1 && port != 80 && port != 443) 
                ? host + ":" + port 
                : host;
            
            String encodedHost = URLEncoder.encode(hostWithPort, "UTF-8");
            
            String path = uri.getPath();
            if (path == null || path.isEmpty()) {
                path = "/";
            }
            
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            
            String typeDir;
            switch (resourceType.toLowerCase()) {
                case "data":
                    typeDir = DATA_DIR;
                    break;
                case "image":
                    typeDir = IMAGES_DIR;
                    break;
                case "font":
                    typeDir = FONTS_DIR;
                    break;
                default:
                    typeDir = "other";
            }
            
            String localPath = CACHE_DIR_NAME + "/" + typeDir + "/" + encodedHost + "/" + path;
            
            return localPath;
            
        } catch (URISyntaxException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static String urlToAssetPath(String url, String resourceType) {
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            String path = uri.getPath();
            
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            
            String typeDir;
            switch (resourceType.toLowerCase()) {
                case "image":
                    typeDir = IMAGES_DIR;
                    break;
                case "font":
                    typeDir = FONTS_DIR;
                    break;
                case "data":
                    typeDir = DATA_DIR;
                    break;
                default:
                    typeDir = "other";
            }
            
            return CACHE_DIR_NAME + "/" + host + "/" + typeDir + "/" + path;
            
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(lastDot + 1).toLowerCase() : "";
    }
    
    public static String getFileType(String url) {
        String ext = getFileExtension(url);
        
        if (Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "webp", "svg").contains(ext)) {
            return "image";
        }
        
        if (Arrays.asList("woff", "woff2", "ttf", "otf", "eot").contains(ext)) {
            return "font";
        }
        
        if (Arrays.asList("json", "xml", "yaml", "yml").contains(ext)) {
            return "data";
        }
        
        return "other";
    }
    
    public static String getFilenameFromUrl(String url) {
        try {
            URI uri = new URI(url);
            String path = uri.getPath();
            int lastSlash = path.lastIndexOf('/');
            return lastSlash >= 0 ? path.substring(lastSlash + 1) : path;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return "";
        }
    }
}
