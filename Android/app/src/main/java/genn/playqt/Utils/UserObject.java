package genn.playqt.Utils;

import android.graphics.Bitmap;

public class UserObject {

    private String username;

    private String password;

    public UserObject(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public boolean isValid(String username, String password) {
        if (this.username.equals(username) && this.password.equals(password)) {
            return true;
        }
        return false;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public static class ImageInfo {

        private Bitmap fileIcon;
        private String fileName;

        public ImageInfo(){}

        public ImageInfo(Bitmap fileThumb, String fileName) {
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
}
