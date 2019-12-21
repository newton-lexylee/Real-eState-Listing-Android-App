package eu.fiskur.simpleviewpager;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

import tmedia.ir.melkeurmia.R;
import tmedia.ir.melkeurmia.otto.AppEvents;
import tmedia.ir.melkeurmia.otto.GlobalBus;
import tmedia.ir.melkeurmia.tools.CONST;


public class SimpleViewPagerAdapter extends PagerAdapter {

    private static final int MODE_URLS = 0;
    private static final int MODE_DRAWABLES = 1;
    private static final int MODE_IDS = 2;
    private int mode = MODE_URLS;

    private Context context;

    private List<String> imageUrls;
    private Drawable[] drawables;
    private int[] resourceIds = null;

    private ImageURLLoader imageURLLoader;
    private ImageResourceLoader imageResourceLoader;
    private ImageView.ScaleType scaleType = null;

    private int current_postion = 0;

    public SimpleViewPagerAdapter(Context context, List<String> imageUrls, ImageURLLoader imageURLLoader, ImageView.ScaleType scaleType) {
        mode = MODE_URLS;
        this.context = context;
        this.imageUrls = imageUrls;
        this.imageURLLoader = imageURLLoader;
        this.scaleType = scaleType;

        if (scaleType == null) {
            this.scaleType = ImageView.ScaleType.CENTER_CROP;
        }
    }

    public SimpleViewPagerAdapter(Context context, Drawable[] drawables, ImageView.ScaleType scaleType) {
        mode = MODE_DRAWABLES;
        this.context = context;
        this.drawables = drawables;
        this.scaleType = scaleType;

        if (scaleType == null) {
            this.scaleType = ImageView.ScaleType.CENTER_CROP;
        }
        setTotalFactor();
    }

    public SimpleViewPagerAdapter(Context context, int[] resourceIds, ImageResourceLoader imageResourceLoader, ImageView.ScaleType scaleType) {
        mode = MODE_IDS;
        this.context = context;
        this.resourceIds = resourceIds;
        this.imageResourceLoader = imageResourceLoader;
        this.scaleType = scaleType;

        if (scaleType == null) {
            this.scaleType = ImageView.ScaleType.CENTER_CROP;
        }
        setTotalFactor();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);
        current_postion = position;
        if (mode == MODE_URLS) {
            imageView.setImageResource(R.drawable.dummy_placeholder);
        }

        imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.WRAP_CONTENT));

        if (scaleType != null) {
            imageView.setScaleType(scaleType);
        } else {
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTotalFactor();
            }
        });

        imageView.setPadding(0, 0, 0, 0);

        switch (mode) {
            case MODE_URLS:
                container.addView(imageView);
                imageURLLoader.loadImage(imageView, imageUrls.get(position));
                break;
            case MODE_DRAWABLES:
                imageView.setImageDrawable(drawables[position]);
                container.addView(imageView);
                break;
            case MODE_IDS:
                container.addView(imageView);
                imageResourceLoader.loadImageResource(imageView, resourceIds[position]);
                break;
            default:
                //unused
        }

        return imageView;
    }


    private void setTotalFactor() {

        AppEvents.PagerAdapterEvent stopSwipeRefresh = new AppEvents.PagerAdapterEvent(current_postion);
        GlobalBus.getBus().post(stopSwipeRefresh);
    }

    @Override
    public int getCount() {
        switch (mode) {
            case MODE_URLS:
                return imageUrls.size();
            case MODE_DRAWABLES:
                return drawables.length;
            case MODE_IDS:
                return resourceIds.length;
            default:
                return 0;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ImageView) object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (object instanceof ImageView) {
            container.removeView((ImageView) object);
        }
    }
}