package tools;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface CallBack  {
   public void onSuccess(String data);
   public void onFail(String error);
}
