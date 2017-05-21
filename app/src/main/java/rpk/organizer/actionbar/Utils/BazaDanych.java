package rpk.organizer.actionbar.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import rpk.organizer.actionbar.MyPlaces.Place;

/**
 * Created by PL on 2017-05-21.
 */

public class BazaDanych extends SQLiteOpenHelper {
    private  static final int DATABASE_VERSION =1;
    public BazaDanych(Context context){
        super(context,"PlacesDB",null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String DATABASE_CREATE = "Create table places"+"(id integer primary key autoincrement,"+"nazwa text not null, "+"adres text not null);";
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS places");
        onCreate(db);
    }
    public void dodaj(Place place){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values =new ContentValues();
        values.put("nazwa", place.getName());
        values.put("adres", place.getPosition());
        db.insert("places",null,values);
        db.close();
    }
    public void usun(String nazwa) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("places", "nazwa = ?",
                new String[] { nazwa });
        db.close();
    }
    public void loadAllPlacess() {
        // Select All Query
        String selectQuery = "SELECT  * FROM places" ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PlacesHandler.addPlace(new Place(cursor.getString(1), cursor.getString(2), "0:00"));
            } while (cursor.moveToNext());
        }
    }


}
