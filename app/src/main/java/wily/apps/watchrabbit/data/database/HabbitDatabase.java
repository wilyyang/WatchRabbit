package wily.apps.watchrabbit.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import wily.apps.watchrabbit.data.dao.HabbitDao;
import wily.apps.watchrabbit.data.entity.Habbit;

@Database(entities = {Habbit.class}, version =  1)
public abstract class HabbitDatabase extends RoomDatabase {
    public abstract HabbitDao habbitDao();

    private static HabbitDatabase INSTANCE = null;
    private static String DB_NAME = "HABBIT_DB";

    public static HabbitDatabase getAppDatabase(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context, HabbitDatabase.class , "habbit-db").build();
        }
        return  INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}