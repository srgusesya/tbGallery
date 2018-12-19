package com.example.sitirahzanagusesya.tbgallery.model;

import android.os.Parcel;
import android.os.Parcelable;

public class GalleryItem implements Parcelable {
    int id;
    String name;
    String location;
    double latitude;
    double longitude;
    String photo;
    String description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public GalleryItem(int id, String name, String location, double latitude, double longitude, String photo, String description) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photo = photo;
        this.description = description;
    }

    public GalleryItem() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.location);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.photo);
        dest.writeString(this.description);
    }

    protected GalleryItem(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.location = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.photo = in.readString();
        this.description = in.readString();
    }

    public static final Creator<GalleryItem> CREATOR = new Creator<GalleryItem>() {
        @Override
        public GalleryItem createFromParcel(Parcel source) {
            return new GalleryItem(source);
        }

        @Override
        public GalleryItem[] newArray(int size) {
            return new GalleryItem[size];
        }
    };
}
