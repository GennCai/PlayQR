package genn.playqt.database;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class BDLocationManager {
    public static final int HANDLER_WHAT = 0x006;

    private LocationClient mLocationClient;
    private LocationClientOption mOption;
    private Handler mHandler;
    private Context mContext;

    public BDLocationManager(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
    }
    public void getLocation() {
        mLocationClient = new LocationClient(mContext);
        mOption = new LocationClientOption();
        initOption(mOption);
        mLocationClient.setLocOption(mOption);
        mLocationClient.registerLocationListener(mLocationListener);
        mLocationClient.start();
    }

    private void initOption(LocationClientOption option) {
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
        option.setCoorType("bd09ll");
       // option.setScanSpan(1000);
        option.setIsNeedAddress(true);
        option.setIsNeedLocationDescribe(true);
        option.setIgnoreKillProcess(false);
        option.setEnableSimulateGps(false);
    }

    private BDLocationListener mLocationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            StringBuilder sb = new StringBuilder();
            sb.append("time : ");
            sb.append(bdLocation.getTime());
            sb.append("\nerror code : ");
            sb.append(bdLocation.getLocType());
            sb.append("\nlatitude : ");
            sb.append(bdLocation.getLatitude());
            sb.append("\nlongitude : ");
            sb.append(bdLocation.getLongitude());
            sb.append("\nradius : ");
            sb.append(bdLocation.getRadius());
            if (bdLocation.getLocType() == BDLocation.TypeGpsLocation || bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
                sb.append("\naddress : ");
                sb.append(bdLocation.getAddrStr());
                Message message = new Message();
                message.what = HANDLER_WHAT;
                message.obj = bdLocation.getAddrStr();
                mHandler.sendMessage(message);
            }else if (bdLocation.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (bdLocation.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (bdLocation.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocation describe : ");
            sb.append(bdLocation.getLocationDescribe());// 位置语义化信息
            Log.i("BaiduLocationApiDem", sb.toString());
            mLocationClient.stop();
            mLocationClient.unRegisterLocationListener(mLocationListener);
        }
    };
}
