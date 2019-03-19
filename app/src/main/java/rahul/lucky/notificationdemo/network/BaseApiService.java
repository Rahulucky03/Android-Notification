package rahul.lucky.notificationdemo.network;

import okhttp3.ResponseBody;
import rahul.lucky.notificationdemo.model.RequestNotificaton;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

import static rahul.lucky.notificationdemo.utils.AppConstants.SERVER_KEY;

public interface BaseApiService {

    @Headers({"Authorization: key=" + SERVER_KEY,
            "Content-Type:application/json"})
    @POST("fcm/send")
    Call<ResponseBody> sendChatNotification(@Body RequestNotificaton requestNotificaton);


}
