package com.example.branchdeeplink;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.SharingHelper;
import io.branch.referral.util.BRANCH_STANDARD_EVENT;
import io.branch.referral.util.BranchEvent;
import io.branch.referral.util.ContentMetadata;
import io.branch.referral.util.LinkProperties;
import io.branch.referral.util.ShareSheetStyle;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        shareMyLink();
    }

    @Override
    public void onStart() {
        if (getIntent() != null) {
            Log.d("myapp", "got intent");
        } else {
            Log.d("myapp", "did not get intent");
        }
        Intent intent = getIntent();
        setIntent(intent);
        intent.putExtra("branch_force_new_session", true);
        super.onStart();
        Branch.sessionBuilder(this)
                .withCallback(branchReferralInitListener)
                .withData(getIntent() != null ? getIntent().getData() : null)
                .init();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        // if activity is in foreground (or in backstack but partially visible) launching the same
        // activity will skip onStart, handle this case with reInitSession
        if (intent != null &&
                intent.hasExtra("branch_force_new_session") &&
                intent.getBooleanExtra("branch_force_new_session", true)) {
            Branch.sessionBuilder(this).withCallback(branchReferralInitListener).reInit();
        }
    }

    private final Branch.BranchReferralInitListener branchReferralInitListener = (linkProperties, error) -> {
        if (linkProperties != null) {
            Log.d("myapp", "lp detail: " + linkProperties);
        } else {
            Log.d("myapp", "received an empty link properties");
        }
        if (error == null) {
            assert linkProperties != null;
            if (linkProperties.has("~referring_link")) {
                Intent intent = new Intent(LauncherActivity.this, OtherActivity.class);
                intent.putExtra("lp", String.valueOf(linkProperties));
                Log.d("myapp", "goto other activity from listener");
                startActivity(intent);
                // comment following finish out if want to keep initial page alive
                finish();
            }
        } else {
            Log.d("myapp", error.getMessage());
        }
    };

    private void shareMyLink() {
        findViewById(R.id.button).setOnClickListener(this::onClick);
    }

    private void onClick(View view) {
        BranchUniversalObject buo = new BranchUniversalObject()
                .setCanonicalIdentifier("branch_deep_link/2022")
                .setCanonicalUrl("https://help.branch.io/using-branch/docs/creating-a-deep-link")
                .setTitle("Branch Deep Link Test")
                .setContentDescription("Explore the Branch Deep Link")
                .setContentImageUrl("https://branch.io/img/logo_icon_black.png")
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .setLocalIndexMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .setContentMetadata(
                        new ContentMetadata()
                                .addCustomMetadata("key1", "value1")
                );

        new BranchEvent(BRANCH_STANDARD_EVENT.VIEW_CART)
                .setRevenue(99.9)
                .addContentItems(buo)
                .logEvent(LauncherActivity.this);

        new BranchEvent("Play with Deep Link")
                .setRevenue(10.1)
                .setCustomerEventAlias("play_with_deep_link")
                .logEvent(LauncherActivity.this);

        // following url demo your favorite country for which only allow the installation of this app
        final String android_url_xx = "https://pan.baidu.com/s/1igwjpQeIKDbDLgQCiKlGog?pwd=mi9m";
        final String android_url = "https://help.branch.io/zh/developers-hub/docs/android-sdk-overview";
        final String ios_url = "https://github.com/kyooryoo/BranchDeepLink/blob/master/fallbacks/IOS.md";
        final String ios_wechat_url = "https://github.com/kyooryoo/BranchDeepLink/blob/master/fallbacks/IOS_WECHAT.md";
        final String ipad_url = "https://github.com/kyooryoo/BranchDeepLink/blob/master/fallbacks/IPAD.md";
        final String fallback_url = "https://help.branch.io/";
        final String after_click_url = "https://branch.io/";

        LinkProperties lp = new LinkProperties()
                .setChannel("copy")
                .setFeature("sharing")
                .setCampaign("Branch Home Work")
                .setStage("new engineer")
                .addTag("Ling's Test")
                .addControlParameter("$fallback_url", fallback_url)
                .addControlParameter("$android_url", android_url)
                .addControlParameter("$android_url_cn", android_url_xx)
                .addControlParameter("$after_click_url", after_click_url)
                .addControlParameter("$ios_url", ios_url)
                .addControlParameter("$ios_wechat_url", ios_wechat_url)
                .addControlParameter("$ipad_url", ipad_url)
                .addControlParameter("custom_random",
                        Long.toString(Calendar.getInstance().getTimeInMillis()));

//        buo.generateShortUrl(this, lp, (url, error) -> {
//            if (error == null) {
//                Log.d("myapp", "Get short Deep Link: " + url);
//            } else {
//                Log.d("myapp", "Error: " + error.getMessage());
//            }
//        });

        ShareSheetStyle ss = new ShareSheetStyle(LauncherActivity.this,
                "Check this out!", "This stuff is awesome: ")
                .setCopyUrlStyle(ContextCompat.getDrawable(LauncherActivity.this,
                        android.R.drawable.ic_menu_send), "Copy", "Added to clipboard")
                .setMoreOptionStyle(ContextCompat.getDrawable(LauncherActivity.this,
                        android.R.drawable.ic_menu_search), "Show more")
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.EMAIL)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.MESSAGE)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.HANGOUT)
                .setAsFullWidthStyle(true)
                .setSharingTitle("Share Branch Deep Link With");

        Branch.BranchLinkShareListener sl = new Branch.BranchLinkShareListener() {
            @Override
            public void onShareLinkDialogLaunched() {
            }

            @Override
            public void onShareLinkDialogDismissed() {
            }

            @Override
            public void onLinkShareResponse(String sharedLink, String sharedChannel, BranchError error) {
                if (error != null) {
                    Log.d("myapp", "Share Failed: " + error.getMessage());
                } else {
                    Log.d("myapp", sharedLink + " is shared by " + sharedChannel);
                }
                Intent intent = new Intent(LauncherActivity.this, SharedActivity.class);
                intent.putExtra("link", sharedLink);
                intent.putExtra("channel", sharedChannel);
                Log.d("myapp", "goto other activity from buo");
                startActivity(intent);
                finish();
            }

            @Override
            public void onChannelSelected(String channelName) {
                Log.d("myapp","selected sharing channel: " + channelName);

            }
        };

        buo.showShareSheet(LauncherActivity.this, lp, ss, sl);
    }
}