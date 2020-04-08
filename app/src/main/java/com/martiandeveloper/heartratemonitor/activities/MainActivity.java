package com.martiandeveloper.heartratemonitor.activities;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.martiandeveloper.heartratemonitor.R;
import com.martiandeveloper.heartratemonitor.tools.ImageProcessing;

import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    // UI Components
    // MaterialToolbar
    @BindView(R.id.activity_main_toolbar)
    MaterialToolbar activity_main_toolbar;
    // SurfaceView
    @BindView(R.id.activity_main_mainSV)
    SurfaceView activity_main_mainSV;
    // HeartRateView
    @SuppressLint("StaticFieldLeak")
    private static View activity_main_mainHRV;
    // MaterialTextView
    private static MaterialTextView activity_main_progressTV;
    // ProgressBar
    @SuppressLint("StaticFieldLeak")
    private static ProgressBar activity_main_progressbar;
    // MaterialButton
    private static MaterialButton activity_main_checkAgainBTN;
    // ConstraintLayout
    @BindView(R.id.activity_main_mainCL)
    ConstraintLayout activity_main_mainCL;
    // FrameLayout
    @BindView(R.id.activity_main_bannerAdFL)
    FrameLayout activity_main_bannerAdFL;

    // String
    @BindString(R.string.app_name)
    String app_name;
    @BindString(R.string.check_out)
    String check_out;
    @BindString(R.string.app_link)
    String app_link;
    @BindString(R.string.heartratemonitor_main_banner)
    String heartratemonitor_main_banner;
    @BindString(R.string.heartratemonitor_main_interstitial)
    String heartratemonitor_main_interstitial;

    // Variables
    // Final
    private static final String TAG = "HeartRateMonitor";
    private static final AtomicBoolean atomicBoolean = new AtomicBoolean(false);
    private static final int averageArraySize = 4;
    private static final int[] averageArray = new int[averageArraySize];
    private static final int beatsArraySize = 3;
    private static final int[] beatsArray = new int[beatsArraySize];
    // SurfaceHolder
    private static SurfaceHolder surfaceHolder;
    // Camera
    private static Camera camera;
    // WakeLock
    private static PowerManager.WakeLock wakeLock;
    // Integer
    private static int averageIndex;
    private static int beatsIndex;
    // Double
    private static double beats;
    // Long
    private static long startTime;
    // InterstitialAd
    private static InterstitialAd interstitialAd;

    public enum TYPE {
        GREEN, RED
    }

    private static TYPE currentType = TYPE.GREEN;

    public static TYPE getCurrent() {
        return currentType;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    private void initUI() {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initialFunctions();
    }

    private void initialFunctions() {
        setToolbar();
        declareVariables();
        setWakeLock();
        checkCameraPermission();
        setAds();
    }

    private void setToolbar() {
        setSupportActionBar(activity_main_toolbar);
    }

    private void declareVariables() {
        // SurfaceHolder
        surfaceHolder = activity_main_mainSV.getHolder();
        // Camera
        camera = null;
        // WakeLock
        wakeLock = null;
        // Integer
        averageIndex = 0;
        beatsIndex = 0;
        // Double
        beats = 0;
        // Long
        startTime = 0;
        // HearRateView
        activity_main_mainHRV = findViewById(R.id.activity_main_mainHRV);
        // MaterialTextView
        activity_main_progressTV = findViewById(R.id.activity_main_progressTV);
        // ProgressBar
        activity_main_progressbar = findViewById(R.id.activity_main_progressbar);
        activity_main_progressbar.setMax(400);
        // MaterialButton
        activity_main_checkAgainBTN = findViewById(R.id.activity_main_checkAgainBTN);
        activity_main_checkAgainBTN.setVisibility(View.GONE);
    }

    @SuppressLint("InvalidWakeLockTag")
    private void setWakeLock() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        assert pm != null;
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DoNotDimScreen");
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }
    }

    private void setAds() {
        // BannerAd
        MobileAds.initialize(this);
        AdView adView = new AdView(this);
        adView.setAdUnitId(heartratemonitor_main_banner);
        activity_main_bannerAdFL.addView(adView);

        AdRequest bannerAdRequest = new AdRequest.Builder().build();
        AdSize adSize = getAdSize();
        adView.setAdSize(adSize);
        adView.loadAd(bannerAdRequest);

        // InterstitialAd
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(heartratemonitor_main_interstitial);
        AdRequest interstitialAdRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(interstitialAdRequest);
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                interstitialAd.loadAd(interstitialAdRequest);
            }
        });
    }

    private AdSize getAdSize() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_menu_share:
                share();
                return true;
            case R.id.main_menu_rate_us:
                rateUs();
                return true;
            case R.id.main_menu_about:
                about();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void share() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, app_name);
        sendIntent.putExtra(Intent.EXTRA_TEXT, check_out + "\n" + app_link);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    private void rateUs() {
        Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.martiandeveloper.heartratemonitor");
        Intent webIntent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(webIntent);
    }

    private void about() {
        Intent aboutIntent = new Intent(this, AboutActivity.class);
        startActivity(aboutIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private static final Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {

        @Override
        public void onPreviewFrame(byte[] data, Camera cam) {
            if (data == null) throw new NullPointerException();
            Camera.Size size = cam.getParameters().getPreviewSize();
            if (size == null) throw new NullPointerException();

            if (!atomicBoolean.compareAndSet(false, true)) return;

            int width = size.width;
            int height = size.height;

            int imgAvg = ImageProcessing.decodeYUV420SPtoRedAvg(data.clone(), width, height);

            if (imgAvg == 0 || imgAvg == 255) {
                atomicBoolean.set(false);
                return;
            }

            int averageArrayAvg = 0;
            int averageArrayCnt = 0;
            for (int item : averageArray) {
                if (item > 0) {
                    averageArrayAvg += item;
                    averageArrayCnt++;
                }
            }

            int rollingAverage = (averageArrayCnt > 0) ? (averageArrayAvg / averageArrayCnt) : 0;
            TYPE newType = currentType;
            if (imgAvg < rollingAverage) {
                newType = TYPE.RED;
                if (newType != currentType) {
                    beats++;
                }
            } else if (imgAvg > rollingAverage) {
                newType = TYPE.GREEN;
            }

            if (averageIndex == averageArraySize) averageIndex = 0;
            averageArray[averageIndex] = imgAvg;
            averageIndex++;

            if (newType != currentType) {
                currentType = newType;
                activity_main_mainHRV.postInvalidate();
            }

            long endTime = System.currentTimeMillis();
            double totalTimeInSecs = (endTime - startTime) / 1000d;
            if (totalTimeInSecs >= 10) {
                double bps = (beats / totalTimeInSecs);
                int dpm = (int) (bps * 60d);
                if (dpm < 30 || dpm > 180) {
                    startTime = System.currentTimeMillis();
                    beats = 0;
                    atomicBoolean.set(false);
                    return;
                }

                if (beatsIndex == beatsArraySize) beatsIndex = 0;
                beatsArray[beatsIndex] = dpm;
                beatsIndex++;

                int beatsArrayAvg = 0;
                int beatsArrayCnt = 0;
                for (int value : beatsArray) {
                    if (value > 0) {
                        beatsArrayAvg += value;
                        beatsArrayCnt++;
                    }
                }
                int beatsAvg = (beatsArrayAvg / beatsArrayCnt);
                activity_main_progressTV.setText(String.valueOf(beatsAvg));
                activity_main_progressbar.setProgress(beatsAvg);
                startTime = System.currentTimeMillis();
                beats = 0;
                activity_main_checkAgainBTN.setVisibility(View.VISIBLE);
                if (interstitialAd != null) {
                    if (interstitialAd.isLoaded()) {
                        interstitialAd.show();
                    }
                }
            }
            atomicBoolean.set(false);
        }
    };

    private static final SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

        @SuppressLint("LongLogTag")
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera.setPreviewDisplay(surfaceHolder);
                camera.setPreviewCallback(previewCallback);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            Camera.Size size = getSmallestPreviewSize(width, height, parameters);
            if (size != null) {
                parameters.setPreviewSize(size.width, size.height);
                Log.d(TAG, "Using width=" + size.width + " height=" + size.height);
            }
            camera.setParameters(parameters);
            camera.startPreview();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    };

    private static Camera.Size getSmallestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea < resultArea) result = size;
                }
            }
        }

        return result;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (wakeLock != null) {
            wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            surfaceHolder.addCallback(surfaceCallback);
            camera = Camera.open();
        } else {
            Snackbar.make(activity_main_mainCL, "Permission needed", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Give Permission", v -> {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_CONTACTS)) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
                        } else {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", MainActivity.this.getPackageName(), null);
                            intent.setData(uri);
                            MainActivity.this.startActivity(intent);
                        }
                    }).show();
        }

        startTime = System.currentTimeMillis();
    }

    @Override
    public void onPause() {
        super.onPause();

        wakeLock.release();

        try {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.activity_main_checkAgainBTN)
    public void activity_main_checkAgainBTNClicked() {
        Intent intent = getIntent();
        MainActivity.this.finish();
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}

