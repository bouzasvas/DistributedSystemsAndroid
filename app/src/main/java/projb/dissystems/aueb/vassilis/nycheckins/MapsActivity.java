package projb.dissystems.aueb.vassilis.nycheckins;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    List<LatLng> coordinates;

    ArrayList<String> photosList;
    Marker poiOnMap = null;
    String poi = null;

    //for creating the circle
    int userTouches = 0; //when user long press 2 points on map then create Circle
    Polygon interestedArea = null;
    Marker center;
    Marker mark1;
    Marker mark2;
    Marker mark3;
    Marker mark4;
    double distX, distY;

    //Initialization of Top-K, fromDate, toDate
    TextView topK_text, fromDate_text, toDate_text;

    //For TopK field
    Spinner topK_selector;
    int firstTime = 0;
    String[] topK_array = {"5", "10", "15", "20", "40", "80", "100"};

    // PORTS
    List<String> addr_ports = null;

    //Data layouts
    LinearLayout topK_layout, fromDate_layout, toDate_layout, initLayout;
    Button done_button;
    Button showHideLayoutButton;
    SupportMapFragment mapFragment;

    // data to Mappers
    int topK;
    double minX, maxX, minY, maxY;
    String fromDate, toDate;
    private String[] dates = new String[2];

    // final result from reducer
    public static Map<Object, List> finalResult = null;
    Handler handler;
    onMapReceived listener = new onMapReceived() {
        @Override
        public void onMapReceived() {
            showAlertDialog("results", R.string.reduce_done, R.string.reducer_done_message, R.drawable.dialog_icon_info);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        topK_text = (TextView) findViewById(R.id.topK_text);
        fromDate_text = (TextView) findViewById(R.id.from_date);
        toDate_text = (TextView) findViewById(R.id.to_date);
        initLayout = (LinearLayout) findViewById(R.id.initValues_layout);


        //SPINNER
        topK_selector = (Spinner) findViewById(R.id.topK_selector);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, topK_array);
        topK_selector.setAdapter(adapter);
        topK_selector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                firstTime++;
                if (firstTime > 1) {
                    topK_text.setText(topK_selector.getItemAtPosition(position).toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        showAlertDialog("ok", R.string.howToUse, R.string.howToUseText, R.drawable.dialog_icon_info);

        //From Eclipse
        addr_ports = new ArrayList<String>();
        initPorts();

        handler = new Handler(Looper.getMainLooper());

        //Layouts init
        topK_layout = (LinearLayout) findViewById(R.id.topK_layout);
        fromDate_layout = (LinearLayout) findViewById(R.id.fromDate_layout);
        toDate_layout = (LinearLayout) findViewById(R.id.toDate_layout);
        done_button = (Button) findViewById(R.id.done_button);

        showHideLayoutButton = (Button) findViewById(R.id.showHideLayout_button);
    }

    //for showing alert dialog
    private void showAlertDialog(String action, int title, int message, int icon) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle(title);
        alertBuilder.setMessage(message);
        alertBuilder.setIcon(icon);

        if (action.equals("ok")) {
            alertBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        } else if (action.equals("exit")) {
            alertBuilder.setNeutralButton("EXIT", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finishAffinity();
                    int pid = android.os.Process.myPid();
                    android.os.Process.killProcess(pid);
                }
            });
        } else if (action.equals("continue")) {
            alertBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    receiveDataFromReducer();
                    setVisibility(false);
                }
            });
        } else if (action.equals("results")) {
            alertBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    completedThread();
                }
            });
        } else if (action.equals("finish")) {
            alertBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        }
        AlertDialog info = alertBuilder.create();
        info.show();
    }

    //Set Buttons and Fields Visibility
    public void setVisibility(boolean buttonVisibility) {
        if (buttonVisibility) {
            topK_layout.setVisibility(View.VISIBLE);
            fromDate_layout.setVisibility(View.VISIBLE);
            toDate_layout.setVisibility(View.VISIBLE);
            done_button.setVisibility(View.VISIBLE);
        } else {
            topK_layout.setVisibility(View.GONE);
            fromDate_layout.setVisibility(View.GONE);
            toDate_layout.setVisibility(View.GONE);
            done_button.setVisibility(View.GONE);
        }

    }

    //for initializing list with ports
    public void initPorts() {
        try {
            AssetManager assetManager = getAssets();

            BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open("ADDR_PORTS")));
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    addr_ports.add(line);
                }
            } catch (IOException e) {
                System.err.println("Error reading next line...");
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error loading file..");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        final LatLng nyc = new LatLng(40.7127, -74.0059);


        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(zoomIn(nyc)));

        mMap.setOnMarkerDragListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);

        //mMap.setInfoWindowAdapter(new InfoWindowAdapter());
    }

    //method for zooming in at specified point
    public CameraPosition zoomIn(LatLng place) {
        return new CameraPosition.Builder().target(place)
                .zoom(10)
                .build();
    }

    //methods for creating circle on user Long Click
    @Override
    public void onMapLongClick(LatLng latLng) {
        userTouches++;

        if (userTouches == 1) {
            LatLng centerPoint = latLng;
            LatLng point1 = new LatLng(latLng.latitude - 0.015, latLng.longitude - 0.02);
            LatLng point2 = new LatLng(latLng.latitude + 0.015, latLng.longitude - 0.02);
            LatLng point3 = new LatLng(latLng.latitude + 0.015, latLng.longitude + 0.02);
            LatLng point4 = new LatLng(latLng.latitude - 0.015, latLng.longitude + 0.02);

            center = mMap.addMarker(new MarkerOptions().position(centerPoint)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_move)).draggable(true));
            mark1 = mMap.addMarker(new MarkerOptions().position(point1)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon)).draggable(true));
            mark2 = mMap.addMarker(new MarkerOptions().position(point2).visible(false));
            mark3 = mMap.addMarker(new MarkerOptions().position(point3)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon)).draggable(true));
            mark4 = mMap.addMarker(new MarkerOptions().position(point4).visible(false));
            createPolygon();
        } else {
            userTouches = 0;
            interestedArea.remove();
            center.remove();
            mark1.remove();
            mark2.remove();
            mark3.remove();
            mark4.remove();
        }
    }

    public void createPolygon() {

        PolygonOptions area = new PolygonOptions()
                .add(mark1.getPosition(), mark2.getPosition(), mark3.getPosition(), mark4.getPosition())
                .fillColor((Color.parseColor("#6E3F79EC")))
                .clickable(true);
        interestedArea = mMap.addPolygon(area);
    }

    //methods for Marker listeners
    @Override
    public void onMarkerDragStart(Marker marker) {
        onMarkerMoved(marker);
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        onMarkerMoved(marker);
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        onMarkerMoved(marker);
    }

    public boolean onMarkerMoved(Marker marker) {
        interestedArea.remove();
        if (marker.equals(mark1)) {

            //Don't allow some moves!
            if (marker.getPosition().latitude > mark2.getPosition().latitude) {
                marker.setPosition(new LatLng(mark2.getPosition().latitude, marker.getPosition().longitude));
            }
            if (marker.getPosition().longitude > mark3.getPosition().longitude) {
                marker.setPosition(new LatLng(marker.getPosition().latitude, mark3.getPosition().longitude));
            }

            mark2.setPosition(new LatLng(mark2.getPosition().latitude, marker.getPosition().longitude));
            mark4.setPosition(new LatLng(marker.getPosition().latitude, mark4.getPosition().longitude));

            distX = (mark3.getPosition().longitude - mark2.getPosition().longitude);
            distY = (mark2.getPosition().latitude - mark1.getPosition().latitude);

            double newLat = (distY / 2) + mark1.getPosition().latitude;
            double newLng = (distX / 2) + mark2.getPosition().longitude;

            LatLng newCenter = new LatLng(newLat, newLng);
            center.setPosition(newCenter);
        } else if (marker.equals(mark3)) {

            //Don't allow some moves!
            if (marker.getPosition().latitude < mark1.getPosition().latitude) {
                marker.setPosition(new LatLng(mark1.getPosition().latitude, marker.getPosition().longitude));
            }
            if (marker.getPosition().longitude < mark1.getPosition().longitude) {
                marker.setPosition(new LatLng(marker.getPosition().latitude, mark1.getPosition().longitude));
            }

            mark2.setPosition(new LatLng(mark3.getPosition().latitude, mark2.getPosition().longitude));
            mark4.setPosition(new LatLng(mark4.getPosition().latitude, marker.getPosition().longitude));

            distX = (mark3.getPosition().longitude - mark2.getPosition().longitude);
            distY = (mark2.getPosition().latitude - mark1.getPosition().latitude);

            double newLat = (distY / 2) + mark1.getPosition().latitude;
            double newLng = (distX / 2) + mark2.getPosition().longitude;

            LatLng newCenter = new LatLng(newLat, newLng);
            center.setPosition(newCenter);
        } else if (marker.equals(center)) {
            LatLng newCenter = marker.getPosition();
            mark1.setPosition(new LatLng(newCenter.latitude - (distY / 2), newCenter.longitude - (distX / 2)));
            mark2.setPosition(new LatLng(newCenter.latitude + (distY / 2), newCenter.longitude - (distX / 2)));
            mark3.setPosition(new LatLng(newCenter.latitude + (distY / 2), newCenter.longitude + (distX / 2)));
            mark4.setPosition(new LatLng(newCenter.latitude - (distY / 2), newCenter.longitude + (distX / 2)));
        }
        createPolygon();
        return false;
    }


    //--------------------- MY METHODS FOR THE MAP-REDUCE PROCEDURE ------------------

    //set Date button
    public void setFromDate(View view) {
        DatePickerFragment datePicker = new DatePickerFragment();
        datePicker.setMapsActivityText(fromDate_text);
        DialogFragment newFragment = datePicker;
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void setToDate(View view) {
        DatePickerFragment datePicker = new DatePickerFragment();
        datePicker.setMapsActivityText(toDate_text);
        DialogFragment newFragment = datePicker;
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    //DONE button OnClick
    public void doneButtonClick(View view) {

        topK = Integer.valueOf(topK_selector.getSelectedItem().toString());

        if (!fromDate_text.getText().toString().equals(""))
            fromDate = fromDate_text.getText().toString() + " 00:00:00";
        else {
            showAlertDialog("ok", R.string.empty_fromDate_title, R.string.empty_fromDate_message, R.drawable.dialog_icon_alert);
            return;
        }

        if (!toDate_text.getText().toString().equals(""))
            toDate = toDate_text.getText().toString() + " 23:59:59";
        else {
            showAlertDialog("ok", R.string.empty_toDate_title, R.string.empty_toDate_message, R.drawable.dialog_icon_alert);
            return;
        }

        //if there is no Polygon show message
        if (userTouches == 1) {
            initValuesToSend();
        } else {
            showAlertDialog("ok", R.string.set_position_title, R.string.set_position_message, R.drawable.dialog_icon_alert);
        }
    }

    //INIT VALUES
    private void initValuesToSend() {
        minX = mark1.getPosition().longitude;
        maxX = mark3.getPosition().longitude;
        minY = mark1.getPosition().latitude;
        maxY = mark3.getPosition().latitude;
        sendToMappers();
    }

    //SEND TO MAPPERS
    private void sendToMappers() {
        initDate(fromDate, toDate);

        ConnectToMapper map1 = new ConnectToMapper(topK, initCoordinates(1, minX, maxX, minY, maxY), dates,
                addr_ports.get(0), Integer.parseInt(addr_ports.get(1)));
        boolean success = false;
        try {
            success = map1.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        ConnectToMapper map2 = new ConnectToMapper(topK, initCoordinates(2, minX, maxX, minY, maxY), dates,
                addr_ports.get(2), Integer.parseInt(addr_ports.get(3)));
        boolean success2 = false;
        try {
            success2 = map2.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        ConnectToMapper map3 = new ConnectToMapper(topK, initCoordinates(3, minX, maxX, minY, maxY), dates,
                addr_ports.get(4), Integer.parseInt(addr_ports.get(5)));
        boolean success3 = false;
        try {
            success3 = map3.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (!(success || success2 || success3)) {
            showAlertDialog("exit", R.string.mapper_offline, R.string.mapper_offline_text, R.drawable.dialog_icon_alert);
        } else
            showAlertDialog("continue", R.string.done_title, R.string.done_message, R.drawable.dialog_icon_info);
    }

    private List<Double> initCoordinates(int k, double minX, double maxX, double minY, double maxY) {
        List<Double> coordinates = new ArrayList<Double>();

        if (k == 1) { // if is mapper1
            coordinates.add(minX);
            double newMaxX = ((maxX - minX) * 1 / 3) + minX;
            coordinates.add(newMaxX);
            coordinates.add(minY);
            coordinates.add(maxY);
        } else if (k == 2) { // if is mapper2
            double newMinX = ((maxX - minX) * 1 / 3) + minX;
            coordinates.add(newMinX);
            double newMaxX = ((maxX - minX) * 2 / 3) + minX;
            coordinates.add(newMaxX);
            coordinates.add(minY);
            coordinates.add(maxY);
        } else if (k == 3) { // if is mapper3
            double newMinX = ((maxX - minX) * 2 / 3) + minX;
            coordinates.add(newMinX);
            coordinates.add(maxX);
            coordinates.add(minY);
            coordinates.add(maxY);
        }
        return coordinates;
    }

    private void initDate(String min, String max) {
        String minDate = "2012-05-09 00:00:00";
        String maxDate = "2012-11-06 23:59:00";
        this.dates[0] = minDate;
        this.dates[1] = maxDate;
    }

    //RECEIVE FROM REDUCER
    private void receiveDataFromReducer() {
        receiveFinalMap getMap = new receiveFinalMap(addr_ports, handler, listener, MapsActivity.this);
        getMap.execute();
    }

    public void completedThread() {
        showListResults();
    }

    public void showCheckinOnMap(String poi) {
        if (poiOnMap != null)
            poiOnMap.remove();
        LatLng checkinPosition = new LatLng((double) finalResult.get(poi).get(4), (double) finalResult.get(poi).get(5));
        MarkerOptions poiMark = new MarkerOptions();
        poiMark
                .position(checkinPosition)
                .title(finalResult.get(poi).get(1).toString())
                .snippet(finalResult.get(poi).get(3).toString() + " checkins");

        poiOnMap = mMap.addMarker(poiMark);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(checkinPosition, 15));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.getTitle() != null) {
            Intent showCheckinInfo = new Intent(this, checkin_info.class);

            showCheckinInfo.putExtra("poi_name", poiOnMap.getTitle());
            showCheckinInfo.putExtra("poi_count", poiOnMap.getSnippet());
            showCheckinInfo.putStringArrayListExtra("photos", photosList);
            startActivity(showCheckinInfo);
        }
        return false;
    }

    public void customizeLayoutToShowResults() {
        initLayout.removeAllViews();
        interestedArea.remove();
        center.remove();
        mark1.remove();
        mark3.remove();
    }

    public void addResultsListToLayout(ArrayList<String> list) {
        final ListView listView = new ListView(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        initLayout.addView(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String poi_name = listView.getAdapter().getItem(position).toString();
                poi = null;

                for (Object key : finalResult.keySet()) {
                    if (finalResult.get(key).get(1).toString().equals(poi_name)) {
                        poi = MapsActivity.finalResult.get(key).get(0).toString();
                        photosList = (ArrayList<String>) finalResult.get(poi).get(2);
                        break;
                    }
                }
                showCheckinOnMap(poi);
            }


//        Intent results = new Intent(this, results.class);
//        results.putStringArrayListExtra("list", list);
//        startActivityForResult(results, 1);
        });
    }

    //Open a list Activity with the results
    private void showListResults() {
        ArrayList<String> list = new ArrayList<String>();

        int count = 0;
        for (Object key : finalResult.keySet()) {
            count++;
            list.add(finalResult.get(key).get(1).toString());
        }

        customizeLayoutToShowResults();
        addResultsListToLayout(list);
    }

    public void showHideLayout(View view) {
        if (initLayout.getVisibility() == View.GONE) {
            initLayout.setVisibility(View.VISIBLE);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mapFragment.getView().getLayoutParams();
            params.weight = 0.63F;
            mapFragment.getView().setLayoutParams(params);
        } else {
            initLayout.setVisibility(View.GONE);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mapFragment.getView().getLayoutParams();
            params.weight = 1.05F;
            mapFragment.getView().setLayoutParams(params);
        }
    }
}