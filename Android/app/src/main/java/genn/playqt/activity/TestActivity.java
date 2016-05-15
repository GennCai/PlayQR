package genn.playqt.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import genn.playqt.R;
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

    LocationManager mLocationManager;
    Location mLocation;
    String mProvider;

    DatabaseHelper mDatabaseHelper;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x001:
                    mEditText.setError("上传图片失败");
                    break;
                case 0x002:
                    mEditText.setText((String)msg.obj);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        List<String> allProviders = mLocationManager.getAllProviders();
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < allProviders.size(); i++) {
            stringBuilder.append(allProviders.get(i) + " ");
        }

        if (allProviders.contains(LocationManager.GPS_PROVIDER)) {
            mProvider = LocationManager.GPS_PROVIDER;
        } else if (allProviders.contains(LocationManager.NETWORK_PROVIDER)) {
            mProvider = LocationManager.NETWORK_PROVIDER;
        } else if (allProviders.contains(LocationManager.PASSIVE_PROVIDER)) {
            mProvider = LocationManager.PASSIVE_PROVIDER;
        } else {
            Toast.makeText(this, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
            finish();
        }

        mTextView.setText(stringBuilder.toString());
        requestPermission(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION}, 0);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Image image = new Image();
        image.setImageFile(new File(User.appDir, "20160513_151446.jpg"));
        image.setDecodeData("12345678");
        image.setName("2078.jpg");
        image.setTakeTime(Image.sSimpleDateFormat.format(new Date(System.currentTimeMillis())));
        image.setPosition("Moon");
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
        }
    }

    @Override
    public void doWorks(int permissionCode) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocation = mLocationManager.getLastKnownLocation(mProvider);
        mLocationManager.requestLocationUpdates(mProvider, 3000, 1, mLocationListener);
    }

    @Override
    public void explainYourWork() {}

    @Override
    public void explainYourWorkForDeny() {}

    LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            updateLocation(location);
            Toast.makeText(TestActivity.this, "Location status changed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Toast.makeText(TestActivity.this, "Location status changed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(TestActivity.this, "Location Provider enabled", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(TestActivity.this, "Location provider disabled", Toast.LENGTH_SHORT).show();
        }
    };

    public void updateLocation(Location location) {
        if (location != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("实时位置信息: \n");
            sb.append("经度:" + location.getLongitude() + "\n");
            sb.append("纬度:" + location.getLatitude() + "\n");
            sb.append("高度:" + location.getAltitude() + "\n");
            sb.append("速度:" + location.getSpeed() + "\n");
            sb.append("方向:" + location.getBearing() + "\n");
            sb.append("精度" + location.getAccuracy() + "\n");
            sb.append("时间" + location.getTime() + "\n");
            sb.append("getElapsedRealtimeNanos: " + location.getElapsedRealtimeNanos() + "\n");
            mEditText.setText(sb.toString());
        }
    }
}
