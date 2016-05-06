package genn.playqt;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import genn.playqt.Utilities.BaseActivity;
import genn.playqt.Utilities.FileUtilities;

public class ChoosePhotoActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private List<PhotoInfo> mData = new ArrayList<>();

    private String[] neededPermissions;
    private int permissionCode = 3;

    private File[] currentFiles;
    private TextView recycleTitle;
    private LinearLayout container;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_photo);
        recycleTitle = (TextView) findViewById(R.id.recycle_title_view);
        container = (LinearLayout) findViewById(R.id.show_recycle_container);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        neededPermissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        requestPermission(this,neededPermissions, permissionCode );

    }

    Pattern pattern = Pattern.compile("\\w+\\\\.(jpg|gif|bmp|png)");
    @Override
    public void doWorks(int permissionCode) {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            String appDirectoryPath, thumbDirectoryPath;

            appDirectoryPath = Environment.getExternalStorageDirectory().getPath() + "/PlayQt";
            thumbDirectoryPath = appDirectoryPath + "/thumbnail";
            FileUtilities.initAppDir(appDirectoryPath, thumbDirectoryPath);

            currentFiles = FileUtilities.appDir.listFiles();
            for (int i=0; i<currentFiles.length; i++) {
                String tmpFileName = currentFiles[i].getName();
                if (tmpFileName.endsWith(".jpg")) {

                    PhotoInfo photoInfo = new PhotoInfo();
                    File tmpFile = new File(FileUtilities.thumbDirPath + "/" + tmpFileName);
                    Bitmap bitmap;
                    if (tmpFile.exists()) {
                        bitmap =  FileUtilities.readFileToBitmap(tmpFile, 1);
                    } else {
                        tmpFile = new File(FileUtilities.appDirPath + "/", tmpFileName);
                        bitmap = FileUtilities.readFileToBitmap(tmpFile, 30);
                    }
                    photoInfo.setFileIcon(bitmap);
                    photoInfo.setFileName(tmpFileName);

                    mData.add(photoInfo);
                }

            }
            recyclerAdapter = new RecyclerAdapter(mData);
            recyclerView.setAdapter(recyclerAdapter);

            if (mData.size() == 0) {
                container.setGravity(Gravity.CENTER);
                recyclerView.setVisibility(View.GONE);
                recycleTitle.setVisibility(View.VISIBLE);

                recycleTitle.setTextSize(30);
                recycleTitle.setText("相册目前为空, 拍张照试试");
            } else {
                recycleTitle.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }

            final AlertDialog dialog = new AlertDialog.Builder(this).create();

            recyclerAdapter.setRecycleItemClickListener(new RecyclerAdapter.OnRecycleItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                    TextView fileNameView = (TextView) findViewById(R.id.file_name_item);
                    String fileName = fileNameView.getText().toString();

                    File showImageFile = new File(FileUtilities.appDirPath + "/" + fileName);
                    Bitmap bitmap = FileUtilities.readFileToBitmap(showImageFile, 1);

                    View showPhotoView = LayoutInflater.from(ChoosePhotoActivity.this).inflate(R.layout.show_photo, null);
                    ImageView showPhoto = (ImageView)showPhotoView.findViewById(R.id.show_photo_image);


                    showPhoto.setImageBitmap(bitmap);

                    dialog.setTitle(fileName);
                    dialog.setView(showPhotoView);
                    dialog.show();

                    //未完待续....
                }
            });
        }
    }

    @Override
    public void explainYourWork() {
        Toast.makeText(this, "请给与读取文件的权限", Toast.LENGTH_LONG).show();
    }

    @Override
    public void explainYourWorkForDeny() {
        Toast.makeText(this, "您拒绝了权限请求,无法读取文件", Toast.LENGTH_LONG).show();
    }
}
