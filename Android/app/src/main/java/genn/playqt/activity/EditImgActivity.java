package genn.playqt.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import genn.playqt.R;
import genn.playqt.database.BDLocationManager;
import genn.playqt.database.DatabaseHelper;
import genn.playqt.database.Image;

public class EditImgActivity extends AppCompatActivity {
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

    int j = 0 , k = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_img);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        mImage = (Image) intent.getSerializableExtra("image");
        String imageName = intent.getStringExtra("imageName");
        editImageName.setText(imageName);
        if (mImage != null) {
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

        String imageName = editImageName.getText().toString();
        String imageData = editImageData.getText().toString();
        String imageTime = editImageTime.getText().toString();
        String imageLocation = editImageLocation.getText().toString();

        if (TextUtils.isEmpty(imageName)) {
            editImageName.setError("ImageName不能为空");
            focusView = editImageName;
            cancel = true;
        } else if (imageName.contains("/")){
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
                mImage.setName(imageName);
                mImage.setDecodeData(imageData);
                mImage.setTakeTime(imageTime);
                mImage.setLocation(imageLocation);
                DatabaseHelper.getInstance(this).updataImage(mImage);
            } else {
                mImage = new Image();
                mImage.setName(imageName);
                mImage.setDecodeData(imageData);
                mImage.setTakeTime(imageTime);
                mImage.setLocation(imageLocation);
                DatabaseHelper.getInstance(this).insertImage(mImage);
            }
            setResult(RESULT_OK);
            finish();
        }
    }
}
