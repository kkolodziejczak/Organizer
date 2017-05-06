package rpk.organizer.actionbar;

public class Place {
    private String Name;
    private String Time;

    public Place(String name, String time) {
        Name = name;
        Time = time;
    }

    public String getName() {
        return Name;
    }

    public String getTime() {
        return Time;
    }
}
