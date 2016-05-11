package projb.dissystems.aueb.vassilis.nycheckins;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class results extends AppCompatActivity {

    ListView results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        results = (ListView) findViewById(R.id.results);
        Intent fromMain = getIntent();

        ArrayList<String> list = fromMain.getStringArrayListExtra("list");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, list);
        results.setAdapter(adapter);
    }
}
