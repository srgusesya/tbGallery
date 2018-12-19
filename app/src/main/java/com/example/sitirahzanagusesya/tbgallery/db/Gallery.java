package com.example.sitirahzanagusesya.tbgallery.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.example.sitirahzanagusesya.tbgallery.model.GalleryItem;

@Entity(tableName = "gallery2")
public class Gallery {

    @PrimaryKey
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "location")
    public String location;

    @ColumnInfo(name = "latitude")
    public Double latitude;

    @ColumnInfo(name = "longitude")
    public Double longitude;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "photo")
    public String photo;

    public void add(GalleryItem m) {
    }
}
