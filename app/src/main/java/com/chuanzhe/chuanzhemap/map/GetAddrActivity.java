package com.chuanzhe.chuanzhemap.map;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.chuanzhe.chuanzhemap.R;

public class GetAddrActivity extends AppCompatActivity  implements LocationSource,
        AMapLocationListener,GeocodeSearch.OnGeocodeSearchListener ,AMap.OnMapClickListener{
    private TextView tv_dizhi;
    private Button btn_add;
    private AMap aMap;
    private MapView mapView;
    private ProgressDialog progDialog = null;
    private GeocodeSearch geocoderSearch;

    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private MarkerOptions markerOption;
    private String dizhi;
    private Double latitude;
    private Double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_addr);
        initview();
        mapView.onCreate(savedInstanceState);

    }



    private void initview() {
        mapView = findViewById(R.id.gaodemap);
        tv_dizhi = findViewById(R.id.tv_dizhi);
        btn_add = findViewById(R.id.addbtnok);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GetAddrActivity.this,AddItemActivity.class);
                Bundle bundle = new Bundle();
                bundle.putDouble("latitude",latitude);
                bundle.putDouble("longitude",longitude);
                bundle.putString("dizhi",dizhi);
                intent.putExtras(bundle);
                setResult(0,intent);
                finish();

            }
        });
        progDialog = new ProgressDialog(this);

        setmap();
    }

    private void setmap() {
        if(aMap == null){
            // 显示地图
            aMap = mapView.getMap();
            CameraUpdate mCameraUpdate = CameraUpdateFactory.zoomTo(16);
            aMap.moveCamera(mCameraUpdate);

        }

        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE) ;//定位一次，且将视角移动到地图中心点。
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.addpoint));// 设置小蓝点的图标*/
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // aMap.setMyLocationType()

        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        aMap.setOnMapClickListener(this);
        markerOption = new MarkerOptions();
    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {

            if (aMapLocation != null
                    && aMapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
                LatLonPoint latLonPoint = new LatLonPoint(aMapLocation.getLatitude(),aMapLocation.getLongitude());

            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode()+ ": " + aMapLocation.getErrorInfo();
                Log.e("AmapErr",errText);
            }
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }



    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        mapView.onDestroy();
    }
    /**
     * 逆地理编码回调
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int i) {
        dismissDialog();
        if (i == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                dizhi = result.getRegeocodeAddress().getFormatAddress();
                tv_dizhi.setText("地址："+dizhi);

                /*addressName = result.getRegeocodeAddress().getFormatAddress()
                        + "附近";*/

            } else {

            }
        } else {

        }

    }

    /**
     * 地理编码查询回调
     */
    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    /**
     * 响应逆地理编码
     */
    public void getAddress(final LatLonPoint latLonPoint) {
        showDialog();
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置异步逆地理编码请求
    }

    /**
     * 显示进度条对话框
     */
    public void showDialog() {
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在获取地址");
        progDialog.show();
    }

    /**
     * 隐藏进度条对话框
     */
    public void dismissDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }



    private void addmarker(LatLng latlng){
        aMap.clear();
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(),R.drawable.addpoint)));
        markerOption.position(latlng);
        // markerOption.title("添加");
        aMap.addMarker(markerOption);

    }

    @Override
    public void onMapClick(LatLng latLng) {
        addmarker(latLng);
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
        latitude = latLng.latitude;
        longitude = latLng.longitude;
        getAddress(new LatLonPoint(latitude,longitude));

        Log.i("click",latLng.toString());
    }


}

