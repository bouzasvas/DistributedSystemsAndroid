package projb.dissystems.aueb.vassilis.nycheckins;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class view_image extends AppCompatActivity {

    ImageView photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        photo = (ImageView) findViewById(R.id.photo);

        Bitmap b = BitmapFactory.decodeByteArray(
                getIntent().getByteArrayExtra("photo"), 0, getIntent().getByteArrayExtra("photo").length);

        photo.setImageBitmap(b);
    }
}