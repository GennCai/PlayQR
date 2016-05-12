package genn.playqt;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

import java.io.IOException;

import genn.playqt.Utils.UserObject;
import genn.playqt.Utils.HttpUtil;

public class TestActivity extends AppCompatActivity {
    public static final String URL_LOGIN = "http://192.168.1.110:5000/login";
    public static final String URL_REGISTER = "http://192.168.1.110:5000/register";
    private WebView webView;
    TextView textView;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                int response = (int) msg.obj;
                textView.setText("服务器回复: " + response);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        textView = (TextView) findViewById(R.id.test_web_text);
        final UserObject authObject = new UserObject("Lucy", "123456");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int response = HttpUtil.registerAccount(URL_REGISTER, authObject);
                    Message message = new Message();
                    message.what = 0x123;
                    message.obj  = response;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
