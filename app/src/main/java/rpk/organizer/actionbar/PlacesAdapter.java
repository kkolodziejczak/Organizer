package rpk.organizer.actionbar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;



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
        TextView godzina = (TextView)convertView.findViewById(R.id.Place);
        godzina.setText(wpis.getName());

        TextView Opis = (TextView)convertView.findViewById(R.id.Time);
        Opis.setText(wpis.getTime());

        return convertView;
    }
}