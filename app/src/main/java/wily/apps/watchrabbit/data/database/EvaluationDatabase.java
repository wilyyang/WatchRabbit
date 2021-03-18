package wily.apps.watchrabbit.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import wily.apps.watchrabbit.data.dao.EvaluationDao;
import wily.apps.watchrabbit.data.entity.Evaluation;

@Database(entities = {Evaluation.class}, version =  1)
public abstract class EvaluationDatabase extends RoomDatabase {
    public abstract EvaluationDao evaluationDao();

    private static EvaluationDatabase INSTANCE = null;
    private static String DB_NAME = "EVALUATION_DB";

    public static EvaluationDatabase getAppDatabase(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context, EvaluationDatabase.class , DB_NAME).build();
        }
        return  INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}