package rpk.organizer.actionbar.Utils;

import android.util.Log;

/**
 * Created by PL on 2017-05-26.
 */

public class BlockClickFlag {
    private static boolean flag=true;
    public static synchronized boolean getFlag(){
        return  flag;
    }
    public static synchronized void setFlagTrue(){
        flag=true;
        Log.i("true","true");
    }
    public static synchronized void setFlagFalse(){
        flag=false;
        Log.i("false","false");
    }



}
