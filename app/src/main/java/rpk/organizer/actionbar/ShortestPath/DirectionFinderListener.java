package rpk.organizer.actionbar.ShortestPath;

import com.google.android.gms.maps.model.LatLng;
import com.google.api.services.calendar.model.Event;

import java.util.ArrayList;
import java.util.List;

import rpk.organizer.actionbar.Calendar.EventsInfo;

/**
 * Created by Mai Thanh Hiep on 4/3/2016.
 */
public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
    void onDirectionFinderSuccess(List<Route> routes, List<Event> list);
}