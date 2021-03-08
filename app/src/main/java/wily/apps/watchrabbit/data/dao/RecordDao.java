package wily.apps.watchrabbit.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import wily.apps.watchrabbit.data.entity.Record;

@Dao
public interface RecordDao {
    @Query("SELECT * FROM Record")
    Single<List<Record>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long> insert(Record work);

    @Query("DELETE FROM Record WHERE id IN (:ids)")
    Single<Integer> deleteItemByIds(List<Long> ids);

    @Query("DELETE FROM Record WHERE hid=:p_hid")
    Single<Integer> deleteItemByHid(int p_hid);

    @Query("UPDATE Record SET time=:p_time WHERE id = :p_id")
    Single<Integer> updateTime(long p_id, long p_time);

    @Query("SELECT * FROM Record WHERE id=:p_id")
    Single<List<Record>> getRecord(long p_id);

    @Query("SELECT time FROM Record WHERE pair=:p_pair AND state = 2001")
    Single<List<Long>> getStartTime(long p_pair);

    @Query("SELECT time FROM Record WHERE pair=:p_pair AND state = 2002 ")
    Single<List<Long>> getStopTime(long p_pair);

    @Query("SELECT * FROM Record WHERE time>=:start AND time<=:stop")
    Single<List<Record>> getTermRecords(long start, long stop);

    @Update
    Completable update(Record work);

    @Delete
    Completable delete(Record work);

    @Query("DELETE FROM Record")
    Completable deleteAll();
}
