package genn.playqt.database;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;

public class Image implements Serializable{

    public static final int THUMB_WIDTH = 150;
    public static final int THUMB_HEIGHT = 150;
    public static SimpleDateFormat sSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //"yyyyMMdd_HHmmss"
    transient private Bitmap icon;

    private File imageFile;
    @SerializedName("image_name")
    private String name;
    @SerializedName("decode_data")
    private String decodeData;
    @SerializedName("time")
    private String takeTime;
    private String location;

    private int id;

    private boolean isUploaded;
    public Image(){}

    public Image(Bitmap thumbBitmap, String imageName) {
        this.name = imageName;
        this.icon = thumbBitmap;
    }

    @Override
    public boolean equals(Object o) {
        Image image = (Image) o;
        if (this.getName().equals(image.getName())) {
            return true;
        }
        return false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
    }

    public String getDecodeData() {
        return decodeData;
    }

    public void setDecodeData(String decodeData) {
        this.decodeData = decodeData;
    }

    public String getTakeTime() {
        return takeTime;
    }

    public void setTakeTime(String takeTime) {
        this.takeTime = takeTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

