package com.yuanze31.splatooninfo.ui;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.yuanze31.splatooninfo.ui.challenges.ChallengesViewModel;
import com.yuanze31.splatooninfo.ui.gear.GearViewModel;
import com.yuanze31.splatooninfo.ui.salmonrun.SalmonrunViewModel;
import com.yuanze31.splatooninfo.ui.schedule.ScheduleViewModel;
import com.yuanze31.splatooninfo.ui.splatfests.SplatfestsViewModel;
import com.yuanze31.splatooninfo.utils.WebCacheConfig;

public class WebImgViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {
    
    private final Context applicationContext;
    
    public WebImgViewModelFactory(@NonNull Context context) {
        this.applicationContext = context.getApplicationContext();
    }
    
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == ScheduleViewModel.class) {
            return (T) new ScheduleViewModel(applicationContext);
        } else if (modelClass == GearViewModel.class) {
            return (T) new GearViewModel(applicationContext);
        } else if (modelClass == SalmonrunViewModel.class) {
            return (T) new SalmonrunViewModel(applicationContext);
        } else if (modelClass == ChallengesViewModel.class) {
            return (T) new ChallengesViewModel(applicationContext);
        } else if (modelClass == SplatfestsViewModel.class) {
            return (T) new SplatfestsViewModel(applicationContext);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass);
    }
}
