package com.example.a21__void.afroturf;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.example.a21__void.Modules.DirectionFinder;
import com.example.a21__void.Modules.DirectionFinderListener;
import com.example.a21__void.Modules.Route;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.clustering.ClusterManager;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, DirectionFinderListener {


    LocationManager locationManager;
    MyLocationListiner locationListener;

    private GoogleMap mMap;
    private double longitude, latitude;
    private TextView ttvUpdate;
    private FusedLocationProviderClient mFusedLocationClient;

    private ClusterManager<MySalon> mClusterManager;

    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;


    Button btn_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_path = findViewById(R.id.btn_path);
        btn_path.setOnClickListener(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        Log.i("ZAQ", "onCreate: ");
        locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        locationListener = new MyLocationListiner(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        setUpMap();

    }


    public void getDeviceLocation(final DataCallBack dataCallBack) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if (location != null && mMap != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    Log.i("ZAQ", "onReceiveResult: " + latitude + "| " + longitude);

                    dataCallBack.onLocation(longitude, latitude);


                }

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        getDeviceLocation(new DataCallBack() {
            @Override
            public void onLocation(double lang, double lat) {

                Log.i("ZAQ", "onMapReady: " + lang + "| " + lat);

                LatLng sydney = new LatLng(lat, lang);
                setUpClusterer(lat+0.1, lang+0.1);

                originMarkers.add(mMap.addMarker(new MarkerOptions()
                        .title("JeffTown")
                        .position(sydney)));
                // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(sydney)      // Sets the center of the map to Mountain View
                        .zoom(17)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 5000, null);

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);

                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {

                        Intent intent = new Intent(getApplicationContext(),Salon.class);
                        startActivityForResult(intent, Salon.REQUEST_PATH);
                        finish();



                    }
                });

                // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 50f));



            }
        });



    }


    private void setUpClusterer(double lat, double lang) {
        // Position the map.
        getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lang), 10));

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<>(this, getMap());

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        getMap().setOnCameraIdleListener(mClusterManager);
        getMap().setOnMarkerClickListener(mClusterManager);

        // Add cluster items (markers) to the cluster manager.
        addItems(lat, lang);
    }
    private void addItems(double lat, double lng) {


        // Add ten cluster items in close proximity, for purposes of this example.
        for (int i = 0; i < 10; i++) {
            double offset = i / 10d;
            lat = lat + offset;
            lng = lng + offset;
            MySalon offsetItem = new MySalon(lat, lng);
            mClusterManager.addItem(offsetItem);
        }
    }
    private void setUpMap() {

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Log.i("ZAQ", "setUpMap: " + latitude + "| " + longitude);

    }

    public void findPath(){
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                Intent intent = new Intent(getApplicationContext(),Salon.class);
                startActivity(intent);



            }
        });
    }

    public GoogleMap getMap() {
        return mMap;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_path:
                sendRequest();

                break;
        }
    }

    private void sendRequest() {
        String origin = Double.toString(latitude) + "," + Double.toString(longitude);
        String destination = Double.toString(latitude+.1) + "," + Double.toString(longitude+.1);
        try{
            new DirectionFinder(this, origin, destination).execute();
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
    }

    public interface DataCallBack{


        void onLocation(double lang, double lat);

    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            ((TextView) findViewById(R.id.ttvDuration)).setText(route.duration.text);
            ((TextView) findViewById(R.id.ttvDistance)).setText(route.distance.text);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("ZAQ", "startActivityForResult: clicked");

        sendRequest();
        //sendRequest();
        if (requestCode == Salon.REQUEST_PATH) {
            // Make sure the request was successful
            Log.i("ZAQ", "startActivityForResult: in");

            sendRequest();


        }


        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);

        Log.i("ZAQ", "startActivityForResult: 1");
    }
}
