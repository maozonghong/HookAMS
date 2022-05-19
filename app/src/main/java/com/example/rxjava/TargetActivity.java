package com.example.rxjava;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by maozonghong
 * <p>
 * on 5/18/22
 **/
public class TargetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target);
    }

    public void onClick(View view) {
        startActivity(new Intent(this,TargetActivity2.class));
    }
}
