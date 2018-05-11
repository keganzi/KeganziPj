package com.example.cuten.android_sms;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


//Git 확인

public class MainActivity extends AppCompatActivity {

    EditText num, mass;
    Button button_sms, btn_contacts;
    TextView sms_textView;

    String receiveName = "";
    String receivePhone = "";

    static final int REQUEST_CONTACTS = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         num =  findViewById(R.id.editText);
         mass = findViewById(R.id.editText2);
         button_sms = findViewById(R.id.bt_sms);
        sms_textView = findViewById(R.id.sms_textView);



        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkVerify();
        } else{
            startApp();
        }

        mass.addTextChangedListener(new TextWatcher() {

            String strCur;

            //변화가 있을때 호출되는 API
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 80){
                    mass.setText(strCur);
                    mass.setSelection(start);
                }else{
                    sms_textView.setText(String.valueOf(s.length()) + "/80");
                }
            }

            //입력되기 전에 호출되는 API
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                strCur = s.toString();
            }

            //입력이 끝났을때 호출되는 API
            @Override
            public void afterTextChanged(Editable editable) {
                if(mass.getLineCount() >= 6){
                    mass.setText(strCur);
                    mass.setSelection(mass.length());
                }

            }
        });


        //Find Button
        btn_contacts = findViewById(R.id.btn_contacts);

        //Click Listener
        btn_contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //단말기에 내장되어 있는 연락처앱을 호출한다.
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                //호출 후, 연락처앱에서 전달되는 결과물을 받기 위해 startActivityForResult로 실행한다.
                startActivityForResult(intent, REQUEST_CONTACTS);
            }
        });




        button_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String inputText = num.getText().toString();
                String inputText2 = mass.getText().toString();

                //Log.i("inputText   ====  " , inputText );
                //Log.i("inputtext2   ====  " , inputText2 );


                if(inputText.length()>0 && inputText2.length()>0) {

                 //   Log.i("inputTextLength  =  " , inputText.toString() );
                 //   Log.i("inputtext2 Length=  " , inputText2.toString() );

                    sendSMS(inputText, inputText2);
                    //Toast.makeText(getBaseContext(), inputText+"\n"+inputText2, Toast.LENGTH_SHORT).show();
                    Toast.makeText(getBaseContext(), "전송성공", Toast.LENGTH_SHORT).show();
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CONTACTS) {
            Cursor cursor = getContentResolver().query(data.getData(),
                    new String[] { ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER },
                    null, null, null);

            cursor.moveToFirst();
            //이름획득
            receiveName = cursor.getString(0);
            //전화번호 획득
            receivePhone = cursor.getString(1);
            cursor.close();

            Toast.makeText(getBaseContext(), "연락처 이름 : " + receiveName + "\n연락처 전화번호 : " + receivePhone, Toast.LENGTH_SHORT).show();

            num.setText(receivePhone);
        }
    }
}