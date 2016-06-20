package projb.dissystems.aueb.vassilis.nycheckins;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class view_image extends AppCompatActivity {

    ImageView photo;
    Button saveImageButton;
    String poi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        photo = (ImageView) findViewById(R.id.photo);
        saveImageButton = (Button) findViewById(R.id.savePic);

        poi = getIntent().getStringExtra("poi");
        String photoURL = getIntent().getStringExtra("photoURL");
        new DownloadImageTask(photo).execute(photoURL);
    }

    public void saveImage(View view) {
        Toast.makeText(view_image.this, "Saved Successfully", Toast.LENGTH_LONG).show();
    }
}