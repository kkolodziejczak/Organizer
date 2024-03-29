package rpk.organizer.actionbar.Utils;

import com.google.api.services.calendar.model.Event;

import java.util.ArrayList;
import java.util.List;

import rpk.organizer.actionbar.Calendar.EventsInfo;


public class EventList {
    private static List<EventsInfo> Events;
    private static List<Event> GoogleEvents;
    private static List<Event> TodaysEvents;
    private static int id = 0;

    public static int Count(){
        if(Events == null)
            Events = new ArrayList<>();
        return Events.size();
    }

    public static int GoogleCount(){
        if(GoogleEvents == null)
            GoogleEvents = new ArrayList<>();
        return GoogleEvents.size();
    }

    public static Event getEventAt(int position){
        if(GoogleEvents.size() >0)
            return GoogleEvents.get(position);
        return null;
    }

    public static void Clear(){
        if(Events == null)
            Events = new ArrayList<>();
        Events.clear();

        if(GoogleEvents == null)
            GoogleEvents = new ArrayList<>();
        GoogleEvents.clear();
    }

    public static void addEvent(EventsInfo event){
        if(Events == null)
            Events = new ArrayList<>();
        Events.add(event);
    }

    public static List<EventsInfo> getEvents(){
        if(Events == null)
            return new ArrayList<>();
        return Events;
    }

    public static void addGoogleEvent(Event event){
        if(GoogleEvents == null)
            GoogleEvents = new ArrayList<>();
        GoogleEvents.add(event);
    }

    public static List<Event> getGoogleEvents(){
        if(GoogleEvents == null)
            return new ArrayList<>();
        return GoogleEvents;
    }


    public static void setID(int i ){
        id = i;
    }

    public static void addEventToday(Event event){
        if(TodaysEvents == null)
            TodaysEvents = new ArrayList<>();
        TodaysEvents.add(event);
    }

    public static String getSummary() {
        return TodaysEvents.get(0).getSummary();
    }

    public static List<Event> getTodaysEvents(){
        if(TodaysEvents == null)
            return new ArrayList<>();
        return TodaysEvents;
    }

    public static void ClearToday() {
        if(TodaysEvents != null)
            TodaysEvents.clear();
    }

    public static int getTodaysEventsSize() {
        if(TodaysEvents == null)
            return 0;
        return TodaysEvents.size();
    }
}
