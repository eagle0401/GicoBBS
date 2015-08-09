package com.gico.gicobbs;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements View.OnClickListener {
    private BBSService mBBSService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        startService(new Intent(LoginActivity.this, BBSService.class));
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.talk_button).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(LoginActivity.this, BBSService.class), connc, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connc);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService(new Intent(LoginActivity.this, BBSService.class));
    }


    private ServiceConnection connc = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBBSService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBBSService = ((BBSService.BBSBinder) service).getService();
            mBBSService.readbbs();
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                mBBSService.loginBBS();
                break;
            case R.id.talk_button:
                mBBSService.talkBBS(((EditText) findViewById(R.id.talk)).getText().toString());
                break;
        }
    }
}

