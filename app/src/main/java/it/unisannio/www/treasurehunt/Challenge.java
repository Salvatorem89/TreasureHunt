package it.unisannio.www.treasurehunt;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class Challenge extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener{

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            try {
                init();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    //widgets
    private ImageView mGps;


    //vars
    private int nextCheckpoint;
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private GoogleApiClient mGoogleApiClient;
    private long start;
    private Location posizioneAttuale;
    private String user;
    private ArrayList<Checkpoint> percorso;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent().getExtras() != null && getIntent().getExtras().containsKey("nextId")){
            nextCheckpoint = getIntent().getExtras().getInt("nextId")+1;
        }
        else {
            nextCheckpoint = 1;
        }
        user = getIntent().getExtras().getString("user");
        if(getIntent().getExtras() != null && getIntent().getExtras().containsKey("start")){
            start = getIntent().getExtras().getLong("start");
        }
        else {
            start = System.currentTimeMillis();
        }


        setContentView(R.layout.activity_challenge);

        mGps = findViewById(R.id.ic_gps);
        if(getIntent().getExtras()!=null && getIntent().getExtras().containsKey("percorsoScelto")) {
            percorso = (ArrayList<Checkpoint>) getIntent().getExtras().get("percorsoScelto");
        }
        else {
            percorso = new ArrayList<Checkpoint>();
        }
        getLocationPermission();
    }

    private void init() throws InterruptedException {
        Log.d(TAG, "init: initializing");
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                getDeviceLocation();
                Location markerAttuale = new Location("");
                markerAttuale.setLatitude(percorso.get(nextCheckpoint-1).getLatitude());
                markerAttuale.setLongitude(percorso.get(nextCheckpoint-1).getLongitude());
                if(posizioneAttuale.distanceTo(markerAttuale)>50){
                    Toast.makeText(getApplicationContext(), "Sei troppo lontano dal Checkpoint. Avvicinati.", Toast.LENGTH_LONG).show();
                }
                else {
                    String question = percorso.get(nextCheckpoint - 1).getQuestion();
                    Intent intent = new Intent("android.intent.action.Answer");
                    intent.putExtra("percorso", percorso);
                    intent.putExtra("nextId", nextCheckpoint);
                    intent.putExtra("checkpoint", percorso.get(nextCheckpoint - 1));
                    intent.putExtra("start", start);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }
                return  false;
            }
        });
        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
            }
        });
        if(nextCheckpoint<=percorso.size())
            setCheckpoint(mMap,nextCheckpoint);
        else{
            double tempoTotale = (double) (System.currentTimeMillis()-start) / 1000;
            String url = "http://treshunte.altervista.org/time.php?user="+user+"&idPercorso="+percorso.get(0).getIdRun()+"&time="+tempoTotale;
            DBRequest rq = new DBRequest(url);
            String resp;
            int stato;
            stato = rq.getStato();
            int progress = 0;
            while (stato != 100) {
                if (stato != progress) {
                    progress = stato;
                }
                stato = rq.getStato();
            }
            Toast.makeText(getApplicationContext(), "HAI COMPLETATO LA SFIDA in " + tempoTotale + " secondi", Toast.LENGTH_LONG).show();
            Thread.sleep(2000);
            Intent intent = new Intent("android.intent.action.HOME");
            intent.putExtra("user", user);
            startActivity(intent);
        }

        hideSoftKeyboard();
    }

    private void setCheckpoint(GoogleMap googleMap, int i){

        for(Checkpoint checkpoint : percorso){
            if(checkpoint.getIdCheckpoint()==i) {
                MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(checkpoint.getLatitude(), checkpoint.getLongitude())).title("" + checkpoint.getIdRun()).visible(true);
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
                googleMap.addMarker(markerOptions);
            }
            else{
                MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(checkpoint.getLatitude(), checkpoint.getLongitude())).title("" + checkpoint.getIdRun()).visible(false);
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
                googleMap.addMarker(markerOptions);
            }
        }

    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            posizioneAttuale = (Location) task.getResult();
                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(Challenge.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if(!title.equals("My Location")){
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }

        hideSoftKeyboard();
    }

    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.create);

        mapFragment.getMapAsync(Challenge.this);
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /*
        --------------------------- google places API autocomplete suggestions -----------------
     */


    public void resume(View view){
        Intent intent = new Intent("android.intent.action.HOME");
        intent.putExtra("user", user);
        startActivity(intent);
    }

    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent("android.intent.action.HOME");
        intent.putExtra("user", user);
        startActivity(intent);
    }
    private boolean isNetworkAvailable(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (cm != null) {
            activeNetworkInfo = cm.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}