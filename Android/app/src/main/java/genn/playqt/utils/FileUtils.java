package genn.playqt.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.Image;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import genn.playqt.database.User;

public class FileUtils {
    public static final String TAG = "PlayQR";
    public static int initCount = 0;

    public static SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");

    public static void initAppDir(String appDirectoryPath, String thumbDirectoryPath){

        if (User.appDir == null) {
            User.appDir = checkAppDirectory(appDirectoryPath);
            User.setAppDirectoryPath(appDirectoryPath);
        }
        if (User.thumbDir == null) {
            User.thumbDir = checkAppDirectory(thumbDirectoryPath);
            User.setThumbDirectoryPath(thumbDirectoryPath);
        }
        initCount++;
    }

    public static boolean saveBitmapToFile(Bitmap bitmap, String targetFile, int quality) {
        FileOutputStream outputFile;
        boolean flag = false;
        try {

            outputFile = new FileOutputStream(targetFile);
            flag = bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputFile);
            outputFile.flush();
            outputFile.close();
            return flag;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

    public static Bitmap readFileToBitmap(File imageFile, int size) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = size;
        Bitmap bitmap = null;
        try {
            bitmap = rotateBitmap(BitmapFactory.decodeStream(new FileInputStream(imageFile), null, options), 90);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int degree) {
        Matrix matrix = new Matrix();
        matrix.setRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
    }

    public static int readPictureDegree(String path) {
        int degree  = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static File checkAppDirectory(String directoryPath) {
        if (User.getAppDirectoryPath().equals(directoryPath) || User.getThumbDirectoryPath().equals(directoryPath)) {
            return null;
        }
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            if (!directory.mkdir()){
                Log.d(TAG, "---------文件夹不存在,建立文件夹失败-----------");
                return null;
            }
            return directory;
        }
        return directory;

    }
}
