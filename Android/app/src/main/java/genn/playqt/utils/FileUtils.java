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
import java.io.InputStream;
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

    public static boolean saveBitmapToFile(Bitmap bitmap, File targetFile, int quality) {
        String targetFileName = targetFile.getAbsolutePath();
        return saveBitmapToFile(bitmap, targetFileName, quality);
    }

    public static Bitmap readFileToBitmap(File imageFile, int size, int rotate) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = size;
        Bitmap bitmap = null;
        try {
            bitmap = rotateBitmap(BitmapFactory.decodeStream(new FileInputStream(imageFile), null, options), rotate);
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

    public static boolean copyFile(File oldFile, File targetFile){
        try {
            if (oldFile.exists() && targetFile.createNewFile()) {
                FileInputStream inputStream = new FileInputStream(oldFile);
                FileOutputStream outputStream = new FileOutputStream(targetFile);
                byte[] buffer = new byte[1024];
                int hasRead;
                while ((hasRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, hasRead);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static boolean renameFile(String directory, String oldName, String newName) {
        File oldFile = new File(directory, oldName);
        File newFile = new File(directory, newName);
        if (oldFile.exists()) {
            return oldFile.renameTo(newFile);
        }
        return false;
    }

}
