package com.example.sitirahzanagusesya.tbgallery.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface GalleryDao {

    @Query("SELECT * FROM gallery2")
    List<Gallery> getAllGallery();

    @Query("SELECT * FROM gallery2 WHERE id = :id")
    Gallery getById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGallery(Gallery gallery);

//    @Delete
//    void delete(Gallery gallery);
}
