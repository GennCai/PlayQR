package genn.playqt.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.Bind;
import butterknife.ButterKnife;
import genn.playqt.R;
import genn.playqt.adapter.RecyclerAdapter;
import genn.playqt.database.Image;
import genn.playqt.utils.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class NetImgActivity extends AppCompatActivity {
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @Bind(R.id.recycle_title_view)
    TextView mRecyclerTitle;

    @Bind(R.id.show_recycle_container)
    LinearLayout mLayoutContainer;

    private RecyclerAdapter mRecyclerAdapter;
    private static List<Image> imageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loc_img);
        ButterKnife.bind(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (imageList == null) {
            if (HttpUtil.getInstance().getImages(HttpUtil.URL_TASKS, mCallback) == 0) {
                Toast.makeText(this, "请先登陆!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }
        } else {
            mRecyclerAdapter = new RecyclerAdapter(imageList);
            mRecyclerView.setAdapter(mRecyclerAdapter);
        }
    }

    Callback mCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            mHandler.sendEmptyMessage(0x001);
            e.printStackTrace();
        }
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            Message mMessage = new Message();
            mMessage.what = 0x002;
            mMessage.obj = response.body().string();
            mHandler.sendMessage(mMessage);
        }
    };
    int count = 0;
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
                    imageList.get(count).setIcon((Bitmap)msg.obj);
                    count++;
                    Log.d("数据接收完成", count + "");
                    if (count == imageList.size()) {
                        mRecyclerAdapter = new RecyclerAdapter(imageList);
                        mRecyclerView.setAdapter(mRecyclerAdapter);
                        pool.shutdown();
                    }
            }
        }
    };

    ExecutorService pool = Executors.newFixedThreadPool(4);
    private void parseJSON(String jsonData) {
        Gson gson = new Gson();
        imageList = gson.fromJson(jsonData, new TypeToken<List<Image>>() {
        }.getType());
        for (final Image image : imageList) {
            pool.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        Bitmap bitmap = HttpUtil.getInstance().downloadImage(HttpUtil.URL_DOWNLOAD, image.getName());
                        Bitmap icon = ThumbnailUtils.extractThumbnail(bitmap, TakePhotoActivity.THUMBNAIL_WIDTH, TakePhotoActivity.THUMBNAIL_HEIGHT);
                        Message message = new Message();
                        message.what = 0x003;
                        message.obj = icon;
                        mHandler.sendMessage(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
