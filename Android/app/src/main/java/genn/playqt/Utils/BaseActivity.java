package genn.playqt.Utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public abstract class BaseActivity extends AppCompatActivity implements RequestPermissionCallBack {
    private int requestCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void requestPermission(Activity activity, String[] permissions, int requestCode){

        this.requestCode = requestCode;
        int checkedPermission = ContextCompat.checkSelfPermission(activity, permissions[0]);

        if (checkedPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[0])) {
                explainYourWorkForDeny();
            }
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
            return;
        }
        doWorks(requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == this.requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doWorks(requestCode);
            } else {
                explainYourWork();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
