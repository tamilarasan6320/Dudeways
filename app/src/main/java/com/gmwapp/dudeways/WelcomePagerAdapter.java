package com.gmwapp.dudeways;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

public class WelcomePagerAdapter extends PagerAdapter {

    private Context context;

    private int[] images = {
            R.drawable.welcome1,
            R.drawable.welcome2,
            R.drawable.welcome3
    };

    private int[] headings = {
            R.string.splash1,
            R.string.splash2,
            R.string.splash3
    };

    public WelcomePagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.length + 1; // Add one more count for the dummy page
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        if (position == images.length) {
            // This is the dummy page
            View view = layoutInflater.inflate(R.layout.slider_dummy_layout, container, false);
            container.addView(view);
            return view;
        } else {
            View view = layoutInflater.inflate(R.layout.slider_layout, container, false);

            ImageView slideTitleImage = view.findViewById(R.id.ivSplashImage);
            TextView slideHeading = view.findViewById(R.id.tvSplashText);

            Glide.with(context).load(images[position]).placeholder(R.drawable.welcome1).into(slideTitleImage);
            slideHeading.setText(headings[position]);

            container.addView(view);
            return view;
        }
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
