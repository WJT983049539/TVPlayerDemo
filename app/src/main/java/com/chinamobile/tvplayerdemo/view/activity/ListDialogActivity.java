package com.chinamobile.tvplayerdemo.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chinamobile.tvplayerdemo.R;
import com.chinamobile.tvplayerdemo.model.playUrlbean;
import com.chinamobile.tvplayerdemo.presenter.TvListAdapter;
import com.chinamobile.tvplayerdemo.tools.ACache;
import com.chinamobile.tvplayerdemo.tools.DensityUtil;
import com.chinamobile.tvplayerdemo.tools.GlobalToast;
import com.chinamobile.tvplayerdemo.tools.LogUtils;
import com.chinamobile.tvplayerdemo.view.customview.CustomEidtDialog;
import com.chinamobile.tvplayerdemo.view.customview.FocusKeepRecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ListDialogActivity extends AppCompatActivity {
    private ArrayList<playUrlbean> urlListdate;
    private RecyclerView urllist;
    private TextView set;
    private LinearLayout list_dialog_lin;
    private TextView add;
    private ACache aCache;;
    private TvListAdapter tvListAdapter;
    // -----
    private SharedPreferences preferences = null;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (getSupportActionBar()!=null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_urllist);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
        WindowManager.LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参值
        int witch= DensityUtil.getScreenWidth(this);
        int height=DensityUtil.getScreenHeight(this);
        p.width=witch/4;
        p.height= height;
        p.alpha = 1.0f; // 设置本身透明度
        p.dimAmount = 0.0f; // 设置黑暗度
        p.x=-witch+20;
        //        getWindow().setGravity(Gravity.LEFT); // 设置靠右对齐
        getWindow().setAttributes(p); // 设置生效
        super.onCreate(savedInstanceState);
        initData();

        initView();//初始化数据


    }
    private void initData() {
        aCache=ACache.get(this);
        ArrayList<playUrlbean> arrayList= (ArrayList<playUrlbean>) aCache.getAsObject("Videolist");
        if(arrayList==null||arrayList.size()==0){
            LogUtils.i("缓存数据为空");
            urlListdate=new ArrayList<playUrlbean>();
            //暂时默认添加几条数据
//        urlList.clear();
            playUrlbean playUrlbean=new playUrlbean();
            playUrlbean.setPlayurl("http://10.2.40.100:10080/vod/spiderman/enc/spiderman.m3u8");
            playUrlbean.setContentId("397e26e5-e320-4aab-8cca-dfecb292e2ac");
            urlListdate.add(playUrlbean);
            playUrlbean videoPathBean2=new playUrlbean();
            videoPathBean2.setPlayurl("http://10.2.40.100:10080/vod/yqcr2/yqcr2.m3u8");
            videoPathBean2.setContentId("20191052");
            urlListdate.add(videoPathBean2);
        }else {
            urlListdate=new ArrayList<playUrlbean>(new HashSet<>(arrayList));
        }


    }
    private void initView() {
        add=findViewById(R.id.add);
        list_dialog_lin=findViewById(R.id.list_dialog_lin);
        urllist=findViewById(R.id.urllist);//url列表布局
        set=findViewById(R.id.set);
        set.setFocusableInTouchMode(true);
        set.setFocusable(true);
        set.requestFocus();

        add.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    add.setTextColor(Color.parseColor("#ffffff"));
                }else{
                    add.setTextColor(Color.parseColor("#000000"));
                }
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CustomEidtDialog customEidtDialog=new CustomEidtDialog(ListDialogActivity.this);
                customEidtDialog.show();
                customEidtDialog.setYesOnclickListener("确定", new CustomEidtDialog.onYesOnclickListener() {
                    @Override
                    public void onYesOnclick(String message, String message2) {
                        if(message.equals("")||message2.equals("")||message==null&&message2==null){
                            GlobalToast.show("节目地址或节目Id不能为空！请重新输入",Toast.LENGTH_LONG);
                        }else{
                            boolean boo= LogUtils.isVideo(message);
                            if(boo){
                                playUrlbean videoPathBean=new playUrlbean();
                                videoPathBean.setPlayurl(message);
                                videoPathBean.setContentId(message2);
                                urlListdate.add(0,videoPathBean);
                                aCache.put("Videolist",urlListdate);//把数据视频列表数据保存到缓存中
                                if(tvListAdapter!=null){
                                    tvListAdapter.notifyDataSetChanged();
                                }

                                customEidtDialog.cancel();
                            }else{
                                GlobalToast.show("不是一个正确的视频地址,请重新输入",Toast.LENGTH_LONG);
                            }
                            }

                    }
                });
                customEidtDialog.setNoOnclickListener("取消", new CustomEidtDialog.onNoOnclickListener() {
                    @Override
                    public void onNoClick() {
                        customEidtDialog.cancel();
                    }
                });

            }
        });

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置点击事件
                Intent intent=new Intent(ListDialogActivity.this,SetActivity.class);
                startActivity(intent);
            }
        });

        set.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                        if(hasFocus){
                            set.setTextColor(Color.parseColor("#ffffff"));
                        }else{
                            set.setTextColor(Color.parseColor("#000000"));
                    }
            }
        });
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        urllist.setLayoutManager(layoutManager);
        tvListAdapter=new TvListAdapter(this,urlListdate);
        urllist.setAdapter(tvListAdapter);
        list_dialog_lin.clearFocus();
        urllist.post(new Runnable() {
            @Override
            public void run() {
//                list_dialog_lin.clearFocus();
//                urllist.setFocusable(true);
//                urllist.getChildAt(0).setFocusableInTouchMode(true);
//                urllist.getChildAt(0).setFocusable(true);
//                urllist.getChildAt(0).requestFocus();

            }
        });

        tvListAdapter.setOnItemClickListen(new TvListAdapter.OnItemClickListen() {
            //url列表点击事件
            @Override
            public void item(int item,View view) {
                set.clearFocus();
                view.requestFocus();
                playUrlbean playurl=urlListdate.get(item);
                Intent intent=new Intent(ListDialogActivity.this,MainActivity.class);
                intent.putExtra("url",playurl);
                ListDialogActivity.this.setResult(2,intent);
                ListDialogActivity.this.finish();
            }
        });

    }
}
