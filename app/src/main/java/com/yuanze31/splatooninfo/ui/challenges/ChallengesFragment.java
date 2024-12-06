package com.yuanze31.splatooninfo.ui.challenges;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.yuanze31.splatooninfo.Refreshable;
import com.yuanze31.splatooninfo.databinding.FragmentChallengesBinding;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ChallengesFragment extends Fragment implements Refreshable {

    private FragmentChallengesBinding binding;
    private ChallengesViewModel challengesViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 初始化 ViewModel
        challengesViewModel = new ViewModelProvider(this).get(ChallengesViewModel.class);

        // 初始化绑定
        binding = FragmentChallengesBinding.inflate(inflater,
                                                    container,
                                                    false);
        View root = binding.getRoot();

        // 配置 WebView
        WebView webView = binding.salmonrunWebView;
        webView.getSettings()
               .setJavaScriptEnabled(true);

        if (savedInstanceState == null) {
            webView.loadUrl("https://splatoon3.ink/challenges");
        } else {
            webView.restoreState(savedInstanceState);
        }

        // 设置拦截逻辑
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String originalUrl = request.getUrl()
                                            .toString();
                String finalUrl = challengesViewModel.getImagePath(getContext(),
                                                                   originalUrl);

                if (finalUrl.startsWith("file:///android_asset/")) {
                    String localFile = finalUrl.replace("file:///android_asset/",
                                                        "");
                    try {
                        InputStream inputStream = getContext().getAssets()
                                                              .open(localFile);
                        return new WebResourceResponse("image/png",
                                                       "UTF-8",
                                                       inputStream);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                return null; // 加载网络资源
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view,
                                     url);

                // 读取 assets 文件夹中的 JavaScript
                try {
                    InputStream inputStream = getContext().getAssets()
                                                          .open("customJavaScripts/splatoon3.ink.js");
                    byte[] buffer = new byte[inputStream.available()];
                    inputStream.read(buffer);
                    inputStream.close();

                    // 转换为字符串
                    String jsCode = new String(buffer,
                                               StandardCharsets.UTF_8);

                    // 执行 JavaScript 代码
                    view.evaluateJavascript(jsCode,
                                            null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return root;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        binding.salmonrunWebView.saveState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDataUpdated() {
        if (binding != null) {
            binding.salmonrunWebView.reload();
        }
    }
}
