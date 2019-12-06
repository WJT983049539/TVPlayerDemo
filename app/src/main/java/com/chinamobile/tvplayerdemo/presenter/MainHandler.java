package com.chinamobile.tvplayerdemo.presenter;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.chinamobile.tvplayerdemo.tools.GlobalToast;
import com.chinamobile.tvplayerdemo.tools.LogUtils;
import com.chinamobile.tvplayerdemo.view.activity.MainActivity;
import com.chinamobile.tvplayerdemo.view.customview.TvPlayerView;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.CommonUtil;

import androidx.annotation.NonNull;

public class MainHandler extends Handler {
    private final static int DRM_PROC_STATUS_NET_ERROR = 5;//网络错误.UDRM获取权限失败
    //drm)proc_status_system_error
    private final static int DRM_PROC_STATUS_SYSTEM_ERROR = 14;//系统错误.
    // UDRM获取权限失败
    //drm)proc_status_cer_error
    private final static int DRM_PROC_STATUS_CER_ERROR = 15;//证书错误.UDRM获取权限失败
    //drm)proc_status_register_error
    private final static int DRM_PROC_STATUS_REGISTER_ERROR = 16;//授权错误.UDRM获取权限失败
    private final static int DRM_PROC_STATUS_START = 18;//开始检测权限
    private final static int DRM_PROC_STATUS_AAA_FAILED = 19;//AAA 检测权限失败
    private final static int DRM_PROC_STATUS_CHECK_FAILED = 20;//检测权限失败,erroNO. = " + ret + ".\n
    private final static int DRM_PROC_STATUS_CHECK_SUCCESS = 21;//检测权限成功
    private static final int RE_PLAY_AFTER_ERROR = 22;
    private static final int UDRM_DECRYPT_FAILED = 23;
    private static final int LOG_MSG_CAT = 24;
    private static final int TIME_LIMIT = 500;
    private static final int TIME_FLAG = 5000;
    private TvPlayerView player;
    private MainActivity mainActivity;
    public MainHandler(TvPlayerView player, MainActivity mainActivity) {
        this.player=player;
        this.mainActivity=mainActivity;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        switch (msg.what){
            case 0x001:
                if(MainActivity.isLongPressKey){//是长按事件
                    float x= (float) msg.obj;
                    if(x>0){ //右滑快进
                        if( LogUtils.localtotal ==0){
                            LogUtils.localtotal=100;//给个初始值
//                        LogUtils.localtotal = (int) GSYVideoManager.instance().getCurrentPosition();//当前进度
                        }

                        int total= (int) GSYVideoManager.instance().getDuration();//总进度
                        String totalTime = CommonUtil.stringForTime(total);
                        String seekTime = CommonUtil.stringForTime( LogUtils.localtotal );
                        LogUtils.localtotal += total/40;
//                        GlobalToast.show("快进增加"+total/35, Toast.LENGTH_LONG);
                        if( LogUtils.localtotal <total){
                            player.showProgressDialog2(x,seekTime, LogUtils.localtotal ,totalTime,total);
                        }else{
                            LogUtils.localtotal=total;
                            player.showProgressDialog2(x,seekTime,  LogUtils.localtotal ,totalTime,total);
                        }

                    }else if(x<0){ //左滑快退

                        int total= (int) GSYVideoManager.instance().getDuration();//总进度
                        String totalTime = CommonUtil.stringForTime(total);
                        String seekTime = CommonUtil.stringForTime( LogUtils.localtotal );
                        if( LogUtils.localtotal >0){
                            player.showProgressDialog2(x,seekTime, LogUtils.localtotal ,totalTime,total);
                            LogUtils.localtotal -= total/40;
//                            GlobalToast.show("快退减少"+total/35, Toast.LENGTH_LONG);
                        }else{
                            LogUtils.localtotal=0;
                            player.showProgressDialog2(x,seekTime, LogUtils.localtotal ,totalTime,total);
                        }

                    }
                }
                break;
            case 0x002:
                player.dissprogrssDialog2();

                break;
            /**
             * urm的日志
             */
            case LOG_MSG_CAT:
                LogUtils.i("DRMplayer日志...");
                break;
            /**
             * udrm 解密失败
             */
            case UDRM_DECRYPT_FAILED:
                LogUtils.i("解密失败...");
                LogUtils.ToastShow(mainActivity,"解密失败!");
                mainActivity.checkPremissFail("解密视频失败");
                break;
            case DRM_PROC_STATUS_START:
                LogUtils.i("开始检测权限...");
                mainActivity.checkPremissFail("正在检测权限");
                break;
            case DRM_PROC_STATUS_AAA_FAILED:
                LogUtils.i(" AAA检测权限失败...");
                mainActivity.checkPremissFail("AAA检测权限失败");
                LogUtils.ToastShow(mainActivity,"AAA检测权限失败!");
                break;
            case DRM_PROC_STATUS_CHECK_FAILED:
                int ret = msg.arg1;
                LogUtils.i(" \"检测权限失败,erroNO. = \" + ret + \".\\n\"");
                mainActivity.checkPremissFail("检测权限失败,解密服务器没有权限解密播放这个视频");
                LogUtils.ToastShow(mainActivity,"检测权限失败,解密服务器没有权限解密播放这个视频!"+ret);
//                singPlayerActivity.zhuanhuanUrl();
                break;
            case DRM_PROC_STATUS_CHECK_SUCCESS:
                LogUtils.i(" 检测权限成功...");
                mainActivity.zhuanhuanUrl();
                break;
            case DRM_PROC_STATUS_CER_ERROR:
                mainActivity.checkPremissFail("证书错误,UDRM获取权限失败!");
                LogUtils.i(" 证书错误.\nUDRM获取权限失败.\n...");
                LogUtils.ToastShow(mainActivity,"证书错误,UDRM获取权限失败!");
                break;
            case DRM_PROC_STATUS_NET_ERROR:
                //暂时用成功的方法
//                singPlayerActivity.zhuanhuanUrl();
//                singPlayerActivity.checkPremissFail(" 网络错误.\nUDRM获取权限失败");
                LogUtils.i(" 网络错误.\nUDRM获取权限失败.\n");
                mainActivity.checkPremissFail("网络错误.\nUDRM获取权限失败");
//                Toast.makeText(singPlayerActivity,"网络错误,请检查网络配置",Toast.LENGTH_LONG).show();
                LogUtils.ToastShow(mainActivity,"网络错误,请检查网络配置!");
                break;
            case DRM_PROC_STATUS_REGISTER_ERROR:
                mainActivity.checkPremissFail("授权错误,UDRM获取权限失败");
                LogUtils.i("授权错误.\nUDRM获取权限失败.\n");
                LogUtils.ToastShow(mainActivity,"授权错误,UDRM获取权限失败!");
                break;
            case DRM_PROC_STATUS_SYSTEM_ERROR:
                mainActivity.checkPremissFail("系统错误,UDRM获取权限失败");
                LogUtils.ToastShow(mainActivity,"系统错误,UDRM获取权限失败!");
                LogUtils.i("系统错误.\nUDRM获取权限失败.\n");
                break;
            /**
             * 转换url成功，把url放入播放器开始播放
             */
            case 0x0019:
                String url=msg.obj.toString();
                mainActivity.UDRMUrlSuccess(url);
                break;
        }
    }
}
