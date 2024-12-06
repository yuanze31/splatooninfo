package com.yuanze31.splatooninfo;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.yuanze31.splatooninfo.databinding.ActivityMainBinding;
import com.yuanze31.splatooninfo.ui.challenges.ChallengesFragment;
import com.yuanze31.splatooninfo.ui.gear.GearFragment;
import com.yuanze31.splatooninfo.ui.salmonrun.SalmonrunFragment;
import com.yuanze31.splatooninfo.ui.schedule.ScheduleFragment;
import com.yuanze31.splatooninfo.ui.splatfests.SplatfestsFragment;
import com.yuanze31.splatooninfo.utils.JsonDataDownloader;

public class MainActivity extends AppCompatActivity {

    // 调试模式
    boolean debugMode = false;
    private ActivityMainBinding binding;
    private FragmentManager fragmentManager;
    private Fragment challengesFragment, gearFragment, salmonrunFragment, scheduleFragment, splatfestsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /* 版本更新时，清除之前所有的缓存
         * 为了避免splatoon3.ink更新产生的错误
         * 或者是单纯避免用户手动去清理 */
        // 获取当前版本号
        int currentVersionCode = 1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // API 28及以上
            try {
                PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(),
                                                                             0);
                currentVersionCode = (int) packageInfo.getLongVersionCode(); // 转为int类型
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            // API 28以下
            try {
                currentVersionCode = getPackageManager().getPackageInfo(getPackageName(),
                                                                        0).versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (debugMode) {
            Toast.makeText(MainActivity.this,
                           "VersionCode: " + currentVersionCode,
                           Toast.LENGTH_SHORT)
                 .show();
        }

        // 获取 SharedPreferences
        SharedPreferences prefs = getSharedPreferences("app_prefs",
                                                       MODE_PRIVATE);
        int savedVersionCode = prefs.getInt("last_version_code",
                                            -1);

        // 比较版本号
        if (savedVersionCode != currentVersionCode) {
            // 如果版本号发生变化，清除外部存储目录内容
            clearFiles("web_img");

            // 更新 SharedPreferences 中的版本号
            prefs.edit()
                 .putInt("last_version_code",
                         currentVersionCode)
                 .apply();
        }

        /* 基础页面设定
         * 五个页面全部加载
         * 为了提高切换的体验，避免切换的时候“卡一下”
         * 最开始加载还是要等一下的，splatoon3.ink的问题，懒得解决了 */
        // BottomNavigationView处理
        BottomNavigationView navView = findViewById(R.id.nav_view);

        // 初始化 FragmentManager
        fragmentManager = getSupportFragmentManager();

        // 初始化 Fragment
        challengesFragment = new ChallengesFragment();
        gearFragment = new GearFragment();
        salmonrunFragment = new SalmonrunFragment();
        scheduleFragment = new ScheduleFragment();
        splatfestsFragment = new SplatfestsFragment();

        // 添加 Fragment
        fragmentManager.beginTransaction()
                       .add(R.id.fragment_container,
                            challengesFragment,
                            "challenges")
                       .add(R.id.fragment_container,
                            gearFragment,
                            "gear")
                       .hide(gearFragment)
                       .add(R.id.fragment_container,
                            salmonrunFragment,
                            "salmonrun")
                       .hide(salmonrunFragment)
                       .add(R.id.fragment_container,
                            scheduleFragment,
                            "schedule")
                       .hide(scheduleFragment)
                       .add(R.id.fragment_container,
                            splatfestsFragment,
                            "splatfests")
                       .hide(splatfestsFragment)
                       .commit();

        // 设置 BottomNavigationView 切换监听
        navView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_challenges) {
                switchFragment("challenges");
                return true;
            } else if (itemId == R.id.navigation_gear) {
                switchFragment("gear");
                return true;
            } else if (itemId == R.id.navigation_salmonrun) {
                switchFragment("salmonrun");
                return true;
            } else if (itemId == R.id.navigation_schedule) {
                switchFragment("schedule");
                return true;
            } else if (itemId == R.id.navigation_splatfests) {
                switchFragment("splatfests");
                return true;
            }
            return false;
        });

        // 默认显示第一个页面
        navView.setSelectedItemId(R.id.navigation_schedule);

        // 让navView正常显示图标（别问我为什么布局文件声明没有用，反正能跑就行）
        navView.setItemIconTintList(null);


        /* 下载当前的数据文件
         * 详情可参考：https://github.com/misenhower/splatoon3.ink/wiki/Data-Access
         * 下载函数请查看 JsonDataDownloader.java
         * 我知道这很不标准，但是就这样吧 */
        // 下载 JSON 文件
        JsonDataDownloader jsonDataDownloader = new JsonDataDownloader();
        List<String> dlJsonURLS = List.of("https://splatoon3.ink/data/schedules.json",
                                          "https://splatoon3.ink/data/gear.json",
                                          "https://splatoon3.ink/data/coop.json",
                                          "https://splatoon3.ink/data/festivals.json");
        File saveDirectory = new File(this.getExternalFilesDir(null),
                                      "web_img/splatoon3.ink/data/");
        jsonDataDownloader.dlJsonFiles(this,
                                       saveDirectory,
                                       dlJsonURLS,
                                       new JsonDataDownloader.JsonDownloadCallback() {
                                           @Override
                                           public void onDownloadCompleted() {
                                               runOnUiThread(() -> {
//                                                   Toast.makeText(MainActivity.this,
//                                                                  "数据更新完成",
//                                                                  Toast.LENGTH_SHORT)
//                                                        .show();
                                                   refreshCurrentFragment(); // 刷新
                                               });
                                           }

                                           @Override
                                           public void onDownloadFailed(String errorMessage) {
                                               runOnUiThread(() -> {
//                                                   Toast.makeText(MainActivity.this,
//                                                                  errorMessage,
//                                                                  Toast.LENGTH_SHORT)
//                                                        .show();
                                               });
                                           }
                                       });
    }

    /**
     * 实现切换Fragment时页面更新
     * 先隐藏所有，再显示目标
     */
    private void switchFragment(String tag) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // 隐藏所有 Fragment
        if (challengesFragment != null) transaction.hide(challengesFragment);
        if (gearFragment != null) transaction.hide(gearFragment);
        if (salmonrunFragment != null) transaction.hide(salmonrunFragment);
        if (scheduleFragment != null) transaction.hide(scheduleFragment);
        if (splatfestsFragment != null) transaction.hide(splatfestsFragment);

        // 显示目标 Fragment 并更新选中状态
        switch (tag) {
            case "challenges":
                transaction.show(challengesFragment);
                binding.navView.getMenu()
                               .findItem(R.id.navigation_challenges)
                               .setChecked(true);
                break;
            case "gear":
                transaction.show(gearFragment);
                binding.navView.getMenu()
                               .findItem(R.id.navigation_gear)
                               .setChecked(true);
                break;
            case "salmonrun":
                transaction.show(salmonrunFragment);
                binding.navView.getMenu()
                               .findItem(R.id.navigation_salmonrun)
                               .setChecked(true);
                break;
            case "schedule":
                transaction.show(scheduleFragment);
                binding.navView.getMenu()
                               .findItem(R.id.navigation_schedule)
                               .setChecked(true);
                break;
            case "splatfests":
                transaction.show(splatfestsFragment);
                binding.navView.getMenu()
                               .findItem(R.id.navigation_splatfests)
                               .setChecked(true);
                break;
        }

        transaction.commit();
    }


    /**
     * 当Json数据下载完成时，通知页面刷新
     * 本来是打算自己写页面的，最后直接套splatoon3.ink了
     * 但是由于使用了数据缓存，所以也可以通知网页刷新
     */
    private void refreshCurrentFragment() {
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if (fragment != null && fragment instanceof Refreshable) {
            ((Refreshable) fragment).onDataUpdated(); // 通知页面
        }
    }

    /**
     * 用于清理Android/data/<package_name>/files下的目录
     * 最起码我这个程序在那个目录放的都不是什么重要东西
     * 已经这样了，应该不会出现误删的问题吧
     */
    private void clearFiles(String relativePath) {
        // 获取 /Android/data/<packagename>/files 目录
        File rootDir = getExternalFilesDir(null);

        if (rootDir != null) {
            // 拼接完整路径
            File target = new File(rootDir,
                                   relativePath);

            // 检查目标是否存在
            if (target.exists()) {
                // 递归删除文件或目录
                deleteRecursive(target);
//            System.out.println("Deleted: " + target.getAbsolutePath());
            } else {
                System.out.println("File or directory does not exist: " + target.getAbsolutePath());
            }
        }
    }

    /**
     * 递归删除文件（夹）
     * 只被clearFiles调用
     */
    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            File[] children = fileOrDirectory.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursive(child);
                }
            }
        }
        fileOrDirectory.delete(); // 删除文件或空目录
    }
}
