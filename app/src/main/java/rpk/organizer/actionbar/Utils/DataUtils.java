package rpk.organizer.actionbar.Utils;

import com.google.api.client.util.DateTime;

public class DataUtils {

    static public String toHourMin(DateTime date, String separator){
        String out[] = date.toStringRfc3339().split("T");
        String out2[] = out[1].split(":");
        return out2[0]+ separator + out2[1];
    }
    static public DateTime addDay(DateTime date,int howMany){
        String out [] = date.toStringRfc3339().split("-");
//        2017-05-07T16:25:52.721Z

//        String.format("%02d", Integer.parseInt(out[2]))
        return new DateTime(out[0]);
    }
}
