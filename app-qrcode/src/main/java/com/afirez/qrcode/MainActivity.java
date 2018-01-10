package com.afirez.qrcode;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.afirez.zxing.QrCodeUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView ivShowQrcode = (ImageView) findViewById(R.id.iv_show_qrcode);
        ivShowQrcode.setImageBitmap(QrCodeUtils.encodeQrCode(
                "你这个方法听起来不错",
                640,
                640
        ));
    }
}
