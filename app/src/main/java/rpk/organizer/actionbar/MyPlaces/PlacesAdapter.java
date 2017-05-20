package rpk.organizer.actionbar.MyPlaces;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import rpk.organizer.actionbar.MyPlaces.Place;
import rpk.organizer.actionbar.R;


public class PlacesAdapter extends BaseAdapter {
    private List<Place> placesList;
    private Context mContext;

    public PlacesAdapter(List<Place> placesList, Context mContext) {
        this.placesList = placesList;
        this.mContext = mContext;
    }


    @Override
    public int getCount() {
       return placesList.size();
    }

    @Override
    public Object getItem(int position) {
        return placesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Place wpis = placesList.get(position);

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.place, null);
        }
        TextView placeName = (TextView)convertView.findViewById(R.id.Place);
        placeName.setMaxLines(1);
        placeName.setText(wpis.getName());

        TextView time = (TextView)convertView.findViewById(R.id.Time);
        time.setText(wpis.getTime());

        return convertView;
    }
}
