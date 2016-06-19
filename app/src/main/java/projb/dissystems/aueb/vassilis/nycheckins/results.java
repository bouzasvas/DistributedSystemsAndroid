package projb.dissystems.aueb.vassilis.nycheckins;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Map;

public class results extends AppCompatActivity {

    ListView results;
    Intent fromMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        results = (ListView) findViewById(R.id.results);
        fromMain = getIntent();

        ArrayList<String> list = fromMain.getStringArrayListExtra("list");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, list);
        results.setAdapter(adapter);

        results.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(results.this);
//                alertBuilder.setTitle("Selected Item");
//                alertBuilder.setMessage("You have selected " + results.getAdapter().getItem(position));
//                AlertDialog dialog = alertBuilder.create();
//                dialog.show();
                String poi_name = results.getAdapter().getItem(position).toString();
                String poi = null;

                for (Object key : MapsActivity.finalResult.keySet()) {
                    if (MapsActivity.finalResult.get(key).get(1).toString().equals(poi_name)) {
                        poi = MapsActivity.finalResult.get(key).get(0).toString();
                        break;
                    }
                }

                fromMain.putExtra("checkin_POI", poi);
                setResult(Activity.RESULT_OK, fromMain);
                finish();
            }
        });
    }
}
