package com.chuanzhe.chuanzhemap.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chuanzhe.chuanzhemap.MainActivity;
import com.chuanzhe.chuanzhemap.R;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        et_username.setText("gao");
        et_pass.setText("123456");


    /*    //初始化Bomb
        Bmob.initialize(this, C.Bmob_APPID);
        MyUser userInfo = BmobUser.getCurrentUser(MyUser.class);
        if(userInfo!=null){
            startActivity(new Intent(LoginActivity.this,MapListActivity.class));
        }
*/



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
}
