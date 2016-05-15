package genn.playqt.database;

import android.graphics.Bitmap;
import java.io.File;
import java.text.SimpleDateFormat;

public class Image {

    public static SimpleDateFormat sSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //"yyyyMMdd_HHmmss"
    private Bitmap icon;

    private File imageFile;
    private String name;
    private String decodeData;
    private String takeTime;
    private String position;

    public Image(){}

    public Image(Bitmap thumbBitmap, String imageName) {
        this.name = imageName;
        this.icon = thumbBitmap;
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
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

