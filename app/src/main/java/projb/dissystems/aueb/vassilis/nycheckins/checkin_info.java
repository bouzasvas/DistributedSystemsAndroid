package projb.dissystems.aueb.vassilis.nycheckins;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class checkin_info extends AppCompatActivity {

    ArrayList<String> photosURL;
    TextView poi_name, poi_count;

    GridView photos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin_info);

        poi_name = (TextView) findViewById(R.id.poi_name);
        poi_count = (TextView) findViewById(R.id.poi_count);
        photos = (GridView) findViewById(R.id.photos);

        Intent fromMap = getIntent();
        poi_name.setText(fromMap.getStringExtra("poi_name"));
        poi_count.setText(fromMap.getStringExtra("poi_count"));
        photosURL = fromMap.getStringArrayListExtra("photos");

        photos.setAdapter(new ImageAdapter(this, photosURL));

        photos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView currentPhoto = (ImageView) photos.getAdapter().getItem(position);
                currentPhoto.buildDrawingCache();
                Bitmap bitmap = currentPhoto.getDrawingCache();

                Intent showImage = new Intent(checkin_info.this, view_image.class);
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
                showImage.putExtra("photo", bs.toByteArray());
                startActivity(showImage);

            }
        });
    }
}
