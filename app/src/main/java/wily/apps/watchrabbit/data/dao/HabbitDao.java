package wily.apps.watchrabbit.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import wily.apps.watchrabbit.data.entity.Habbit;

@Dao
public interface HabbitDao {
    @Query("SELECT * FROM Habbit")
    Flowable<List<Habbit>> getAll();

    @Query("SELECT * FROM Habbit")
    Flowable<List<Habbit>> getActiveHabbit();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(Habbit work);

    @Update
    Completable update(Habbit work);

    @Delete
    Completable delete(Habbit work);

    @Query("DELETE FROM Habbit")
    Completable deleteAll();
}
