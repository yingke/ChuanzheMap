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
import com.chuanzhe.chuanzhemap.bean.MapProject;

import java.util.List;


/**
 * Created by  yingke on 2018-06-10.
 * yingke.github.io
 */
public class MapRVAdapter extends RecyclerView.Adapter<MapRVAdapter.MyTVHolder>{
    private List<MapProject> mlist;
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private MyItemClickListener mListener;

    public MapRVAdapter(Context context,List<MapProject> list) {
       mlist = list;
       Log.i("listaaa",""+list.size());
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context; }



    @Override
    public MyTVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyTVHolder(mLayoutInflater.inflate(R.layout.maplist_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyTVHolder holder, int position) {
        holder.mTextView.setText(mlist.get(position).getProjectname());
    }

    @Override
    public int getItemCount() {
        return  mlist == null ? 0 : mlist.size();
    }
    public void setOnItemClickListener(MyItemClickListener listener){
        this.mListener = listener;
    }

    public class MyTVHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView mTextView;
        public MyTVHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.tv_test);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClick(v, getPosition());
            }

        }
    }
}



