package com.gico.gicobbs;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BBSService extends Service {
    private final Socket mPttSocket = new Socket();
    private final IBinder mBinder = new BBSBinder();
    BufferedReader mSocketIn = null;
    DataOutputStream mDataOutputStream = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                SocketAddress sockaddr = new InetSocketAddress("ptt.cc", 443);
                try {
                    mPttSocket.connect(sockaddr);
                    mSocketIn = new BufferedReader(new InputStreamReader(mPttSocket.getInputStream(), "Big5"));
                    mDataOutputStream = new DataOutputStream(mPttSocket.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public boolean isBBSConnect() {
        return mPttSocket != null && mPttSocket.isConnected();
    }

    public void readbbs() {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Pattern pattern = Pattern.compile("\u001B(.*?)m");
                    StringBuilder sb = new StringBuilder();
                    while (true) {
                        char socketInChar = (char) mSocketIn.read();
                        sb.append(socketInChar);
                        Log.d("testChar", "\t" + socketInChar);
                        Matcher matcher = pattern.matcher(sb.toString());
                        if (sb.toString().contains("\n")) {
                            String newString = matcher.replaceAll("");
                            Log.d("testString", newString);
                            sb = new StringBuilder();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void loginBBS(String account, String password) {
        try {
            mDataOutputStream.writeUTF(account + "\r\n");
            mDataOutputStream.flush();
            mDataOutputStream.writeUTF(password + "\r\n");
            mDataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void talkBBS(String talk) {
        try {
            mDataOutputStream.writeUTF(talk);
            mDataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class BBSBinder extends Binder {
        BBSService getService() {
            return BBSService.this;
        }
    }
}
