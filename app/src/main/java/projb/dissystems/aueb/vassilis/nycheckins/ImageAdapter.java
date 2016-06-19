package projb.dissystems.aueb.vassilis.nycheckins;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vassilis on 17/6/2016.
 */
public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    WindowManager wm;
    Display display;
    Point size = new Point();

    private ArrayList<String> photosURL;
    List<ImageView> images = new ArrayList<ImageView>();

    public ImageAdapter(Context c, ArrayList<String> photosURL) {
        mContext = c;
        this.photosURL = photosURL;
        wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        display = wm.getDefaultDisplay();
        display.getSize(size);
    }


    public int getCount() {
        return photosURL.size();
    }

    public ImageView getItem(int position) {
        return images.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams((size.x) / 2, (size.y) / 3));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

        //new DownloadImageTask(imageView).execute(photosURL.get(position));
        //imageView.setImageResource(photosTest[position]);
        loadBitmap(imageView, photosURL.get(position));
        images.add(imageView);
        return imageView;
    }

    public void loadBitmap(ImageView imageView, String url) {
        final DownloadImageTask task = new DownloadImageTask(imageView);
        final AsyncDrawable asyncDrawable =
                new AsyncDrawable(task);
        imageView.setImageDrawable(asyncDrawable);
        task.execute(url);
    }
}