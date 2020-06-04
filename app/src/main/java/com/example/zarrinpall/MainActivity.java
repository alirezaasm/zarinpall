package com.example.zarrinpall;

import androidx.appcompat.app.AppCompatActivity;

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

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    TextView tv;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv=findViewById(R.id.text);
        button=findViewById(R.id.button2);
        Uri data=getIntent().getData();
        ZarinPal.getPurchase(this).verificationPayment(data,new OnCallbackVerificationPaymentListener(){

            @Override
            public void onCallbackResultVerificationPayment(boolean isPaymentSuccess, String refID, PaymentRequest paymentRequest) {
                if(isPaymentSuccess)
                {
                    tv.setText("موفق");
                }else
                    {
                        tv.setText("ناموفق");
                    }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payment();

            }
        });





    }


    public void payment()
    {
        ZarinPal purchase =ZarinPal.getPurchase(this);
        PaymentRequest payment =ZarinPal.getPaymentRequest();

        payment.setMerchantID("0c5db223-a20f-4789-8c88-56d78e29ff63");
        payment.setAmount(100);
        payment.setDescription("تست جهت برنامه");
        payment.setCallbackURL("return://zarinpalpayment");

        purchase.startPayment(payment,new  OnCallbackRequestPaymentListener(){
            @Override
            public void onCallbackResultPaymentRequest(int status, String authority, Uri paymentGatewayUri, Intent intent) {
                if(status==100) startActivity(intent);
                else Toast.makeText(getApplicationContext(),"پرداخت با موفقیت انجام نشد",Toast.LENGTH_LONG).show();
            }
        });

    }
}
