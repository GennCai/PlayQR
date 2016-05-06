package genn.playqt;

import android.graphics.Bitmap;

public class PhotoInfo {

    private Bitmap fileIcon;
    private String fileName;

    public PhotoInfo(){}

    public PhotoInfo(Bitmap fileThumb, String fileName) {
        this.fileName = fileName;
        this.fileIcon =fileThumb;
    }

    public Bitmap getFileIcon() {
        return fileIcon;
    }

    public void setFileIcon(Bitmap fileIcon) {
        this.fileIcon = fileIcon;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
