package com.example.tianbin.control;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

public class ElectricMachineActivity extends BaseActivity {
    private static final String TAG = "ElectricMachineActivity";
    private LinearLayout ll;
    private RadioButton rb11;
    private RadioButton rb12;
    private RadioButton rb13;
    private RadioButton rb21;
    private RadioButton rb22;
    private RadioButton rb23;
    private RadioButton rb31;
    private RadioButton rb32;
    private RadioButton rb33;
    private RadioButton rb41;
    private RadioButton rb42;
    private RadioButton rb43;
    private RadioGroup rg1;
    private RadioGroup rg2;
    private RadioGroup rg3;
    private RadioGroup rg4;
    /**
     * 从0-8代表二进制由低到高的八位
     */
    private List<Integer> statusList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electric_machine);
        initView();
        initListeners();
    }

    private void initView() {
        ll = (LinearLayout) findViewById(R.id.ll);
        rb11 = (RadioButton) findViewById(R.id.rb11);
        rb12 = (RadioButton) findViewById(R.id.rb12);
        rb13 = (RadioButton) findViewById(R.id.rb13);
        rb21 = (RadioButton) findViewById(R.id.rb21);
        rb22 = (RadioButton) findViewById(R.id.rb22);
        rb23 = (RadioButton) findViewById(R.id.rb23);
        rb31 = (RadioButton) findViewById(R.id.rb31);
        rb32 = (RadioButton) findViewById(R.id.rb32);
        rb33 = (RadioButton) findViewById(R.id.rb33);
        rb41 = (RadioButton) findViewById(R.id.rb41);
        rb42 = (RadioButton) findViewById(R.id.rb42);
        rb43 = (RadioButton) findViewById(R.id.rb43);
        rg1 = (RadioGroup) findViewById(R.id.rg1);
        rg2 = (RadioGroup) findViewById(R.id.rg2);
        rg3 = (RadioGroup) findViewById(R.id.rg3);
        rg4 = (RadioGroup) findViewById(R.id.rg4);
        Log.e(TAG, "initView:========client.isConnected()========== " + client.isConnected());
    }

    private void read() {
        statusList.clear();
        //strs从0-8代表二进制由低到高的八位
        String[] strs = client.read();
        //0代表实际的第一个开关
        for (int i = 0; i < 8; i++) {
            int status = 0;
            String sT = "";
            try {
                sT = strs[i];
            } catch (Exception e) {
                sT = "0";
            }
            status = Integer.parseInt(sT);
            statusList.add(status);
            Log.e(TAG, "run:======= " + status + "***" + "第" + (i + 1) + "个灯");
        }
    }

    private void initListeners() {
        rg1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb11:
                        control(6, 7, 0, 1);
                        break;
                    case R.id.rb12:
                        control(6, 7, 1, 0);
                        break;
                    case R.id.rb13:
                        control(6, 7, 0, 0);
                        break;
                }
            }
        });
        rg2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb21:
                        control(4, 5, 0, 1);
                        break;
                    case R.id.rb22:
                        control(4, 5, 1, 0);
                        break;
                    case R.id.rb23:
                        control(4, 5, 0, 0);
                        break;
                }
            }
        });
        rg3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb31:
                        control(2, 3, 0, 1);
                        break;
                    case R.id.rb32:
                        control(2, 3, 1, 0);
                        break;
                    case R.id.rb33:
                        control(2, 3, 0, 0);
                        break;
                }
            }
        });
        rg4.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb41:
                        control(0, 1, 0, 1);
                        break;
                    case R.id.rb42:
                        control(0, 1, 1, 0);
                        break;
                    case R.id.rb43:
                        control(0, 1, 0, 0);
                        break;
                }
            }
        });
    }

    /**
     * 具体控制逻辑
     *
     * @param key1   两位中的第一个所在整体数组位置（非二进制位置）
     * @param key2   两位中的第二个所在整体数组位置（非二进制位置）
     * @param value1 两位中的第一个的状态
     * @param value2 两位中的第二个的状态
     */
    private void control(final int key1, final int key2, final int value1, final int value2) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                read();
                List<Integer> list = new ArrayList<>();//此集合中第0个元素代表的也是二进制的最高位
                for (int i = 0; i < 8; i++) {
                    if (i == key1) {
                        list.add(value1);
                    } else if (i == key2) {
                        list.add(value2);
                    } else {
                        list.add(statusList.get(7 - i));
                    }
                }
                String ss = "";
                for (int i = 0; i < 8; i++) {
                    ss += list.get(i);
                }
                int res = Integer.parseInt(ss, 2);
                Log.e(TAG, ss + "=====run: ===" + res);
                String str11 = client.setAll(res + "");
                Log.e(TAG, "onCheckedChanged: " + str11);
            }
        });
    }
}
