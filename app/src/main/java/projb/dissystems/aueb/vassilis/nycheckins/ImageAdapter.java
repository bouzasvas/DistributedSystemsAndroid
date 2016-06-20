package projb.dissystems.aueb.vassilis.nycheckins;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

import com.squareup.picasso.Picasso;

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

    public String getItem(int position) {
        return photosURL.get(position);
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
        } else {
            imageView = (ImageView) convertView;
        }

        if (!photosURL.get(position).equals("Not exists")) {
            imageView.setLayoutParams(new GridView.LayoutParams((size.x) / 2, (size.y) / 3));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Picasso.with(mContext)
                    .load(photosURL.get(position))
                    .placeholder(R.drawable.loader)
                    .fit()
                    .centerCrop().into(imageView);
        } else {

            imageView.setImageResource(R.drawable.no_image_icon);
        }

        images.add(imageView);
        return imageView;
    }
}
