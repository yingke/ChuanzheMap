package com.chuanzhe.chuanzhemap.map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.provider.SyncStateContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.Circle;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.chuanzhe.chuanzhemap.MainActivity;
import com.chuanzhe.chuanzhemap.R;
import com.chuanzhe.chuanzhemap.bean.MapProject;
import com.chuanzhe.chuanzhemap.bean.MyUser;
import com.chuanzhe.chuanzhemap.bean.PointItems;
import com.chuanzhe.chuanzhemap.utility.C;
import com.chuanzhe.chuanzhemap.utility.SensorEventHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MainPoint extends AppCompatActivity implements LocationSource,
        AMapLocationListener ,AMap.OnMarkerClickListener ,AMap.OnInfoWindowClickListener{

    MapView mapView;
    @BindView(R.id.mapfab)
    FloatingActionButton fab;
    private AMap aMap;
    private UiSettings uiSettings;//设置地图自带按钮的位置
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private MapProject project;
    private SensorEventHelper mSensorHelper;
    private boolean mFirstFix = false;
    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    private Marker mLocMarker;
    private Circle mCircle;
    public static final String LOCATION_MARKER_FLAG = "mylocation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_point);
        ButterKnife.bind(this);
        initview();
        mapView.onCreate(savedInstanceState);
        Intent intent = getIntent();
       // Bundle myBundle = this.getIntent().getSerializableExtra();
        project = (MapProject) intent.getSerializableExtra("id");
        getmarks(project.getObjectId());
    }

    private void initview() {
        mapView = findViewById(R.id.map);
        if(aMap == null){// 显示地图
         aMap = mapView.getMap();
           CameraUpdate mCameraUpdate = CameraUpdateFactory.zoomTo(16);
          aMap.moveCamera(mCameraUpdate);
            setmap();
      }


        mSensorHelper = new SensorEventHelper(this);
        if (mSensorHelper != null) {
            mSensorHelper.registerSensorListener();
        }

    }

    private void setmap() {
        aMap.getUiSettings().setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);//设置缩放按钮去位置
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
       myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.gps_point));// 设置小蓝点的图标*//*
       myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
         //myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.setOnMarkerClickListener(this);
        aMap.setOnInfoWindowClickListener(this);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // aMap.setMyLocationType()



    }


