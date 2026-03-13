package com.yuanze31.splatooninfo.utils;

import java.util.List;

public class WebCacheConfig {
    
    public static final class DataSources {
        public static final String SPLATOON3_INK = "splatoon3.ink";
    }
    
    public static final class FileTypes {
        public static final List<String> IMAGES = List.of(
            "jpg",
            "jpeg",
            "png",
            "gif",
            "bmp",
            "webp",
            "svg"
        );
        
        public static final List<String> FONTS = List.of(
            "woff",
            "woff2",
            "ttf",
            "otf"
        );
        
        public static final List<String> DATA = List.of(
            "json",
            "xml",
            "yaml",
            "yml"
        );
        
        public static final List<String> ALLOWED_EXTENSIONS = List.of(
            "jpg",
            "jpeg",
            "png",
            "gif",
            "bmp",
            "woff2"
        );
    }
    
    public static final class SpecialFiles {
        public static final List<String> JSON_DATA_FILES = List.of(
            DataSources.SPLATOON3_INK + "/data/schedules.json",
            DataSources.SPLATOON3_INK + "/data/gear.json",
            DataSources.SPLATOON3_INK + "/data/coop.json",
            DataSources.SPLATOON3_INK + "/data/festivals.json"
        );
    }
    
    public static final class URLs {
        public static final String SPLATOON3_INK = "https://splatoon3.ink";
        public static final String SPLATOON3_INK_GEAR = SPLATOON3_INK + "/gear";
        public static final String SPLATOON3_INK_SALMONRUN = SPLATOON3_INK + "/salmonrun";
        public static final String SPLATOON3_INK_CHALLENGES = SPLATOON3_INK + "/challenges";
        public static final String SPLATOON3_INK_SPLATFESTS = SPLATOON3_INK + "/splatfests";
        
        public static final List<String> JSON_DATA_URLS = List.of(
            SPLATOON3_INK + "/data/schedules.json",
            SPLATOON3_INK + "/data/gear.json",
            SPLATOON3_INK + "/data/coop.json",
            SPLATOON3_INK + "/data/festivals.json"
        );
    }
    
    public static final class Cache {
        public static final long DATA_UPDATE_INTERVAL = 60 * 60 * 1000;
        public static final long IMAGE_CACHE_EXPIRY = 7 * 24 * 60 * 60 * 1000;
        public static final long MAX_CACHE_SIZE = 100 * 1024 * 1024;
    }
}
