package wily.apps.watchrabbit.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import wily.apps.watchrabbit.data.dao.AlarmDao;
import wily.apps.watchrabbit.data.entity.Alarm;

@Database(entities = {Alarm.class}, version =  1)
public abstract class AlarmDatabase extends RoomDatabase {
    public abstract AlarmDao alarmDao();

    private static AlarmDatabase INSTANCE = null;
    private static String DB_NAME = "ALARM_DB";

    public static AlarmDatabase getAppDatabase(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context, AlarmDatabase.class , DB_NAME).build();
        }
        return  INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}