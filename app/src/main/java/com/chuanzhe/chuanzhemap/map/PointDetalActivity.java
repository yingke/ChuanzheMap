package com.chuanzhe.chuanzhemap.map;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapException;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.NaviPara;
import com.chuanzhe.chuanzhemap.MainActivity;
import com.chuanzhe.chuanzhemap.R;
import com.chuanzhe.chuanzhemap.adapter.DetalPointAdapter;
import com.chuanzhe.chuanzhemap.bean.MapProject;
import com.chuanzhe.chuanzhemap.bean.MyUser;
import com.chuanzhe.chuanzhemap.bean.PointItems;
import com.chuanzhe.chuanzhemap.bean.Qiandao;
import com.chuanzhe.chuanzhemap.utility.C;
import com.chuanzhe.chuanzhemap.utility.DownloadImageTask;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class PointDetalActivity extends AppCompatActivity {
    private PointItems items;
    private String id;
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


      //  getDetal(items);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detal, menu);
        if(items.getIsDelete() == 1){
            return false;
        }else {
            return true;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_edit) {
            if (items.getAmyuser().getObjectId().equals(BmobUser.getCurrentUser(MyUser.class).getObjectId()) ){
                Toast.makeText(this,"编辑",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                MapProject project = items.getMapProject();
                bundle.putSerializable("id",project);
                bundle.putSerializable("point",items);
                bundle.putString(C.ACTION, C.EDIT);
                intent.putExtras(bundle);
                intent.setClass(PointDetalActivity.this, AddItemActivity.class);
                startActivity(intent);
            }else {
                Toast.makeText(this,"您无权修改",Toast.LENGTH_SHORT).show();
            }

            return true;
        }else if (item.getItemId() == R.id.action_favorite){
            PointItems updataitem = new PointItems();
            updataitem.setIsfavorite(1);
            updataitem.update(items.getObjectId(),new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if(e==null){
                      toast("修改成功");

                    }else{
                        toast("修改失败");
                    }

                }
            });

            return true;

        }else if (item.getItemId() == R.id.action_delete){

            dialog();

            return true;

        }


        return super.onOptionsItemSelected(item);
    }

    public void daohang(View view){
        NaviPara naviPara = new NaviPara();
        naviPara.setTargetPoint(new LatLng(items.getLatitude(),items.getLongitude()));

        try {
            AMapUtils.openAMapNavi(naviPara,this);
        } catch (AMapException e) {
            e.printStackTrace();
        }
    }
    public  void qiandao(View view){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("items",items);
        intent.putExtras(bundle);
        intent.setClass(PointDetalActivity.this, QiandaoActivity.class);
        startActivity(intent);

    }

    private void initview(PointItems items) {
        tv_shopname.setText("店铺："+items.getShopname());
        tv_bianhao.setText("编号："+items.getUnm());
        tv_kehu.setText("客户："+items.getKehu());
        tv_phone.setText("电话："+items.getKehuphone());
        new DownloadImageTask(imageView).execute(items.getImgurl());

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bigimg();
            }
        });
        tv_iszs.setText("周期："+items.getZhouqi());
        tv_vip.setText("备注："+items.getBeizhu());
        qiandaoList = new ArrayList<Qiandao>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new DetalPointAdapter(this,qiandaoList);
        recyclerView.setAdapter(adapter);

    }

    private void getDetal(PointItems items) {
        qiandaoList.clear();
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

    public void Bigimg(){
        View v = LayoutInflater.from(PointDetalActivity.this).inflate(R.layout.bigimg_layout,null);

        ImageView img = v.findViewById(R.id.big_img);
        final AlertDialog dialog = new AlertDialog.Builder(PointDetalActivity.this) .create();
        new DownloadImageTask(img).execute(items.getImgurl());
        dialog.setCancelable(true);
        dialog.setView(v);//设置自定义view
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        items = (PointItems) getIntent().getSerializableExtra("items");
        if(items.getIsDelete() == 1){
            fab.setVisibility(View.GONE);
        }
        BmobQuery<PointItems> query = new BmobQuery<PointItems>();
        query.getObject(items.getObjectId(), new QueryListener<PointItems>() {
            @Override
            public void done(PointItems items, BmobException e) {
                initview(items);
                getDetal(items);
            }
        });

    }

    private void toast(String s){
        Toast.makeText(PointDetalActivity.this,s, Toast.LENGTH_SHORT).show();

    }

    protected void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PointDetalActivity.this);
        builder.setMessage("确认删除吗？");

        builder.setTitle("提示");

        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                PointItems updataitem = new PointItems();
                updataitem.setValue("isDelete",1);
                updataitem.update(items.getObjectId(),new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            toast("删除成功");
                            finish();

                        }else{
                            toast("删除失败");
                        }

                    }
                });

                dialog.dismiss();

            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }
}
