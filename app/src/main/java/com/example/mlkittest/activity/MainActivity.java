package com.example.mlkittest.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mlkittest.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.detectFacesBtn)
    Button detectFacesBtn;
    @BindView(R.id.recognizeTextBtn)
    Button recognizeTextBtn;
    @BindView(R.id.scanBarcodeBtn)
    Button scanBarcodeBtn;

    private Context _context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _context = this;
        ButterKnife.bind(this);


    }


    @OnClick({R.id.detectFacesBtn, R.id.recognizeTextBtn, R.id.scanBarcodeBtn})
    public void onViewClicked(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.detectFacesBtn:
                intent = new Intent(_context, DetectFaceActivity.class);
                break;
            case R.id.recognizeTextBtn:
                intent = new Intent(_context, RecognizeTextActivity.class);
                break;
            case R.id.scanBarcodeBtn:
                intent = new Intent(_context, ScanBarcodeActivity.class);
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }
}
