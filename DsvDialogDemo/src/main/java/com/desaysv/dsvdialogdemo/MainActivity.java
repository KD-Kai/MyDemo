package com.desaysv.dsvdialogdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.desaysv.dsvdialogdemo.dialog.DialogButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DsvDialog dsvDialog = new DsvDialog(this);
        dsvDialog.setDialogStyle(DsvDialog.STYLE_DIALOG_SIZE_S
                | DsvDialog.STYLE_DIALOG_BUTTON_DOUBLE);
        dsvDialog.setSystemDialog(true);
        dsvDialog.show();
    }
}