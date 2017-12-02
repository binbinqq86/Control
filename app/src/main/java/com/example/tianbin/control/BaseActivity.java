package com.example.tianbin.control;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BaseActivity extends AppCompatActivity {

    protected Client client;
    protected ExecutorService executorService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        initVariables();
    }

    private void initVariables() {
        client = Client.getInstance();
        executorService= Executors.newCachedThreadPool();
    }


}
