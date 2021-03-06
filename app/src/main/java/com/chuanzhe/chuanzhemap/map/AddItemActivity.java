package com.chuanzhe.chuanzhemap.map;

import android.app.ProgressDialog;
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
import com.chuanzhe.chuanzhemap.utility.C;
import com.chuanzhe.chuanzhemap.utility.DownloadImageTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
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
    private EditText et_zhouqi;
    private EditText et_beizhu;
    private ImageView imageView;

    private String dizhi;
    private Double latitude;
    private Double longitude;
    private String unm;
    private String shopname ;
    private  String kehu ;
    private  String kehuphone ;

    private Button btn_getaddr ;
    private Button btn_ok ;
    MyUser userInfo;
    private String imgpath =null;
    private String action;
    private PointItems item;
    private Integer zhouqi;
    private String beizhu;

    private ProgressDialog progDialog = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.additem);
        ActionBar actionBar =getSupportActionBar();
        actionBar.setTitle("添加");
        Intent i = getIntent();
        objectid  = (MapProject) i.getSerializableExtra("id");
        action = i.getStringExtra(C.ACTION);
        Log.i("添加页传值",action);
        if (action.equals(C.EDIT)){
          item = (PointItems) i.getSerializableExtra("point")  ;
        }
        initview();

    }

    private void initview() {
        progDialog = new ProgressDialog(this);
        et_shopnum =findViewById(R.id.et_num);
        et_shouname =findViewById(R.id.et_shpomane);
        et_kehu =findViewById(R.id.et_kehu);
        et_kehuphone =findViewById(R.id.et_kehuphone);
        btn_ok = findViewById(R.id.btn_add);
        et_addr = findViewById(R.id.et_dizhi);
        et_zhouqi =findViewById(R.id.et_zhouqi);
        et_beizhu =findViewById(R.id.et_beizhu);

        imageView = findViewById(R.id.imageView);
        if(action.equals(C.EDIT)){
            btn_ok.setText("修改");
            et_shouname.setText(item.getShopname());
            et_shopnum.setText(item.getUnm());
            et_kehu.setText(item.getKehu());
            et_kehuphone.setText(item.getKehuphone());
            if(item.getImgurl().endsWith(".jpg")){
                new DownloadImageTask(imageView).execute(item.getImgurl());
            }else {
               imageView.setBackgroundResource(R.mipmap.addimages);
            }

            dizhi =item.getDizhi();
            et_addr.setText(dizhi);
            zhouqi = item.getBuhuozhouqi();
            if (zhouqi!=null){
                et_zhouqi.setText(zhouqi+"");
            }

            beizhu = item.getBeizhu();
            et_beizhu.setText(beizhu);
            latitude = item.getLatitude();
            longitude = item.getLongitude();

        }



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
        showDialog();
        btn_ok.setEnabled(false);
         unm = et_shopnum.getText().toString().trim();
         shopname = et_shouname.getText().toString().trim();
         kehu = et_kehu.getText().toString().trim();
         kehuphone = et_kehuphone.getText().toString().trim();
         zhouqi = Integer.valueOf(et_zhouqi.getText().toString().trim());
         beizhu = et_beizhu.getText().toString().trim();
         userInfo = BmobUser.getCurrentUser(MyUser.class);

         if (action.equals(C.EDIT)){
           final  PointItems updataitem = new PointItems();
             updataitem.setUnm(unm);
             updataitem.setShopname(shopname);
             updataitem.setKehu(kehu);
             updataitem.setKehuphone(kehuphone);
             updataitem.setDizhi(dizhi);
             updataitem.setLatitude(latitude);
             updataitem.setLongitude(longitude);
             updataitem.setBuhuozhouqi(zhouqi);
             updataitem.setBeizhu(beizhu);
             updataitem.setAmyuser(userInfo);
             updataitem.setMapProject(objectid);
             if (TextUtils.isEmpty(imgpath)){

                 updataitem.update(item.getObjectId(), new UpdateListener() {
                     @Override
                     public void done(BmobException e) {
                         if(e==null){
                             dismissDialog();
                             btn_ok.setEnabled(true);
                             finish();
                         }else{
                             Log.i("bmob","更新失败："+e.getMessage()+","+e.getErrorCode());
                             dismissDialog();
                             btn_ok.setEnabled(true);
                         }
                     }
                 });
             }else {
                 final BmobFile file = new BmobFile(new File(imgpath));
                 file.uploadblock(new UploadFileListener() {

                     @Override
                     public void done(BmobException e) {
                         if(e==null){
                             updataitem.setImgurl(file.getFileUrl());
                             updataitem.update(item.getObjectId(), new UpdateListener() {
                                 @Override
                                 public void done(BmobException e) {
                                     if(e==null){
                                         Log.i("bmob","更新成功");
                                        dismissDialog();

                                         finish();
                                     }else{
                                         dismissDialog();
                                         Log.i("bmob","更新失败："+e.getMessage()+","+e.getErrorCode());
                                     }
                                 }
                             });

                         }else{
                            dismissDialog();
                             btn_ok.setEnabled(true);
                             toast("上传文件失败：" + e.getMessage());
                         }

                     }

                     @Override
                     public void onProgress(Integer value) {
                         // 返回的上传进度（百分比）
                     }
                 });
             }

         }else {

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
                     Log.i("path000",imgpath);
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
                                 items.setLatitude(latitude);
                                 items.setLongitude(longitude);
                                 items.setBuhuozhouqi(zhouqi);
                                 items.setImgurl(bmobFile.getFileUrl());
                                 items.setBeizhu(beizhu);
                                 items.setAmyuser(userInfo);
                                 items.setMapProject(objectid);
                                 SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                                 String Currentdate = df.format(new Date());
                                 items.setQiandaotime(Currentdate);
                                 items.setCunhuoliang(0);
                                 items.setBuhuoliang(0);
                                 items.setIsDelete(0);
                                 items.setIsfavorite(0);
                                 items.save(new SaveListener<String>() {
                                     @Override
                                     public void done(String s, BmobException e) {
                                         if(e==null){
                                             Intent mIntent = new Intent();
                                             mIntent.putExtra("b",s);
                                             // 设置结果，并进行传送

                                             AddItemActivity.this.setResult(1024, mIntent);

                                             toast("添加店铺成功");
                                             finish();
                                         }else{
                                             btn_ok.setEnabled(true);
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

        }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (null != data){
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



}
