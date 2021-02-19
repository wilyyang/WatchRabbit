package wily.apps.watchrabbit.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import wily.apps.watchrabbit.data.entity.Habbit;

@Dao
public interface HabbitDao {
    @Query("SELECT * FROM Habbit")
    Flowable<List<Habbit>> getAll();

    @Query("SELECT * FROM Habbit WHERE id=:p_id")
    Flowable<List<Habbit>> getHabbit(int p_id);

    @Query("UPDATE Habbit SET type=:p_type, title=:p_title, active=:p_act, goalCost=:p_goalCost, initCost=:p_initCost, perCost=:p_perCost WHERE id = :p_id")
    Completable updateHabbit(int p_id, int p_type, String p_title, boolean p_act, int p_goalCost, int p_initCost, int p_perCost);

//    @Query("SELECT * FROM Habbit")
//    Flowable<List<Habbit>> getActiveHabbit();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(Habbit work);

    @Update
    Completable update(Habbit work);

    @Delete
    Completable delete(Habbit work);

    @Query("DELETE FROM Habbit")
    Completable deleteAll();
}
