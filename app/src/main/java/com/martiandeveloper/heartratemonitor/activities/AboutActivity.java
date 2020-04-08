package com.martiandeveloper.heartratemonitor.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.google.android.material.appbar.MaterialToolbar;
import com.martiandeveloper.heartratemonitor.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutActivity extends AppCompatActivity {

    // UI Components
    // ImageView
    @BindView(R.id.activity_about_facebookIV)
    ImageView activity_about_facebookIV;
    @BindView(R.id.activity_about_instagramIV)
    ImageView activity_about_instagramIV;
    @BindView(R.id.activity_about_twitterIV)
    ImageView activity_about_twitterIV;
    @BindView(R.id.activity_about_whatsappIV)
    ImageView activity_about_whatsappIV;
    @BindView(R.id.activity_about_websiteIV)
    ImageView activity_about_websiteIV;
    @BindView(R.id.activity_about_emailIV)
    ImageView activity_about_emailIV;
    // TextView
    @BindView(R.id.activity_about_copyrightTV)
    TextView activity_about_copyrightTV;
    // MaterialToolbar
    @BindView(R.id.activity_about_toolbar)
    MaterialToolbar activity_about_toolbar;

    // String
    @BindString(R.string.send_via)
    String send_via;
    @BindString(R.string.copyright1)
    String copyright1;
    @BindString(R.string.copyright2)
    String copyright2;
    @BindString(R.string.facebook)
    String facebook;
    @BindString(R.string.instagram)
    String instagram;
    @BindString(R.string.twitter)
    String twitter;
    @BindString(R.string.whatsapp)
    String whatsapp;
    @BindString(R.string.website)
    String website;
    @BindString(R.string.email)
    String email;
    @BindString(R.string.google_play)
    String google_play;
    @BindString(R.string.privacy_policy_link)
    String privacy_policy_link;
    @BindString(R.string.app_name)
    String app_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    private void initUI() {
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        initialFunctions();
    }

    private void initialFunctions() {
        setToolbar();
        setImage();
        setText();
    }

    private void setToolbar() {
        setSupportActionBar(activity_about_toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setImage() {
        List<Integer> images = new ArrayList<>();
        images.add(R.drawable.facebook);
        images.add(R.drawable.instagram);
        images.add(R.drawable.twitter);
        images.add(R.drawable.whatsapp);
        images.add(R.drawable.website);
        images.add(R.drawable.email);

        List<ImageView> imageViews = new ArrayList<>();
        imageViews.add(activity_about_facebookIV);
        imageViews.add(activity_about_instagramIV);
        imageViews.add(activity_about_twitterIV);
        imageViews.add(activity_about_whatsappIV);
        imageViews.add(activity_about_websiteIV);
        imageViews.add(activity_about_emailIV);

        for (int i = 0; i < images.size(); i++) {
            Glide.with(this)
                    .load(images.get(i))
                    .priority(Priority.IMMEDIATE)
                    .into(imageViews.get(i));
        }
    }

    @OnClick(R.id.activity_about_facebookIV)
    public void activity_about_facebookIVCLicked() {
        open(facebook);
    }

    @OnClick(R.id.activity_about_instagramIV)
    public void activity_about_instagramIVCLicked() {
        open(instagram);
    }

    @OnClick(R.id.activity_about_twitterIV)
    public void activity_about_twitterIVCLicked() {
        open(twitter);
    }

    @OnClick(R.id.activity_about_whatsappIV)
    public void activity_about_whatsappIVCLicked() {
        call();
    }

    @OnClick(R.id.activity_about_websiteIV)
    public void activity_about_websiteIVCLicked() {
        open(website);
    }

    @OnClick(R.id.activity_about_emailIV)
    public void activity_about_emailIVCLicked() {
        openEmail();
    }

    @OnClick(R.id.activity_about_otherAppsTV)
    public void activity_about_otherAppsTVClicked() {
        open(google_play);
    }

    private void open(String url) {
        Uri open_uri = Uri.parse(url);
        Intent open_intent = new Intent(Intent.ACTION_VIEW, open_uri);
        startActivity(open_intent);
    }

    private void call() {
        Uri call_uri = Uri.parse(whatsapp);
        Intent call_intent = new Intent(Intent.ACTION_DIAL, call_uri);
        startActivity(call_intent);
    }

    private void openEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", email, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, app_name);
        emailIntent.putExtra(Intent.EXTRA_TEXT, "");
        startActivity(Intent.createChooser(emailIntent, send_via));
    }

    @OnClick(R.id.activity_about_privacyPolicyTV)
    public void activity_about_privacyPolicyTVClicked() {
        open(privacy_policy_link);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void setText() {
        String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        String copyright = copyright1 + " " + year + " " + copyright2;
        activity_about_copyrightTV.setText(copyright);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}