package rpk.organizer.actionbar.Utils;

import java.util.ArrayList;
import java.util.List;

import rpk.organizer.actionbar.EventInfo;


public class EventList {
    private static List<EventInfo> Events;

    public static void Clear(){
        Events.clear();
    }

    public static void addEvent(EventInfo event){
        if(Events == null)
            Events = new ArrayList<>();
        Events.add(event);
    }

    public static List<EventInfo> getEvents(){
        if(Events == null)
            return new ArrayList<>();
        return Events;
    }
}
