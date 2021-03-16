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
import io.reactivex.Single;
import wily.apps.watchrabbit.data.entity.Habbit;

@Dao
public interface HabbitDao {
    @Query("SELECT * FROM Habbit")
    Single<List<Habbit>> getAll();

    @Query("SELECT * FROM Habbit WHERE active=:p_active")
    Single<List<Habbit>> getHabbitActive(boolean p_active);

    @Query("SELECT * FROM Habbit WHERE id=:p_id")
    Single<List<Habbit>> getHabbit(int p_id);

    @Query("UPDATE Habbit SET type=:p_type, title=:p_title, priority=:p_prio, active=:p_act, goalCost=:p_goalCost, initCost=:p_initCost, perCost=:p_perCost WHERE id = :p_id")
    Single<Integer> updateHabbit(int p_id, int p_type, String p_title, int p_prio, boolean p_act, int p_goalCost, int p_initCost, int p_perCost);

    @Query("DELETE FROM Habbit WHERE id IN (:p_ids)")
    Single<Integer> deleteHabbitByIds(List<Integer> p_ids);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long> insert(Habbit work);
}
