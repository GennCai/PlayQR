package genn.playqt.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import genn.playqt.database.Image;
import genn.playqt.database.User;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public  class HttpUtil {
    private static HttpUtil instance;
    private static OkHttpClient okHttpClient;
    public static final String URL_LOGIN = "http://192.168.1.110:5000/login";
    public static final String URL_REGISTER = "http://192.168.1.110:5000/register";
    public static final String URL_TASKS = "http://192.168.1.110:5000/playqr/api/v1.0/tasks";
    public static final String URL_TASK = "http://192.168.1.110:5000/playqr/api/v1.0/task/";
    public static final String TAG = "Auth Message";
    public static final MediaType MEDIA_TYPE = MediaType.parse("application/octet-stream");

    private static Request.Builder authRequestBuilder;

    private HttpUtil() {
        okHttpClient = new OkHttpClient();
    }

    public static HttpUtil getInstance(){
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

    //public static Request.Builder getAuthRequestBuilder()
    public static Request.Builder configAuthRequestBuilder(Request.Builder requestBuilder, String username, String password) {
        return requestBuilder != null ? requestBuilder.header("Authorization", Credentials.basic(username, password)) : null;
    }

    public static int loginVerify(String url, String username, String password) throws IOException{
        okHttpClient = getClient();
        RequestBody requestBody = new FormBody.Builder().build();
        String credential = Credentials.basic(username, password);
        Request request = new Request.Builder().url(url)
                .header("Authorization", credential)
                .post(requestBody)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        authRequestBuilder = configAuthRequestBuilder(new Request.Builder(), username, password);
        return response.code();
    }

    public static int registerAccount(String url, String username, String password) throws IOException{
        okHttpClient = getClient();
        RequestBody formBody = new FormBody.Builder()
                .add("username",username)
                .add("password", password)
                .build();
        Request request = new Request.Builder().url(url).post(formBody).build();

        Response response = okHttpClient.newCall(request).execute();
        authRequestBuilder = configAuthRequestBuilder(new Request.Builder(), username, password);
        return response.code();
    }

    public int uploadImage(String url, Image image, Callback callback) {
        okHttpClient = getClient();
        RequestBody fileBody = RequestBody.create(MEDIA_TYPE, image.getImageFile());
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image_data", image.getName(), fileBody)
                .addFormDataPart("decode_data", image.getDecodeData())
                .addFormDataPart("take_time", image.getTakeTime())
                .addFormDataPart("position", image.getPosition())
                .build();
        if (authRequestBuilder != null) {
            Request request = authRequestBuilder.url(url).post(requestBody).build();
            okHttpClient.newCall(request).enqueue(callback);
            return 1;
        } else {
            return 0;
        }
    }

    public int getImages(String url, User user) {
        return 0;
    }

    public int getImage(String url, User user) {
        return 0;
    }

    public int updataImage(String url, User user, Image image) {
        return 0;
    }

    public int deleteImage(String url, User user, Image image) {
        return 0;
    }
}
