package genn.playqt.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import genn.playqt.R;
import genn.playqt.database.DatabaseHelper;
import genn.playqt.database.User;
import genn.playqt.utils.FileUtils;
import genn.playqt.database.Image;
import genn.playqt.adapter.RecyclerAdapter;
import genn.playqt.utils.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LocImgActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public List<Image> mData = new ArrayList<>();

    private String[] neededPermissions;
    private int permissionCode = 3;

    private File[] currentFiles;
    private TextView recycleTitle;
    private LinearLayout container;

    private View showPhotoView;
    private ImageView showPhoto;
    private Bitmap showBitmap;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_loc_img);

        recycleTitle = (TextView) findViewById(R.id.recycle_title_view);
        container = (LinearLayout) findViewById(R.id.show_recycle_container);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        neededPermissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        requestPermission(this,neededPermissions, permissionCode );

    }
    @Override
    protected void onStart() {
        super.onStart();
        if (!User.getInstance().isLogin()) {
            Intent intent = new Intent(LocImgActivity.this, LoginActivity.class);
            startActivity(intent);
            Toast.makeText(LocImgActivity.this, "登陆后才能查看照片", Toast.LENGTH_LONG).show();
        }
    }

    Pattern pattern = Pattern.compile("\\w+\\\\.(jpg|gif|bmp|png)");
    @Override
    public void doWorks(int permissionCode) {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            String appDirectoryPath, thumbDirectoryPath;

            appDirectoryPath = Environment.getExternalStorageDirectory().getPath() + "/PlayQt";
            thumbDirectoryPath = appDirectoryPath + "/thumbnail";
            FileUtils.initAppDir(appDirectoryPath, thumbDirectoryPath);

            currentFiles = User.appDir.listFiles();
            for (int i=0; i<currentFiles.length; i++) {
                String tmpFileName = currentFiles[i].getName();
                if (tmpFileName.endsWith(".jpg") || tmpFileName.endsWith(".png")) {
                    Image newImage = new Image();
                    File tmpFile = new File(User.getThumbDirectoryPath() + "/" + tmpFileName);
                    Bitmap bitmap;
                    if (tmpFile.exists()) {
                        bitmap =  FileUtils.readFileToBitmap(tmpFile, 1, 90);
                    } else {
                        tmpFile = new File(User.getAppDirectoryPath() + "/", tmpFileName);
                        bitmap = FileUtils.readFileToBitmap(tmpFile, 50, 90);
                    }
                    newImage.setIcon(bitmap);
                    newImage.setName(tmpFileName);

                    mData.add(newImage);
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

            final AlertDialog.Builder clickDialogBuilder = new AlertDialog.Builder(LocImgActivity.this);
            recyclerAdapter.setItemClickListener(new RecyclerAdapter.OnRecycleItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                    TextView fileNameText = (TextView) view.findViewById(R.id.file_name_item);
                    String fileName = fileNameText.getText().toString();

                    showPhotoView = LayoutInflater.from(LocImgActivity.this).inflate(R.layout.activity_show_photo, null);
                    showPhoto = (ImageView)showPhotoView.findViewById(R.id.show_photo_image);

                    File showImageFile = new File(User.getAppDirectoryPath() + "/" + fileName);
                    showBitmap = FileUtils.readFileToBitmap(showImageFile, 1, 90);
                    showPhoto.setImageBitmap(showBitmap);

                    clickDialogBuilder.setTitle(fileName);
                    clickDialogBuilder.setView(showPhotoView);
                    clickDialogBuilder.create();
                    clickDialogBuilder.show();
                }
            });


            recyclerAdapter.setItemLongClickListener(new RecyclerAdapter.OnRecycleItemLongClickListener() {
                @Override
                public void onItemLongClick(View view, final int position) {
                    AlertDialog.Builder longClickBuilder = new AlertDialog.Builder(LocImgActivity.this);
                    final TextView fileNameText = (TextView) view.findViewById(R.id.file_name_item);
                    ImageView fileIconView = (ImageView) view.findViewById(R.id.file_icon_item);
                    final String fileName = fileNameText.getText().toString();

                    final Image image = DatabaseHelper
                            .getInstance(LocImgActivity.this)
                            .queryImage(fileName);
                    longClickBuilder.setTitle(fileName);
                    longClickBuilder.setIcon(fileIconView.getDrawable());

                    if (image != null) {
                        String[] items = new String[]{image.getDecodeData(), image.getTakeTime(), image.getLocation()};
                        longClickBuilder.setItems(items, null);

                        if (!image.isUploaded()) {
                            longClickBuilder.setNeutralButton("上传", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    File imageFile = new File(User.getAppDirectoryPath(), fileName);
                                    if (imageFile.exists()) {
                                        image.setImageFile(imageFile);
                                        HttpUtil.getInstance().uploadImage(HttpUtil.URL_TASKS, image, new Callback() {
                                            @Override
                                            public void onFailure(Call call, IOException e) {
                                                Looper.prepare();
                                                Toast.makeText(LocImgActivity.this, "图片上传失败", Toast.LENGTH_LONG).show();
                                                Looper.loop();
                                            }

                                            @Override
                                            public void onResponse(Call call, Response response) throws IOException {
                                                if (response.isSuccessful()) {
                                                    DatabaseHelper.getInstance(LocImgActivity.this).updateImageState(image.getName(), 1);
                                                    Looper.prepare();
                                                    Toast.makeText(LocImgActivity.this, "图片上传成功", Toast.LENGTH_LONG).show();
                                                    File thumbFile = new File(User.getThumbDirectoryPath(), image.getName());
                                                    if (thumbFile.exists()) {
                                                        image.setIcon(FileUtils.readFileToBitmap(thumbFile, 1, 90));
                                                    }
                                                    NetImgActivity.isNetUpdate = false;
                                                    Looper.loop();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    } else longClickBuilder.setMessage("这张图片暂时没有信息");

                    longClickBuilder.setNegativeButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            File imageFile = new File(User.getAppDirectoryPath(), fileName);
                            File thumbFile = new File(User.getThumbDirectoryPath(), fileName);
                            if (imageFile.exists()) imageFile.delete();
                            if (thumbFile.exists()) thumbFile.delete();
                            if (image != null)
                                DatabaseHelper.getInstance(LocImgActivity.this).deleteImage(image.getName());
                            Message message = new Message();
                            message.what = 0x001;
                            message.obj = position;
                            mHandler.sendMessage(message);
                        }
                    });
                    longClickBuilder.setPositiveButton("编辑", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(LocImgActivity.this, EditImgActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("image", image);
                            bundle.putString("imageName", fileName);
                            bundle.putBoolean("isFromLoc", true);
                            intent.putExtras(bundle);
                            startActivityForResult(intent, position);
                        }
                    });
                    longClickBuilder.create();
                    longClickBuilder.show();

                }
            });
        }
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x001:
                    int position = (int) msg.obj;
                    mData.remove(position);
                    recyclerAdapter.notifyItemRemoved(position);
                    break;
            }
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "数据提交成功", Toast.LENGTH_SHORT).show();
                Image image = (Image) data.getSerializableExtra("image");
                mData.get(requestCode).setName(image.getName());
                mData.get(requestCode).setDecodeData(image.getDecodeData());
                mData.get(requestCode).setTakeTime(image.getTakeTime());
                mData.get(requestCode).setLocation(image.getLocation());
                recyclerAdapter.notifyItemChanged(requestCode);
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
