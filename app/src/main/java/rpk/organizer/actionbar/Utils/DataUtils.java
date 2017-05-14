package rpk.organizer.actionbar.Utils;

import com.google.api.client.util.DateTime;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DataUtils {

    static public String toHourMin(DateTime date, String separator){
        String out[] = date.toStringRfc3339().split("T");
        String out2[] = out[1].split(":");
        return out2[0]+ separator + out2[1];
    }
    static public String toHourMin(DateTime date){
        return toHourMin(date,":");
    }

    public static String toDayMonthYear(DateTime date) {
        org.joda.time.DateTime dt = new org.joda.time.DateTime(date.toString());
        DateTimeFormatter fmt = DateTimeFormat.forPattern("E, dd MMMM yyyy");
        return fmt.print(dt);

    }
}
