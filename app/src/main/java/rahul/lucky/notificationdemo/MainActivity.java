package rahul.lucky.notificationdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import okhttp3.ResponseBody;
import rahul.lucky.notificationdemo.model.RequestNotificaton;
import rahul.lucky.notificationdemo.model.SendNotificationModel;
import rahul.lucky.notificationdemo.network.BaseApiService;
import rahul.lucky.notificationdemo.network.RetrofitClient;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity {

    private String device_token;
    private BaseApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                device_token = instanceIdResult.getToken();
            }
        });

        final EditText etTitle = findViewById(R.id.etTitle);
        final EditText etDesc = findViewById(R.id.etDesc);
        Button btnHit = findViewById(R.id.btnHit);

        btnHit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getValuesAndHitServer(etTitle, etDesc);
            }
        });

    }

    private void getValuesAndHitServer(EditText etTitle, EditText etDesc) {
        String title = etTitle.getText().toString();
        String desc = etDesc.getText().toString();

        sendNotificationToPatner(title, desc);

    }

    private void sendNotificationToPatner(String title, String desc) {

        SendNotificationModel sendNotificationModel = new SendNotificationModel(title, desc);
        RequestNotificaton requestNotificaton = new RequestNotificaton();
        requestNotificaton.setSendNotificationModel(sendNotificationModel);
        //token is id , whom you want to send notification ,
        requestNotificaton.setToken(device_token);

        apiService = RetrofitClient.getClient().create(BaseApiService.class);
        retrofit2.Call<ResponseBody> responseBodyCall = apiService.sendChatNotification(requestNotificaton);

        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                Log.d("kkkk", "done");
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {

            }
        });
    }


}
