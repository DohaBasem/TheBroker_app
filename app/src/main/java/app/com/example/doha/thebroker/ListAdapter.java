package app.com.example.doha.thebroker;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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

        TextView country=(TextView)itemView.findViewById(R.id.CountryText);
        TextView Address=(TextView)itemView.findViewById(R.id.AddressText);
        ImageView icon=(ImageView)itemView.findViewById(R.id.home_icon);
        Drawable home=AdapterContext.getResources().getDrawable(R.drawable.ic_action_name);
        icon.setImageDrawable(home);
        country.setText(Assets.get(position).getCountry());
        Address.setText(Assets.get(position).getAddress());
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
