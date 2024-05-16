package gmw.app.paring;

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
            R.drawable.splash_1,
            R.drawable.splash_2,
            R.drawable.splash_3
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
        return headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.slider_layout, container, false);

        ImageView slideTitleImage = view.findViewById(R.id.ivSplashImage);
        TextView slideHeading = view.findViewById(R.id.tvSplashText);


        Glide.with(context).load(images[position]).placeholder(R.drawable.splash_1).into(slideTitleImage);

//
//        // Using Glide to load images efficiently
//        Glide.with(context)
//                .load(images[position])
//                .apply(new RequestOptions()
//                        .placeholder(R.drawable.logo) // Placeholder image while loading
//                      //  .error(R.drawable.error) // Error image if loading fails
//                        .diskCacheStrategy(DiskCacheStrategy.ALL) // Caching
//                        .override(800, 600) // Resizing the image
//                )
//                .into(slideTitleImage);

        slideHeading.setText(headings[position]);


        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
