package com.chuanzhe.chuanzhemap.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chuanzhe.chuanzhemap.R;
import com.chuanzhe.chuanzhemap.bean.Goods;

import java.util.List;

public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.GoodsTVHolder>{
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private List<Goods> goodsList;
    @NonNull
    @Override
    public GoodsTVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GoodsTVHolder(mLayoutInflater.inflate(R.layout.goods_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull GoodsTVHolder holder, int position) {
        holder.tv_goodsname.setText(goodsList.get(position).getGoodsName());
        holder.tv_goodsprice.setText("单价:"+String.valueOf(goodsList.get(position).getGoodsPrice()));

    }

    public GoodsAdapter(Context mContext, List<Goods> goodsList) {

        this.mContext = mContext;
        this.goodsList = goodsList;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getItemCount() {
        return  goodsList == null ? 0 : goodsList.size();
    }

    public class GoodsTVHolder extends RecyclerView.ViewHolder{
        public TextView tv_goodsname;
        public TextView tv_goodsprice;

        public GoodsTVHolder(View itemView) {
            super(itemView);
            tv_goodsname = itemView.findViewById(R.id.tv_goodsname);
            tv_goodsprice = itemView.findViewById(R.id.tv_goodsprice);
        }
    }


}
