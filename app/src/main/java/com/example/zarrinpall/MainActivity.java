package com.example.zarrinpall;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zarinpal.ewallets.purchase.OnCallbackRequestPaymentListener;
import com.zarinpal.ewallets.purchase.OnCallbackVerificationPaymentListener;
import com.zarinpal.ewallets.purchase.PaymentRequest;
import com.zarinpal.ewallets.purchase.ZarinPal;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    String domain="http://192.168.42.60:8000";
    String before_payment_url="/payment/request/";
    String after_payment_url="/payment/verify/";
    TextView tv;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv=findViewById(R.id.text);
        button=findViewById(R.id.button2);
        Uri data=getIntent().getData();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkHttpClient client=new OkHttpClient();
                final String total_id="3";
                final Long total_pr=500L;
                RequestBody formBody = new FormBody.Builder()
                        .add("total_pr",total_pr.toString())
                        .add("total_id",total_id)
                        .build();

                Request request = new Request.Builder()
                        .url(domain+before_payment_url)
                        .post(formBody)
                        .addHeader("Accept","application/json")
                        .addHeader("Authorization","Token d719044db31a6156183d2c919ff6d43ba19d487a")
                        .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            tv.setText("خريد ناموفق");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()){
                                //if course is free i send you 201
                                if (response.code()==201)
                                    tv.setText("خريد موفق");
                                else
                                    payment(total_pr,total_id);

                            }
                            else
                            {
                                tv.setText("خريد ناموفق");
                            }
                        }
                    });

            }
        });


        ZarinPal.getPurchase(this).verificationPayment(data,new OnCallbackVerificationPaymentListener(){

            @Override
            public void onCallbackResultVerificationPayment(boolean isPaymentSuccess, final String refID, PaymentRequest paymentRequest) {
                if(isPaymentSuccess)
                {
                    OkHttpClient client=new OkHttpClient();

                    HttpUrl after_url = HttpUrl.parse(domain+after_payment_url).newBuilder()
                            .addQueryParameter("Status", "OK")
                            .addQueryParameter("Authority", paymentRequest.getAuthority())
                            .build();

                    Request request = new Request.Builder()
                            .url(after_url)
                            .addHeader("Accept","application/json")
                            .addHeader("Authorization","Token d719044db31a6156183d2c919ff6d43ba19d487a")
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            tv.setText("خريد ناموفق لطفا با پشتیبان تماس بگیرید");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()){
                                if (response.code()==226)
                                    //this is duplicate
                                    tv.setText("پرداخت قبلا انجام شده است");
                                else
                                    tv.setText(refID + "پرداخت با موفقیت انجام شد");

                            }
                            else
                            {
                                tv.setText("پرداخت ناموفق");
                            }
                        }
                    });
                }
                 else
                    {
                        tv.setText("پرداخت ناموفق");
                    }
            }
        });





    }


    public void payment(Long amount ,String courses )
    {
        ZarinPal purchase =ZarinPal.getPurchase(this);
        PaymentRequest payment =ZarinPal.getPaymentRequest();

        payment.setMerchantID("0c5db223-a20f-4789-8c88-56d78e29ff63");
        payment.setAmount(amount);
        payment.setDescription("List of courses id ==> " +
                courses + " Total price ==> " + amount.toString());
        payment.setCallbackURL("return://tabesh");

        purchase.startPayment(payment,new  OnCallbackRequestPaymentListener(){
            @Override
            public void onCallbackResultPaymentRequest(int status, String authority, Uri paymentGatewayUri, Intent intent) {
                if(status==100) startActivity(intent);
                else Toast.makeText(getApplicationContext(),"پرداخت با موفقیت انجام نشد",Toast.LENGTH_LONG).show();}
        });

    }
}
