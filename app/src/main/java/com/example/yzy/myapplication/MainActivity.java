package com.example.yzy.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View pView){
        int i = 0;
        int j = 10/i;
        Log.e("yzy", "这里产生了一个异常将被自己的crashHandler捕获 " + j);
    }

}
