package com.trungcoder.youtubeforcar;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.trungcoder.youtubeforcar.databinding.ActivityMainBinding;
import com.trungcoder.youtubeforcar.fragment.BrowseFragment.BrowseFragment;
import com.trungcoder.youtubeforcar.fragment.WebViewFragment;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager;
    public static ActivityMainBinding binding;
    public static YouTubePlayerView youTubePlayerView;
    public static IFramePlayerOptions iFramePlayerOptions;
    public static List<VideoItem> queue;
    public static QueueAdapter queueAdapter;
    public static int currentVideoQueueIndex = -1;

    //ProgressDialog can be shown while downloading data from the internet
    //which indicates that the query is being processed
    private ProgressDialog mProgressDialog;

    private final BroadcastReceiver exit = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        registerReceiver(exit, new IntentFilter("STOP"));

        queue = new ArrayList<>();
        youTubePlayerView = binding.youTubePlayerView;
        bottomNavigationView = binding.bottomNavigationView;
        fragmentManager = getSupportFragmentManager();


        queueAdapter = new QueueAdapter(queue);
        binding.rvQueue.setAdapter(queueAdapter);
        binding.rvQueue.setLayoutManager(new LinearLayoutManager(this));

        BrowseFragment browseFragment = new BrowseFragment();
        WebViewFragment webViewFragment = new WebViewFragment();
        bottomNavigationView.setSelectedItemId(R.id.miBrowse);
        setFragment(browseFragment);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.miQueue:
                        binding.fragmentContainer.setVisibility(View.GONE);
                        break;
                    case R.id.miBrowse:
                        binding.fragmentContainer.setVisibility(View.VISIBLE);
                        setFragment(browseFragment);
                        break;
                    case R.id.miWebview:
                        binding.fragmentContainer.setVisibility(View.VISIBLE);
                        setFragment(webViewFragment);
                        break;
                }
                return true;
            }
        });

        // Handle fullscreen view
        FrameLayout fullscreenViewContainer = binding.fullscreenViewContainer;
        WindowInsetsControllerCompat windowInsetsController = WindowCompat.getInsetsController(getWindow(),getWindow().getDecorView());
        windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);


        youTubePlayerView.setEnableAutomaticInitialization(false);
        iFramePlayerOptions = new IFramePlayerOptions.Builder()
                .controls(1)
                .fullscreen(1)
                .build();
        youTubePlayerView.addFullscreenListener(new FullscreenListener() {
            @Override
            public void onEnterFullscreen(@NonNull View fullscreenView, @NonNull Function0<Unit> function0) {
                youTubePlayerView.setVisibility(View.GONE);
                bottomNavigationView.setVisibility(View.GONE);
                fullscreenViewContainer.setVisibility(View.VISIBLE);
                fullscreenViewContainer.addView(fullscreenView);
                // optionally request landscape orientation
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
            @Override
            public void onExitFullscreen() {
                youTubePlayerView.setVisibility(View.VISIBLE);
                bottomNavigationView.setVisibility(View.VISIBLE);
                fullscreenViewContainer.setVisibility(View.GONE);
                fullscreenViewContainer.removeAllViews();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        });

        // Handle notification control
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("running_channel", "Running notifications", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

        // Start service
        Intent videoServiceIntent = new Intent(this, VideoService.class);
        videoServiceIntent.setAction(VideoService.Actions.START.toString());
        startService(videoServiceIntent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(exit);
        youTubePlayerView.release();
    }

    private void setFragment(Fragment fragment){
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment,null)
                .setReorderingAllowed(true)
                .addToBackStack(null) // Name can be null
                .commit();
    }
}