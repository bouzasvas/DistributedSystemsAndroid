package projb.dissystems.aueb.vassilis.nycheckins;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button mainButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //change language on activity startup
//        SharedPreferences languagepref = getSharedPreferences("language",MODE_PRIVATE);
//        String language = languagepref.getString("languageToLoad", "el_GR");
//
//
//        Locale myLocale = new Locale(language.toLowerCase());
//        Resources res = getResources();
//        DisplayMetrics dm = res.getDisplayMetrics();
//        Configuration conf = res.getConfiguration();
//        conf.locale = myLocale;
//        res.updateConfiguration(conf, dm);
        //before setContentView!!

        setContentView(R.layout.activity_main);

        mainButton = (Button) findViewById(R.id.mainButton);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.info_menu):
                Intent info_activity = new Intent(this, info.class);
                startActivity(info_activity);
                break;
            case (R.id.settings_menu):
                Intent settings_activity = new Intent(this, settings.class);
                startActivity(settings_activity);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void mainButtonClick(View view) {
        Intent toMap = new Intent(this, MapsActivity.class);
        startActivity(toMap);
    }
}
