package genn.playqt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.zijunlin.Zxing.Demo.CaptureActivity;

public class MainActivity extends AppCompatActivity {

    private Button takePhoto, choosePhoto, detectQR, login, testPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login = (Button) findViewById(R.id.login_btn);
        takePhoto = (Button) findViewById(R.id.take_photo_btn);
        choosePhoto = (Button) findViewById(R.id.choose_photo_btn);
        detectQR = (Button) findViewById(R.id.detect_qr_btn);

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
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
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
}
