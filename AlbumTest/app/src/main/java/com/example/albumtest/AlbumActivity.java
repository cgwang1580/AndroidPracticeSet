package com.example.albumtest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
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
    private Bitmap mBitmap = null;

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
        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImageToGallery();
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

        mBitmap = BitmapFactory.decodeFile(picturePath);
        if (mBitmap != null) {
            mImageView.setBackground(new BitmapDrawable(getResources(), mBitmap));
        }
    }

    final MediaScannerConnection msc = new MediaScannerConnection(this, new MediaScannerConnection.MediaScannerConnectionClient() {
        @Override
        public void onMediaScannerConnected() {
            msc.scanFile(null, null);
        }

        @Override
        public void onScanCompleted(String path, Uri uri) {
            msc.disconnect();
        }
    });

    private void saveImageToGallery () {
        //String path = "/sdcard/test_avatar.jpeg";
        //Bitmap bitmap = BitmapFactory.decodeFile(path);
        if (mBitmap != null) {
            mImageView.setBackground(new BitmapDrawable(getResources(), mBitmap));
            //String pathImg = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "", "");
            GalleryFileSaver.saveBitmapToGallery(this, "test_avatar2.jpeg", mBitmap);
            //GalleryFileSaver.saveToSystemGallery(this, mBitmap);
        }
    }

    public static String getFilePathByContentResolver(Context context, Uri uri) {
        if (null == uri) {
            return null;
        }
        Cursor c = context.getContentResolver().query(uri, null, null, null, null);
        String filePath  = null;
        if (null == c) {
            throw new IllegalArgumentException(
                    "Query on " + uri + " returns null result.");
        }
        try {
            if ((c.getCount() != 1) || !c.moveToFirst()) {

            } else {
                filePath = c.getString(c.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
            }
        } finally {
            c.close();
        }
        return filePath;
    }
}