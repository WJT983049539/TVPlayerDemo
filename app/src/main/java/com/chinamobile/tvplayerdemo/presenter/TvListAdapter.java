package com.chinamobile.tvplayerdemo.presenter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinamobile.tvplayerdemo.R;
import com.chinamobile.tvplayerdemo.model.playUrlbean;
import com.chinamobile.tvplayerdemo.view.activity.MainActivity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TvListAdapter  extends RecyclerView.Adapter<TvListAdapter.MyHolder> {
    private Activity mainActivity;
    private List<playUrlbean> urlList;
    public TvListAdapter(Activity mainActivity, List<playUrlbean> urlList) {
        this.mainActivity=mainActivity;
        this.urlList=urlList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(mainActivity).inflate(R.layout.tv_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {

        holder.item_tv_url.setText(urlList.get(position).getPlayurl());

        if (mLongClickListener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    return mLongClickListener.onLongClick(position,view);
                }
            });
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListen!=null){
                    onItemClickListen.item(position,v);
                }
            }
        });

    }

    @Override
    public int getItemCount() {

        return urlList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView item_tv_url;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            item_tv_url= itemView.findViewById(R.id.item_tv_url);
            itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){
                        item_tv_url.setTextColor(Color.parseColor("#ffffff"));
                    }else{
                        item_tv_url.setTextColor(Color.parseColor("#000000"));
                    }
                }
            });
        }
    }
    //点击事件
    public interface OnItemClickListen{
        void item(int item,View view);
    }
    OnItemClickListen onItemClickListen;
    public void setOnItemClickListen(OnItemClickListen onItemClickListen){
        this.onItemClickListen=onItemClickListen;
    }
    private OnLongClickListener mLongClickListener;

    public interface OnLongClickListener{
        boolean onLongClick(int position,View view);
    }
    public void setLongClickListener(OnLongClickListener longClickListener) {
        mLongClickListener = longClickListener;
    }
}
