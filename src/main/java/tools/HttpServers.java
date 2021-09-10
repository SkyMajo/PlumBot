package tools;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;

import static conf.Config.PPY_URL;

public class HttpServers {
    private OkHttpClient okHttpClient;
    private  FormBody.Builder builder ;
    private MultipartBody.Builder mutipartBody;
    public HttpServers(){
        okHttpClient = new OkHttpClient();
        builder = new FormBody.Builder();
        mutipartBody = new MultipartBody.Builder();
        mutipartBody.setType(MultipartBody.FORM);


    }

    public HttpServers setValue(String key,String value){
        builder.add(key,value);
        return this;
    }
    public HttpServers setFormValue(String key , String value){
        mutipartBody.addFormDataPart(key,value);
        return this;
    }

    public void postFormBody(String url ,CallBack callBack){
        MultipartBody body = mutipartBody.build();
        Request request = new Request.Builder()
                .url(PPY_URL+url)
                .post(body)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callBack.onFail(e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();


                callBack.onSuccess(json);

            }
        });

    }

}
