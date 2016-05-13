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

}
