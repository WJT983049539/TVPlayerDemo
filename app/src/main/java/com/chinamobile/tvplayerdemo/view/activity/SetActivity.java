package com.chinamobile.tvplayerdemo.view.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.chinamobile.tvplayerdemo.R;
import com.chinamobile.tvplayerdemo.tools.GlobalToast;
import com.chinamobile.tvplayerdemo.tools.LogUtils;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.unitend.udrm.util.UDRM;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 设置界面
 */
public class SetActivity extends AppCompatActivity {
    private EditText ed_yunyingshang;
    private EditText ed_ip;
    private TextView clear;
    private Switch switchx;
    private TextView version;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (getSupportActionBar()!=null) {//隐藏头
            getSupportActionBar().hide();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_set);
        inintView();
    }

    private void inintView() {
        ed_yunyingshang=findViewById(R.id.ed_yunyingshang);
        ed_ip=findViewById(R.id.ed_ip);
        clear=findViewById(R.id.clear);
        switchx=findViewById(R.id.switchx);
        version=findViewById(R.id.version);
        UDRM udrm=new UDRM(this);
        String versioncode=udrm.getUdrmVersion();
        version.setText("UDRM版本号:"+versioncode);
        SharedPreferences preferences = getSharedPreferences("TVset", Context.MODE_PRIVATE);
        final SharedPreferences.Editor edit= preferences.edit();
        Boolean drmswitch=preferences.getBoolean("DrmSwitch",true);
        String udrmserviceip=preferences.getString("udrmserviceurl","");
        ed_ip.setText(udrmserviceip);
        String uremserviceyys=preferences.getString("uremserviceyys","");
        ed_yunyingshang.setText(uremserviceyys);
        switchx.setChecked(drmswitch);
        switchx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                edit.putBoolean("DrmSwitch",isChecked);//存到共享参数
                edit.commit();
                LogUtils.i("设置解密服务开关为:"+isChecked);
            }
        });


        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  清除缓存
                GSYVideoManager.instance().clearAllDefaultCache(SetActivity.this);
                GlobalToast.show("清除视频缓存成功", Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences preferences = getSharedPreferences("TVset", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit= preferences.edit();
        String serviceIp=ed_ip.getText().toString();
        if(!serviceIp.equals("")){
            edit.putString("udrmserviceurl",serviceIp);

        }
        String ed_yys=ed_yunyingshang.getText().toString();
        if(ed_yys.equals("")){
            edit.putString("uremserviceyys",ed_yys);
        }


    }
}
