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

    // 1.1) Get List
    @Query("SELECT * FROM Record")
    List<Record> getAll();

    @Query("SELECT * FROM Record WHERE hid=:p_hid AND time BETWEEN :p_start AND :p_end ORDER BY time ASC")
    List<Record> getRecordByHidAndTime(int p_hid, long p_start, long p_end);

    // 1.2) Get
    @Query("SELECT * FROM Record WHERE id=:p_id")
    List<Record> getRecord(long p_id);

    // 1.3) Get attr

    // 2) Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Record record);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(List<Record> records);

    // 3) Update
    @Query("UPDATE Record SET time=:p_time WHERE id = :p_id")
    int updateTime(long p_id, long p_time);

    @Query("UPDATE Record SET term=:p_term WHERE id = :p_id")
    int updateTerm(long p_id, long p_term);

    @Query("UPDATE Record SET time=:p_time, term=:p_term WHERE id = :p_id")
    int updateTimeAndTerm(long p_id, long p_time, long p_term);

    // 4) Delete
    @Query("DELETE FROM Record WHERE id IN (:p_ids)")
    int deleteRecordByIds(List<Long> p_ids);

    @Query("DELETE FROM Record WHERE hid IN (:p_hids)")
    int deleteRecordByHids(List<Integer> p_hids);
}
