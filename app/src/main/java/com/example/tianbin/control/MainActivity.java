package com.example.tianbin.control;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "MainActivity";

    private Client client;
    private EditText etIp;
    private EditText etPort;
    private EditText etId;
    private EditText etCmd;
    private TextView tvStatus;
    private Button bt;
    private StringBuffer sb;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    tvStatus.setText(sb.toString());
                    break;
            }
        }
    };
    private CheckBox rb1;
    private CheckBox rb2;
    private CheckBox rb3;
    private CheckBox rb4;
    private CheckBox rb5;
    private CheckBox rb6;
    private CheckBox rb7;
    private CheckBox rb8;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initVariables();
        initListeners();
    }

    private void initListeners() {
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(etIp.getText().toString())
                        || TextUtils.isEmpty(etPort.getText().toString())
//                        ||TextUtils.isEmpty(etId.getText().toString())
//                        ||TextUtils.isEmpty(etCmd.getText().toString())
                        ) {
                    return;
                }
                if (client.isConnected()) {
                    Toast.makeText(MainActivity.this, "设备已连接...", Toast.LENGTH_SHORT).show();
                    return;
                }
                new Thread(){
                    @Override
                    public void run() {
                        sb.delete(0, sb.length());
                        sb.append(tvStatus.getText().toString());

                        //建立连接
                        String s1 = "connect: " + client.connect(etIp.getText().toString(), Integer.parseInt(etPort.getText().toString()));
                        Log.e(TAG, s1);
                        sb.append(s1 + "\n\n");
                        mHandler.sendEmptyMessage(0);

                        //scan
                        String ss="getID: " + client.scan();
                        Log.e(TAG, ss);
                        sb.append(ss + "\n\n");
                        mHandler.sendEmptyMessage(0);

                        //获取设备id
                        id=client.getID();
                        String s2 = "getID: " + id;
                        Log.e(TAG, s2);
                        sb.append(s2 + "\n\n");
                        mHandler.sendEmptyMessage(0);

                        //读取设备状态
                        String[] strs = client.read();
                        String s3 = "read: ";
                        for (String s : strs) {
                            s3 += s + "-";
                        }
                        Log.e(TAG, s3);
                        sb.append(s3 + "\n\n");
                        mHandler.sendEmptyMessage(0);
                    }
                }.start();
            }
        });
        rb1.setOnCheckedChangeListener(this);
        rb2.setOnCheckedChangeListener(this);
        rb3.setOnCheckedChangeListener(this);
        rb4.setOnCheckedChangeListener(this);
        rb5.setOnCheckedChangeListener(this);
        rb6.setOnCheckedChangeListener(this);
        rb7.setOnCheckedChangeListener(this);
        rb8.setOnCheckedChangeListener(this);
    }

    private void initVariables() {
        sb = new StringBuffer();
        client = Client.getInstance();
    }

    private void initView() {
        etIp = (EditText) findViewById(R.id.et_ip);
        etPort = (EditText) findViewById(R.id.et_port);
        etId = (EditText) findViewById(R.id.et_id);
        etCmd = (EditText) findViewById(R.id.et_cmd);
        tvStatus = (TextView) findViewById(R.id.tv_status);
        bt = (Button) findViewById(R.id.bt);
        rb1 = (CheckBox) findViewById(R.id.rb1);
        rb2 = (CheckBox) findViewById(R.id.rb2);
        rb3 = (CheckBox) findViewById(R.id.rb3);
        rb4 = (CheckBox) findViewById(R.id.rb4);
        rb5 = (CheckBox) findViewById(R.id.rb5);
        rb6 = (CheckBox) findViewById(R.id.rb6);
        rb7 = (CheckBox) findViewById(R.id.rb7);
        rb8 = (CheckBox) findViewById(R.id.rb8);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        final String str=b?"1":"0";
        String idTemp="1";
        switch (compoundButton.getId()){
            case R.id.rb1:
                idTemp="1";
                break;
            case R.id.rb2:
                idTemp="2";
                break;
            case R.id.rb3:
                idTemp="3";
                break;
            case R.id.rb4:
                idTemp="4";
                break;
            case R.id.rb5:
                idTemp="5";
                break;
            case R.id.rb6:
                idTemp="6";
                break;
            case R.id.rb7:
                idTemp="7";
                break;
            case R.id.rb8:
                idTemp="8";
                break;
        }
        final String idf=idTemp;
        new Thread(){
            @Override
            public void run() {
                super.run();
                String res="set: id="+idf+",status="+str+",result="+client.set(idf,str);
                Log.e(TAG, res );
                sb.append(res+"\n\n");
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }
}
