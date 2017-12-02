package com.example.tianbin.control;

import android.text.TextUtils;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by TianBin on 2017/10/22 12:36.
 * Description :tcp client模式连接控制盒（盒子作为tcp server）
 */

public class Client {
    private static final String TAG = "Client";
    private Socket socket = null;
    private int packNum;//不同命令该值不能相同

    private Client() {
    }

    ;

    public static Client getInstance() {
        return SingletonHolder.client;
    }

    private static class SingletonHolder {
        private static final Client client = new Client();
    }

    public boolean isConnected() {
        if (socket != null && socket.isConnected()) {
            return true;
        }
        return false;
    }

    public Socket connect(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            return socket;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "connect: error==============");
        }
        return null;
    }

    /**
     * 控制多路——8路(传0000 0000代表的十进制)
     *
     * @param command
     * @return
     */
    public String setAll(String command) {
        try {
            // 输出
            OutputStream out = socket.getOutputStream();
            // 输入
            InputStream input = socket.getInputStream();

            byte b[] = new byte[64];// 接受的指令的长度
            int len = 0;

            String string = "RELAY-SET_ALL-" + (++packNum) + "," + command;
            out.write(string.getBytes());// 输入指令在这里
            Thread.sleep(100);
            out.flush();
            len = input.read(b);
            // 打印继电器的反馈消息
            String s = new String(b, 0, len);
            Log.e(TAG, "setAll: RELAY-SET-1=" + s);
            return s;
        } catch (Exception e) {
            Log.e(TAG, "setAll: eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee======"+e.getMessage() );
            e.printStackTrace();
        }
        return "setAll failure";
    }

    /**
     * 控制单个
     *
     * @param id
     * @param command
     * @return
     */
    public String set(String id, String command) {
        try {
            // 输出
            OutputStream out = socket.getOutputStream();
            // 输入
            InputStream input = socket.getInputStream();

            byte b[] = new byte[64];// 接受的指令的长度
            int len = 0;

            String string = "RELAY-SET-" + (++packNum) + "," + id + "," + command;
            out.write(string.getBytes());// 输入指令在这里
            Thread.sleep(100);
            out.flush();
            len = input.read(b);
            // 打印继电器的反馈消息
            String s = new String(b, 0, len);
            Log.e(TAG, "set:RELAY-SET-1=" + s);
            return s;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "failure";

    }

    /**
     * 读取单个
     *
     * @param id
     * @return
     */
    public String read(String id) {
        try {
            // 输出
            OutputStream out = socket.getOutputStream();
            // 输入
            InputStream input = socket.getInputStream();

            byte b[] = new byte[64];// 接受的指令的长度
            int len = 0;
            String string = "RELAY-READ-" + (++packNum)+","+id;
            out.write(string.getBytes());
            Thread.sleep(100);
            out.flush();
            len = input.read(b);
            // 打印继电器的反馈消息
            String string2 = new String(b, 0, len);
            Log.e(TAG, "read: RELAY-READ-1=" + string2);
            return string2;
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    /**
     * 读取所有
     *
     * @return
     */
    public String[] read() {
        try {
            // 输出
            OutputStream out = socket.getOutputStream();
            // 输入
            InputStream input = socket.getInputStream();

            byte b[] = new byte[64];// 接受的指令的长度
            int len = 0;
            String string = "RELAY-STATE-" + (++packNum);
            out.write(string.getBytes());
            Thread.sleep(100);
            out.flush();
            len = input.read(b);
            // 打印继电器的反馈消息
            String string2 = new String(b, 0, len);
            Log.e(TAG, "read: RELAY-STATE-1=" + string2);

            String[] split = string2.split(",");
            String num = split[1];
            //转为二进制表示，8位代表8路的状态
            String stringRes = Integer.toBinaryString(Integer.valueOf(num));
            String[] arr = new String[stringRes.length()];
            for (int i = 0; i < stringRes.length(); i++) {
                arr[i] = String.valueOf(stringRes.charAt(stringRes.length() - (i + 1)));
            }
            return arr;//arr从0-8代表二进制由低到高的八位
//            return num;//直接返回十进制也可以
        } catch (Exception e) {
            e.printStackTrace();
            return new String[]{"error"};
        }
    }

    /**
     * 获取设备id
     *
     * @return
     */
    public String getID() {
        String id = "";
        try {
            // 输出
            OutputStream out = socket.getOutputStream();
            // 输入
            InputStream input = socket.getInputStream();

            byte b[] = new byte[64];// 接受的指令的长度
            int len = 0;

            out.write("RELAY-HOST-NOW".getBytes());
            Thread.sleep(100);
            out.flush();
            len = input.read(b);
            // 打印继电器的反馈消息
            id = new String(b, 0, len);
            Log.e(TAG, "getID: RELAY-HOST-NOW=" + id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(id) && id.startsWith("HOST-CHKLIC-")) {
            id = id.split("-")[2];
        }
        return id;

    }

    /**
     * 每次设备上电后，必须先扫描
     *
     * @return
     */
    public String scan() {
        try {
            // 输出
            OutputStream out = socket.getOutputStream();
            // 输入
            InputStream input = socket.getInputStream();

            out.write("RELAY-SCAN_DEVICE-NOW".getBytes());
            Thread.sleep(100);
            out.flush();
            byte b[] = new byte[64];// 接受的指令的长度
            int len = 0;
            len = input.read(b);
            // 打印继电器的反馈消息
            String str = new String(b, 0, len);
            Log.e(TAG, "getID:RELAY-SCAN_DEVICE-NOW= " + str);
            return str;
        } catch (Exception e) {
            e.printStackTrace();
            return "failure";
        }

    }

    /**
     * 查询输入状态
     * @return
     */
    public String query() {
        try {
            // 输出
            OutputStream out = socket.getOutputStream();
            // 输入
            InputStream input = socket.getInputStream();

            out.write(("RELAY-GET_INPUT-"+ (++packNum)).getBytes());
            Thread.sleep(100);
            out.flush();
            byte b[] = new byte[64];// 接受的指令的长度
            int len = 0;
            len = input.read(b);
            // 打印继电器的反馈消息
            String str = new String(b, 0, len);
            Log.e(TAG, "RELAY-GET_INPUT-= " + str);
            return str;
        } catch (Exception e) {
            e.printStackTrace();
            return "failure";
        }

    }
}
