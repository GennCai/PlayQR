package genn.playqt.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import genn.playqt.R;
import genn.playqt.database.User;
import genn.playqt.utils.FileUtils;

public class MainActivity extends BaseActivity {

    private Button takePhoto, choosePhoto, detectQR, testPage;
    private TextView loginText, usernameText;

    private static final int REQUEST_LOGIN_CODE = 0;

    public String[] neededPermissions;
    public final int permissionCode4TakePhone = 1;
    public final int permissionCode4Location = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        takePhoto = (Button) findViewById(R.id.take_photo_btn);
        choosePhoto = (Button) findViewById(R.id.choose_photo_btn);
        detectQR = (Button) findViewById(R.id.detect_qr_btn);

        loginText = (TextView) findViewById(R.id.login_text);
        usernameText = (TextView) findViewById(R.id.username_text);

        neededPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestPermission(this, neededPermissions, permissionCode4TakePhone);

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (User.getInstance().isLogin()) {
                    usernameText.setText("匿名用户");
                    loginText.setText("登陆");
                    User.getInstance().setLogin(false);
                } else {

                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivityForResult(intent, REQUEST_LOGIN_CODE);
                }
            }
        });

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TakePhotoActivity.class);
                startActivity(intent);
            }
        });

        choosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent("android.intent.action.GET_CONTENT");
                intent.setType("image*//*");*/
                Intent intent = new Intent(MainActivity.this, BrsImgActivity.class);
                startActivity(intent);
            }
        });

        detectQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyCaptureActivity.class);
                startActivity(intent);
            }
        });
        testPage = (Button) findViewById(R.id.test_btn);
        testPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TestActivity.class);
                startActivity(intent);
            }
        });
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
    public void explainYourWork() {

    }

    @Override
    public void explainYourWorkForDeny() {

    }

    @Override
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
}
