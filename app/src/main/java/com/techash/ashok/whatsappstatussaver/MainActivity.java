package com.techash.ashok.whatsappstatussaver;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_GROUP_PERMISSION = 666;
    public static int REQUEST_READ_STORAGE = 125;
    public static int REQUEST_WRITE_STORAGE = 225;
    PermissionUtil permissionUtil;
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    view_pager_adapter pager_adapter;
    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        permissionUtil = new PermissionUtil(this);
        MobileAds.initialize(getApplicationContext(),
                "ca-app-pub-7152320266992965~4085348501");
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.container);
        pager_adapter = new view_pager_adapter(getSupportFragmentManager());
        pager_adapter.add_fragments(new pictures(), "Pictures");
        pager_adapter.add_fragments(new videos(), "Videos");
        viewPager.setAdapter(pager_adapter);
        tabLayout.setupWithViewPager(viewPager);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-7152320266992965/6701216633");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });

        requestNewInterstitial();

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED)
        {
            requestAllPermissions();
        }

    }

    @Override
    public void onBackPressed() {
        if (mInterstitialAd.isLoaded())
            mInterstitialAd.show();
        super.onBackPressed();
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_rate) {
            if (mInterstitialAd.isLoaded())
                mInterstitialAd.show();

            AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
            ad.setTitle("Do you Love this app?");
            ad.setMessage("Then Rate Us 5 Stars!");
            ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                            Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    try {
                        startActivity(goToMarket);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
                    }
                }

            });
            ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            ad.show();
            return true;
        } else if (id == R.id.menu_share) {
            if (mInterstitialAd.isLoaded())
                mInterstitialAd.show();

            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("text/plain");
            share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            share.putExtra(Intent.EXTRA_SUBJECT, "WA Status Saver");
            share.putExtra(Intent.EXTRA_TEXT, "http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName());
            startActivity(Intent.createChooser(share, "Share to Friends!"));
            return true;
        } else if (id == R.id.menu_about) {
            if (mInterstitialAd.isLoaded())
                mInterstitialAd.show();

            startActivity(new Intent(MainActivity.this, about.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        viewPager.getAdapter().notifyDataSetChanged();
        super.onResume();
    }


    private void requestGroupPermission(ArrayList<String> permissions)
    {
        String[] permissionList=new String[permissions.size()];
        permissions.toArray(permissionList);
        ActivityCompat.requestPermissions(MainActivity.this,permissionList,REQUEST_GROUP_PERMISSION);
    }

    public void requestAllPermissions()
    {
        ArrayList<String> permissionsNeeded=new ArrayList<>();
        ArrayList<String> permissionsAvailable=new ArrayList<>();
        permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        for(String permission: permissionsAvailable)
        {
            if(ContextCompat.checkSelfPermission(this,permission)!=PackageManager.PERMISSION_GRANTED)
                permissionsNeeded.add(permission);
        }
        requestGroupPermission(permissionsNeeded);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case REQUEST_GROUP_PERMISSION:
                int i=0;
                String status="";
                for(String permission : permissions)
                {
                    if(grantResults[i]==PackageManager.PERMISSION_GRANTED)
                        status="GRANTED";
                    else
                    {
                        status="DENIED";
                    }
                    i++;
                }
                if(status.equals("DENIED"))
                {
                    Toast.makeText(this, "Please allow all permission in your app settings",Toast.LENGTH_LONG).show();
                    Toast.makeText(this, "Please allow all permission in your app settings",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri=Uri.fromParts("package",this.getPackageName(),null);
                    intent.setData(uri);
                    this.startActivity(intent);
                }
                else if (status.equals("GRANTED"))
                {
                    finish();
                    startActivity(getIntent());
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }




}