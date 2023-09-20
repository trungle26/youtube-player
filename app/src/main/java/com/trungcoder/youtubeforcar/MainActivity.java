package com.trungcoder.youtubeforcar;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.trungcoder.youtubeforcar.adapter.ViewPagerAdapter;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private static YouTubePlayer youTubePlayer;
    public static YouTubePlayerView youTubePlayerView;

    //ProgressDialog can be shown while downloading data from the internet
    //which indicates that the query is being processed
    private ProgressDialog mProgressDialog;
    private boolean isFullscreen = false;
    private OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            if(isFullscreen){
                youTubePlayer.toggleFullscreen();

            }else{
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tabs);
        viewPager2 = findViewById(R.id.view_pager);
        youTubePlayerView = findViewById(R.id.youTubePlayerView);
        FrameLayout fullscreenViewContainer = findViewById(R.id.fullscreenViewContainer);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager2.setAdapter(adapter);
        viewPager2.setUserInputEnabled(false);

        WindowInsetsControllerCompat windowInsetsController = WindowCompat.getInsetsController(getWindow(),getWindow().getDecorView());
        windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        getWindow().getDecorView().setOnApplyWindowInsetsListener(((view, windowInsets) -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (windowInsets.isVisible(WindowInsetsCompat.Type.navigationBars())
                        || windowInsets.isVisible(WindowInsetsCompat.Type.statusBars())) {
                    if(isFullscreen){
                        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
                    }
                } else {
                    if(!isFullscreen){
                        windowInsetsController.show(WindowInsetsCompat.Type.systemBars());
                    }
                }
            }
            return view.onApplyWindowInsets(windowInsets);
        }));


        String[] labels = {"Youtube","Nhạc US-UK","Truyện nói"};
        (new TabLayoutMediator(tabLayout, viewPager2,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        tab.setText(labels[position]);
                    }
                })).attach();

        IFramePlayerOptions iFramePlayerOptions = new IFramePlayerOptions.Builder()
                .controls(1)
                .fullscreen(1)
                .build();
        youTubePlayerView.setEnableAutomaticInitialization(false);
        youTubePlayerView.addFullscreenListener(new FullscreenListener() {
            @Override
            public void onEnterFullscreen(@NonNull View fullscreenView, @NonNull Function0<Unit> function0) {
                isFullscreen = true;
                youTubePlayerView.setVisibility(View.GONE);
                fullscreenViewContainer.setVisibility(View.VISIBLE);
                fullscreenViewContainer.addView(fullscreenView);
                // optionally request landscape orientation
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }

            @Override
            public void onExitFullscreen() {
                isFullscreen = false;
                youTubePlayerView.setVisibility(View.VISIBLE);
                fullscreenViewContainer.setVisibility(View.GONE);
                fullscreenViewContainer.removeAllViews();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        });
        youTubePlayerView.initialize(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                MainActivity.youTubePlayer = youTubePlayer;
                super.onReady(youTubePlayer);
            }
        }, iFramePlayerOptions);
        youTubePlayerView.enableBackgroundPlayback(true);
    }

}