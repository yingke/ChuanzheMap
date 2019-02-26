package com.chuanzhe.chuanzhemap.map;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMapException;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.NaviPara;
import com.chuanzhe.chuanzhemap.R;
import com.chuanzhe.chuanzhemap.adapter.DetalPointAdapter;
import com.chuanzhe.chuanzhemap.bean.MapProject;
import com.chuanzhe.chuanzhemap.bean.MyUser;
import com.chuanzhe.chuanzhemap.bean.PointItems;
import com.chuanzhe.chuanzhemap.bean.Qiandao;
import com.chuanzhe.chuanzhemap.utility.C;
import com.chuanzhe.chuanzhemap.utility.DownloadImageTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class PointDetalActivity extends AppCompatActivity implements AMapLocationListener {
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

    public AMapLocationClient mLocationClient = null;
    private LatLng piontLatlng;
    private LatLng currlatlon;

    private Double latitude;
    private Double longitude;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption option = null;

    private Boolean isqiandaoing = true;
    private Integer cunhuo = 0;
    private Integer buhuo = 0;
    private String dingdanhao;


    @BindView(R.id.detal_rec) RecyclerView recyclerView;
    private DetalPointAdapter adapter;
    private List<Qiandao>    qiandaoList = new ArrayList<Qiandao>();
    private Integer d;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            ArrayList list = bundle.getParcelableArrayList("list");
            qiandaoList.addAll(list);
            adapter.notifyDataSetChanged();

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_detal);
        ButterKnife.bind(this);


        mLocationClient = new AMapLocationClient(getApplication());

        mLocationClient.setLocationListener(this);

        setmap();

        items = (PointItems) getIntent().getSerializableExtra("items");
        d = items.getIsDelete();
        if(d !=null && d ==1){
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




      //  getDetal(items);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detal, menu);

        return d == null || d != 1;

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

        /*Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("items",items);
        intent.putExtras(bundle);
        intent.setClass(PointDetalActivity.this, QiandaoActivity.class);
//        startActivity(intent);

*/

        showDialog();

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
        if (items.getBuhuozhouqi() !=null && items.getBuhuozhouqi()!= 0){
            tv_iszs.setText("周期："+items.getBuhuozhouqi());
        }else if(items.getZhouqi()!=null){
            tv_iszs.setText("周期："+items.getZhouqi());
        }
        tv_vip.setText("备注："+items.getBeizhu());

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



                Message msg = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("list",(ArrayList)list);
                msg.setData(bundle);
                handler.sendMessage(msg);
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
        getDetal(items);


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



    public String getCurrentdate(){
        Calendar calendar= Calendar.getInstance();
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");
        System.out.println();
       return dateFormat.format(calendar.getTime());

    }

    public void showDialog(){
        View v = LayoutInflater.from(PointDetalActivity.this).inflate(R.layout.activity_qiandao,null);
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setCancelable(true);
        dialog.setView(v);
        dialog.show();
        Window window = dialog.getWindow();
        window.getDecorView().setPadding(0, 150, 0, 0);
        window.setGravity(Gravity.CENTER);

        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.FILL_PARENT;
        lp.height = WindowManager.LayoutParams.FILL_PARENT;
        window.setAttributes(lp);

        EditText et_buhuo = v.findViewById(R.id.et_buhuoliang);

        EditText et_cunhuo = v.findViewById(R.id.et_cunhuoliang);

        EditText et_dingdan = v.findViewById(R.id.et_dingdanhao);

        Button btn_qiandao = v.findViewById(R.id.btn_qiandao);


        cunhuo = Integer.valueOf(et_cunhuo.getText().toString().trim());
        buhuo = Integer.valueOf(et_buhuo.getText().toString().trim());
        dingdanhao = et_dingdan.getText().toString().trim();

        btn_qiandao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isqiandaoing){
                    isqiandaoing = false;
                    piontLatlng =new LatLng(items.getLatitude(),items.getLongitude());
                    currlatlon = new LatLng(latitude,longitude);
                    Float juli =  AMapUtils.calculateLineDistance(piontLatlng,currlatlon);
                    Log.i("距离：","=="+juli);
                    if (juli>500.00){
                        isqiandaoing = true;
                        toast("请到店铺附近签到");
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
                                    java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                                    String Currentdate = df.format(new Date());

                                    pointItems.setQiandaotime(Currentdate);
                                    pointItems.setIsfavorite(0);

                                    pointItems.update(items.getObjectId(), new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if(e==null){
                                                toast("签到成功");
                                                finish();

                                            }else {
                                                Qiandao q= new Qiandao();
                                                q.setObjectId(id);
                                                q.delete(new UpdateListener() {
                                                    @Override
                                                    public void done(BmobException e) {
                                                        isqiandaoing = true;

                                                    }
                                                });
                                            }
                                        }
                                    });
                                }else {
                                    isqiandaoing = true;
                                    toast("签到失败");
                                }
                            }
                        });

                    }

                }
            }
        });





    }



    public void showDialog1(final Qiandao qiandao){
        View v = LayoutInflater.from(PointDetalActivity.this).inflate(R.layout.activity_qiandao,null);
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setCancelable(true);
        dialog.setView(v);
        dialog.show();
        Window window = dialog.getWindow();
        window.getDecorView().setPadding(0, 150, 0, 0);
        window.setGravity(Gravity.CENTER);

        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.FILL_PARENT;
        lp.height = WindowManager.LayoutParams.FILL_PARENT;
        window.setAttributes(lp);

        final EditText et_buhuo = v.findViewById(R.id.et_buhuoliang);

        final EditText et_cunhuo= v.findViewById(R.id.et_cunhuoliang);

        final EditText et_dingdan = v.findViewById(R.id.et_dingdanhao);

        Button btn_qiandao = v.findViewById(R.id.btn_qiandao);

        et_buhuo.setText(qiandao.getBuhuoliang());
        et_cunhuo.setText(qiandao.getCunhuoliang());
        et_dingdan.setText(qiandao.getDingdanhao());



        btn_qiandao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cunhuo = Integer.valueOf(et_cunhuo.getText().toString().trim());
                buhuo = Integer.valueOf(et_buhuo.getText().toString().trim());
                dingdanhao = et_dingdan.getText().toString().trim();

                Qiandao qiandao1 = new Qiandao();
                qiandao1.setBuhuoliang(buhuo);
                qiandao1.setCunhuoliang(cunhuo);
                qiandao1.setDingdanhao(dingdanhao);

                qiandao.update(qiandao.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            toast("成功");
                            finish();

                        }
                    }
                });
            }
        });


    }



    private void setmap() {
        /**
         * 设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
         */
        option = new AMapLocationClientOption();
        option.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
        if(null != mLocationClient){
            mLocationClient.setLocationOption(option);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
        }
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位模式为AMapLocationMode.Battery_Saving，低功耗模式。
        //   option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //设置定位模式为AMapLocationMode.Device_Sensors，仅设备模式。
        // option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Device_Sensors);
        option.setInterval(1000);

        option.setHttpTimeOut(20000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(option);
//启动定位
        mLocationClient.startLocation();

    }

    private void toast(String s){
        Toast.makeText(PointDetalActivity.this,s, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {

        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
               //可在其中解析amapLocation获取相应内容。
                latitude = aMapLocation.getLatitude();
                longitude = aMapLocation.getLongitude();
                String dizhi= aMapLocation.getAddress();

            }else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e("AmapError","location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }

    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.onDestroy();
    }

}
