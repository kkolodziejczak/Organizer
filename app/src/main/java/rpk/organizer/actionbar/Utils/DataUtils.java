package rpk.organizer.actionbar.Utils;

import com.google.api.client.util.DateTime;

public class DataUtils {

    static public String toHourMin(DateTime date, String separator){
        String out[] = date.toStringRfc3339().split("T");
        String out2[] = out[1].split(":");
        return out2[0]+ separator + out2[1];
    }
}
