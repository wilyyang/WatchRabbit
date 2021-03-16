package wily.apps.watchrabbit.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import wily.apps.watchrabbit.data.dao.RecordDao;
import wily.apps.watchrabbit.data.entity.Record;

@Database(entities = {Record.class}, version =  1)
public abstract class RecordDatabase extends RoomDatabase {
    public abstract RecordDao recordDao();

    private static RecordDatabase INSTANCE = null;
    private static String DB_NAME = "RECORD_DB";

    public static RecordDatabase getAppDatabase(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context, RecordDatabase.class , DB_NAME).build();
        }
        return  INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}