package rpk.organizer.actionbar.Utils;

import java.util.ArrayList;
import java.util.List;

import rpk.organizer.actionbar.MyPlaces.Place;
import rpk.organizer.actionbar.MyPlaces.PlacesAdapter;


public class PlacesHandler {
    private static List<Place> Places;
    private static int iter=0;
    private static PlacesAdapter adapter;
    public static void addPlace(Place place){
        if(Places == null)
            Places = new ArrayList<>();
        Places.add(place);
    }

    public static List<Place> getPlaces(){
        if(Places == null)
            return new ArrayList<>();
        return Places;
    }

    public static int getIter() {
        return iter;
    }

    public static void IterIncrement() {
        PlacesHandler.iter ++;
    }

    public static Place getPlace(int id){
        return Places.get(id);
    }
    public static void setAdapter(PlacesAdapter ad){
        adapter=ad;
    }
    public static PlacesAdapter getAdapter(){
        return adapter;
    }
}
