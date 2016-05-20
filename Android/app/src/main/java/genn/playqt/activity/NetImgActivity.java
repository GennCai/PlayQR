package genn.playqt.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import genn.playqt.R;
import genn.playqt.adapter.RecyclerAdapter;
import genn.playqt.database.DatabaseHelper;
import genn.playqt.database.Image;
import genn.playqt.database.User;
import genn.playqt.utils.FileUtils;
import genn.playqt.utils.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class NetImgActivity extends Activity {
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @Bind(R.id.recycle_title_view)
    TextView mRecyclerTitle;

    @Bind(R.id.show_recycle_container)
    LinearLayout mLayoutContainer;

    private RecyclerAdapter mRecyclerAdapter;

    public List<Image> imageList = new ArrayList<>();
    public static List<Image> cacheImageList = new ArrayList<>();

    public static boolean isNetUpdate = false;

    private View showPhotoView;
    private ImageView showPhoto;
    private Bitmap showBitmap;

    private HttpUtil mHttpUtil;
    private DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loc_img);
        ButterKnife.bind(this);

        mHttpUtil = HttpUtil.getInstance();
        mDatabaseHelper = DatabaseHelper.getInstance(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        imageList.addAll(cacheImageList);
    }

    @Override
    protected void onStart() {
        super.onStart();
     //   count = imageList.size();
        if (!isNetUpdate) {
            if (HttpUtil.getInstance().getImages(HttpUtil.URL_TASKS, mCallback) == 0 || !User.getInstance().isLogin()) {
                Toast.makeText(this, "请先登陆!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }
        } else {
            if (imageList.size() == 0) {
                mLayoutContainer.setGravity(Gravity.CENTER);
                mRecyclerView.setVisibility(View.GONE);
                mRecyclerTitle.setVisibility(View.VISIBLE);
                mRecyclerTitle.setTextSize(30);
                mRecyclerTitle.setText("您还有没上传过照片");
            } else {
                mRecyclerAdapter = new RecyclerAdapter(imageList);
                mRecyclerView.setAdapter(mRecyclerAdapter);
                initAdapterListener(mRecyclerAdapter);
            }
        }
    }
    @OnClick(R.id.recycle_title_view)
    public void updateOnClickTitle(View view) {
        HttpUtil.getInstance().getImages(HttpUtil.URL_TASKS, mCallback);
    }

    Callback mCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            mHandler.sendEmptyMessage(0x005);
            e.printStackTrace();
        }
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (response.code() == 205) {
                mHandler.sendEmptyMessage(0x005);
                return;
            } else if (response.isSuccessful()) {
                Message mMessage = new Message();
                mMessage.what = 0x002;
                mMessage.obj = response.body().string();
                mHandler.sendMessage(mMessage);
                return;
            } else if (response.code() == 401) {
                mHandler.sendEmptyMessage(0x001);
            }
            Log.d(HttpUtil.TAG_NET, response.code() + response.body().string());
        }
    };

    private void initAdapterListener(RecyclerAdapter adapter) {

        final AlertDialog.Builder clickBuilder = new AlertDialog.Builder(NetImgActivity.this);

        adapter.setItemClickListener(new RecyclerAdapter.OnRecycleItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (imageList.get(position).getImageFile() != null) {
                    showPhotoView = LayoutInflater.from(NetImgActivity.this).inflate(R.layout.activity_show_photo, null);
                    showPhoto = (ImageView)showPhotoView.findViewById(R.id.show_photo_image);

                    showBitmap = FileUtils.readFileToBitmap(imageList.get(position).getImageFile(), 1, 90);
                    showPhoto.setImageBitmap(showBitmap);
                    clickBuilder.setTitle(imageList.get(position).getName());
                    clickBuilder.setView(showPhotoView);
                    clickBuilder.create();
                    clickBuilder.show();
                } else {
                    Toast.makeText(NetImgActivity.this, "tmpFile没有被存入imageList", Toast.LENGTH_LONG).show();
                }
            }
        });

        adapter.setItemLongClickListener(new RecyclerAdapter.OnRecycleItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, final int position) {
                final Image image = imageList.get(position);

                final AlertDialog.Builder build = new AlertDialog.Builder(NetImgActivity.this);
                String[] items = new String[]{image.getDecodeData(), image.getTakeTime(), image.getLocation()};
                build.setTitle(imageList.get(position).getName());
                build.setItems(items, null);
                if (mDatabaseHelper.queryImage(image.getName()) == null) {
                    build.setNeutralButton("保存", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            File targetFile = new File(User.getAppDirectoryPath(), image.getName());
                            if (FileUtils.copyFile(image.getImageFile(), targetFile)) {
                                Toast.makeText(NetImgActivity.this, "图片保存成功!", Toast.LENGTH_LONG).show();
                            }
                            FileUtils.saveBitmapToFile(image.getIcon(),
                                    User.getThumbDirectoryPath() + "/" + image.getName(), 100);
                            mDatabaseHelper.insertImage(image);
                            mDatabaseHelper.updateImageState(image.getName(), 1);

                        }
                    });
                }
                build.setPositiveButton("编辑", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(NetImgActivity.this, EditImgActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("image", image);
                        bundle.putBoolean("isFromLoc", false);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, position);
                    }
                });

                build.setNegativeButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mHttpUtil.deleteImage(HttpUtil.URL_TASK, image.getId(), new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Looper.prepare();
                                Toast.makeText(NetImgActivity.this, "删除数据失败", Toast.LENGTH_LONG).show();
                                Looper.loop();
                            }
                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                Looper.prepare();
                                if (response.code() == 209) {
                                    Toast.makeText(NetImgActivity.this, response.body().string(), Toast.LENGTH_LONG).show();
                                } else if (response.isSuccessful()) {
                                    Toast.makeText(NetImgActivity.this, "远程数据删除成功!", Toast.LENGTH_LONG).show();
                                    if (mDatabaseHelper.queryImage(image.getName()) != null) {
                                        mDatabaseHelper.updateImageState(image.getName(), 0);
                                    }
                                    Message message = new Message();
                                    message.what = 0x006;
                                    message.obj = position;
                                    mHandler.sendMessage(message);
                                }
                                Looper.loop();
                            }
                        });
                    }
                });
                build.create();
                build.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Toast.makeText(this, "数据提交成功", Toast.LENGTH_SHORT).show();
            Image image = (Image) data.getSerializableExtra("image");
            imageList.get(requestCode).setName(image.getName());
            imageList.get(requestCode).setTakeTime(image.getTakeTime());
            imageList.get(requestCode).setLocation(image.getLocation());
            imageList.get(requestCode).setDecodeData(image.getDecodeData());
            mRecyclerAdapter.notifyItemChanged(requestCode);
        }
    }
    int netRequestCount = 0;
    static List<Image> netImages;
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 0x001:
                    mLayoutContainer.setGravity(Gravity.CENTER);
                    mRecyclerView.setVisibility(View.GONE);
                    mRecyclerTitle.setVisibility(View.VISIBLE);
                    mRecyclerTitle.setTextSize(30);
                    mRecyclerTitle.setText("貌似网路出现问题, 待会再试试吧");
                    break;
                case 0x002:
                    parseJSON((String)msg.obj);
                    break;
                case 0x003:
                    netRequestCount++;
                    Log.d("数据接收完成", netRequestCount + "");
                    if (netRequestCount == netImages.size()) {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mRecyclerTitle.setVisibility(View.GONE);
                        mRecyclerAdapter = new RecyclerAdapter(imageList);
                        mRecyclerView.setAdapter(mRecyclerAdapter);
                        initAdapterListener(mRecyclerAdapter);
                        cacheImageList = imageList;
                        isNetUpdate = true;
                        pool.shutdown();
                    }
                    break;
                case 0x005:
                    mLayoutContainer.setGravity(Gravity.CENTER);
                    mRecyclerView.setVisibility(View.GONE);
                    mRecyclerTitle.setVisibility(View.VISIBLE);
                    mRecyclerTitle.setTextSize(30);
                    mRecyclerTitle.setText("您还有没上传过照片, 点击刷新");
                    isNetUpdate = true;
                    break;
                case 0x006:
                    int position = (int)msg.obj;
                    imageList.remove(position);
                    cacheImageList = imageList;
                    mRecyclerAdapter.notifyItemRemoved(position);
            }
        }
    };

    ExecutorService pool = Executors.newFixedThreadPool(4);

    private void parseJSON(String jsonData) {
        Log.d("JsonObject", jsonData);
        Gson gson = new Gson();
        netImages = gson.fromJson(jsonData, new TypeToken<List<Image>>() {
        }.getType());
        if (cacheImageList.size() > 0) {
            for (Image image : cacheImageList) {
                netImages.remove(image);
            }
        }
        imageList.addAll(netImages);
        for (final Image image : netImages) {
            pool.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        Bitmap bitmap = HttpUtil.getInstance().downloadImage(HttpUtil.URL_DOWNLOAD, image.getName());
                        File tmpFile = File.createTempFile("tmpImage", null);
                        if (FileUtils.saveBitmapToFile(bitmap, tmpFile, 100)) {
                            image.setImageFile(tmpFile);
                            Bitmap icon = ThumbnailUtils.extractThumbnail(bitmap, Image.THUMB_WIDTH, Image.THUMB_HEIGHT);
                            image.setIcon(icon);
                        }
                        mHandler.sendEmptyMessage(0x003);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
