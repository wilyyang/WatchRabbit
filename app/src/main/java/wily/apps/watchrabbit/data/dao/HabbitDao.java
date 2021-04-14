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
    Single<List<Habbit>> getAllSingle();

    @Query("SELECT * FROM Habbit")
    List<Habbit> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Habbit habbit);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long> insertSingle(Habbit habbit);

    @Query("SELECT * FROM Habbit WHERE id=:p_id")
    List<Habbit> getHabbit(int p_id);

    @Query("SELECT * FROM Habbit WHERE id=:p_id")
    Single<List<Habbit>> getHabbitSingle(int p_id);

    @Query("UPDATE Habbit SET type=:p_type, title=:p_title, priority=:p_prio, active=:p_act, goalCost=:p_goalCost, initCost=:p_initCost, perCost=:p_perCost WHERE id = :p_id")
    int updateHabbit(int p_id, int p_type, String p_title, int p_prio, boolean p_act, int p_goalCost, int p_initCost, int p_perCost);

    @Query("SELECT state FROM Habbit WHERE id=:p_id")
    Single<Integer> getHabbitState(int p_id);

    @Query("UPDATE Habbit SET state=:p_state WHERE id = :p_id")
    Single<Integer> updateHabbitState(int p_id, int p_state);

    @Query("UPDATE Habbit SET curRecordId=:p_recordId WHERE id = :p_id")
    Single<Integer> updateCurRecordId(int p_id, long p_recordId);

    @Query("UPDATE Habbit SET currentResultCost=:p_currentResultCost, currentAchiveRate=:p_currentAchiveRate, day7ResultCost=:p_day7ResultCost, day7AchiveRate=:p_day7AchiveRate, day30ResultCost=:p_day30ResultCost, day30AchiveRate=:p_day30AchiveRate WHERE id = :p_id")
    int updateHabbitEvaluation(int p_id, long p_currentResultCost, long p_currentAchiveRate, long p_day7ResultCost, long p_day7AchiveRate, long p_day30ResultCost, long p_day30AchiveRate);

    @Query("UPDATE Habbit SET currentResultCost=:p_currentResultCost, currentAchiveRate=:p_currentAchiveRate WHERE id = :p_id")
    int updateHabbitEvaluationToday(int p_id, long p_currentResultCost, long p_currentAchiveRate);

    @Query("SELECT curRecordId FROM Habbit WHERE id=:p_id")
    Single<Integer> getCurRecordId(int p_id);

    @Query("DELETE FROM Habbit WHERE id IN (:p_ids)")
    int deleteHabbitByIds(List<Integer> p_ids);

    @Query("SELECT * FROM Habbit WHERE active=:p_active")
    Single<List<Habbit>> getHabbitActive(boolean p_active);

    //
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<List<Long>> insertAllSingle(List<Habbit> habbits);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(List<Habbit> habbits);
}
