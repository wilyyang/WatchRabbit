package wily.apps.watchrabbit.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import wily.apps.watchrabbit.data.entity.Alarm;

@Dao
public interface AlarmDao {

    // 1.1) Get List
    @Query("SELECT * FROM Alarm")
    List<Alarm> getAll();

    @Query("SELECT * FROM Alarm WHERE hid=:p_hid ORDER BY time ASC")
    List<Alarm> getAlarmByHid(int p_hid);

    @Query("SELECT * FROM Alarm WHERE hid=:p_hid AND time BETWEEN :p_start AND :p_end ORDER BY time ASC")
    List<Alarm> getAlarmByHidAndTerm(int p_hid, long p_start, long p_end);

    // 1.2) Get
    @Query("SELECT * FROM Alarm WHERE id=:p_id")
    List<Alarm> getAlarm(long p_id);

    // 1.3) Get attr

    // 2) Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Alarm alarm);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(List<Alarm> alarms);

    // 3) Update
    @Query("UPDATE Alarm SET title=:p_title, time=:p_time, range=:p_range, cost=:p_cost WHERE id = :p_id")
    int updateAlarm(int p_id, String p_title, long p_time, long p_range, int p_cost);

    // 4) Delete
    @Query("DELETE FROM Alarm WHERE id IN (:p_ids)")
    int deleteAlarmByIds(List<Long> p_ids);

    @Query("DELETE FROM Alarm WHERE hid IN (:p_hids)")
    int deleteAlarmByHids(List<Integer> p_hids);
}
