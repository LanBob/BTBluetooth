package com.lusr.app;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class SearchServer extends Thread {

    private boolean isRunning = true;

    private BluetoothAdapter bluetoothAdapter = null;
    private InputStream mmInStream = null;
    private Handler handler;
    private Context context;
    private BluetoothDevice mmDevice = null;
    private BluetoothSocket mmSocket = null;
    private String fileName = "";

    private UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private String myName = "CCHX005";

    public SearchServer(Context context, BluetoothAdapter bluetoothAdapter, BluetoothDevice device, Handler handler, String fileName) {
        this.context = context;
        this.mmDevice = device;
        this.bluetoothAdapter = bluetoothAdapter;
        this.handler = handler;
        this.fileName = fileName;

        BluetoothSocket tmp = null;
        try {
            tmp = device.createRfcommSocketToServiceRecord(uuid);// Get a BluetoothSocket for a connection with the given BluetoothDevice
        } catch (IOException e) {
        }
        mmSocket = tmp;
    }

    @Override
    public void run() {
        //当连接成功，取消蓝牙适配器搜索蓝牙设备的操作，因为搜索操作非常耗时

//        获取Socket
        try {
//            //                传给Handler（用于主进程展示）
//            Message msg = handler.obtainMessage();
//            Bundle bundle = new Bundle();
//            bundle.putByteArray("data", "获得BluetoothServerSocket，等待获取Socket...".getBytes());
//            msg.setData(bundle);
//            handler.sendMessage(msg);
            mmSocket.connect();
//            Message msg1 = handler.obtainMessage();
//            Bundle bundle1 = new Bundle();
//            bundle1.putByteArray("data", "\n得到Socket,正在获取数据".getBytes());
//            msg1.setData(bundle);
//            handler.sendMessage(msg1);
        } catch (IOException e) {
            if (mmSocket.isConnected()) {
                try {
                    mmSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            e.printStackTrace();
        }

        while (isRunning) {
            InputStream tmp = null;

            try {
                Thread.sleep(1800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 处理客户端socket
            if (mmSocket != null) {
                try {
                    tmp = mmSocket.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            mmInStream = tmp;
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//================================================================获取数据
            byte[] tempInputBuffer = new byte[1024];
            int len = 0;
            String data = null;
            try {
                len = mmInStream.read(tempInputBuffer);//返回接收的长度
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (len > 0) {
//                传给Handler（用于主进程展示）
                Message msg = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putByteArray("data", tempInputBuffer);

                msg.setData(bundle);
                handler.sendMessage(msg);

//                保存一份txt
                String str;
                try {
                    str = FileStringHelper.getSubUtil(new String(tempInputBuffer, "utf-8"));
                    LocationUtil locationUtil = new LocationUtil(context);
                    str += locationUtil.getJingDu() + "  " + locationUtil.getWeidu();
                    FileStringHelper.save(this.fileName, str.getBytes());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
//================================================================获取数据
        }

//        当调用cancle方法的时候，可以关闭连接
        try {
            if (mmSocket.isConnected())
                mmSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cancle() {
        isRunning = false;
        if (mmSocket.isConnected()) {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
