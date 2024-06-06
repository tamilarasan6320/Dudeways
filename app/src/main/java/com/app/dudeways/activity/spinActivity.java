package com.app.dudeways.activity;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.app.dudeways.R;

import java.util.Random;

public class spinActivity extends AppCompatActivity {

    private static final String [] sectors = {"1","2","3","4","5","6","7","8","9"};
    private static final int [] sectorDegrees = new int[sectors.length];
    private static final Random random = new Random();
    private int degree = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spin);

        final ImageView spinBtn = findViewById(R.id.spinBtn);
        final ImageView wheel1 = findViewById(R.id.wheel1);


    }
}