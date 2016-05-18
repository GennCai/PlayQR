package genn.playqt.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import genn.playqt.R;
import genn.playqt.database.BDLocationManager;
import genn.playqt.database.DatabaseHelper;
import genn.playqt.database.Image;
import genn.playqt.database.User;
import genn.playqt.utils.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TestActivity extends BaseActivity {

    @Bind(R.id.test_text)
    TextView mTextView;

    @Bind(R.id.test_edit_text)
    EditText mEditText;

    @Bind(R.id.test_image_view)
    ImageView mImageView;
    Bitmap mBitmap;

    @Bind(R.id.test_button)
    Button mButton;
    LocationManager mLocationManager;
    Location mLocation;
    String mProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
        requestPermission(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
    }
    @OnClick(R.id.test_button)
    public void BtnTest(View view) {
        List<Image> images = DatabaseHelper.getInstance(this).queryImages();
        mEditText.setText(images.get(0).getLocation());
    }
    @Override
    protected void onStart() {
        super.onStart();
        /*Image image = new Image();
        image.setImageFile(new File(User.appDir, "20160513_151446.jpg"));
        image.setDecodeData("12345678");
        image.setName("2078.jpg");
        image.setTakeTime(Image.sSimpleDateFormat.format(new Date(System.currentTimeMillis())));
        image.setLocation("Moon");
        HttpUtil httpUtil = HttpUtil.getInstance();
        int aaa = httpUtil.uploadImage(HttpUtil.URL_TASKS, image, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = new Message();
                message.what = 0x001;
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message message = new Message();
                message.what = 0x002;
                message.obj = response.body().string();
                handler.sendMessage(message);
            }
        });
        if (aaa == 0) {
            mEditText.setError("还没有登陆");
        }*/

        /*new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mBitmap = HttpUtil.getInstance().downloadImage(HttpUtil.URL_DOWNLOAD, "ainmgg.jpg");
                    if (mBitmap != null) {
                        handler.sendEmptyMessage(0x005);
                    } else {
                        handler.sendEmptyMessage(0x001);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();*/
    }

    @Override
    public void doWorks(int permissionCode) {
       /* BDLocationManager locationManager = new BDLocationManager(handler);
        locationManager.getLocation(this);*/
    }

    @Override
    public void explainYourWork() {
    }

    @Override
    public void explainYourWorkForDeny() {
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x001:
                    mEditText.setError("下载图片失败");
                    break;
                case 0x002:
                    mEditText.setText((String) msg.obj);
                    break;
                case BDLocationManager.HANDLER_WHAT:
                    mEditText.setText((String)msg.obj);
                    break;
                case 0x005:
                    mImageView.setImageBitmap(mBitmap);
            }
        }
    };
}

