package rpk.organizer.actionbar.Utils;

import java.util.ArrayList;
import java.util.List;

import rpk.organizer.actionbar.Calendar.EventInfo;


public class EventList {
    private static List<EventInfo> Events;

    public static int Count(){
        if(Events == null)
            Events = new ArrayList<>();
        return Events.size();
    }

    public static void Clear(){
        if(Events == null)
            Events = new ArrayList<>();
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
