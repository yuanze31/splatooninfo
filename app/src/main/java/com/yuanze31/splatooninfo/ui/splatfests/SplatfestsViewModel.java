package com.yuanze31.splatooninfo.ui.splatfests;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.yuanze31.splatooninfo.utils.WebImgHandler;

import java.util.Arrays;

public class SplatfestsViewModel extends ViewModel {
    private final WebImgHandler webImgHandler;

    public SplatfestsViewModel() {
        webImgHandler = new WebImgHandler(Arrays.asList("example.com",
                                                        "splatoon3.ink"),
                                          Arrays.asList("jpg",
                                                        "jpeg",
                                                        "png",
                                                        "gif",
                                                        "bmp",
                                                        "woff2"),
                                          Arrays.asList("splatoon3.ink/data/schedules.json",
                                                        "splatoon3.ink/data/gear.json",
                                                        "splatoon3.ink/data/coop.json",
                                                        "splatoon3.ink/data/festivals.json"));
    }

    public String getImagePath(Context context, String originalUrl) {
        return webImgHandler.getImagePath(context,
                                          originalUrl);
    }
}
