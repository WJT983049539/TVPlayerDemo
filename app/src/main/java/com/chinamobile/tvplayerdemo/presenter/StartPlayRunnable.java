package com.chinamobile.tvplayerdemo.presenter;

import android.os.Handler;
import android.os.Message;

import com.chinamobile.tvplayerdemo.view.activity.MainActivity;
import com.unitend.udrm.util.UDRM;

/**
 * 这个runnable是为了转换成代理url
 */
public class StartPlayRunnable implements Runnable{
    private MainActivity singPlayerActivity;
    private UDRM mUDRM;
    private String path;
    private Handler playhandler;


    public StartPlayRunnable(MainActivity singPlayerActivity, UDRM mUDRM, String path, Handler playhandler) {
        this.singPlayerActivity=singPlayerActivity;
        this.mUDRM=mUDRM;
        this.path=path;
        this.playhandler=playhandler;
    }

    @Override
    public void run() {
        String getUrl = mUDRM.startPlayerAgent(path);
        Message message=new Message();
        message.what=0x0019;
        message.obj=getUrl;
        playhandler.sendMessage(message);
    }
}
