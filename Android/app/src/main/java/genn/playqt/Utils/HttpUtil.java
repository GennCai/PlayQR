package genn.playqt.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public  class HttpUtil {
    public static HttpUtil instance;
    public static OkHttpClient okHttpClient;
    public static final String URL = "http://www.baidu.com";
    public static final String TAG = "Auth Message";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private HttpUtil() {
        okHttpClient = new OkHttpClient();
    }

    public HttpUtil getInstance(){
        return instance == null ? new HttpUtil() : instance;
    }
    public static OkHttpClient getClient() {
        return okHttpClient == null ? new OkHttpClient() : okHttpClient;
    }

    private static String getReponseContent(Response response) {
        InputStream inputStream = response.body().byteStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        StringBuilder content = new StringBuilder();
        try {
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return content.toString();
    }

    public static int loginVerify(String url, final UserObject authObject) throws IOException{
        okHttpClient = getClient();
        String credential = Credentials.basic(authObject.getUsername(), authObject.getPassword());
        Request request = new Request.Builder().url(url).header("Authorization", credential).build();

        Response response = okHttpClient.newCall(request).execute();

        return response.code();
    }

    public static int registerAccount(String url, UserObject authObject) {
        okHttpClient = getClient();
        RequestBody formBody = new FormBody.Builder()
                .add("username", authObject.getUsername())
                .add("password", authObject.getPassword())
                .build();
        Request request = new Request.Builder().url(url).post(formBody).build();

        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
            return response.code();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 400;
    }

    public int uploadImage(String url, UserObject authObject, File imageFile, ImageObject imageObject) {
        return 0;
    }

    public int getImages(String url, UserObject userObject) {
        return 0;
    }

    public int getImage(String url, UserObject userObject) {
        return 0;
    }

    public int updataImage(String url, UserObject userObject, ImageObject imageObject) {
        return 0;
    }

    public int deleteImage(String url, UserObject userObject, ImageObject imageObject) {
        return 0;
    }
}
