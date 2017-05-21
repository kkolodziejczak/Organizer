package rpk.organizer.actionbar.MyPlaces;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import rpk.organizer.actionbar.MyPlaces.Place;
import rpk.organizer.actionbar.R;
import rpk.organizer.actionbar.Utils.PlacesHandler;


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
    public View getView(final int position, View convertView, final ViewGroup parent) {
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

        ImageView image =(ImageView)convertView.findViewById(R.id.DeleteView);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(mContext);
                dialog.setContentView(R.layout.dialog_delete);
                dialog.setTitle("Delete");
                Button dialogButtonCancel = (Button) dialog.findViewById(R.id.customDialogCancel);
                Button dialogButtonOk = (Button) dialog.findViewById(R.id.customDialogOk);
                dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialogButtonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        placesList.remove(position);
                        dialog.dismiss();
                        notifyDataSetChanged();
                    }
                });

                dialog.show();
            }
        });
        return convertView;
    }
}
