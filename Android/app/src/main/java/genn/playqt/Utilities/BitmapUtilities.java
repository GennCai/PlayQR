package genn.playqt.Utilities;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtilities {
    public static boolean saveBitmapToFile(Bitmap bitmap, String targetFile) {
        FileOutputStream outputFile;
        boolean flag = false;
        try {

            outputFile = new FileOutputStream(targetFile);
            flag = bitmap.compress(Bitmap.CompressFormat.PNG, 50, outputFile);
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
}
