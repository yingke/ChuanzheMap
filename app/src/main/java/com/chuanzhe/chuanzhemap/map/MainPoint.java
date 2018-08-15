package com.chuanzhe.chuanzhemap.map;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapOptions;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.chuanzhe.chuanzhemap.MainActivity;
import com.chuanzhe.chuanzhemap.R;
import com.chuanzhe.chuanzhemap.bean.MapProject;
import com.chuanzhe.chuanzhemap.bean.PointItems;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MainPoint extends AppCompatActivity implements LocationSource,
        AMapLocationListener ,AMap.OnMarkerClickListener ,AMap.OnInfoWindowClickListener{
    @BindView(R.id.map)
    MapView mapView;
    @BindView(R.id.mapfab)
    FloatingActionButton fab;

    private AMap aMap;
    private UiSettings uiSettings;//设置地图自带按钮的位置
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;

    private boolean mPermissionEnabled = false;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    private MapProject project;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_point);
        ButterKnife.bind(this);
        isPermissionOK();
        initview();
        mapView.onCreate(savedInstanceState);

        Intent intent = getIntent();
       // Bundle myBundle = this.getIntent().getSerializableExtra();
        project = (MapProject) intent.getSerializableExtra("id");
      getmarks(project.getObjectId());


    }

    private void initview() {
        if(aMap == null){
            aMap = mapView.getMap();
            uiSettings = aMap.getUiSettings();
            //设置缩放级别
            CameraUpdate mCameraUpdate = CameraUpdateFactory.zoomTo(14);
            aMap.moveCamera(mCameraUpdate);

        }
        setmap();
    }

    private void setmap() {
        uiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);//设置缩放按钮去位置
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.gps_point));// 设置小蓝点的图标
       myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
         //myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
        aMap.setMyLocationEnabled(true);
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.setOnMarkerClickListener(this);
        aMap.setOnInfoWindowClickListener(this);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // aMap.setMyLocationType()
    }

    public void addpoint(View view){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("id",project);
        intent.putExtras(bundle);
        intent.setClass(MainPoint.this, AddItemActivity.class);

        startActivity(intent);
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
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissionsNeeded.add("文件管理权限");
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
        new AlertDialog.Builder(MainPoint.this)
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


    private void getmarks(String s){
        BmobQuery<PointItems> pointItemBmobQuery = new BmobQuery<PointItems>();
        pointItemBmobQuery.addWhereEqualTo("mapProject",s);
        pointItemBmobQuery.findObjects(new FindListener<PointItems>() {
            @Override
            public void done(List<PointItems> list, BmobException e) {
                if(e==null){
                    Log.i("bmob","数据："+list.size());
                    addMarkers(list);

                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });

    }

    private void addMarkers(List<PointItems> object) {

        ArrayList<MarkerOptions> markerOptionlst = new ArrayList<MarkerOptions>();
        for (int i = 0; i < object.size(); i++) {
            LatLng latLng = new LatLng(object.get(i).getLatitude(), object.get(i).getLongitude());
            String title = object.get(i).getShopname();
            // String miaoshu = object.get(i).getMiaoshu();
            MarkerOptions markerOption = new MarkerOptions();
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(), R.mipmap.marker)));
            String  s  = object.get(i).getUpdatedAt().substring(0,10)+","+object.get(i).getCunhuoliang()+","+object.get(i).getBuhuoliang();
            markerOption.position(latLng);
            markerOption.title(title).snippet(s);
            markerOptionlst.add(markerOption);
            aMap.addMarker(markerOption).setObject(object.get(i));


        }
    }


        @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {

            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr",errText);
            }
        }

    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            mLocationOption.setMockEnable(true);
            mLocationOption.setInterval(2000);
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
        if(null != mlocationClient){
            mlocationClient.onDestroy();
        }
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return true;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        PointItems items = (PointItems) marker.getObject();
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("items",items);
        intent.putExtras(bundle);
        intent.setClass(MainPoint.this, QiandaoActivity.class);

        startActivity(intent);
    }
}

