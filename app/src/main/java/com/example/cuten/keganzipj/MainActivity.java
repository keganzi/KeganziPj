package com.example.cuten.keganzipj;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

//Git 확인

public class MainActivity extends AppCompatActivity {
    private EditText etNumber, etMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etNumber = (EditText) findViewById(R.id.et_number);
        etMessage = (EditText) findViewById(R.id.et_message);
    }

    //버튼 클릭 리스너 등록 : 전화하기
    public void onClickCall(View v) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {

/**
 * 사용자 단말기의 권한 중 "전화걸기" 권한이 허용되어 있는지 체크한다.
 * int를 쓴 이유? 안드로이드는 C기반이기 때문에, Boolean 이 잘 안쓰인다.
 */

            int permissionResult = checkSelfPermission(Manifest.permission.CALL_PHONE);

            /* CALL_PHONE의 권한이 없을 때 */
// 패키지: 안드로이드 어플리케이션 아이디
            if (permissionResult == PackageManager.PERMISSION_DENIED) {

/**
 * 사용자가 CALL_PHONE 권한을 한번이라도 거부한 적이 있는지 조사한다.
 * 거부한 이력이 한번이라도 있다면, true를 리턴한다.
 */
                if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {

                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setTitle("권한이 필요합니다.")
                            .setMessage("이 기능을 사용하기 위해서는 단말기의 \"전화걸기\" 권한이 필요합니다. " +
                                    "계속하시겠습니까?")
                            .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{
                                                Manifest.permission.CALL_PHONE}, 1000);
                                    }
                                }
                            })
                            .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText( MainActivity.this, "기능을 취소했습니다.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            })
                            .create()
                            .show();
                } else {//최초로 권한을 요청할 때
// CALL_PHONE 권한을 Android OS 에 요청한다.
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1000);
                }

            }else {/* CALL_PHONE의 권한이 있을 때 */
                Uri n = Uri.parse("tel: " + etNumber.getText());
                startActivity(new Intent(Intent.ACTION_CALL, n));
            }
        }else {/* 사용자의 OS 버전이 마시멜로우 이하일 떄 */
            Uri n = Uri.parse("tel: " + etNumber.getText());
            startActivity(new Intent(Intent.ACTION_CALL, n));
        }
    }

    //버튼 클릭 리스너 등록 : 다이얼 로그 열기
    public void onClickDial(View v) {
        Uri n = Uri.parse("tel: " + etNumber.getText());
        startActivity(new Intent(Intent.ACTION_DIAL, n));
    }

    //버튼 클릭 리스너 등록 : SMS전송
    public void onClickSMS(View v) {
        Uri n = Uri.parse("smsto: " + etNumber.getText());
        Intent intent = new Intent(Intent.ACTION_SENDTO, n);
        String t = etMessage.getText().toString();
        intent.putExtra("sms_body", t);
        startActivity(intent);
    }


    /**
     * 사용자가 권한을 허용했는지 거부했는지 체크
     * reference : http://ande226.tistory.com/136
     * @param requestCode 1000번
     * @param permissions 개발자가 요청한 권한들
     * @param grantResults 권한에 대한 응답들
     * permissions와 grantResults는 인덱스 별로 매칭된다.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 1000) {

/* 요청한 권한을 사용자가 "허용"했다면 인텐트를 띄워라
내가 요청한 게 하나밖에 없기 때문에. 원래 같으면 for문을 돈다.*/
            if ( grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission( this, Manifest.permission.CALL_PHONE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    Uri n = Uri.parse("tel: " + etNumber.getText());
                    startActivity(new Intent(Intent.ACTION_CALL, n));
                }
            } else {
                Toast.makeText(MainActivity.this, "권한 요청을 거부했습니다.", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
