package app.com.example.doha.thebroker;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ProprietaryForm extends AppCompatActivity {
    proprietary item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proprietary_form);
        //To get the proprietary object from the MainActivity

        item = (proprietary) getIntent().getSerializableExtra("item");

        //UI elements
        //To populate the spinner with the available countries
        Locale[] locale = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<String>();
        String country;
        for (Locale loc : locale) {
            country = loc.getDisplayCountry();
            if (country.length() > 0 && !countries.contains(country)) {
                countries.add(country);
            }
        }
        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);

        Spinner countrySpinner = (Spinner) findViewById(R.id.country_spinner);
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, countries);
        countrySpinner.setAdapter(countryAdapter);
        //Populate types spinner
        ArrayList<String> type = new ArrayList<String>();
        type.add("Apartement");
        type.add("Villa");
        type.add("Other");
        Spinner typeSpinner = (Spinner) findViewById(R.id.type_Spinner);
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, type);
        typeSpinner.setAdapter(typeAdapter);
        ArrayList<String> actions = new ArrayList<String>();
        actions.add("Rent");
        actions.add("Sell");

        Spinner actionsSpinner = (Spinner) findViewById(R.id.action_spinner);
        ArrayAdapter<String> actionsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, actions);
        actionsSpinner.setAdapter(actionsAdapter);
        EditText address = (EditText) findViewById(R.id.Address_EditText);
        Button publish=(Button)findViewById(R.id.publish_button);

        if (item != null) {
            //Translate the Location sent from the MainActivity to an address
            ArrayList<String> Add = fromLocToAddress(new LatLng(item.getLatitude(), item.getLongitude()));
            String stringCountry = Add.get(1);
            String stringAddress = Add.get(0);
            address.setText(stringAddress);
            countrySpinner.setSelection(countryAdapter.getPosition(stringCountry));
            //set the attributes of the item-proprietary object
            item.setAddress(stringAddress);
            item.setCountry(stringCountry);
        } else {
            //get the location of the item by translating address and country entered by the user to location
            String stringAddress = countrySpinner.getSelectedItem().toString() + ", " + address.getText();
            LatLng LatLnglocation = getLocationFromAddress(stringAddress);
            item.setLatitude(LatLnglocation.latitude);
            item.setLongitude(LatLnglocation.longitude);
            item.setCountry(countrySpinner.getSelectedItem().toString());
            item.setAddress(address.getText().toString());
            //Get all the fields to put them in the db


        }
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            AddingAsset addedItem=new AddingAsset();
                addedItem.execute();
                Toast.makeText(getApplicationContext(),"HERRREEEE",Toast.LENGTH_LONG).show();
            }
        });
    }

    //A function that converts the recieved latLng location to the address
    public ArrayList<String> fromLocToAddress(LatLng loc) {
        // LatLng loc=p.getMapLocation();
        ArrayList<String> addresses = new ArrayList<String>();
        Geocoder geo = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> Addresses = geo.getFromLocation(loc.latitude, loc.longitude, 1);
            String street = Addresses.get(0).getAddressLine(0) + "," + Addresses.get(0).getAddressLine(1);
            String country = Addresses.get(0).getCountryName();
            addresses.add(street);
            addresses.add(country);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return addresses;
    }

    public LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1 = null;


        try {
            address = coder.getFromLocationName(strAddress, 5);
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();
            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (IOException e) {
            e.printStackTrace();
        }


        return p1;

    }

    class AddingAsset extends AsyncTask<Void, Void, Void> {
       // private String urlPath = "";
      //  String urlPath=" http:// 192.168.1.2:49199/client/addItem";
    //   String urlPath="https://www.facebook.com";
       String urlPath="http://localhost:8080/client/addItem";

        //urlPath="...../client/addItem"
        @Override
        protected Void doInBackground(Void... params) {
            if (isNetworkAvailable()) {
                if (isOnline()) {
                    LatLng location=new LatLng(item.getLatitude(),item.getLongitude());
                   URL url = null;
                    try {
                        url = new URL(urlPath);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    HttpURLConnection urlConnection = null;
                    try {
                        urlConnection = (HttpURLConnection) url.openConnection();
               //         urlConnection.connect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        urlConnection.setRequestMethod("POST");
                    } catch (ProtocolException e) {
                        e.printStackTrace();
                    }
                    urlConnection.setRequestProperty("Address", item.getAddress());
                    urlConnection.setRequestProperty("Country", item.getCountry());
                    //Doha?How should the location be saved here
                    urlConnection.setRequestProperty("Location", location.toString());
                    urlConnection.setRequestProperty("price", String.valueOf(item.getPrice()));
                    //Doha?I want to add the owner of the item also
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);

                    try {
                        urlConnection.connect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid){

            Toast.makeText(getApplicationContext(),"ON post execute",Toast.LENGTH_LONG).show();
        }
    }

    //A function implemented to check Internet Connectivity
    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

}