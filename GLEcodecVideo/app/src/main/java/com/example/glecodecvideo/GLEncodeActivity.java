package com.example.glecodecvideo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.io.IOException;

public class GLEncodeActivity extends AppCompatActivity {

    EncodeAndMuxTest mEncodeAndMuxTest = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEncodeAndMuxTest = new EncodeAndMuxTest();
    }


    @Override
    protected void onResume () {
        super.onResume();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mEncodeAndMuxTest.testEncodeVideoToMp4();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onPause () {
        super.onPause();
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
    }
}
