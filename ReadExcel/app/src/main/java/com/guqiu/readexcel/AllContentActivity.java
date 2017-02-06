package com.guqiu.readexcel;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.InputStream;

import jxl.Sheet;
import jxl.Workbook;

/**
 * Created by alvinlee on 2017/1/20.
 */

public class AllContentActivity extends Activity {

    private TextView mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_content);
        initView();
        readExcel();
    }

    private void initView() {
        mContent = (TextView) findViewById(R.id.txt_id);

        mContent.setMovementMethod(ScrollingMovementMethod.getInstance());
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
            // 获得第一个工作表对象
            Sheet sheet = book.getSheet(0);
            int Rows = sheet.getRows();
            int Cols = sheet.getColumns();

            for (int i = 0; i < Rows; i++) {
                mContent.append(sheet.getCell(0, i).getContents() + " : " + sheet.getCell(1, i).getContents() + "\n");
            }
            book.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
