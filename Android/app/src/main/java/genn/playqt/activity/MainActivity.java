package genn.playqt.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import genn.playqt.R;
import genn.playqt.database.Image;
import genn.playqt.database.User;
import genn.playqt.utils.FileUtils;
import genn.playqt.utils.HttpUtil;
import okhttp3.Request;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    private Button takePhoto, chooseLocImg,chooseNetImg, detectQR;
    private TextView loginText, usernameText;

    private static final int REQUEST_LOGIN_CODE = 0;

    public String[] neededPermissions;
    public final int permissionCode4TakePhone = 1;
    public final int permissionCode4Location = 2;

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        takePhoto = (Button) findViewById(R.id.take_photo_btn);
        chooseLocImg = (Button) findViewById(R.id.choose_loc_img_btn);
        chooseNetImg = (Button) findViewById(R.id.choose_net_img_btn);
        detectQR = (Button) findViewById(R.id.detect_qr_btn);

        loginText = (TextView) findViewById(R.id.login_text);
        usernameText = (TextView) findViewById(R.id.username_text);

        neededPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestPermission(this, neededPermissions, permissionCode4TakePhone);

        loginText.setOnClickListener(this);
        takePhoto.setOnClickListener(this);
        chooseLocImg.setOnClickListener(this);
        chooseNetImg.setOnClickListener(this);
        detectQR.setOnClickListener(this);

        mPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
    //    boolean isLogin = mPreferences.getBoolean("isLogin", false);
        if (mPreferences.getBoolean("isLogin", false)) {
            String username = mPreferences.getString("username", "");
            String password = mPreferences.getString("password", "");
            int id = mPreferences.getInt("id", -1);
            HttpUtil.configAuthRequestBuilder(new Request.Builder(), username, password);
            User.setInstance(username, password, true);
            User.getInstance().setId(id);

        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        Log.d("MainActivity", "onCreate()");
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d("MainActivity", "onStart()");
        if (!User.getInstance().isLogin()) {
            Log.d("MainActivity", "onStart()");
        }
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.login_text:
                if (User.getInstance().isLogin()) {
                    usernameText.setText("匿名用户");
                    loginText.setText("登陆");
                    User.getInstance().setLogin(false);
                } else {
                    intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.detect_qr_btn:
                intent = new Intent(MainActivity.this, MyCaptureActivity.class);
                startActivity(intent);
                break;
            case R.id.take_photo_btn:
                intent = new Intent(MainActivity.this, TakePhotoActivity.class);
                startActivity(intent);
                break;
            case R.id.choose_loc_img_btn:
                /*Intent intent = new Intent("android.intent.action.GET_CONTENT");
                intent.setType("image*//*");*/
                intent = new Intent(MainActivity.this, LocImgActivity.class);
                startActivity(intent);
                break;
            case R.id.choose_net_img_btn:
                intent = new Intent(MainActivity.this, NetImgActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void doWorks(int permissionCode) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String appDirectoryPath, thumbDirectoryPath;
            appDirectoryPath = Environment.getExternalStorageDirectory().getPath() + "/PlayQt";
            thumbDirectoryPath = appDirectoryPath + "/thumbnail";
            FileUtils.initAppDir(appDirectoryPath, thumbDirectoryPath);
        } else {
            Toast.makeText(this, "请插入存储卡", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        User user = User.getInstance();
        if (user.isLogin()) {
            usernameText.setText(user.getUsername());
            loginText.setText("退出");
        }
        Log.d("MainActivity", "onResume()");
    }

    @Override
    public void explainYourWork() {

    }

    @Override
    public void explainYourWorkForDeny() {

    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_LOGIN_CODE:
                if (resultCode == RESULT_OK) {
                    usernameText.setText(data.getStringExtra("username"));
                    loginText.setText("退出");
                }
        }
    }
*/

    @Override
    protected void onPause() {
        super.onPause();
        mEditor = mPreferences.edit();
        User user = User.getInstance();
        boolean isLogin = user.isLogin();
        String username = user.getUsername();
        String password = user.getPassword();
        int id = user.getId();
        mEditor.putBoolean("isLogin", isLogin);
        mEditor.putString("username", username);
        mEditor.putString("password", password);
        mEditor.putInt("id", id);
        mEditor.commit();
        Log.d("MainActivity", "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("MainActivity", "onStop()");
    }

    @Override
    protected void onDestroy() {
        if (NetImgActivity.netImages != null) {
            for (Image image : NetImgActivity.netImages) {
                if (image.getImageFile() != null) {
                    image.getImageFile().delete();
                }
            }
        }
        Log.d("MainActivity", "onDestroy()");
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("MainActivity", "onRestart()");
    }
}
