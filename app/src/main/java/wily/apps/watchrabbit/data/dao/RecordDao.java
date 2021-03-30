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
public interface RecordDao {
    @Query("SELECT * FROM Record")
    Single<List<Record>> getAll();

    @Query("SELECT * FROM Record  WHERE hid = :p_hid AND time >= :p_start AND time < :p_stop AND state !="+ Habbit.STATE_TIMER_INPROGRESS)
    Single<List<Record>> getRecordByHidDateNotStop(int p_hid, long p_start, long p_stop);

    @Query("SELECT * FROM Record WHERE state != "+Habbit.STATE_TIMER_INPROGRESS)
    Single<List<Record>> getStartRecords();

    @Query("SELECT * FROM Record WHERE state = "+Habbit.STATE_TIMER_INPROGRESS)
    Single<List<Record>> getStopRecords();

    @Query("UPDATE Record SET time=:p_time WHERE id = :p_id")
    Single<Integer> updateTime(long p_id, long p_time);

    @Query("UPDATE Record SET time=:p_time WHERE pair = :p_pair")
    Single<Integer> updateTimePair(long p_pair, long p_time);

    @Query("DELETE FROM Record WHERE id IN (:p_ids)")
    Single<Integer> deleteRecordByIds(List<Long> p_ids);

    @Query("DELETE FROM Record WHERE hid IN (:p_hids)")
    Single<Integer> deleteRecordByHids(List<Integer> p_hids);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long> insert(Record work);
}
