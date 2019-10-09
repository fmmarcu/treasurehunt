package com.threess.summership.treasurehunt.model.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.threess.summership.treasurehunt.model.Treasure;

import java.util.List;

@Dao
public interface TreasureDao {

    @Insert
    void insert(Treasure treasure);

    @Update
    void update(Treasure treasure);

    @Delete
    void delete(Treasure treasure);

    @Query("DELETE FROM treasure_table")
    void deleteAllTreasures();

    @Query("SELECT * FROM treasure_table")
    List<Treasure> getAllTreasures();

}
