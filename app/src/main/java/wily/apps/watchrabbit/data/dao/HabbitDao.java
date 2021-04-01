package wily.apps.watchrabbit.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Single;
import wily.apps.watchrabbit.data.entity.Habbit;
import wily.apps.watchrabbit.data.entity.Record;

@Dao
public interface HabbitDao {
    @Query("SELECT * FROM Habbit")
    Single<List<Habbit>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long> insert(Habbit habbit);

    @Query("SELECT * FROM Habbit WHERE id=:p_id")
    Single<List<Habbit>> getHabbit(int p_id);

    @Query("UPDATE Habbit SET type=:p_type, title=:p_title, priority=:p_prio, active=:p_act, goalCost=:p_goalCost, initCost=:p_initCost, perCost=:p_perCost WHERE id = :p_id")
    Single<Integer> updateHabbit(int p_id, int p_type, String p_title, int p_prio, boolean p_act, int p_goalCost, int p_initCost, int p_perCost);

    @Query("SELECT state FROM Habbit WHERE id=:p_id")
    Single<Integer> getHabbitState(int p_id);

    @Query("UPDATE Habbit SET state=:p_state WHERE id = :p_id")
    Single<Integer> updateHabbitState(int p_id, int p_state);

    @Query("UPDATE Habbit SET curRecordId=:p_recordId WHERE id = :p_id")
    Single<Integer> updateCurRecordId(int p_id, long p_recordId);

    @Query("SELECT curRecordId FROM Habbit WHERE id=:p_id")
    Single<Integer> getCurRecordId(int p_id);

    @Query("DELETE FROM Habbit WHERE id IN (:p_ids)")
    Single<Integer> deleteHabbitByIds(List<Integer> p_ids);

    @Query("SELECT * FROM Habbit WHERE active=:p_active")
    Single<List<Habbit>> getHabbitActive(boolean p_active);


    //
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<List<Long>> insertAll(List<Habbit> habbits);
}
