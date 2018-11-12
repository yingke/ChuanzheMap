package com.chuanzhe.chuanzhemap.login;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chuanzhe.chuanzhemap.MainActivity;
import com.chuanzhe.chuanzhemap.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.btn_login)
    Button bnt_login;
    @BindView(R.id.btn_Registered) Button btn_reg;
    @BindView(R.id.et_username)
    EditText et_username;
    @BindView(R.id.et_pass) EditText et_pass;

    private boolean mPermissionEnabled = false;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

       // isPermissionOK();

     /*   et_username.setText("gao");
        et_pass.setText("123456");*/

    }

    /**
     *  如果没有帐号 跳转到注册界面
     * @param view
     */
    public  void ToRegistered(View view){
       startActivity(new Intent(this,RegisteredActivity.class));


    }

    /**
     * 登录
     * @param view
     */
    public void login(View view){
        String username=et_username.getText().toString();
        String userpass=et_pass.getText().toString();
        if (TextUtils.isEmpty(username)){
            et_username.setError(" 用户名不能为空");
        }else if (TextUtils.isEmpty(userpass)){
            et_pass.setError("密码不能为空");
        }else {
            BmobUser bu2 = new BmobUser();
            bu2.setUsername(username);
            bu2.setPassword(userpass);
            bu2.login(new SaveListener<BmobUser>() {

                @Override
                public void done(BmobUser bmobUser, BmobException e) {
                    if(e==null){

                        Toast.makeText(LoginActivity.this, "登录成功~~", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();
                        //通过BmobUser user = BmobUser.getCurrentUser()获取登录成功后的本地用户信息
                        //如果是自定义用户对象MyUser，可通过MyUser user = BmobUser.getCurrentUser(MyUser.class)获取自定义用户信息
                    }else{
                        // loge(e);
                        Toast.makeText(LoginActivity.this, "登录失败"+e, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }



    /*#########################动态获取权限###########################################*/
    @TargetApi(Build.VERSION_CODES.M)
    private boolean checkPermission() {
        boolean ret = true;
        List<String> permissionsNeeded = new ArrayList<String>();
        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            permissionsNeeded.add("位置权限");
        }
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionsNeeded.add("GPS");
        }
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissionsNeeded.add("文件管理权限");
        }
        if (!addPermission(permissionsList, Manifest.permission.CAMERA)) {
            permissionsNeeded.add("拍照权限");
        }
        if (!addPermission(permissionsList, Manifest.permission.READ_PHONE_STATE)) {
            permissionsNeeded.add("运行时权限");
        }

        if (permissionsNeeded.size() > 0) {
            // Need Rationale
            String message = "本程序需要获取 " + permissionsNeeded.get(0);
            for (int i = 1; i < permissionsNeeded.size(); i++) {
                message = message + ", " + permissionsNeeded.get(i);
            }
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permissionsList.get(0))) {
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }
                        });
            }
            else {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            }
            ret = false;
        }

        return ret;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        boolean ret = true;
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            ret = false;
        }

        return ret;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(LoginActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    private boolean isPermissionOK() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mPermissionEnabled = true;
            return true;
        }
        else {
            return checkPermission();
        }
    }
    /*####################################################################*/

}
