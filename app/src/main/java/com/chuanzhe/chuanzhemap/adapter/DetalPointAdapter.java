package com.chuanzhe.chuanzhemap.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.chuanzhe.chuanzhemap.R;
import com.chuanzhe.chuanzhemap.bean.Qiandao;
import java.util.List;

/**
 * Created by  yingke on 2018-08-21.
 * yingke.github.io
 *
 */



public class DetalPointAdapter extends RecyclerView.Adapter<DetalPointAdapter.MyDetalHolder>{
    private List<Qiandao> mlist;
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;


    public DetalPointAdapter(Context context,List<Qiandao> list) {
        mlist = list;
        Log.i("listaaa",""+list.size());
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context; }



    @Override
    public MyDetalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyDetalHolder(mLayoutInflater.inflate(R.layout.qiandao_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyDetalHolder holder, int position) {
        Qiandao qiandao = mlist.get(position);
        holder.tv_cun.setText("存货量："+qiandao.getCunhuoliang());
        holder.tv_buhuo.setText("补货量："+qiandao.getBuhuoliang());
        holder.tv_time.setText(qiandao.getUpdatedAt().substring(0,10));
    }

    @Override
    public int getItemCount() {
        return  mlist == null ? 0 : mlist.size();

    }


    public class MyDetalHolder extends RecyclerView.ViewHolder {
        TextView tv_cun;
        TextView tv_buhuo;
        TextView tv_time;
        public MyDetalHolder(View itemView) {
            super(itemView);
            tv_cun = itemView.findViewById(R.id.tv_item_cunhuo);
            tv_buhuo = itemView.findViewById(R.id.tv_item_buhuo);
            tv_time = itemView.findViewById(R.id.tv_item_time);



        }


    }
}





