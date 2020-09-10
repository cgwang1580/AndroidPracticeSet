package com.example.albumtest;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.permission.PermissionHelper;
import com.example.permission.PermissionInterface;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private final String TAG  = this.getClass().getName();

    private final static String[]PermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    private boolean bPermissionOK = false;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init ();
        bPermissionOK = requestPermission ();
    }

    public boolean requestPermission () {
        return PermissionHelper.MyRequestPermission(this, PermissionList, new PermissionInterface() {
            @Override
            public int doPermissionSucceed() {
                bPermissionOK = true;
                Toast.makeText(HomeActivity.this, "HomeActivity requestPermission doPermissionSucceed", Toast.LENGTH_SHORT).show();
                return 0;
            }

            @Override
            public int doPermissionFailed() {
                Toast.makeText(HomeActivity.this, "HomeActivity requestPermission doPermissionFailed", Toast.LENGTH_SHORT).show();
                return 0;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionHelper.onMyRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume () {
        super.onResume();
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
    }

    private void init () {

        findViewById(R.id.home_btn_album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAlbumActivity();
            }
        });

        findViewById(R.id.home_btn_gallery_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGalleryActivity();
            }
        });
    }

    private void startAlbumActivity() {
        if (bPermissionOK) {
            Intent intent = new Intent(this, AlbumActivity.class);
            startActivity(intent);
        }
    }

    private void startGalleryActivity() {
        if (bPermissionOK) {
            Intent intent = new Intent(this, GalleryViewActivity.class);
            final String picture_folder = "/test_jpeg";
            intent.putExtra("folder", picture_folder);
            startActivity(intent);
        }
    }

}
