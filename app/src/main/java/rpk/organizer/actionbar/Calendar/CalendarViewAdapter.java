package rpk.organizer.actionbar.Calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import rpk.organizer.actionbar.R;


public class CalendarViewAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mListCalendarNames;

    public CalendarViewAdapter(Context mContext, List<String> mListCalendarNames) {
        this.mContext = mContext;
        this.mListCalendarNames = mListCalendarNames;
    }


    @Override
    public int getCount() {
        return mListCalendarNames.size();
    }

    @Override
    public Object getItem(int position) {
        return mListCalendarNames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String wpis = mListCalendarNames.get(position);

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.event_view, null);
        }

        TextView godzina = (TextView)convertView.findViewById(R.id.Godzina);
        godzina.setText(wpis);

        return convertView;
    }
}
