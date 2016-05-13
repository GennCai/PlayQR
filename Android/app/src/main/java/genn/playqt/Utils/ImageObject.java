package genn.playqt.Utils;

import android.graphics.Bitmap;

public class ImageObject {
    private Bitmap fileIcon;
    private String fileName;

    public ImageObject(){}

    public ImageObject(Bitmap fileThumb, String fileName) {
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

