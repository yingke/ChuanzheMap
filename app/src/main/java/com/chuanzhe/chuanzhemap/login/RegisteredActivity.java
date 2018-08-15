package com.chuanzhe.chuanzhemap.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chuanzhe.chuanzhemap.MainActivity;
import com.chuanzhe.chuanzhemap.R;
import com.chuanzhe.chuanzhemap.bean.MyUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class RegisteredActivity extends AppCompatActivity {
    private static final String TAG = "RegisteredActivity";
    @BindView(R.id.et_username)
    EditText et_username;
    @BindView(R.id.et_pass) EditText et_pass;
    @BindView(R.id.et_passagin) EditText getEt_passagin;
    @BindView(R.id.btn_login)
    Button btn_login;
    @BindView(R.id.btn_Registered) Button btn_registerd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered);
        ButterKnife.bind(this);

    }

    public void Registered(View view){
        String username=et_username.getText().toString();
        String userpass=et_pass.getText().toString();
        String userpassagain=getEt_passagin.getText().toString();

        if (TextUtils.isEmpty(username)){
            et_username.setError(" 用户名不能为空");
        }else if (TextUtils.isEmpty(userpass)){
            et_pass.setError("密码不能为空");
        }else if (TextUtils.isEmpty(userpassagain)){
            getEt_passagin.setError("密码不能为空");
        }else if(!userpass.equals(userpassagain)){
            getEt_passagin.setError("两次密码输入不一致");
        }else{
            MyUser muser = new MyUser();
            muser.setUsername(username);
            muser.setPassword(userpass);
            muser.setIsAdmin(1);

            muser.signUp(new SaveListener<MyUser>() {
                @Override
                public void done(MyUser s, BmobException e) {
                    if(e==null){
                        Toast.makeText(RegisteredActivity.this, "注册成功,请登录", Toast.LENGTH_SHORT).show();
                        startLogin();
                    }else{
                        Log.i(TAG, "Registered: "+e);
                        Toast.makeText(RegisteredActivity.this, "注册失败："+e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    }

    public void ToLoginActivity(View view){
        startLogin();
    }

    private  void startLogin(){
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }
}
