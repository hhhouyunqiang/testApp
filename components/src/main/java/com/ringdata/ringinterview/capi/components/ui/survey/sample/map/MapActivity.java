package com.ringdata.ringinterview.capi.components.ui.survey.sample.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.blankj.utilcode.util.SPUtils;
import com.ringdata.ringinterview.capi.components.R;
import com.ringdata.ringinterview.capi.components.R2;
import com.ringdata.ringinterview.capi.components.constant.SPKey;
import com.ringdata.ringinterview.capi.components.data.dao.SampleDao;
import com.ringdata.ringinterview.capi.components.data.model.Sample;
import com.ringdata.ringinterview.capi.components.utils.AppUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;

public class MapActivity extends AppCompatActivity implements BaiduMap.OnMapLoadedCallback,BaiduMap.OnMarkerClickListener,BaiduMap.OnMapClickListener{

    private Unbinder mUnbinder=null;
    public TextureMapView mMapView;
    private BaiduMap mBaiduMap;
    private Sample currentSample;
    // 定位相关
    LocationClient mLocClient;
    public BDAbstractLocationListener myListener = new MyLocationListenner();
    private MyLocationConfiguration.LocationMode mCurrentMode;
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;
    boolean isFirstLoc = true; // 是否首次定位
    private MyLocationData locData;
    private float direction;
    private List<Sample> samples;
    public static String SAMPLES = "SAMPLES";
    MapStatus ms;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_map);
        Integer surveyId = SPUtils.getInstance().getInt(SPKey.SURVEY_ID);
        Integer userId = SPUtils.getInstance().getInt(SPKey.USERID);
        samples = SampleDao.selectListForMapBySurveyId(surveyId,userId);
        mUnbinder =  ButterKnife.bind(this);
        mMapView = (TextureMapView) findViewById(R.id.map_sample);
        mMapView.setVisibility(View.INVISIBLE);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setOnMapLoadedCallback(this);
        mBaiduMap.setOnMarkerClickListener(this);
        mBaiduMap.setOnMapClickListener(this);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);

        mLocClient.setLocOption(option);
        mLocClient.start();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
    }

    @OnClick(R2.id.icTv_topbar_back)
    void onClickBack() {
        finish();
    }


    @Override
    public void onMapLoaded() {
        // TODO Auto-generated method stub
        ms = new MapStatus.Builder().zoom(14).build();
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));
        addMark(samples);
    }
    private void showInfoWindow(final Sample sample){
        //创建InfoWindow展示的view
        View infoView = LayoutInflater.from(this).inflate(R.layout.view_sample_map_infowindow, null);
        //定义用于显示该InfoWindow的坐标点
        LatLng pt = new LatLng(sample.getLat(), sample.getLon());
        //创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量
        InfoWindow mInfoWindow = new InfoWindow(infoView, pt, -47);
        TextView tv_name = (TextView) infoView.findViewById(R.id.tv_infowindow_name);
        TextView tv_address = (TextView) infoView.findViewById(R.id.tv_infowindow_address);
        TextView tv_infowindow_call = (TextView) infoView.findViewById(R.id.tv_infowindow_call);
        Button nav = (Button)infoView.findViewById(R.id.btn_infowindow_nav);
        Button call = (Button)infoView.findViewById(R.id.btn_infowindow_call);
        tv_name.setText(sample.getName());
        tv_address.setText("地址：" + sample.getAddress());
        tv_infowindow_call.setText("电话：" + sample.getPhone());
        if(TextUtils.isEmpty(sample.getPhone())){
            call.setClickable(false);
            call.setBackgroundResource(R.drawable.map_infowindow_btn_unable);
        }

        nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopWindow();
            }
        });
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callPhone(sample.getPhone());
            }
        });

        //显示InfoWindow
        mBaiduMap.showInfoWindow(mInfoWindow);
    }

    private void callPhone(final String phone){
        RxPermissions rxPermission = new RxPermissions(this);
        rxPermission
                .request(Manifest.permission.CALL_PHONE)
                .subscribe(new Consumer<Boolean>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void accept(Boolean permission) throws Exception {
                        if (permission) {
                            Intent intent = new Intent(Intent.ACTION_CALL);
                            Uri data = Uri.parse("tel:" + phone);
                            intent.setData(data);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                });
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mBaiduMap.hideInfoWindow();
    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        return false;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Bundle bundle = marker.getExtraInfo();
        int index = (int)bundle.get("index");
        showInfoWindow((Sample)samples.get(index));
        return false;
    }

    /**
     * 显示样本位置
     * @param list
     */
    private void addMark(List<Sample> list){
        List<OverlayOptions> options = new ArrayList<OverlayOptions>();
        for(int i = 0; i< list.size(); i++){
            currentSample = (Sample) list.get(i);
            Float lat = currentSample.getLat();
            Float lon = currentSample.getLon();
            if(lat != 0&& lon != 0){
                //定义Maker坐标点
                LatLng point = new LatLng(lat,lon);
                //构建Marker图标
                BitmapDescriptor bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.location_xx);
                Bundle bundle = new Bundle();
                bundle.putInt("index",i);
                //构建MarkerOption，用于在地图上添加Marker
                OverlayOptions option = new MarkerOptions()
                        .animateType(MarkerOptions.MarkerAnimateType.jump)
                        .position(point)
                        .extraInfo(bundle)
                        .title("R")
                        .icon(bitmap)
                        .perspective(true);
                options.add(option);
            }
        }
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlays(options);
    }
    private void showPopWindow(){

        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.show();
        final Window window = dialog.getWindow();
        if (window != null) {
            window.setContentView(R.layout.view_sample_map_popwindow);
            window.setGravity(Gravity.BOTTOM);
            window.setWindowAnimations(R.style.anim_panel_up_from_bottom);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //设置属性
            final WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            params.dimAmount = 0.5f;
            window.setAttributes(params);
            Button baidu = (Button) window.findViewById(R.id.btn_map_baidu);
            Button gaode = (Button) window.findViewById(R.id.btn_map_gaode);
            Button tengxun = (Button) window.findViewById(R.id.btn_map_tengxun);
            if(!AppUtil.isPackageInstalled("com.baidu.BaiduMap")){
                baidu.setEnabled(false);
                baidu.setTextColor(ContextCompat.getColor(this,R.color.divider_line));
            }
            if(!AppUtil.isPackageInstalled("com.tencent.map")){
                baidu.setEnabled(false);
                tengxun.setTextColor(ContextCompat.getColor(this,R.color.divider_line));
            }
            if(!AppUtil.isPackageInstalled("com.autonavi.minimap")){
                baidu.setEnabled(false);
                gaode.setTextColor(ContextCompat.getColor(this,R.color.divider_line));
            }
            baidu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 百度地图
                    Intent naviIntent = new Intent("android.intent.action.VIEW", android.net.Uri.parse("baidumap://map/direction?destination=name:"+currentSample.getAddress()+"|latlng:"+currentSample.getLat()+","+currentSample.getLon()));
                    startActivity(naviIntent);
                    dialog.dismiss();
                }
            });

            tengxun.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 腾讯地图
                    Intent naviIntent = new Intent("android.intent.action.VIEW", android.net.Uri.parse("qqmap://map/routeplan?type=drive&to=" +currentSample.getAddress()+ "&tocoord=" + currentSample.getLat() + "," + currentSample.getLon() + "&referer=云调研"));
                    startActivity(naviIntent);
                    dialog.dismiss();
                }
            });

            gaode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 高德地图
                    Intent naviIntent = new Intent("android.intent.action.VIEW", android.net.Uri.parse("amapuri://route/plan/?dlat=" + currentSample.getLat() + "&dlon=" + currentSample.getLon() + "&dname="+ currentSample.getAddress() +"&dev=0&t=0"));
                    startActivity(naviIntent);
                    dialog.dismiss();

                }
            });
            window.findViewById(R.id.btn_map_cancle).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mUnbinder.unbind();
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // location_xx view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAccracy = location.getRadius();
            locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
            mMapView.setVisibility(View.VISIBLE);
        }
    }
}
