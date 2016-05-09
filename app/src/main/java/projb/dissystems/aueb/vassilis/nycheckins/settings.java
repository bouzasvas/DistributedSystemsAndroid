package projb.dissystems.aueb.vassilis.nycheckins;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import java.util.Locale;

public class settings extends AppCompatActivity {

    Spinner languages;
    String prefLang;
    String[] langs = {"English", "Greek"};

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

        setContentView(R.layout.activity_settings);

        languages = (Spinner) findViewById(R.id.language_list);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        languages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                prefLang = languages.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        languages.setAdapter(adapter);
    }

    public void setLocale(String language) {
        String lang = "";
        Locale myLocale = null;

        if (language.equals("English") || language.equals("Αγγλικά")) {
            lang = "en";
            Toast.makeText(this, "Language changed to English!", Toast.LENGTH_SHORT).show();
        } else if (language.equals("Greek") || language.equals("Ελληνικά")) {
            lang = "el_GR";
            Toast.makeText(this, "Έγινε αλλαγή της γλώσσας στα Ελληνικά!", Toast.LENGTH_SHORT).show();
        }

        myLocale = new Locale(lang.toLowerCase());
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

        SharedPreferences languagepref = getSharedPreferences("language", MODE_PRIVATE);
        SharedPreferences.Editor editor = languagepref.edit();
        editor.putString("languageToLoad", lang);
        editor.commit();
    }

    public void saveButtonClick(View view) {
        setLocale(prefLang);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.alert_title);
        builder.setMessage(R.string.alert_message);
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finishAffinity();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
