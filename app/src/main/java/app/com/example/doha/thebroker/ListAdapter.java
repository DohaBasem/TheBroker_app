package app.com.example.doha.thebroker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by DOHA on 02/08/2016.
 */
public class ListAdapter extends ArrayAdapter {
    ArrayList<proprietary>Assets;
    Context AdapterContext;
    LayoutInflater inflater;
    public ListAdapter(Context context, int resource, ArrayList objects) {
        super(context, resource, objects);
        this.AdapterContext=context;
        this.Assets=objects;
        inflater= LayoutInflater.from(AdapterContext);

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView=convertView;


        if(itemView==null) //No recycled view ,so I will create my own view

            itemView=inflater.inflate(R.layout.search_item,null);


       //Set the items
        return itemView;
    }
    @Override
    public int getCount() {

        //int count =this.ImgUrls.size();
        int count =this.Assets.size();
        return count;
    }
}
