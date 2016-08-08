package app.com.example.doha.thebroker;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class SimpleSearch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_search);
    ArrayList<proprietary> P=new ArrayList<proprietary>();
       proprietary p1=new proprietary();
        p1.setCountry("Egypt");
        p1.setAddress("Rehab");
        proprietary p2=new proprietary();
        p2.setCountry("KSA");
        p2.setAddress("Olia");
       P.add(p1);
       P.add(p2);
        MakeListAdapter(P);

    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.simple_search, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchBox = (SearchView) MenuItemCompat.getActionView(searchItem);
        //onQueryTextListener detects two events as shown in the code below
        searchBox.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
             //   Toast.makeText(getApplicationContext(),"Text Submitted",Toast.LENGTH_LONG).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
             //   Toast.makeText(getApplicationContext(),newText,Toast.LENGTH_LONG).show();
                return false;
            }
        });
        return true;
    }
public class fetchAssets extends AsyncTask<String,Void,Void>{
String urlPath="http://brokerserver-doha.rhcloud.com/client/addItem";
    public ArrayList<proprietary> Assets=new ArrayList<proprietary>();
    @Override
    protected Void doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String AssetsDataJsonStr = null;
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
        //As I am getting from the server,then GET method is used
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
            // Nothing to do.
            Log.d("NULL_INPUTSTREAM","inputStream is Null");
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
            // Stream was empty.  No point in parsing.
            Log.d("EMPTY_BUFFER","buffer length is zero");
        }
        AssetsDataJsonStr = buffer.toString();

        return null;
    }
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    if(Assets!=null){

        MakeListAdapter(Assets);
    }
    }
}
    public void MakeListAdapter(ArrayList<proprietary> P){
        final ListAdapter adapter=new ListAdapter(getApplicationContext(),R.layout.search_item,P);

        adapter.notifyDataSetChanged();
        ListView resultList=(ListView)findViewById(R.id.myList);
        resultList.setAdapter(adapter);
    }
}
