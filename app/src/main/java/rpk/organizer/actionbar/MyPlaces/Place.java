package rpk.organizer.actionbar.MyPlaces;

public class Place {
    private String Name;
    private String Position;
    private String Time;

    public Place(String name, String time) {
        Name = name;
        Time = time;
        Position="Police Kinga";
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
    public String getPosition(){ return Position;}
}
