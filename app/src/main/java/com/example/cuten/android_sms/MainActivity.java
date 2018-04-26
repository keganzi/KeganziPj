package com.example.cuten.android_sms;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


//Git 확인

public class MainActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText num = (EditText) findViewById(R.id.editText);
        final EditText mass = (EditText) findViewById(R.id.editText2);
        Button button = (Button)findViewById(R.id.bt_sms);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkVerify();
        } else{
            startApp();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String inputText = num.getText().toString();
                String inputText2 = mass.getText().toString();

                if(inputText.length()>0 && inputText2.length()>0) {
                    sendSMS(inputText, inputText2); Toast.makeText(getBaseContext(), inputText+"\n"+inputText2, Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(getBaseContext(), "전화번호와 메시지를 입력해주세요.", Toast.LENGTH_SHORT).show();
            }});

    }

    //마쉬멜로 이상 권한설정 체크
    @TargetApi(Build.VERSION_CODES.M)
    private void checkVerify(){
        if(checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){


           if(shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS)){

           }

           requestPermissions(new String[]{Manifest.permission.SEND_SMS},1);
        }else{
            startApp();
        }
    }
    //리퀘스트를 통해 권한이 허용되었는지와 거부되었는지를 확인하여 각각 실행하도록 하고 거부시에 허용할 수 있는 창을 띄우게 한다.

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode ==1) {
            if (grantResults.length > 0) {
                for (int i = 0; i < grantResults.length; ++i) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        new AlertDialog.Builder(this).setTitle("권한알림").setMessage("권한을 허용해주세요").setPositiveButton("종료", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).setNegativeButton("권한설정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                        .setData(Uri.parse("pakage:" + getApplicationContext().getPackageName()));
                                getApplicationContext().startActivity(intent);
                            }
                        }).setCancelable(false).show();
                        return;
                    }
                }
                startApp();
            }
        }
    }
    private void startApp(){
        Toast.makeText(getApplicationContext(),"앱을 실행합니다", Toast.LENGTH_SHORT).show();
    }

    private void sendSMS(String phoneNumber, String message)
    {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
        Toast.makeText(getApplicationContext(),"전송 성공", Toast.LENGTH_SHORT).show();
    }
}