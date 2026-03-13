package com.yuanze31.splatooninfo.ui.challenges;

import android.content.Context;

import androidx.lifecycle.ViewModel;
import com.yuanze31.splatooninfo.utils.WebImgHandler;
import com.yuanze31.splatooninfo.utils.WebCacheConfig;

import java.util.List;

public class ChallengesViewModel extends ViewModel {
    private final WebImgHandler webImgHandler;

    public ChallengesViewModel(Context context) {
        webImgHandler = new WebImgHandler(context, 
                                          List.of(WebCacheConfig.DataSources.SPLATOON3_INK),
                                          WebCacheConfig.FileTypes.ALLOWED_EXTENSIONS,
                                          WebCacheConfig.SpecialFiles.JSON_DATA_FILES);
    }

    public String getImagePath(Context context, String originalUrl) {
        return webImgHandler.getImagePath(context, originalUrl);
    }
}