//    private void setmap() {
//        if(aMap == null){
//            // 显示地图
//            aMap = mapView.getMap();
//            CameraUpdate mCameraUpdate = CameraUpdateFactory.zoomTo(16);
//            aMap.moveCamera(mCameraUpdate);
//
//        }
//
//        MyLocationStyle myLocationStyle = new MyLocationStyle();
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE) ;//定位一次，且将视角移动到地图中心点。
//        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
//                .fromResource(R.drawable.addpoint));// 设置小蓝点的图标*/
//        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
//        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
//        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
//        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
//        aMap.setMyLocationStyle(myLocationStyle);
//
//        aMap.getUiSettings().setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
//        aMap.setLocationSource(this);// 设置定位监听
//        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
//        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
//        // aMap.setMyLocationType()
//
//    }
    public void addpoint(View view){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("id",project);
        bundle.putString(C.ACTION, C.ADD);
        intent.putExtras(bundle);
        intent.setClass(MainPoint.this, AddItemActivity.class);

        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSubmitButtonEnabled(true);//显示提交按钮
        searchView.setQueryHint(getString(R.string.search));//设置默认无内容时的文字提示
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //提交按钮的点击事件
                Toast.makeText(MainPoint.this, query, Toast.LENGTH_SHORT).show();

                BmobQuery<PointItems> id = new BmobQuery<PointItems>();
                id.addWhereEqualTo("mapProject",project.getObjectId());

                BmobQuery<PointItems> bianhao = new BmobQuery<PointItems>();
                bianhao.addWhereEqualTo("unm",query);

                List<BmobQuery<PointItems>> queries = new ArrayList<BmobQuery<PointItems>>();
                queries.add(id);
                queries.add(bianhao);
                BmobQuery<PointItems> search = new BmobQuery<PointItems>();
                search.and(queries);
                search.findObjects(new FindListener<PointItems>() {
                    @Override
                    public void done(List<PointItems> list, BmobException e) {
                        if(e==null){


                            LatLng latLng =new LatLng(list.get(0).getLatitude(),list.get(0).getLongitude());
                            changeCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                    latLng , 18, 30, 30)));
                            aMap.clear();
                            addMarkers(list);
                            aMap.getMapScreenMarkers().get(0).showInfoWindow();

                        }else{
                            Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                        }
                    }
                });



                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //当输入框内容改变的时候回调
                Log.i("sousuo","内容: " + newText);
                return true;
            }
        });

        return true;
    }

    /**
     * 根据动画按钮状态，调用函数animateCamera或moveCamera来改变可视区域
     */
    private void changeCamera(CameraUpdate update) {

        aMap.moveCamera(update);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        return super.onOptionsItemSelected(item);
    }





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
                    Log.i("bmob","chaxun失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });

    }

    private void addMarkers(List<PointItems> object) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String Currentdate = df.format(new Date());


        ArrayList<MarkerOptions> markerOptionlst = new ArrayList<MarkerOptions>();
        for (int i = 0; i < object.size(); i++) {
            Double lat= object.get(i).getLatitude();
            Double lon = object.get(i).getLongitude();
            long date = C.getDistanceTime(object.get(i).getUpdatedAt(),Currentdate);
            String color = DateDistance(date,15,30,45);

            if (lat == null | lon ==null){
                continue;
            }else {

                String title = object.get(i).getShopname();
                LatLng latLng = new LatLng(lat,lon);
                MarkerOptions markerOption = new MarkerOptions();

                if (color.equals(C.GREEN)){
                    markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                            .decodeResource(getResources(), R.mipmap.green)));
                }else if (color.equals(C.YELLOW)){
                    markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                            .decodeResource(getResources(), R.mipmap.yellow)));
                }else if (color.equals(C.BLUE)){
                    markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                            .decodeResource(getResources(), R.mipmap.blue)));
                }else if (color.equals(C.RED)){
                    markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                            .decodeResource(getResources(), R.mipmap.red)));
                }

                String  s  = object.get(i).getUpdatedAt().substring(0,10)+","+object.get(i).getCunhuoliang()+","+object.get(i).getBuhuoliang();
                markerOption.position(latLng);
                markerOption.title(title).snippet(s);
                markerOptionlst.add(markerOption);
                aMap.addMarker(markerOption).setObject(object.get(i));
            }



        }
    }


        @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {

            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                LatLng location = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                if (!mFirstFix) {
                    mFirstFix = true;
                   // addCircle(location, amapLocation.getAccuracy());//添加定位精度圆
                    addMarker(location);//添加定位图标
                    mSensorHelper.setCurrentMarker(mLocMarker);//定位图标旋转
                } else {
                    mCircle.setCenter(location);
                    mCircle.setRadius(amapLocation.getAccuracy());
                    mLocMarker.setPosition(location);
                }
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 18));
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr",errText);
            }
        }

    }

    private void addCircle(LatLng latlng, double radius) {
        CircleOptions options = new CircleOptions();
        options.strokeWidth(1f);
        options.fillColor(FILL_COLOR);
        options.strokeColor(STROKE_COLOR);
        options.center(latlng);
        options.radius(radius);
        mCircle = aMap.addCircle(options);
    }

    private void addMarker(LatLng latlng) {
        if (mLocMarker != null) {
            return;
        }
        Bitmap bMap = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.navi_map_gps_locked);
        BitmapDescriptor des = BitmapDescriptorFactory.fromBitmap(bMap);

//		BitmapDescriptor des = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
        MarkerOptions options = new MarkerOptions();
        options.icon(des);
        options.anchor(0.5f, 0.5f);
        options.position(latlng);
        mLocMarker = aMap.addMarker(options);
        mLocMarker.setTitle(LOCATION_MARKER_FLAG);
    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            mLocationOption.setMockEnable(true);

            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);

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
        /*aMap.clear();
        getmarks(project.getObjectId());
        Log.i("bmob","onResume()");*/
        if (mSensorHelper != null) {
            mSensorHelper.registerSensorListener();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
        Log.i("bmob","onPause()");
        if (mSensorHelper != null) {
            mSensorHelper.unRegisterSensorListener();
            mSensorHelper.setCurrentMarker(null);
            mSensorHelper = null;
        }
        mFirstFix = false;
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);

        Log.i("bmob","onSaveInstanceState");
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
        Log.i("bmob","onDestroy()");
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
        intent.setClass(MainPoint.this, PointDetalActivity.class);

        startActivity(intent);
    }


    public  String DateDistance(long t,long i,long j,long k){
        if(t>=0 && t<=i){
            return C.GREEN;
        }else if (t>i && t<=j){
            return C.YELLOW;
        }else if (t>k&&t>=k){
            return C.BLUE;
        }else {
            return C.RED;
        }
    }

    private void toast(String s){
        Toast.makeText(MainPoint.this,s,Toast.LENGTH_SHORT).show();

    }

}

