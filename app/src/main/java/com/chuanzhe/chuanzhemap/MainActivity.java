package com.chuanzhe.chuanzhemap;
//adb uninstall com.chuanzhe.chuanzhemap

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chuanzhe.chuanzhemap.adapter.MapRVAdapter;
import com.chuanzhe.chuanzhemap.adapter.MyItemClickListener;
import com.chuanzhe.chuanzhemap.bean.MapProject;
import com.chuanzhe.chuanzhemap.bean.MyUser;
import com.chuanzhe.chuanzhemap.login.LoginActivity;
import com.chuanzhe.chuanzhemap.utility.C;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class MainActivity extends AppCompatActivity implements MyItemClickListener{
    @BindView(R.id.map_rc)
    RecyclerView rc;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private boolean mPermissionEnabled = false;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    @OnClick(R.id.fab) void add() {
        if (myUser.getIsAdmin() == 1){
            addproject();
        }else {
            toast("暂无权限");
        }

    }


    private MyUser myUser;
    private MapRVAdapter adapter;
    private List<MapProject> prolist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ActionBar actionBar =getSupportActionBar();
        actionBar.setTitle("项目列表");


        isPermissionOK();
        myUser = BmobUser.getCurrentUser(MyUser.class);
        if(myUser == null){
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
            finish();
        }else {
            prolist =new ArrayList<MapProject>();
            GridLayoutManager layoutManager = new GridLayoutManager(this,2);
            layoutManager.setOrientation(GridLayoutManager.VERTICAL);
            rc.setLayoutManager(layoutManager);

            adapter = new MapRVAdapter(MainActivity.this,prolist);
            adapter.setOnItemClickListener(MainActivity.this);
            rc.setAdapter(adapter);
            getdate();

        }

    }

    @Override
    public void onItemClick(View view, int postion) {
        MapProject mapProject = prolist.get(postion);
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("id",mapProject);
        intent.putExtras(bundle);

       intent.setClass(MainActivity.this,Main2Activity.class);

        startActivity(intent);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.userinfo,menu);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       //toast(myUser.getUsername()+"222222");
        dialog();

        return super.onOptionsItemSelected(item);
    }

    private  void addproject(){
        View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_view,null);
        final EditText et_projectname = (EditText)v.findViewById(R.id.et_projectname);
        final EditText et_projectmiaoshu = (EditText)v.findViewById(R.id.et_projectmiashu);
        Button btn_ok = (Button)v.findViewById(R.id.btn_OK);
        Button btn_quxiao = (Button)v.findViewById(R.id.btn_quxiao);

        final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this) .create();
        //创建
        dialog.setTitle("添加项目");
        dialog.setCancelable(false);
        dialog.setView(v);//设置自定义view
        dialog.show();

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pro_name = et_projectname.getText().toString();
                String pro_miaoshu = et_projectmiaoshu.getText().toString();
                if (TextUtils.isEmpty(pro_name)){
                    et_projectname.setError("项目名称不能为空");

                }else if(TextUtils.isEmpty(pro_miaoshu)){
                    et_projectmiaoshu.setError("项目描述不能为空");

                }else {
                    MapProject mapProject = new MapProject();
                    mapProject.setProjectname(pro_name);
                    mapProject.setProjectmiaoshu(pro_miaoshu);
                    mapProject.setMyUser(BmobUser.getCurrentUser(MyUser.class));
                    mapProject.setMaxcycle(60);
                    mapProject.setMincycle(3);
                    mapProject.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if(e==null){
                                Intent intent = new Intent();
                                intent.putExtra("id",s);
                                intent.setClass(MainActivity.this, MainActivity.class);

                                startActivity(intent);

                                toast("添加数据成功，返回objectId为："+s);
                            }else{
                                toast("创建数据失败：" + e.getMessage());
                            }
                        }
                    });
                }


            }
        });

        btn_quxiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void toast(String s){
        Toast.makeText(MainActivity.this,s,Toast.LENGTH_SHORT).show();

    }

    private void getdate(){

        if(C.isNetworkConnected(this)){
            BmobQuery<MapProject> query = new BmobQuery<MapProject>();
            query.addWhereEqualTo("myUser", BmobUser.getCurrentUser(MyUser.class));
            query.findObjects(new FindListener<MapProject>() {
                @Override
                public void done(List<MapProject> list, BmobException e) {

                    prolist.addAll(list);
                    adapter.notifyDataSetChanged();
                }
            });

        }else {
            fab.setVisibility(View.GONE);
            toast("暂无网络····");
        }

    }



    protected void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("确认退出当前账户吗？");

        builder.setTitle("提示");

        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                BmobUser.logOut();   //清除缓存用户对象
                if(BmobUser.getCurrentUser() == null){
                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    finish();
                }


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



    /*#########################动态获取权限###########################################*/
    @TargetApi(Build.VERSION_CODES.M)
    private boolean checkPermission() {
        boolean ret = true;
        List<String> permissionsNeeded = new ArrayList<String>();
        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            permissionsNeeded.add("位置权限");
        }
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionsNeeded.add("GPS");
        }
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissionsNeeded.add("文件管理权限");
        }
        if (!addPermission(permissionsList, Manifest.permission.CAMERA)) {
            permissionsNeeded.add("拍照权限");
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
        new AlertDialog.Builder(MainActivity.this)
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


}
