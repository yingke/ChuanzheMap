package com.chuanzhe.chuanzhemap.map;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chuanzhe.chuanzhemap.R;
import com.chuanzhe.chuanzhemap.adapter.DetalPointAdapter;
import com.chuanzhe.chuanzhemap.bean.PointItems;
import com.chuanzhe.chuanzhemap.bean.Qiandao;
import com.chuanzhe.chuanzhemap.utility.DownloadImageTask;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class PointDetalActivity extends AppCompatActivity {
    private PointItems items;
    @BindView(R.id.tv_detal_shaopname)
    TextView tv_shopname;
    @BindView(R.id.tv_detal_bianhao)TextView tv_bianhao;
    @BindView(R.id.tv_detal_kehu)TextView tv_kehu;
    @BindView(R.id.tv_detal_phone)TextView tv_phone;
    @BindView(R.id.tv_detal_iszs) TextView tv_iszs;
    @BindView(R.id.tv_detal_vip)TextView tv_vip;
    @BindView(R.id.detalfab)
    FloatingActionButton fab;
    @BindView(R.id.main_iv_placeholder)
    ImageView imageView;

    @BindView(R.id.detal_rec) RecyclerView recyclerView;
    private DetalPointAdapter adapter;
    private List<Qiandao> qiandaoList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_detal);
        ButterKnife.bind(this);
        items = (PointItems) getIntent().getSerializableExtra("items");
        initview();
        getDetal(items);
    }

    public  void qiandao(View view){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("items",items);
        intent.putExtras(bundle);
        intent.setClass(PointDetalActivity.this, QiandaoActivity.class);

        startActivity(intent);
    }

    private void initview() {
        tv_shopname.setText("店铺："+items.getShopname());
        tv_bianhao.setText("编号："+items.getUnm());
        tv_kehu.setText("客户："+items.getKehu());
        tv_phone.setText("电话："+items.getKehuphone());
        new DownloadImageTask(imageView).execute(items.getImgurl());;
        if (items.getIsfree()==0){
            tv_iszs.setText("赠送：是");
        }else {
            tv_iszs.setText("赠送：否");
        }
        if (items.getIsvip()==0){
            tv_vip.setText("VIP：是");
        }else {
            tv_vip.setText("VIP：否");
        }
        qiandaoList = new ArrayList<Qiandao>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new DetalPointAdapter(this,qiandaoList);
        recyclerView.setAdapter(adapter);

    }

    private void getDetal(PointItems items) {
        BmobQuery<Qiandao> query = new BmobQuery<Qiandao>();
        query.addWhereEqualTo("items",new BmobPointer(items));
        query.order("-updatedAt");
       final String s= items.getObjectId();
        query.findObjects(new FindListener<Qiandao>() {
            @Override
            public void done(List<Qiandao> list, BmobException e) {
                qiandaoList.addAll(list);
                adapter.notifyDataSetChanged();
            }
        });

    }

}
