package wily.apps.watchrabbit.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Single;
import wily.apps.watchrabbit.data.entity.Evaluation;

@Dao
public interface EvaluationDao {

    // 1.1) Get List
    @Query("SELECT * FROM Evaluation")
    List<Evaluation> getAll();

    @Query("SELECT * FROM Evaluation WHERE hid=:p_hid")
    List<Evaluation> getEvaluationByHid(int p_hid);

    @Query("SELECT * FROM Evaluation WHERE hid=:p_hid AND time BETWEEN :p_start AND :p_end ORDER BY time ASC")
    List<Evaluation> getEvaluationByHidAndTerm(int p_hid, long p_start, long p_end);

    @Query("SELECT * FROM Evaluation WHERE hid=:p_hid AND time BETWEEN :p_start AND :p_end ORDER BY time DESC")
    List<Evaluation> getEvaluationByHidAndTermDESC(int p_hid, long p_start, long p_end);

    // 1.2) Get
    @Query("SELECT * FROM Evaluation WHERE id=:p_id")
    List<Evaluation> getEvaluation(long p_id);

    @Query("SELECT * FROM Evaluation WHERE hid=:p_hid AND time =:p_time")
    List<Evaluation> getEvaluationByTime(int p_hid, long p_time);

    // 1.3) Get attr

    // 2) Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(Evaluation eval);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(List<Evaluation> evals);

    // 3) Update

    // 4) Delete
    @Query("DELETE FROM Evaluation WHERE hid IN (:p_hids)")
    int deleteEvaluationByHids(List<Integer> p_hids);

    @Query("DELETE FROM Evaluation WHERE hid=:p_hid AND time =:p_time")
    int deleteEvaluationByTerm(int p_hid, long p_time);

    @Query("DELETE FROM Evaluation WHERE hid=:p_hid AND time BETWEEN :p_start AND :p_end")
    int deleteEvaluationByTerm(int p_hid, long p_start, long p_end);
}
