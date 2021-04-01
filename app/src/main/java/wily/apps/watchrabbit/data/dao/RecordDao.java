package wily.apps.watchrabbit.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Single;
import wily.apps.watchrabbit.data.entity.Evaluation;
import wily.apps.watchrabbit.data.entity.Habbit;
import wily.apps.watchrabbit.data.entity.Record;

@Dao
public interface RecordDao {
    @Query("SELECT * FROM Record")
    Single<List<Record>> getAll();

    @Query("SELECT * FROM Record WHERE id=:p_id")
    Single<List<Record>> getRecord(int p_id);

    @Query("DELETE FROM Record WHERE id IN (:p_ids)")
    Single<Integer> deleteRecordByIds(List<Long> p_ids);

    @Query("DELETE FROM Record WHERE hid IN (:p_hids)")
    Single<Integer> deleteRecordByHids(List<Integer> p_hids);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long> insert(Record record);

    @Query("UPDATE Record SET time=:p_time WHERE id = :p_id")
    Single<Integer> updateTime(long p_id, long p_time);

    @Query("UPDATE Record SET time=:p_time, term=:p_term WHERE id = :p_id")
    Single<Integer> updateTimeAndTerm(long p_id, long p_time, long p_term);

    @Query("UPDATE Record SET term=:p_term WHERE id = :p_id")
    Single<Integer> updateTerm(long p_id, long p_term);

    @Query("SELECT * FROM Record WHERE hid=:p_hid AND time BETWEEN :p_start AND :p_end ORDER BY time ASC")
    List<Record> getRecordByHidAndTime(int p_hid, long p_start, long p_end);

    // <tempo>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<List<Long>> insertAll(List<Record> records);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAllSync(List<Record> records);
}
