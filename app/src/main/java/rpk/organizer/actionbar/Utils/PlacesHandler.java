package rpk.organizer.actionbar.Utils;

import java.util.ArrayList;
import java.util.List;

import rpk.organizer.actionbar.MyPlaces.Place;



public class PlacesHandler {
    private static List<Place> Places;
    private static int iter=0;
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
}
