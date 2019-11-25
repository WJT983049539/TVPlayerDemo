package com.chinamobile.tvplayerdemo.presenter;

import android.os.Handler;
import android.os.Message;

import com.unitend.udrm.util.UDRM;

public class CheckPremissionRunnable implements Runnable {


    //drm)proc_status_net_error
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
    private Handler playhandler;
    private  String drmUrl;
    private  String username;
    private String passwd;
    private String authenticationinfo;
    private String macAddress;
    private String contentID;
    private String operator;
    private UDRM mUDRM;

    public CheckPremissionRunnable(UDRM mUDRM, Handler playhandler, String drmUrl, String username, String passwd, String authenticationinfo, String macAddress, String contentID, String operator) {
        this.playhandler=playhandler;
        this.authenticationinfo=authenticationinfo;
        this.contentID=contentID;
        this.drmUrl=drmUrl;
        this.macAddress=macAddress;
        this.operator=operator;
        this.passwd=passwd;
        this.username=username;
        this.mUDRM=mUDRM;
    }

    @Override
    public void run() {
        int ret = mUDRM.checkUdrmRights(drmUrl, "UDRM", "UDRM", "", macAddress, contentID, operator, null);
        if (ret == 0) {
            playhandler.sendEmptyMessage(DRM_PROC_STATUS_CHECK_SUCCESS);
        } else if (ret == -1) {
            playhandler.sendEmptyMessage(DRM_PROC_STATUS_CER_ERROR);
        } else if (ret == -2) {
            playhandler.sendEmptyMessage(DRM_PROC_STATUS_NET_ERROR);
        } else if (ret == -3) {
            playhandler.sendEmptyMessage(DRM_PROC_STATUS_REGISTER_ERROR);
        } else if (ret == -4) {
            playhandler.sendEmptyMessage(DRM_PROC_STATUS_SYSTEM_ERROR);
        } else if (ret == -5) {
            playhandler.sendEmptyMessage(DRM_PROC_STATUS_AAA_FAILED);
        } else {
            Message msg = new Message();
            msg.what = DRM_PROC_STATUS_CHECK_FAILED;
            msg.arg1 = ret;
            playhandler.sendMessage(msg);
        }
    }
}
