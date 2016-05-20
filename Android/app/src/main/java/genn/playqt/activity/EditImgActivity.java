package genn.playqt.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import genn.playqt.R;
import genn.playqt.database.BDLocationManager;
import genn.playqt.database.DatabaseHelper;
import genn.playqt.database.Image;
import genn.playqt.database.User;
import genn.playqt.utils.FileUtils;
import genn.playqt.utils.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class EditImgActivity extends Activity {
    @Bind(R.id.edit_image_name)
    EditText editImageName;
    @Bind(R.id.edit_image_data)
    EditText editImageData;
    @Bind(R.id.edit_image_time)
    EditText editImageTime;
    @Bind(R.id.edit_image_location)
    EditText editImageLocation;
    Image mImage = null;
    View focusView;
    DatabaseHelper mDatabaseHelper;
    int j = 0 , k = 0;
    boolean isFromLoc;
    String oldName, newName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_img);
        ButterKnife.bind(this);
        mDatabaseHelper = DatabaseHelper.getInstance(this);
        Intent intent = getIntent();
        mImage = (Image) intent.getSerializableExtra("image");
        isFromLoc = intent.getBooleanExtra("isFromLoc", false);
        if (isFromLoc) {
            oldName = intent.getStringExtra("imageName");
            editImageName.setText(oldName);
            if (mImage != null) {
                editImageData.setText(mImage.getDecodeData());
                editImageTime.setText(mImage.getTakeTime());
                editImageLocation.setText(mImage.getLocation());
            }
        } else {
            oldName = mImage.getName();
            editImageName.setText(mImage.getName());
            editImageData.setText(mImage.getDecodeData());
            editImageTime.setText(mImage.getTakeTime());
            editImageLocation.setText(mImage.getLocation());
        }

        editImageTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && j==0) {
                    editImageTime.setText(Image.sSimpleDateFormat.format(new Date()));
                    j++;
                }
            }
        });

        editImageLocation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && k==0) {
                    new BDLocationManager(EditImgActivity.this, new Handler(){
                        @Override
                        public void handleMessage(Message msg) {
                            editImageLocation.setText((String)msg.obj);
                        }
                    }).getLocation();
                    k++;
                }
            }
        });

        editImageData.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == R.id._submit) {
                    attemptSubmit();
                    return true;
                }
                return false;
            }
        });
    }

    @OnClick(R.id.edit_reset)
    public void btnReset(View view) {
        editImageName.setText("");
        editImageData.setText("");
        editImageTime.setText("");
        editImageLocation.setText("");
    }
    @OnClick(R.id.edit_submit)
    public void btnSubmit(View view) {
        attemptSubmit();
    }

    private void attemptSubmit() {
        editImageName.setError(null);
        editImageData.setError(null);
        editImageTime.setError(null);
        editImageLocation.setError(null);

        boolean cancel = false;

        newName = editImageName.getText().toString();
        String imageData = editImageData.getText().toString();
        String imageTime = editImageTime.getText().toString();
        String imageLocation = editImageLocation.getText().toString();

        if (TextUtils.isEmpty(newName)) {
            editImageName.setError("ImageName不能为空");
            focusView = editImageName;
            cancel = true;
        } else if (newName.contains("/")){
            editImageName.setError("ImageName不能包含\"/\"");
            focusView = editImageName;
            cancel = true;
        }

        if (TextUtils.isEmpty(imageData)) {
            editImageData.setError("ImageData不能为空");
            focusView = editImageData;
            cancel = true;
        }

        if (TextUtils.isEmpty(imageTime)) {
            editImageTime.setError("ImageTime不能为空");
            focusView = editImageTime;
            cancel = true;
        } else if (imageTime.length() != 19) {
            editImageTime.setError("ImageTime的格式必须为\"yyyy-MM-dd HH:mm:ss\"");
            focusView = editImageTime;
            cancel = true;
        }

        if (TextUtils.isEmpty(imageLocation)) {
            focusView = editImageLocation;
            editImageLocation.setError("ImageLocation不能为空");
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            if (mImage != null) {
                mImage.setName(newName);
                mImage.setDecodeData(imageData);
                mImage.setTakeTime(imageTime);
                mImage.setLocation(imageLocation);
                if (isFromLoc) {
                    mDatabaseHelper.updateImage(mImage, oldName);
                    // 本地images数据库id 和远程数据库images id不一致
                    /*if (mImage.isUploaded()) {
                        HttpUtil.getInstance().updateImage(HttpUtil.URL_TASK, mImage, mCallback);
                    }*/
                } else {
                    HttpUtil.getInstance().updateImage(HttpUtil.URL_TASK, mImage, mCallback);
                    if (mDatabaseHelper.queryImage(mImage.getName()) != null) {
                        mDatabaseHelper.updateImage(mImage, oldName);
                    }
                }
            } else {
                mImage = new Image();
                mImage.setName(newName);
                mImage.setDecodeData(imageData);
                mImage.setTakeTime(imageTime);
                mImage.setLocation(imageLocation);
                mDatabaseHelper.insertImage(mImage);
            }
            if (!oldName.equals(newName)) {
                FileUtils.renameFile(User.getAppDirectoryPath(), oldName, newName);
                FileUtils.renameFile(User.getThumbDirectoryPath(), oldName, newName);
            }
            Intent intent = new Intent();
            intent.putExtra("image", mImage);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    Callback mCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Looper.prepare();
            Toast.makeText(EditImgActivity.this, "数据更新失败", Toast.LENGTH_LONG).show();
            Looper.loop();
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            Looper.prepare();
            if (response.code() == 409) {
                Toast.makeText(EditImgActivity.this, response.body().string(), Toast.LENGTH_LONG).show();
            } else if (response.isSuccessful()){
                Toast.makeText(EditImgActivity.this, "远程数据更新成功!", Toast.LENGTH_LONG).show();
            }
            Looper.loop();
        }
    };
}
