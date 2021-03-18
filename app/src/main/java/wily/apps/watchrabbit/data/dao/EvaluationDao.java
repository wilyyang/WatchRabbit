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
    @Query("SELECT * FROM Evaluation")
    Single<List<Evaluation>> getAll();

    @Query("SELECT * FROM Evaluation WHERE id=:p_id")
    Single<List<Evaluation>> getEvaluation(int p_id);

    @Query("SELECT * FROM Evaluation WHERE hid=:p_hid")
    Single<List<Evaluation>> getEvaluationByHid(int p_hid);

    @Query("DELETE FROM Evaluation WHERE id IN (:p_ids)")
    Single<Integer> deleteEvaluationByIds(List<Integer> p_ids);

    @Query("DELETE FROM Evaluation WHERE hid IN (:p_hids)")
    Single<Integer> deleteEvaluationByHids(List<Integer> p_hids);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long> insert(Evaluation eval);
}
