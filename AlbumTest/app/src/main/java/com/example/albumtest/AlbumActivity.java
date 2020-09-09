package com.example.albumtest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.utils.MyLog;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;

public class AlbumActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getName();

    private final int REQUEST_CODE_OPEN_CAMERA = 0x100;
    private final int REQUEST_CODE_OPEN_DOC = 0x101;
    private final int REQUEST_CODE_OPEN_ALBUM = 0x102;
    private ImageView mImageView = null;
    private Button mBtnOpenAlbum = null;
    private TextView mTextViewInfo = null;
    private Bitmap mBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        initUI();
    }

    private void initUI () {
        View decorView = this.getWindow().getDecorView();
        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        decorView.setSystemUiVisibility(uiOptions);
        mImageView = this.findViewById(R.id.image_view);
        mBtnOpenAlbum = this.findViewById(R.id.btn_open_album);
        mTextViewInfo = this.findViewById(R.id.text_view_info);
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

        findViewById(R.id.btn_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSystemCamera();
            }
        });

        findViewById(R.id.btn_document).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSystemDocument();
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
            case REQUEST_CODE_OPEN_CAMERA:
                saveCaptureImage (data);
                break;
            case REQUEST_CODE_OPEN_DOC:
                selectDocFile (data);
                break;
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
            String path = "test_" + System.currentTimeMillis()/1000 + ".jpeg";
            MyLog.d(TAG, "saveImageToGallery begin");
            GalleryFileSaver.saveBitmapToGallery(this, path, mBitmap);
            MyLog.d(TAG, "saveImageToGallery end");
            //GalleryFileSaver.saveToSystemGallery(this, mBitmap);
            Toast.makeText(this, "saveImageToGallery done", Toast.LENGTH_SHORT).show();
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

    private void openSystemCamera() {
        String imagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/Camera/" + System.currentTimeMillis()+".jpeg";
        File filePath = new File(imagePath);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //judge sdk version
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            //如果在Android7.0以上,使用FileProvider获取Uri
            cameraIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(this, "com.example.albumtest", filePath);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        } else {
            //否则使用Uri.fromFile(file)方法获取Uri
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(filePath));
        }
        cameraIntent.putExtra("path", imagePath);
        startActivityForResult(cameraIntent, REQUEST_CODE_OPEN_CAMERA);
    }

    private void saveCaptureImage (Intent intent) {
        if (intent != null) {
            String path = intent.getStringExtra("path");
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            setImageViewBackground (mImageView, bitmap);
        }
    }

    private void openSystemDocument () {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_OPEN_DOC);
    }

    private void selectDocFile (Intent intent) {
        Uri uri = null;
        if (intent != null) {
            uri = intent.getData();
            MyLog.d(TAG, "selectDocFile " + uri.toString());
        }
        /*Bitmap bitmap = getBitmapFromUri(uri);
        setImageViewBackground (mImageView, bitmap);*/
        ImageFileTask imageFileTask = new ImageFileTask();
        imageFileTask.execute(uri);
    }

    private Bitmap getBitmapFromUri (Uri uri) {
        MyLog.d(TAG, "getBitmapFromUri uri = " + uri.toString());
        Bitmap bitmap = null;
        try {
            ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return bitmap;
    }

    private void setImageViewBackground (ImageView imageView, Bitmap bitmap) {
        if (imageView != null && bitmap != null) {
            imageView.setBackground(new BitmapDrawable(getResources(), bitmap));
        } else {
            MyLog.e(TAG, "setImageViewBackground error");
        }
    }

    private class ImageFileTask extends AsyncTask<Uri, Integer, Bitmap> {

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPreExecute() {
            MyLog.d(TAG, "onPreExecute");
            mTextViewInfo.setText("loading");
        }

        @Override
        protected Bitmap doInBackground(Uri... uris) {
            return getBitmapFromUri (uris[0]);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            setImageViewBackground(mImageView, bitmap);
            mTextViewInfo.setText("done");
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}