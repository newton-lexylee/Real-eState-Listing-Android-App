package tmedia.ir.melkeurmia.itemView;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.veinhorn.scrollgalleryview.MediaInfo;
import com.veinhorn.scrollgalleryview.ScrollGalleryView;

import java.util.ArrayList;
import java.util.List;

import tmedia.ir.melkeurmia.R;
import tmedia.ir.melkeurmia.tools.PicassoImageLoader;

public class FullScreenGallery extends AppCompatActivity {


    private ScrollGalleryView scrollGalleryView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_gallery);

        Bundle bundle = getIntent().getExtras();
        ArrayList<String> img = bundle.getStringArrayList("imgs");
        int pos = bundle.getInt("pos");



        List<MediaInfo> infos = new ArrayList<>(img.size());
        for (int i=0;i<img.size(); i++){
            infos.add(MediaInfo.mediaLoader(new PicassoImageLoader(img.get(i))));
        }

        scrollGalleryView = (ScrollGalleryView) findViewById(R.id.scroll_gallery_view);
        scrollGalleryView
                .setThumbnailSize(100)
                .setZoom(false)
                .setFragmentManager(getSupportFragmentManager())
                .addMedia(infos)
                .hideThumbnails();

        scrollGalleryView.setCurrentItem(pos - 1);


    }
}
