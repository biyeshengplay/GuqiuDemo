package com.guqiu.readexcel;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

/**
 * Created by alvinlee on 2017/1/20.
 */

public class CustomScanActivity extends Activity implements DecoratedBarcodeView.TorchListener{ // 实现相关接口
    // 添加一个按钮用来控制闪光灯，同时添加两个按钮表示其他功能，先用Toast表示

    TextView swichLight;
    DecoratedBarcodeView mDBV;

    private CaptureManager captureManager;
    private boolean isLightOn = false;

    @Override
    protected void onPause() {
        super.onPause();
        captureManager.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        captureManager.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        captureManager.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        captureManager.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mDBV.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        initView();

        mDBV.setTorchListener(this);

        // 如果没有闪光灯功能，就去掉相关按钮
        if(!hasFlash()) {
            swichLight.setVisibility(View.GONE);
        }

        //重要代码，初始化捕获
        captureManager = new CaptureManager(this,mDBV);
        captureManager.initializeFromIntent(getIntent(),savedInstanceState);
        captureManager.decode();
    }

    private void initView() {
        swichLight = (TextView) findViewById(R.id.btn_switch);
        mDBV = (DecoratedBarcodeView) findViewById(R.id.dbv_custom);

        swichLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLightOn){
                    mDBV.setTorchOff();
                }else{
                    mDBV.setTorchOn();
                }
            }
        });
    }

    // torch 手电筒
    @Override
    public void onTorchOn() {
        Toast.makeText(this,"torch on",Toast.LENGTH_SHORT).show();
        isLightOn = true;
    }

    @Override
    public void onTorchOff() {
        Toast.makeText(this,"torch off", Toast.LENGTH_SHORT).show();
        isLightOn = false;
    }

    // 判断是否有闪光灯功能
    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

}
