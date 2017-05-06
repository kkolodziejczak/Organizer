package rpk.organizer.actionbar.Calendar;

public class EventInfo {
    private String who;
    private String what;
    private String where;
    private String time;

        EventInfo(String _what,String time){
            this.what = _what;
            this.time = time;
        }
        EventInfo(String _who, String _what, String _where, String time){
            this.who = _who;
            this.what = _what;
            this.where = _where;
            this.time = time;
        }

    public String getTime() {
        return time;
    }

    public String getInfo() {
        return what;
    }
}
