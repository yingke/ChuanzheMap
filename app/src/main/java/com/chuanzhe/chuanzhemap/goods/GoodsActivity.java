package com.chuanzhe.chuanzhemap.goods;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chuanzhe.chuanzhemap.R;
import com.chuanzhe.chuanzhemap.bean.Goods;
import com.chuanzhe.chuanzhemap.bean.MyUser;
import com.chuanzhe.chuanzhemap.utility.C;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class GoodsActivity extends AppCompatActivity {
    @BindView(R.id.rec_goods)
    RecyclerView recgoods;
    @BindView(R.id.fab_goods)
    FloatingActionButton fabgoods;
    private List<Goods> goodsList;
    @OnClick(R.id.fab_goods) void addGoods(){
        addproject();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods);
        ButterKnife.bind(this);
        goodsList = new ArrayList<>();
        getgoods();

    }


    private void getgoods(){
        if(C.isNetworkConnected(this)){
            BmobQuery<Goods> query = new BmobQuery<Goods>();
          //  query.addWhereEqualTo("myUser", BmobUser.getCurrentUser(MyUser.class));

            query.findObjects(new FindListener<Goods>() {
                @Override
                public void done(List<Goods> list, BmobException e) {
                    Log.i("Goods",String.valueOf(list.size()));

                    goodsList.addAll(list);
                    //adapter.notifyDataSetChanged();
                }
            });

        }else {
            fabgoods.setVisibility(View.GONE);
            toast("暂无网络····");
        }

    }
    private  void addproject(){
        View v = LayoutInflater.from(GoodsActivity.this).inflate(R.layout.dialog_view,null);
        final EditText et_projectname = (EditText)v.findViewById(R.id.et_projectname);
        final EditText et_projectmiaoshu = (EditText)v.findViewById(R.id.et_projectmiashu);
        et_projectname.setHint("请输入商品名称");
        et_projectmiaoshu.setHint("亲输入商品价格");
        InputFilter[] filters = {new EditInputFilter()};

        et_projectmiaoshu.setFilters(filters);
        et_projectmiaoshu.setInputType(EditorInfo.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_CLASS_NUMBER);
        Button btn_ok = (Button)v.findViewById(R.id.btn_OK);
        Button btn_quxiao = (Button)v.findViewById(R.id.btn_quxiao);

        final AlertDialog dialog = new AlertDialog.Builder(GoodsActivity.this) .create();
        //创建
        dialog.setTitle("添加商品");
        dialog.setCancelable(false);
        dialog.setView(v);//设置自定义view
        dialog.show();

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String goods_name = et_projectname.getText().toString();
                String goodsprice = et_projectmiaoshu.getText().toString();
                if (TextUtils.isEmpty(goods_name)){
                    et_projectname.setError("商品名称不能为空");

                }else if(TextUtils.isEmpty(goodsprice)){
                    et_projectmiaoshu.setError("商品价格不能为空");

                }else {
                    Goods goods = new Goods();
                    goods.setGoodsName(goods_name);
                    goods.setGoodsPrice(Double.valueOf(goodsprice));
                    goods.setMyUser(BmobUser.getCurrentUser(MyUser.class));
                    goods.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if(e==null){
                                toast("添加数据成功，返回objectId为："+s);
                            }else{
                                toast("创建数据失败：" + e.getMessage());
                            }
                            dialog.dismiss();
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
        Toast.makeText(GoodsActivity.this,s,Toast.LENGTH_SHORT).show();

    }
}
