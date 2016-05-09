package projb.dissystems.aueb.vassilis.nycheckins;

import android.content.res.AssetManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    List<LatLng> coordinates;

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
    private Map<Object, Long> finalResult = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        topK_text = (EditText) findViewById(R.id.topK_text);

        //From Eclipse
        addr_ports = new ArrayList<String>();
        initPorts();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        final LatLng nyc = new LatLng(40.7127, -74.0059);

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                PolygonOptions rectOptions = new PolygonOptions()
                        .add(nyc, new LatLng(40.7127, -73.9990), new LatLng(40.7027, -73.9990), new LatLng(40.7027, -74.0059));
                //.addAll(createRectangle(nyc, 2, 2));
                Polygon polygon = mMap.addPolygon(rectOptions);

                //get the Polygon Coordinates -- 1 for each point!!!
                coordinates = polygon.getPoints();
                polygon.setClickable(true);
            }
        });

        //LatLngBounds NY = new LatLngBounds(new LatLng(40.55085246740427, -74.27476644515991), new LatLng(40.988331719265304, -73.68382519820906));

        // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(NY));

//        int width = getResources().getDisplayMetrics().widthPixels;
//        int height = getResources().getDisplayMetrics().heightPixels;
//        int padding = (int) (width * 0.12);
//
//        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(NY, width, height, padding));
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(zoomIn(nyc)));
    }

    public CameraPosition zoomIn(LatLng place) {
        return new CameraPosition.Builder().target(place)
                .zoom(10)
                .bearing(330)
                .tilt(30)
                .build();
    }

    private List<LatLng> createRectangle(LatLng center, double halfWidth, double halfHeight) {
        return Arrays.asList(new LatLng(center.latitude - halfHeight, center.longitude - halfWidth),
                new LatLng(center.latitude - halfHeight, center.longitude + halfWidth),
                new LatLng(center.latitude + halfHeight, center.longitude + halfWidth),
                new LatLng(center.latitude + halfHeight, center.longitude - halfWidth),
                new LatLng(center.latitude - halfHeight, center.longitude - halfWidth));
    }

    public void doneButtonClick(View view) {
        topK = Integer.valueOf(topK_text.getText().toString());
        sendToMappers();
        receiveFinalMap getMap = new receiveFinalMap(addr_ports);
        try {
            finalResult = getMap.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        showMarkersOnMap();
    }

    private void showMarkersOnMap() {

    }

    //copy-paste from Eclipse
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

    private void sendToMappers() {
        initDate(fromDate, toDate);

        ConnectToMapper map1 = new ConnectToMapper(topK, initCoordinates(1, minX, maxX, minY, maxY), dates,
                addr_ports.get(0), Integer.parseInt(addr_ports.get(1)));
        map1.execute();
        // map1.closeConnection();

        ConnectToMapper map2 = new ConnectToMapper(topK, initCoordinates(2, minX, maxX, minY, maxY), dates,
                addr_ports.get(2), Integer.parseInt(addr_ports.get(3)));
        map2.execute();
        // map2.closeConnection();

        ConnectToMapper map3 = new ConnectToMapper(topK, initCoordinates(3, minX, maxX, minY, maxY), dates,
                addr_ports.get(4), Integer.parseInt(addr_ports.get(5)));
        map3.execute();
        // map3.closeConnection();
    }


    //METHODS USED BY sendToMappers() method
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
}
