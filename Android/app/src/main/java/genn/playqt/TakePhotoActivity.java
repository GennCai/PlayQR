package genn.playqt;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

import genn.playqt.Utilities.BaseActivity;
import genn.playqt.Utilities.FileUtilities;

public class TakePhotoActivity extends BaseActivity {
    public static final int TAKE_PHOTO = 1;
    public static final int THUMBNAIL_WIDTH = 150;
    public static final int THUMBNAIL_HEIGHT = 100;

    private String appDirectoryPath, thumbDirectoryPath;
    private String imageFilePath, thumbFilePath;
    private File outputImageFile;

    private String[] neededPermissions;
    private final int permissionCode4TakePhone = 1;
    private final int permissionCode4Location = 2;
    private ImageView showPhoto;
    private Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_photo);

        showPhoto = (ImageView) findViewById(R.id.show_photo_image);
        neededPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestPermission(this, neededPermissions, permissionCode4TakePhone);
    }

    @Override
    public void doWorks(int permissionCode) {
        switch (permissionCode) {
            case permissionCode4TakePhone:
                //拍照并存储照片和相应的Thumbnail
                takePhoto();
                break;
            case permissionCode4Location:
                break;
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


    Bitmap dBitmap;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bitmap, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);

                        if (!FileUtilities.saveBitmapToFile(thumbnail, thumbFilePath)) {
                            Toast.makeText(this, "保存thumbnail失败!", Toast.LENGTH_LONG).show();
                        }
                        //     degree = FileUtilities.readPictureDegree(thumbFilePath);
                        dBitmap = FileUtilities.rotateBitmap(bitmap, 90);
                        showPhoto.setImageBitmap(dBitmap);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                } else {
                    if (outputImageFile.exists() && outputImageFile.length() == 0) {
                        outputImageFile.delete();
                        finish();
                    } else {
                        finish();
                    }
                }
                break;
        }
    }


    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
    private void takePhoto() {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            appDirectoryPath = Environment.getExternalStorageDirectory().getPath() + "/PlayQt";
            thumbDirectoryPath = appDirectoryPath + "/thumbnail";
            FileUtilities.initAppDir(appDirectoryPath, thumbDirectoryPath);

            String fileTime = formatter.format(new Date(System.currentTimeMillis()));

            imageFilePath = appDirectoryPath + "/" + fileTime + ".jpg";
            thumbFilePath = thumbDirectoryPath + "/" + fileTime + ".jpg";

            outputImageFile = new File(imageFilePath);

            try {
                if (outputImageFile.exists()) {
                    outputImageFile.delete();
                }
                if (!outputImageFile.createNewFile()) {
                    Toast.makeText(this, "Make Image Failed!!!", Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            imageUri = Uri.fromFile(outputImageFile);
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, TAKE_PHOTO);
        } else {
            Toast.makeText(this, "请插入存储卡", Toast.LENGTH_LONG).show();
        }
    }
}
