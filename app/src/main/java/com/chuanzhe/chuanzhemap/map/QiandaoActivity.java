package com.chuanzhe.chuanzhemap.map;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chuanzhe.chuanzhemap.R;
import com.chuanzhe.chuanzhemap.bean.MyUser;
import com.chuanzhe.chuanzhemap.bean.PointItems;
import com.chuanzhe.chuanzhemap.bean.Qiandao;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class QiandaoActivity extends AppCompatActivity {
    @BindView(R.id.et_buhuoliang)
    EditText et_buhuo;
    @BindView(R.id.et_cunhuoliang)
    EditText et_cunhuo;
    @BindView(R.id.et_dingdanhao)
    EditText et_dingdan;

    @BindView(R.id.btn_qiandao)
    Button btn_qiandao;
    private PointItems items;
    private int cunhuo = 0;
    private int buhuo = 0;
    private String dingdanhao;
    String id;



    @OnClick(R.id.btn_qiandao) void qiandao(){

        cunhuo = Integer.valueOf(et_cunhuo.getText().toString().trim());
        buhuo = Integer.valueOf(et_buhuo.getText().toString().trim());
        dingdanhao = et_dingdan.getText().toString().trim();
        if(TextUtils.isEmpty(dingdanhao)){
            et_dingdan.setError("订单号不能为空");
        }else {
            Qiandao qiandao = new Qiandao();
            qiandao.setCunhuoliang(cunhuo);
            qiandao.setBuhuoliang(buhuo);
            qiandao.setDingdanhao(dingdanhao);
            qiandao.setUser(BmobUser.getCurrentUser(MyUser.class));
            qiandao.setItems(items);

            qiandao.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    id=s;
                    if(e == null){
                        PointItems pointItems = new PointItems();
                        pointItems.setCunhuoliang(cunhuo);
                        pointItems.setBuhuoliang(buhuo);
                        pointItems.update(items.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                  finish();
                                }else {
                                    Qiandao q= new Qiandao();
                                    q.setObjectId(id);
                                    q.delete(new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {

                                        }
                                    });
                                }

                            }
                        });
                    }else {
                        toast("签到失败");
                    }

                }
            });
        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qiandao);
        ButterKnife.bind(this);
        items = (PointItems) getIntent().getSerializableExtra("items");
    }


    private void toast(String s){
        Toast.makeText(QiandaoActivity.this,s, Toast.LENGTH_SHORT).show();

    }
}
