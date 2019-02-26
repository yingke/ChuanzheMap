package com.chuanzhe.chuanzhemap.map;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.chuanzhe.chuanzhemap.R;
import com.chuanzhe.chuanzhemap.bean.MapProject;
import com.chuanzhe.chuanzhemap.bean.PointItems;
import com.chuanzhe.chuanzhemap.utility.C;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;

public class MainProject extends AppCompatActivity  implements LocationSource,
        AMapLocationListener,GeocodeSearch.OnGeocodeSearchListener ,
       AMap.OnMarkerClickListener ,AMap.OnInfoWindowClickListener,AMap.OnMapClickListener{

    private AMap aMap;
    private MapView mapView;
    private ProgressDialog progDialog = null;
    private GeocodeSearch geocoderSearch;
    private String id;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private MarkerOptions markerOption;
    private int limit = 100; // 每页的数据是500条
    private int curPage = 0; // 当前页的编号，从0开始
    private Marker curShowWindowMarker;
    private SimpleDateFormat df ;//设置日期格式
    private String Currentdate ;
    private Integer d ;//删除状态
    private  Integer f ; //收藏状态
    private String Pointid = null;
    private int code =1024;
    private MapProject project;
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            ArrayList list = bundle.getParcelableArrayList("list");
            addMarkers(list);
            if (list.size() == limit){
                querydata(id,curPage);
            }else {
                dismissDialog();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_point);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ButterKnife.bind(this);
        ActionBar actionBar =getSupportActionBar();
        initview();
        mapView.onCreate(savedInstanceState);
        project = (MapProject) getIntent().getSerializableExtra("id");
        actionBar.setTitle(project.getProjectname());
        id = project.getObjectId();
        curPage = 0;
        querydata(id , curPage);
    }

    private void initview() {
        mapView = findViewById(R.id.map);
        progDialog = new ProgressDialog(this);
        setmap();
    }

    public void addpoint(View view){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("id",project);
        bundle.putString(C.ACTION, C.ADD);
        intent.putExtras(bundle);
        intent.setClass(MainProject.this, AddItemActivity.class);
        startActivityForResult(intent,code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data !=null){
            switch (requestCode){
                case 1024:
                   String ssid = data.getStringExtra("b");
                    BmobQuery<PointItems> query = new BmobQuery<PointItems>();
                    query.getObject(ssid, new QueryListener<PointItems>() {
                        @Override
                        public void done(PointItems items, BmobException e) {
                            getmarkinfo(items);
                        }
                    });
                    break;
            }
        }
    }

    private void  querydata(String s , int Page){
        showDialog();
        BmobQuery<PointItems> pointItemBmobQuery = new BmobQuery<PointItems>();
        pointItemBmobQuery.addWhereEqualTo("mapProject",s);
        pointItemBmobQuery.setLimit(limit);
        pointItemBmobQuery.setSkip(curPage*limit);
        pointItemBmobQuery.order("-createdAt");
        pointItemBmobQuery.findObjects(new FindListener<PointItems>() {
            @Override
            public void done(List<PointItems> list, BmobException e) {
                if(e==null){
                    Log.i("bmob"," 第"+curPage+"页"+"数据："+list.size());
                    Message msg = handler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("list",(ArrayList)list);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                    curPage ++;
                }else{
                    Log.i("bmob","chaxun失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    private void addMarkers(List<PointItems> object) {
         df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
         Currentdate = df.format(new Date());
        ArrayList<MarkerOptions> markerOptionlst = new ArrayList<MarkerOptions>();
        for (int i = 0; i < object.size(); i++) {
            getmarkinfo(object.get(i));
        }
    }
    public void getmarkinfo( PointItems options  ){
        String  color = C.GARY;
        String  s ="";
        long date = 0;
        Double lat= options.getLatitude();
        Double lon = options.getLongitude();
        Integer cunhuo = options.getCunhuoliang();
        Integer buhuo = options.getBuhuoliang();
        String qiaodaotime = options.getQiandaotime();
        String UpdatedAt = options.getUpdatedAt();
        String zhouqi = options.getZhouqi();
        Integer buhuozhouqi =options.getBuhuozhouqi();
        d = options.getIsDelete();
        f =options.getIsfavorite();
        String title = options.getShopname();
        if (lat == null | lon ==null){

        }else {
            if (null == qiaodaotime){
                date = C.getDistanceTime(UpdatedAt,Currentdate);
                s  =UpdatedAt.substring(0,10)+","+cunhuo+","+buhuo;
            }else {
                date = C.getDistanceTime(qiaodaotime,Currentdate);
                s  = qiaodaotime.substring(0,10)+","+cunhuo+","+buhuo;
            }
            if (d ==null ){
                d =0;
            }
            if (f ==null){
                f = 0;
            }
            if ( d ==0){
                if (f ==0){
                    if (buhuozhouqi !=null && buhuozhouqi!= 0){
                        color = getZhouqi(date,buhuozhouqi);
                    }else {
                        color = getZhouqi(date,30);
                    }
                }else {
                    color =C.BLACK;
                }
            }else {
                color = C.GARY;
            }
        }
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
        }else if (color.equals(C.BLACK)){
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(), R.mipmap.black)));
        }else if (color.equals(C.GARY)){
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(), R.mipmap.gray)));
        }

        markerOption.position(latLng);
        markerOption.title(title).snippet(s);
        aMap.addMarker(markerOption).setObject(options);

    }
    /*
    * t  时间
    * i 绿色[0-0.8x]
    * j 黄色[0.8x-x]
    * k 蓝色[x-1.2x]
    * 绿色[0-0.8x]，黄色[0.8x-x]，蓝色[x-1.2x],，红色[1.2x-200]，黑色>200
    * */
    public  String DateDistance(long t,long i,long j,long k){
        if(t>=0 && t<=i){
            return C.GREEN;
        }else if (t>i && t<=j){
            return C.YELLOW;
        }else if (t>j&&t<=k){
            return C.BLUE;
        }else if(t>k && t<=200) {
            return C.RED;
        }else return C.RED;
    }

    public String getZhouqi(long data,int i){

       long green = new BigDecimal(Double.toString(i*0.8)).setScale(0, BigDecimal.ROUND_HALF_UP).longValue() ;
       long blue = new BigDecimal(Double.toString(i*1.2)).setScale(0, BigDecimal.ROUND_HALF_UP).longValue();
       Log.i("Bomb","green="+green+",blue="+blue);
       return DateDistance(data,green,i,blue);
    }

    private void setmap() {
        if(aMap == null){
            // 显示地图
            aMap = mapView.getMap();
            CameraUpdate mCameraUpdate = CameraUpdateFactory.zoomTo(16);
            aMap.moveCamera(mCameraUpdate);
        }
        aMap.getUiSettings().setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);//设置缩放按钮去位置
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE) ;//定位一次，且将视角移动到地图中心点。
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.gps_point));// 设置小蓝点的图标*/
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.setOnMarkerClickListener(this);
        aMap.setOnMapClickListener(this);
        aMap.setOnInfoWindowClickListener(this);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
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
               // Toast.makeText(MainProject.this, query, Toast.LENGTH_SHORT).show();
                 showDialog();
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

                           dismissDialog();
                            LatLng latLng =new LatLng(list.get(0).getLatitude(),list.get(0).getLongitude());
                            changeCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                    latLng , 18, 30, 30)));
                            aMap.clear();
                            addMarkers(list);
                            aMap.getMapScreenMarkers().get(0).showInfoWindow();

                        }else{
                            Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                            dismissDialog();
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

        if (Pointid ==null){

        }else {

            BmobQuery<PointItems> query = new BmobQuery<PointItems>();
            query.getObject(Pointid, new QueryListener<PointItems>() {
                @Override
                public void done(PointItems items, BmobException e) {
                    getmarkinfo(items);
                }
            });
        }

    }


    private void toast(String s){
        Toast.makeText(MainProject.this,s,Toast.LENGTH_SHORT).show();
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
        progDialog.setMessage("加载中·····");
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
    @Override
    public boolean onMarkerClick(Marker marker) {
        curShowWindowMarker = marker;
        marker.showInfoWindow();
        return true;
    }
    @Override
    public void onInfoWindowClick(Marker marker) {
        PointItems items = (PointItems) marker.getObject();
        Pointid = items.getObjectId();

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("items",items);

        intent.putExtras(bundle);
        intent.setClass(MainProject.this, PointDetalActivity.class);

        startActivity(intent);
        marker.remove();
    }


    /**
     * 根据动画按钮状态，调用函数animateCamera或moveCamera来改变可视区域
     */
    private void changeCamera(CameraUpdate update) {

        aMap.moveCamera(update);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if(curShowWindowMarker!=null){
            curShowWindowMarker.hideInfoWindow();
        }
    }


}