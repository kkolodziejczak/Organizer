package rpk.organizer.actionbar.MyPlaces;

import java.io.Serializable;

public class Place implements Serializable {
    private String Name;
    private String Position;
    private String Time;

    public Place(String name, String time) {
        Name = name;
        Time = time;
        Position="zut rciitt";
    }
    public Place(String name,String position,String time){
        Name=name;
        Position=position;
        Time=time;
    }

    public String getName() {
        return Name;
    }

    public String getTime() {
        return Time;
    }
    public void  setTime(String time){
        Time=time;
    }
    public String getPosition(){ return Position;}
}
