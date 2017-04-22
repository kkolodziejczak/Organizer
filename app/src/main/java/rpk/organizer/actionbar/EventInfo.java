package rpk.organizer.actionbar;

/**
 * Created by Themo on 2017-04-22.
 */
public class EventInfo {
    private String who;
    private String what;
    private String where;
    private String time;

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
