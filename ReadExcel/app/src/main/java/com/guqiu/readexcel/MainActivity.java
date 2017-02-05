package com.guqiu.readexcel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.meituan.android.walle.WalleChannelReader;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;

import jxl.Sheet;
import jxl.Workbook;

public class MainActivity extends Activity {

    TextView mInfo = null;
    EditText mCode = null;
    TextView mResult = null;
    TextView mAll = null;
    TextView mScan = null;
    TextView mChanel = null;

    HashMap<String, String> map = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        readExcel();
    }

    private void initView() {
        mInfo = (TextView) findViewById(R.id.txt_show);
        mAll = (TextView) findViewById(R.id.txt_all_content);
        mScan = (TextView) findViewById(R.id.txt_scan);
        mCode = (EditText) findViewById(R.id.txt_code);
        mResult = (TextView) findViewById(R.id.txt_result);
        mChanel = (TextView) findViewById(R.id.txt_chanel);
        mChanel.setText(getNewChannelId(getApplicationContext()));
        mInfo.setMovementMethod(ScrollingMovementMethod.getInstance());
        mAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AllContentActivity.class);
                startActivity(intent);
            }
        });
        mScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customScan();
            }
        });
        mCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                setReslut();
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void readExcel() {
        try {
            /**
             * 后续考虑问题,比如Excel里面的图片以及其他数据类型的读取
             **/
            InputStream is = new FileInputStream("mnt/sdcard/price.xls");
            //Workbook book = Workbook.getWorkbook(new File("mnt/sdcard/test.xls"));
            Workbook book = Workbook.getWorkbook(is);

            int num = book.getNumberOfSheets();
            mInfo.setText("num of sheets : " + num + "\n");
            // 获得第一个工作表对象
            Sheet sheet = book.getSheet(0);
            int Rows = sheet.getRows();
            int Cols = sheet.getColumns();
            mInfo.append("name of sheet : " + sheet.getName() + "\n");
            mInfo.append("total rows : " + Rows + "\n");
            mInfo.append("total cols : " + Cols);
            for (int i = 0; i < Rows; i++) {
                map.put(sheet.getCell(0, i).getContents().trim(), sheet.getCell(1, i).getContents());
            }
            book.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void customScan() {
        new IntentIntegrator(this)
                .setOrientationLocked(false)
                .setCaptureActivity(CustomScanActivity.class) // 设置自定义的activity是CustomActivity
                .initiateScan(); // 初始化扫描
    }

    @Override
// 通过 onActivityResult的方法获取 扫描回来的 值
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(this, "商品ID为空", Toast.LENGTH_SHORT).show();
                mResult.setText("商品ID为：\n查询价格为：");
            } else {
                // result 为 获取到的字符串
                String result = intentResult.getContents();
                Toast.makeText(this, "扫描成功", Toast.LENGTH_SHORT).show();
                if (map.containsKey(result)) {
                    String price = map.get(result);
                    mResult.setText("商品ID为：" + result + "\n查询价格为：" + price);
                } else {
                    Toast.makeText(this, "没有找到改商品的价格", Toast.LENGTH_SHORT).show();
                    mResult.setText("商品ID为：\n查询价格为：");
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
            mResult.setText("商品ID为：\n查询价格为：");
        }
    }

    private void setReslut() {
        if (mCode.getText().toString().endsWith("\n")) {
            String result = mCode.getText().toString().trim();
            if (map.containsKey(result)) {
                String price = map.get(result);
                mResult.setText("商品ID为：" + result + "\n查询价格为：" + price);
            } else {
                Toast.makeText(this, "没有找到改商品的价格", Toast.LENGTH_SHORT).show();
                mResult.setText("商品ID为：\n查询价格为：");
            }
            mCode.setText("");
        }
    }

    public static String getNewChannelId(Context context) {
        String channel = WalleChannelReader.getChannel(context);
        return !TextUtils.isEmpty(channel) ? channel : "ORIGINAL";
    }

}
