package wily.apps.watchrabbit.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Single;
import wily.apps.watchrabbit.data.entity.Habbit;

@Dao
public interface HabbitDao {

    // 1.1) Get List
    @Query("SELECT * FROM Habbit")
    List<Habbit> getAll();

    @Query("SELECT * FROM Habbit")
    Single<List<Habbit>> getAllSingle();

    @Query("SELECT * FROM Habbit WHERE active=:p_active")
    Single<List<Habbit>> getHabbitActiveSingle(boolean p_active);

    // 1.2) Get
    @Query("SELECT * FROM Habbit WHERE id=:p_id")
    List<Habbit> getHabbit(int p_id);

    @Query("SELECT * FROM Habbit WHERE id=:p_id")
    Single<List<Habbit>> getHabbitSingle(int p_id);

    // 1.3) Get attr
    @Query("SELECT state FROM Habbit WHERE id=:p_id")
    int getHabbitState(int p_id);

    @Query("SELECT curRecordId FROM Habbit WHERE id=:p_id")
    long getCurRecordId(int p_id);

    // 2) Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Habbit habbit);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(List<Habbit> habbits);

    // 3) Update
    @Query("UPDATE Habbit SET type=:p_type, title=:p_title, priority=:p_prio, active=:p_act, goalCost=:p_goalCost, initCost=:p_initCost, perCost=:p_perCost WHERE id = :p_id")
    int updateHabbit(int p_id, int p_type, String p_title, int p_prio, boolean p_act, int p_goalCost, int p_initCost, int p_perCost);

    @Query("UPDATE Habbit SET state=:p_state WHERE id = :p_id")
    int updateHabbitState(int p_id, int p_state);

    @Query("UPDATE Habbit SET curRecordId=:p_recordId WHERE id = :p_id")
    int updateCurRecordId(int p_id, long p_recordId);

    @Query("UPDATE Habbit SET currentResultCost=:p_currentResultCost, currentAchiveRate=:p_currentAchiveRate, day7ResultCost=:p_day7ResultCost, day7AchiveRate=:p_day7AchiveRate, day30ResultCost=:p_day30ResultCost, day30AchiveRate=:p_day30AchiveRate WHERE id = :p_id")
    int updateHabbitEvaluation(int p_id, long p_currentResultCost, long p_currentAchiveRate, long p_day7ResultCost, long p_day7AchiveRate, long p_day30ResultCost, long p_day30AchiveRate);

    // 4) Delete
    @Query("DELETE FROM Habbit WHERE id IN (:p_ids)")
    int deleteHabbitByIds(List<Integer> p_ids);
}
