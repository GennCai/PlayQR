package genn.playqt.database;

import java.io.File;

public class User {

    public static String appDirectoryPath = "", thumbDirectoryPath = "";
    public static File appDir, thumbDir;

    private static User sUser;
    private String username;
    private String password;
    private boolean isLogin;
    private int id;

    private User() {}

    public static User getInstance(){
        if (sUser == null) {
            sUser = new User();
        }
        return sUser;
    }
    public static User setInstance(String username, String password, boolean isLogin) {
        sUser = getInstance();
        sUser.setUsername(username);
        sUser.setPassword(password);
        sUser.setLogin(isLogin);
        return sUser;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    public String getUsername() {
        return username;
    }

    public static String getAppDirectoryPath() {
        return appDirectoryPath;
    }

    public static void setAppDirectoryPath(String appDirectoryPath) {
        User.appDirectoryPath = appDirectoryPath;
    }

    public static String getThumbDirectoryPath() {
        return thumbDirectoryPath;
    }

    public static void setThumbDirectoryPath(String thumbDirectoryPath) {
        User.thumbDirectoryPath = thumbDirectoryPath;
    }

}
