package it.unisannio.www.treasurehunt;


import android.Manifest;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;


public class CreateChallenge extends AppCompatActivity implements OnMapReadyCallback,
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
            mMap.setContentDescription("mappa");
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            init();
        }
    }

    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    private ImageView mGps;


    //vars
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private GoogleApiClient mGoogleApiClient;
    private String user;
    private ArrayList<Checkpoint> percorso;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_challenge);
        mGps = findViewById(R.id.ic_gps);
        if(getIntent().getExtras()!=null && getIntent().getExtras().containsKey("percorso")) {
            percorso = (ArrayList<Checkpoint>) getIntent().getExtras().get("percorso");
            Checkpoint checkpoint = new Checkpoint();
            checkpoint.setQuestion(getIntent().getExtras().getString("question"));
            checkpoint.setLatitude(getIntent().getExtras().getDouble("lat"));
            checkpoint.setLongitude(getIntent().getExtras().getDouble("long"));
            percorso.add(checkpoint);

        }
        else {
            percorso = new ArrayList<Checkpoint>();
        }
        user = getIntent().getExtras().getString("user");
        getLocationPermission();
    }

    private void init(){
        Log.d(TAG, "init: initializing");

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        if(!percorso.isEmpty())
            setCheckpoint(mMap);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                Intent intent = new Intent("android.intent.action.Question");
                intent.putExtra("percorso", percorso);
                intent.putExtra("lat", point.latitude);
                intent.putExtra("long", point.longitude);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
            }
        });
        hideSoftKeyboard();
    }

    public void endChallenge(View view) {
        if (isNetworkAvailable()) {
            String url = "http://treshunte.altervista.org/idPercorso.php";
            DBRequest rq = new DBRequest(url);
            String resp = "";
            int stato = 0;
            stato = rq.getStato();
            int progress = 0;
            while (stato != 100) {
                if (stato != progress) {
                    progress = stato;
                }

                stato = rq.getStato();
            }
            resp = rq.getResult();
            int idPercorso = Integer.parseInt(resp) + 1;
            int idCheckpoint = 1;

            for (int i = 0; i < percorso.size(); i++) {
                Checkpoint checkpoint = percorso.get(i);
                String quest = checkpoint.getQuestion().replace(" ", "_");
                url = "http://treshunte.altervista.org/saveCheckpoint.php?idPercorso=" + idPercorso + "&idCheckpoint="
                        + idCheckpoint + "&lat=" + checkpoint.getLatitude() + "&long=" + checkpoint.getLongitude() + "&question="
                        + quest;
                rq = new DBRequest(url);
                resp = "";
                stato = 0;
                stato = rq.getStato();
                progress = 0;
                while (stato != 100) {
                    if (stato != progress) {
                        progress = stato;
                    }
                    stato = rq.getStato();
                }
                resp = rq.getResult();
                idCheckpoint++;
            }
            Toast.makeText(getApplicationContext(), "Creazione sfida avvenuta con successo", Toast.LENGTH_LONG).show();
            Intent intent = new Intent("android.intent.action.HOME");
            intent.putExtra("user", user);
            startActivity(intent);
        }
        else {
            Toast to = Toast.makeText(getApplicationContext(), "Tentativo di connessione fallito. Attiva connessione dati", Toast.LENGTH_LONG);
            to.show();
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
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM,
                                    "My Location");

                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(CreateChallenge.this, "unable to get current location", Toast.LENGTH_SHORT).show();
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

        mapFragment.getMapAsync(CreateChallenge.this);
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

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

    private void setCheckpoint(GoogleMap googleMap){
        for(Checkpoint checkpoint : percorso){
            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(checkpoint.getLatitude(),checkpoint.getLongitude())).title(""+checkpoint.getIdCheckpoint());
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
            googleMap.addMarker(markerOptions);
        }

    }
    public void cancelChallenge(View view){
        Intent intent = new Intent("android.intent.action.HOME");
        intent.putExtra("user", user);
        startActivity(intent);
    }

    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent("android.intent.action.CreateChallenge");
        intent.putExtra("user", user);
        startActivity(intent);
    }
    private boolean isNetworkAvailable(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
