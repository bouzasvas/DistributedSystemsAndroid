package projb.dissystems.aueb.vassilis.nycheckins;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import java.util.Locale;

public class info extends AppCompatActivity {

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

        setContentView(R.layout.activity_info);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
