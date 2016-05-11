package projb.dissystems.aueb.vassilis.nycheckins;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    List<LatLng> coordinates;

    //for creating the circle
    int userTouches = 0; //when user long press 2 points on map then create Circle
    Marker centerMarker;
    Marker radiusMarker;
    double radius;
    Circle interestedArea;

    //top-K results
    EditText topK_text;

    // PORTS
    List<String> addr_ports = null;

    // data to Mappers -- TEST
    int topK;
    double minX = -74.0144996501386;
    double maxX = -73.9018372248612;
    double minY = 40.67747711364791;
    double maxY = 40.76662365086325;
    String fromDate = "2012-05-09 00:00:00";
    String toDate = "2012-11-06 23:59:00";
    private String[] dates = new String[2];

    // final result from reducer
    public static Map<Object, Long> finalResult = null;
    Handler handler;
    onMapReceived listener = new onMapReceived() {
        @Override
        public void onMapReceived() {
            completedThread();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        topK_text = (EditText) findViewById(R.id.topK_text);

        showAlertDialog("ok", R.string.howToUse, R.string.howToUseText, R.drawable.dialog_icon_info);

        //From Eclipse
        addr_ports = new ArrayList<String>();
        initPorts();

        handler = new Handler(Looper.getMainLooper());
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
        }
        AlertDialog info = alertBuilder.create();
        info.show();
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
    }

    //method for zooming in at specified point
    public CameraPosition zoomIn(LatLng place) {
        return new CameraPosition.Builder().target(place)
                .zoom(10)
                .bearing(330)
                .tilt(30)
                .build();
    }

    //methods for creating circle on user Long Click
    @Override
    public void onMapLongClick(LatLng latLng) {
        userTouches++;

        if (userTouches == 1) {
            LatLng center = latLng;
            centerMarker = mMap.addMarker(new MarkerOptions().position(center).draggable(true));
        } else if (userTouches == 2) {
            LatLng radiusPoint = latLng;
            radiusMarker = mMap.addMarker(new MarkerOptions().position(radiusPoint).draggable(true));

            radius = distanceBetweenCenterAndRadius(centerMarker.getPosition(), radiusMarker.getPosition());
            createCircle(centerMarker.getPosition(), radiusMarker.getPosition());
        } else {
            userTouches = 0;
            centerMarker.remove();
            radiusMarker.remove();
            interestedArea.remove();
        }
    }

    public void createCircle(LatLng center, LatLng radius) {
        interestedArea = mMap.addCircle(new CircleOptions().center(center).radius(distanceBetweenCenterAndRadius(center, radius)).fillColor(Color.parseColor("#6E3F79EC")));
    }

    public double distanceBetweenCenterAndRadius(LatLng center, LatLng radius) {
        float[] rad = new float[1];
        Location.distanceBetween(center.latitude, center.longitude, radius.latitude, radius.longitude, rad);
        return rad[0];
    }

    private static LatLng toRadiusLatLng(LatLng center, double radius) {
        double radiusAngle = Math.toDegrees(radius / 6371009) /
                Math.cos(Math.toRadians(center.latitude));
        return new LatLng(center.latitude, center.longitude + radiusAngle);
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
        if (marker.equals(centerMarker)) {
            interestedArea.setCenter(marker.getPosition());
            radiusMarker.setPosition(toRadiusLatLng(marker.getPosition(), radius));
            return true;
        }
        if (marker.equals(radiusMarker)) {
            radius = distanceBetweenCenterAndRadius(centerMarker.getPosition(), radiusMarker.getPosition());
            interestedArea.setRadius(radius);
            return true;
        }
        return false;
    }


    //--------------------- MY METHODS FOR THE MAP-REDUCE PROCEDURE ------------------


    public void doneButtonClick(View view) {
        try {
            topK = Integer.valueOf(topK_text.getText().toString());
        } catch (NumberFormatException ex) {
            showAlertDialog("ok", R.string.empty_text_title, R.string.empty_text_message, R.drawable.dialog_icon_alert);
            return;
        }

        //if there is no circle default values will be send
        if (centerMarker != null && radius != 0) {
            initValuesToSend(centerMarker.getPosition(), radius);
        } else
            showAlertDialog("ok", R.string.set_position_title, R.string.set_position_message, R.drawable.dialog_icon_alert);
        sendToMappers();
        receiveDataFromReducer();
    }

    //INIT VALUES
    private void initValuesToSend(LatLng center, double radius) {
        LatLng southwest = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 225);
        LatLng northeast = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 45);
        minX = southwest.longitude;
        maxX = northeast.longitude;
        minY = southwest.latitude;
        maxY = northeast.latitude;
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
            showAlertDialog("ok", R.string.done_title, R.string.done_message, R.drawable.dialog_icon_info);
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
        receiveFinalMap getMap = new receiveFinalMap(addr_ports, handler, listener);
        getMap.execute();
    }

    public void completedThread() {
        showAlertDialog("ok", R.string.reduce_done, R.string.reducer_done_message, R.drawable.dialog_icon_info);
        showMarkersOnMap();
    }

    //SHOW FINAL RESULTS ON MAP
    private void showMarkersOnMap() {
        //test on other activity
        ArrayList<String> list = new ArrayList<String>();

        for (Object key : finalResult.keySet()) {
            list.add(key.toString());
        }

        Intent results = new Intent(this, results.class);
        results.putStringArrayListExtra("list", list);
        startActivity(results);
    }
}

//creates a polygon near NY City -- TEST
//        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
//            @Override
//            public void onMapLongClick(LatLng latLng) {
//                PolygonOptions rectOptions = new PolygonOptions()
//                        .add(nyc, new LatLng(40.7127, -73.9990), new LatLng(40.7027, -73.9990), new LatLng(40.7027, -74.0059));
//                //.addAll(createRectangle(nyc, 2, 2));
//                Polygon polygon = mMap.addPolygon(rectOptions);
//
//                //get the Polygon Coordinates -- 1 for each point!!!
//                coordinates = polygon.getPoints();
//                polygon.setClickable(true);
//            }
//        });

//LatLngBounds NY = new LatLngBounds(new LatLng(40.55085246740427, -74.27476644515991), new LatLng(40.988331719265304, -73.68382519820906));

// mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//mMap.moveCamera(CameraUpdateFactory.newLatLng(NY));

//        int width = getResources().getDisplayMetrics().widthPixels;
//        int height = getResources().getDisplayMetrics().heightPixels;
//        int padding = (int) (width * 0.12);
//
//        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(NY, width, height, padding));

//    private List<LatLng> createRectangle(LatLng center, double halfWidth, double halfHeight) {
//        return Arrays.asList(new LatLng(center.latitude - halfHeight, center.longitude - halfWidth),
//                new LatLng(center.latitude - halfHeight, center.longitude + halfWidth),
//                new LatLng(center.latitude + halfHeight, center.longitude + halfWidth),
//                new LatLng(center.latitude + halfHeight, center.longitude - halfWidth),
//                new LatLng(center.latitude - halfHeight, center.longitude - halfWidth));
//    }