package genn.playqt;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.zijunlin.Zxing.Demo.CaptureActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

import genn.playqt.Utilities.BaseActivity;
import genn.playqt.Utilities.BitmapUtilities;

public class MainActivity extends BaseActivity {
    public static final int TAKE_PHOTO = 1;
    public static final int THUMBNAIL_WIDTH = 90;
    public static final int THUMBNAIL_HEIGHT = 160;
    private String thumbName;
    private Button takePhoto, choosePhoto, detectQR;
    private ImageView showPhoto;
    private Uri imageUri;
    private String[] neededPermissions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        takePhoto = (Button) findViewById(R.id.take_photo_btn);
        choosePhoto = (Button) findViewById(R.id.choose_photo_btn);
        detectQR = (Button) findViewById(R.id.detect_qr_btn);
        showPhoto = (ImageView) findViewById(R.id.show_photo_image);

        neededPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission(MainActivity.this, neededPermissions);
            }
        });

        choosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.intent.action.GET_CONTENT");
                intent.setType("image/*");
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

    }

    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
    //拍照并存储照片和相应的Thumbnail
    @Override
    public void doWorks() {
        File outputImage, appDirectory, thumbnailsDirectory;

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            appDirectory = new File(Environment.getExternalStorageDirectory().getPath() + "/PlayQt");
            thumbnailsDirectory = new File(appDirectory, "thumbnail");

            if (!appDirectory.exists()) {
                if (!appDirectory.mkdir() || !thumbnailsDirectory.mkdir()) {
                    Toast.makeText(this, "Make AppDirectory Failed!!!", Toast.LENGTH_LONG).show();
                }
            } else if (!thumbnailsDirectory.exists()) {
                if (!thumbnailsDirectory.mkdir()) {
                    Toast.makeText(this, "Make ThumbnailDirectory Failed!!!", Toast.LENGTH_LONG).show();
                }
            }

            String imageName = formatter.format(new Date(System.currentTimeMillis()));
            thumbName =  thumbnailsDirectory.getAbsolutePath() + "/THUMB_" + imageName + ".png";
            outputImage = new File(appDirectory, imageName + ".jpg");

            try {
                if (outputImage.exists()) {
                    outputImage.delete();
                }
                if (!outputImage.createNewFile()) {
                    Toast.makeText(this, "Make Image Failed!!!", Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            imageUri = Uri.fromFile(outputImage);
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, TAKE_PHOTO);
        } else {
            Toast.makeText(this, "请插入存储卡", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void explainYourWork() {
        Toast.makeText(this, "拍照需要权限", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void explainYourWorkForDeny() {
        Toast.makeText(this, "你拒绝了权限请求,无法拍照,请打开权限", Toast.LENGTH_LONG).show();
    }

    Matrix matrix = new Matrix();//旋转拍照图片显示的角度
    int degree;
    Bitmap dBitmap;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bitmap, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
                        if (!BitmapUtilities.saveBitmapToFile(thumbnail, thumbName)) {
                            Toast.makeText(this, "保存thumbnail失败!", Toast.LENGTH_LONG).show();
                        }
                   //     degree = BitmapUtilities.readPictureDegree(thumbName);
                        matrix.setRotate(90);
                        dBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                        showPhoto.setImageBitmap(dBitmap);


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
