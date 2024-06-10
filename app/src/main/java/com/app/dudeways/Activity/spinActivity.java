package com.app.dudeways.Activity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.dudeways.R;

import java.util.Random;

public class spinActivity extends AppCompatActivity {

    private static final String [] sectors = {"1","2","3","4","5","6","7","8","9","10"};
    private static final int [] sectorDegrees = new int[sectors.length];
    private static final Random random = new Random();
    private int degree = 0;
    private boolean isSpinning = false;

    private ImageView wheel1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spin);

        final Button spinBtn = findViewById(R.id.spinBtn);
        wheel1 = findViewById(R.id.wheel1);

        getDegreeForSectors();

        spinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSpinning){

                    spin();
                    isSpinning = true;

                }

            }
        });


    }

    private void spin() {
        degree = random.nextInt(sectors.length -1 );

        RotateAnimation rotateAnimation = new RotateAnimation(0,(360 * sectors.length + sectorDegrees[degree]),
                RotateAnimation.RELATIVE_TO_SELF,0.5f,RotateAnimation.RELATIVE_TO_SELF,0.5f);

        rotateAnimation.setDuration(3600);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setInterpolator(new DecelerateInterpolator());
        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isSpinning = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Toast.makeText(spinActivity.this,"You've got"+sectors[sectors.length - (degree + 1)],Toast.LENGTH_SHORT).show();
                isSpinning = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        wheel1.startAnimation(rotateAnimation);

    }

    private void getDegreeForSectors() {
        int sectorDegree = 360/sectors.length;
        for(int i=0; i < sectors.length; i++) {
            sectorDegrees[i] = (i + 1) * sectorDegree;

        }
    }


}