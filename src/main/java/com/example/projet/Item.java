package com.example.projet;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.ArrayMap;

import androidx.annotation.RequiresApi;

import java.util.Arrays;


public class Item implements Parcelable {

    public static final String TAG = Item.class.getSimpleName();

    private long id; // used for the _id colum of the db helper

    private String name;
    private String idItem; // used for web service
    private String[] categories;
    private String description;
    private int[] timeFrame;
    private int year;
    private String brand;
    private String[] technicalDetails;
    private boolean working;
    private String[][] pictures;
    private String demos;

    public Item(String idItem) {
        this.idItem = idItem;
    }

    public Item(long id, String name, String idItem, String[] categories, String description, int[] timeFrame, int year, String brand, String[] technicalDetails, boolean working, String[][] pictures, String demos) {
        this.id = id;
        this.name = name;
        this.idItem = idItem;
        this.categories = categories;
        this.description = description;
        this.timeFrame = timeFrame;
        this.year = year;
        this.brand = brand;
        this.technicalDetails = technicalDetails;
        this.working = working;
        this.pictures = pictures;
        this.demos = demos;
    }

    public long getId() { return id; }

    public String getName() { return name; }

    public String getIdItem() {
        return idItem;
    }

    public String[] getCategories() {
        return categories;
    }

    public String getDescription() { return description; }

    public int[] getTimeFrame() {
        return timeFrame;
    }

    public int getYear() {
        return year;
    }

    public String getBrand() {
        return brand;
    }

    public String[] getTechnicalDetails() {
        return technicalDetails;
    }

    public boolean isWorking() {
        return working;
    }

    public String[][] getPictures() {
        return pictures;
    }

    public String getDemos() {
        return demos;
    }


    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIdItem(String idItem) {
        this.idItem = idItem;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }

    public void setDescription(String description) { this.description = description; }

    public void setTimeFrame(int[] timeFrame) { this.timeFrame = timeFrame; }

    public void setYear(int year) { this.year = year; }

    public void setBrand(String brand) { this.brand = brand; }

    public void setTechnicalDetails(String[] technicalDetails) { this.technicalDetails = technicalDetails; }

    public void setWorking(boolean working) { this.working = working; }

    public void setPictures(String[][] pictures) { this.pictures = pictures; }

    public void setDemos(String demos) { this.demos = demos; }

    @Override
    public int describeContents() {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(idItem);
        dest.writeStringArray(categories);
        dest.writeString(description);
        dest.writeIntArray(timeFrame);
        dest.writeInt(year);
        dest.writeString(brand);
        dest.writeStringArray(technicalDetails);
        dest.writeBoolean(working);
        dest.writeString(demos);

        if (pictures != null) {
            int nbOfArrays = pictures.length;
            dest.writeInt(nbOfArrays);
            for (int i = 0; i < nbOfArrays; i++) {
                dest.writeStringArray(pictures[i]);
            }
        }
    }

    public static final Creator<Item> CREATOR = new Creator<Item>()
    {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public Item createFromParcel(Parcel source)
        {
            return new Item(source);
        }

        @Override
        public Item[] newArray(int size)
        {
            return new Item[size];
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public Item(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.idItem = in.readString();
        this.categories = in.createStringArray();
        this.description = in.readString();
        this.timeFrame = in.createIntArray();
        this.year = in.readInt();
        this.brand = in.readString();
        this.technicalDetails = in.createStringArray();
        this.working = in.readBoolean();
        this.demos = in.readString();

        int nbOfArrays = in.readInt();
        if (nbOfArrays != 0) {
            this.pictures = new String[nbOfArrays][2];
            for (int i = 0; i < nbOfArrays; i++) {
                this.pictures[i] = in.createStringArray();
            }
        }
    }

}
