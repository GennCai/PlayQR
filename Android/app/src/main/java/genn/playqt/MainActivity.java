package genn.playqt;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zijunlin.Zxing.Demo.CaptureActivity;

import genn.playqt.Utils.BaseActivity;
import genn.playqt.Utils.FileUtils;

public class MainActivity extends BaseActivity {

    private Button takePhoto, choosePhoto, detectQR, login, testPage;

    public String appDirectoryPath, thumbDirectoryPath;

    public String[] neededPermissions;
    public final int permissionCode4TakePhone = 1;
    public final int permissionCode4Location = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login = (Button) findViewById(R.id.login_btn);
        takePhoto = (Button) findViewById(R.id.take_photo_btn);
        choosePhoto = (Button) findViewById(R.id.choose_photo_btn);
        detectQR = (Button) findViewById(R.id.detect_qr_btn);

        neededPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestPermission(this, neededPermissions, permissionCode4TakePhone);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
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
                Intent intent = new Intent(MainActivity.this, ChoosePhotoActivity.class);
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
}
