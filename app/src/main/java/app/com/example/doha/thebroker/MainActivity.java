package app.com.example.doha.thebroker;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    //For Finding the Current Location
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LatLng myLocation;
    private LocationRequest request;
    int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 0;

    //For the Map display
    private GoogleMap mMap;

    String addHomeOptions[] = {"To the Current Location ", "To another Location "};
    String clickMarkerArray[]={"Remove","Edit"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Gets the map fragment defined in the layout
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        //getMapAsync Creates the GoogleMap object that initializes the map system and the view
        mapFragment.getMapAsync(this);

//Call tha asynctask here

        //Check if GooglePlayServices are availble on the device
        boolean Services_available = checkGooglePlayServices(this);
        if (Services_available) {
            //Build the Google API client
           buildGoogleApiClient();
           createLocationRequest();
        }
        //The items in the DB are displayed on the map

        /*mMap.addMarker(new MarkerOptions().position(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude())).draggable(true));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.remove();
                return false;
            }
        });*/
        registerdItems getItems=new registerdItems();
    //   getItems.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        //Connect the google api client to start Location updates
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect(); //The onConnected() method is then invoked
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect(); //The onConnected() method is then invoked
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.action_addHome) {


            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
            builder.setTitle("Where Would You Like to Add the Home ?")
                    .setItems(addHomeOptions, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position
                            // of the selected item
                           final proprietary house=new proprietary();
                            if (which == 0) {
                                house.setLatitude(mLastLocation.getLatitude());
                                house.setLongitude(mLastLocation.getLongitude());
                                Intent intent=new Intent(getApplicationContext(),ProprietaryForm.class);
                                intent.putExtra("item",house);
                                startActivity(intent);

                                /*mMap.addMarker(new MarkerOptions().position(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude())).draggable(true));
                               mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){


                                   @Override
                                   public boolean onMarkerClick(Marker marker) {
                                       Toast.makeText(getApplicationContext(),"Hey I am a marker", Toast.LENGTH_LONG).show();

                                       return false;
                                   }
                               });*/


                            } else if (which == 1) {

                                Intent intent=new Intent(getApplicationContext(),ProprietaryForm.class);
                                startActivity(intent);
                            }

                        }
                    });
            builder.create();
            android.support.v7.app.AlertDialog options = builder.show();

        }
        else if(id == R.id.action_search){
            Intent intent=new Intent(getApplicationContext(),SimpleSearch.class);
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //Let the map show the current location
        mMap.setMyLocationEnabled(true);

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
mLastLocation=location;
        Toast.makeText(this, "Latitude:" + mLastLocation.getLatitude() + ", Longitude:" + mLastLocation.getLongitude(), Toast.LENGTH_LONG).show();

        // mMap.addMarker(new MarkerOptions().position(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude())).draggable(false));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }
    //Create the googleApiClient that would be used to get the location
    public synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }
    //Create Location requests to periodically request a location update
    protected void createLocationRequest() {
        request = new LocationRequest();
        request.setInterval(20000);
        request.setFastestInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);

        }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, request, this);
//            mMap.setMyLocationEnabled(true);

    }

    public boolean checkGooglePlayServices(Context mContext){
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        AlertDialog unavailable_play_services=new AlertDialog.Builder(this).create();
        unavailable_play_services.setTitle("Google Play services are not installed");
        unavailable_play_services.setMessage("Please Install Google Play Services");
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                unavailable_play_services.show();
            }

            return false;
        }

        return true;
    }
    class registerdItems extends AsyncTask<Void,Void,Void>{

      //  private String urlPath = "http://brokerserver-doha.rhcloud.com/users/getAllItems";
      //  private String urlPath =" http://localhost:8080/users/getAllItems";
    //  private String urlPath ="http://10.0.2.2:8080/users/getAllItems";
        private String urlPath ="http://192.168.156.1:8080/users/getAllItems";

        private String AssetsDataJsonStr;
        //ArrayList that is populated after recieving a response from the server with all the availble assets
        ArrayList<proprietary>allAssets;

        private void getAssetsArray(String JSONString){

            //Since the string starts with curly brackets ,so it is a JSON object
            try {
                JSONObject urlJSON=new JSONObject(JSONString);
                JSONArray resultsArray = urlJSON.getJSONArray("assets");
                for(int i=0;i<resultsArray.length();i++){
                    // String urlString;
                    proprietary prop;
                    JSONObject proprietary=resultsArray.getJSONObject(i);

                    prop=new proprietary();
                    prop.setAddress(proprietary.getString("Address"));
                    prop.setCountry(proprietary.getString("country"));
                    prop.setLatitude(proprietary.getDouble("location_lat"));
                    prop.setLongitude(proprietary.getDouble("location_long"));
                    prop.setPrice(proprietary.getDouble("price"));
                    allAssets.add(prop);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }



        }
        @Override
        protected Void doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            // Will contain the raw JSON response as a string.
            String AssetsJsonStr = null;
            URL url = null;
            try {
                url = new URL(urlPath);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                urlConnection.setRequestMethod("GET");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            try {
                urlConnection.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Read the input stream into a String
            InputStream inputStream = null;
            try {
                inputStream = urlConnection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                Log.d("INPUT_STREAM","inputStream is Null");
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (buffer.length() == 0) {
                Log.d("BUFFER_LENGTH","buffer length is zero");
            }
            AssetsDataJsonStr = buffer.toString();

            //A function that parses the JSON string and populates an arraylist of Proprietary Objects
            getAssetsArray(AssetsDataJsonStr);

            return null;
        }
        protected void onPostExecute(){
//Display the items on the map
            //Iterate over the ArrayList of allAssets and put a marker on the map
            for(proprietary item:allAssets){
                //The marker is dragable
                 mMap.addMarker(new MarkerOptions().position(new LatLng(item.getLatitude(),item.getLongitude())).draggable(true));
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        marker.remove();
                        return false;
                    }
                });

            }
        }
    }

}
