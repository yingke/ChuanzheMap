package com.chuanzhe.chuanzhemap.map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;
import com.chuanzhe.chuanzhemap.R;
import com.chuanzhe.chuanzhemap.bean.MapProject;
import com.chuanzhe.chuanzhemap.bean.MyUser;
import com.chuanzhe.chuanzhemap.bean.PointItems;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

public class AddItemActivity extends AppCompatActivity {
    private int requestCode = 0;
    private int imgcode = 1;
    private MapProject objectid;
    private EditText et_addr;
    private EditText et_shopnum;
    private EditText et_shouname;
    private EditText et_kehu;
    private EditText et_kehuphone;
    private ImageView imageView;
    private Switch sw_vip;
    private Switch sw_free;
    private String dizhi;
    private Double latitude;
    private Double longitude;
    private String unm;
    private String shopname ;
    private  String kehu ;
    private  String kehuphone ;

    private int isvip = 0;
    private int isfree = 0;
    private Button btn_getadd ;
    private Button btn_ok ;
    MyUser userInfo;
    private String imgpath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        ActionBar actionBar =getSupportActionBar();
        actionBar.setTitle("添加");
        Intent i = getIntent();
        objectid  = (MapProject) i.getSerializableExtra("id");;
        initview();

    }

    private void initview() {
        et_shopnum =findViewById(R.id.et_num);
        et_shouname =findViewById(R.id.et_shpomane);
        et_kehu =findViewById(R.id.et_kehu);
        et_kehuphone =findViewById(R.id.et_kehuphone);
        btn_getadd = findViewById(R.id.btn_add);
        et_addr = findViewById(R.id.et_dizhi);
        sw_vip =findViewById(R.id.switch_vip);
        sw_free = findViewById(R.id.switch_free);
        imageView = findViewById(R.id.imageView);

        sw_vip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isvip = 1;
                }else {
                    isvip = 0;
                }
            }
        });

        sw_free.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isfree = 1;
                }else {
                    isfree = 0;
                }
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null)
                { startActivityForResult(takePictureIntent, imgcode); }
            }
        });

    }

    public  void getadd(View view){
        Intent intent = new Intent(AddItemActivity.this,GetAddrActivity.class);
        startActivityForResult(intent,requestCode);
    }

    public  void tosave(View view){
         unm = et_shopnum.getText().toString().trim();
         shopname = et_shouname.getText().toString().trim();
         kehu = et_kehu.getText().toString().trim();
         kehuphone = et_kehuphone.getText().toString().trim();
         userInfo = BmobUser.getCurrentUser(MyUser.class);

        if (TextUtils.isEmpty(imgpath)){
            toast("请选择店铺图片");
        }else if(TextUtils.isEmpty(unm)){
            et_shopnum.setError("店铺编码不能为空");
        }else if(TextUtils.isEmpty(shopname)){
            et_shouname.setError("店铺名称不能为空");
        }else if (TextUtils.isEmpty(kehu)){
            et_kehu.setError("客户姓名不能为空");
        }else if (TextUtils.isEmpty(kehuphone)){
            et_kehuphone.setError("客户电话不能为空");
        }else if (TextUtils.isEmpty(dizhi)){
            et_addr.setError("地址不能为空");
        } else if(TextUtils.isEmpty(unm)){
            et_shopnum.setError("店铺编码不能为空");
        }else  if(TextUtils.isEmpty(shopname)){
            et_shouname.setError("店铺名称不能为空");
        }else if (TextUtils.isEmpty(kehu)){
            et_kehu.setError("客户姓名不能为空");
        }else if (TextUtils.isEmpty(kehuphone)){
            et_kehuphone.setError("客户电话不能为空");
        }else if (TextUtils.isEmpty(dizhi)){
            et_addr.setError("地址不能为空");
        }else {

           final BmobFile bmobFile = new BmobFile(new File(imgpath));
            bmobFile.uploadblock(new UploadFileListener() {

                @Override
                public void done(BmobException e) {
                    if(e==null){
                        //bmobFile.getFileUrl()--返回的上传文件的完整地址
                        //toast("上传文件成功:" + bmobFile.getFileUrl());

                        PointItems items = new PointItems();
                        items.setUnm(unm);
                        items.setShopname(shopname);
                        items.setKehu(kehu);
                        items.setKehuphone(kehuphone);
                        items.setDizhi(dizhi);
                        Log.i("save",latitude+"");
                        items.setLatitude(latitude);
                        items.setLongitude(longitude);
                        items.setIsvip(isvip);
                        items.setIsfree(isfree);
                        items.setImgurl(bmobFile.getFileUrl());
                        items.setAmyuser(userInfo);
                        items.setMapProject(objectid);
                        items.setCunhuoliang(0);
                        items.setBuhuoliang(0);
                        items.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if(e==null){

                                    toast("添加数据成功，返回objectId为："+s);
                                    finish();
                                }else{
                                    toast("创建数据失败：" + e.getMessage());
                                    Log.i("保存",e.getMessage());
                                }
                            }
                        });

                    }else{
                        toast("上传文件失败：" + e.getMessage());
                    }

                }

                @Override
                public void onProgress(Integer value) {
                    // 返回的上传进度（百分比）
                }
            });

        }


        }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 0:
                Bundle b=data.getExtras();
                dizhi = b.getString("dizhi");
                et_addr.setText(dizhi);
                latitude = b.getDouble("latitude");
                Log.i("onresult",latitude+"");
                longitude  =b.getDouble("longitude");
                Log.i("result",dizhi+latitude);
                break;
            case 1:

                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imageView.setImageBitmap(imageBitmap);
                imgpath= saveImage("crop", imageBitmap);
                Log.i("path",imgpath);

                break;
        }
    }


    private void toast(String s){
        Toast.makeText(AddItemActivity.this,s, Toast.LENGTH_SHORT).show();

    }


    public String saveImage(String name, Bitmap bmp) {
        File appDir = new File(Environment.getExternalStorageDirectory().getPath());
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = name + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
