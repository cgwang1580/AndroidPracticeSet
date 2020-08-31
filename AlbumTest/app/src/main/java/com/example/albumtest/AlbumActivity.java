package com.example.albumtest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.utils.MyLog;

import java.io.File;

public class AlbumActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getName();

    private final int REQUEST_CODE_OPEN_ALBUM = 0;
    private ImageView mImageView = null;
    private Button mBtnOpenAlbum =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        initUI();
    }

    private void initUI () {
        mImageView = this.findViewById(R.id.image_view);
        mBtnOpenAlbum = this.findViewById(R.id.btn_open);
        mBtnOpenAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSystemAlbum();
            }
        });
    }

    private void openSystemAlbum () {
        MyLog.d(TAG, "openSystemAlbum");
        Intent albumIntent = new Intent(Intent.ACTION_PICK);
        albumIntent.setType("image/*");
        startActivityForResult(albumIntent, REQUEST_CODE_OPEN_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK != resultCode) return;
        switch (requestCode) {
            case REQUEST_CODE_OPEN_ALBUM:
                selectPicture(data);
                break;
            default:
                break;
        }
    }

    private void selectPicture (Intent intent) {
        Uri selectedImageUri = intent.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);
        assert cursor != null;
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();

        File file = new File(picturePath);
        Uri fileUri = FileProvider.getUriForFile(this, "com.example.albumtest", file);

        Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
        if (bitmap != null) {
            mImageView.setBackground(new BitmapDrawable(getResources(), bitmap));
        }
    }
}