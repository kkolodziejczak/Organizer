package rpk.organizer.actionbar.Calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import rpk.organizer.actionbar.R;

class EventAdapter extends BaseAdapter {
    private Context mContext;
    private List<EventInfo> mListEvents;

    EventAdapter(Context context, List<EventInfo> list){
        this.mContext = context;
        this.mListEvents = list;
    }

    @Override
    public int getCount() {
        return mListEvents.size();
    }

    @Override
    public Object getItem(int position) {
        return mListEvents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EventInfo wpis = mListEvents.get(position);

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.event_view, null);
        }
        TextView godzina = (TextView)convertView.findViewById(R.id.Godzina);
        godzina.setText(wpis.getTime());

        TextView Opis = (TextView)convertView.findViewById(R.id.InformacjeOEvencie);
        Opis.setText(wpis.getInfo());

        return convertView;
    }
}
