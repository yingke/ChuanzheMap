package com.chuanzhe.chuanzhemap.map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.LatLng;
import com.chuanzhe.chuanzhemap.R;
import com.chuanzhe.chuanzhemap.bean.MyUser;
import com.chuanzhe.chuanzhemap.bean.PointItems;
import com.chuanzhe.chuanzhemap.bean.Qiandao;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class QiandaoActivity extends AppCompatActivity implements AMapLocationListener{
    @BindView(R.id.et_buhuoliang)
    EditText et_buhuo;
    @BindView(R.id.et_cunhuoliang)
    EditText et_cunhuo;
    @BindView(R.id.et_dingdanhao)
    EditText et_dingdan;
    @BindView(R.id.btn_qiandao)
    Button btn_qiandao;
    @BindView(R.id.et_shangpinname)
    EditText et_shangpinname;
    @BindView(R.id.et_shangpindanjia)
    EditText et_shangpindanjia;
    @BindView(R.id.et_shuliang)
    EditText et_shangpinshuliang;

    private PointItems items;
    private Integer cunhuo = 0;
    private Integer buhuo = 0;
    private String dingdanhao;
    private String id;
    private Double latitude;
    private Double longitude;
    private String shangpinname;
    private Float shangpindanjia;
    private Integer shangpinshuliang;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    private LatLng piontLatlng;
    private LatLng currlatlon;
    private Boolean isqiandaoing = true;
    private Qiandao q0;
    private Qiandao q1;
    private boolean issamedate;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption option = null;

    @OnClick(R.id.btn_qiandao) void qiandao(){

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

                cunhuo = Integer.valueOf(et_cunhuo.getText().toString().trim());
                buhuo = Integer.valueOf(et_buhuo.getText().toString().trim());
                dingdanhao = et_dingdan.getText().toString().trim();
                shangpinname = et_shangpinname.getText().toString().trim();
                shangpindanjia = Float.valueOf(et_shangpindanjia.getText().toString().trim());
                shangpinshuliang = Integer.valueOf(et_shangpinshuliang.getText().toString().trim());


                    Qiandao qiandao = new Qiandao();
                    qiandao.setCunhuoliang(cunhuo);
                    qiandao.setBuhuoliang(buhuo);
                    qiandao.setDingdanhao(dingdanhao);
                    qiandao.setGoodsname(shangpinname);
                    qiandao.setGoodsPrice(shangpindanjia);
                    qiandao.setGoodsquantity(shangpinshuliang);
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
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                                String Currentdate = df.format(new Date());
                                pointItems.setQiandaotime(Currentdate);
                                pointItems.setIsfavorite(0);

                                pointItems.update(items.getObjectId(), new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if(e==null){
                                            toast("签到成功");

                                            Intent intent = new Intent(QiandaoActivity.this,PointDetalActivity.class);
                                            intent.putExtra("qdid",id);
                                            setResult(0,intent);
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

  @OnClick(R.id.btn_QD_quxiao) void quxiao(){
        finish();
  }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qiandao);
        ButterKnife.bind(this);
        Intent intent = getIntent();

        btn_qiandao.setText("确定");
        items = (PointItems) intent.getSerializableExtra("items");

        mLocationClient = new AMapLocationClient(getApplication());

        mLocationClient.setLocationListener(this);

        setmap();

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return super.onKeyDown(keyCode, event);
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
        Toast.makeText(QiandaoActivity.this,s, Toast.LENGTH_SHORT).show();

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
