package com.chinamobile.tvplayerdemo.view.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chinamobile.tvplayerdemo.R;
import com.chinamobile.tvplayerdemo.model.playUrlbean;
import com.chinamobile.tvplayerdemo.presenter.CheckPremissionRunnable;
import com.chinamobile.tvplayerdemo.presenter.MainHandler;
import com.chinamobile.tvplayerdemo.presenter.StartPlayRunnable;
import com.chinamobile.tvplayerdemo.presenter.TvListAdapter;
import com.chinamobile.tvplayerdemo.tools.GlobalToast;
import com.chinamobile.tvplayerdemo.tools.LogUtils;
import com.chinamobile.tvplayerdemo.view.customview.TvPlayerView;
import com.chinamobile.tvplayerdemo.view.customview.WrningEidtDialog;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.listener.VideoAllCallBack;
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.shuyu.gsyvideoplayer.player.SystemPlayerManager;
import com.shuyu.gsyvideoplayer.utils.CommonUtil;
import com.unitend.udrm.util.OnUDRMListener;
import com.unitend.udrm.util.UDRM;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private String playUrl;
    private String authenticationinfo="";
    private String username="";
    private String passwd="";
    private String contentID;
    private final String drminfo = "METHOD=SAMPLE-AES,VDECFORMAT=h264-f,KEYFORMATVERSIONS=1,KEYFORMAT=chinadrm,OPERATOR=voole,MIMETYPE=application/vnd.unitend.drm,IV=000102030405060708090A0B0C0D0E0F,CONTENTID=TC_00001,IP=10.110.95.118,PORT=8080,URI=udrm://10.110.95.118:443/udrmservice/services/UdrmSysWS.UdrmSysWSHttpSoap12Endpoint/";
    private String operator;
    private UDRM mUDRM;//安全播放器对象
    private String drmUrl;//拼接后的代理服务器地址;
    private MainHandler mainHandler;
    private Timer progressDoalogTimer;
    private TvPlayerView player;
    private static double DOUBLE_CLICK_TIME = 0L;
    public static boolean isLongPressKey=false ;//判断长按还是
    private static boolean lockLongPressKey;
    private boolean isDoublePressKey;//判断是否快速点击
    private static final int UDRM_DECRYPT_FAILED = 23;
    private static final int LOG_MSG_CAT = 24;
    /**
     * 播放状态
     */
    //正常
    public static final int CURRENT_STATE_NORMAL = 0;
    //准备中
    public static final int CURRENT_STATE_PREPAREING = 1;
    //播放中
    public static final int CURRENT_STATE_PLAYING = 2;
    //开始缓冲
    public static final int CURRENT_STATE_PLAYING_BUFFERING_START = 3;
    //暂停
    public static final int CURRENT_STATE_PAUSE = 5;
    //自动播放结束
    public static final int CURRENT_STATE_AUTO_COMPLETE = 6;
    //错误状态
    public static final int CURRENT_STATE_ERROR = 7;


    private SharedPreferences preferences = null;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar()!=null) {
            getSupportActionBar().hide();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inintView();
    }

    private void inintView() {
        preferences = getSharedPreferences("TVset", Context.MODE_PRIVATE);//拿到共享参数
        editor = preferences.edit();
//        PlayerFactory.setPlayManager(SystemPlayerManager.class);//默认原生播放器
        PlayerFactory.setPlayManager(IjkPlayerManager.class);//ijk模式
        player=findViewById(R.id.player);
        mainHandler=new MainHandler(player,MainActivity.this);
        player.getBackButton().setVisibility(View.GONE);
        player.getThunbView().setVisibility(View.VISIBLE);
        //停止事件
        if(player.getStopView()!=null){
            player.getStopView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    player.getCurrentPlayer().release();
//                    player.getThunbView().setVisibility(View.VISIBLE);

                }
            });
        }


        ImageView imageView= (ImageView) player.getThunbView();
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.fitCenter();

        Glide.with(this).load(R.mipmap.thumb).apply(requestOptions).into(imageView);
//        player.setThumbImageView(imageView);
        player.setVideoAllCallBack(new VideoAllCallBack() {
            @Override
            public void onStartPrepared(String url, Object... objects) {
                LogUtils.i("onStartPrepared");
//                TextView textView= (TextView) sing_player.getStatuView();
//                if(textView!=null){
//                    textView.setText("准备播放");
//                };
                player.getThunbView().setVisibility(View.GONE);
                LogUtils.localtotal=0;
            }

            @Override
            public void onPrepared(String url, Object... objects) {
                LogUtils.i("onPrepared");
                player.getStartButton().requestFocus();

            }

            @Override
            public void onClickStartIcon(String url, Object... objects) {
                LogUtils.i("onClickStartIcon");
            }

            @Override
            public void onClickStartError(String url, Object... objects) {
                LogUtils.i("onClickStartError");
            }

            @Override
            public void onClickStop(String url, Object... objects) {
                LogUtils.i("onClickStop");
//                GlobalToast.show("onClickStop", Toast.LENGTH_LONG);
            }

            @Override
            public void onClickStopFullscreen(String url, Object... objects) {
                LogUtils.i("onClickStopFullscreen");
            }

            @Override
            public void onClickResume(String url, Object... objects) {
                LogUtils.i("onClickResume");
            }

            @Override
            public void onClickResumeFullscreen(String url, Object... objects) {
                LogUtils.i("onClickResumeFullscreen");
            }

            @Override
            public void onClickSeekbar(String url, Object... objects) {
                LogUtils.i("onClickSeekbar");
            }

            @Override
            public void onClickSeekbarFullscreen(String url, Object... objects) {
                LogUtils.i("onClickSeekbarFullscreen");
            }

            @Override
            public void onAutoComplete(String url, Object... objects) {
                LogUtils.i("onAutoComplete");

            }

            @Override
            public void onEnterFullscreen(String url, Object... objects) {
                LogUtils.i("onEnterFullscreen");
            }

            @Override
            public void onQuitFullscreen(String url, Object... objects) {
                LogUtils.i("onQuitFullscreen");
//                if (orientationUtils != null) {
//                    orientationUtils.backToProtVideo();
//                }
            }

            @Override
            public void onQuitSmallWidget(String url, Object... objects) {
                LogUtils.i("onQuitSmallWidget");
            }

            @Override
            public void onEnterSmallWidget(String url, Object... objects) {
                LogUtils.i("onEnterSmallWidget");
            }

            @Override
            public void onTouchScreenSeekVolume(String url, Object... objects) {
                LogUtils.i("onTouchScreenSeekVolume");
            }

            @Override
            public void onTouchScreenSeekPosition(String url, Object... objects) {
                LogUtils.i("onTouchScreenSeekPosition");
            }

            @Override
            public void onTouchScreenSeekLight(String url, Object... objects) {
                LogUtils.i("onTouchScreenSeekLight");
            }
            @Override
            public void onPlayError(String url, Object... objects) {
                LogUtils.i("onPlayError");


//                LogUtils.ToastShow(SingPlayerActivity.this,"播放失败,请检查播放链接或网络状况!");
////                Toast.makeText(SingPlayerActivity.this,"播放失败,请检查播放链接",Toast.LENGTH_LONG).show();
//                //到这里了
//                SingPlayerActivity.this.finish();//关闭本窗口
            }

            @Override
            public void onClickStartThumb(String url, Object... objects) {
                LogUtils.i("onClickStartThumb");
            }

            @Override
            public void onClickBlank(String url, Object... objects) {
                LogUtils.i("onClickBlank");
            }

            @Override
            public void onClickBlankFullscreen(String url, Object... objects) {
                LogUtils.i("onClickBlankFullscreen");
            }
        });
        //增加封面
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:     //确定键enter
            case KeyEvent.KEYCODE_DPAD_CENTER:
                if(player!=null){
                    player.clickStartButton();
                }
                break;

            case KeyEvent.KEYCODE_BACK:    //返回键
//                    this.finish();
                final WrningEidtDialog wrningEidtDialog=new WrningEidtDialog(this);
                wrningEidtDialog.show();
                wrningEidtDialog.setMEssage("是否退出播放器?");
                wrningEidtDialog.setYesOnclickListener("确定", new WrningEidtDialog.onYesOnclickListener() {
                    @Override
                    public void onYesOnclick() {
                        wrningEidtDialog.cancel();
                        MainActivity.this.finish();
                    }

                });
                wrningEidtDialog.setNoOnclickListener("取消", new WrningEidtDialog.onNoOnclickListener() {
                    @Override
                    public void onNoClick() {
                        wrningEidtDialog.cancel();
                    }
                });

                return false;   //这里由于break会退出，所以我们自己要处理掉 不返回上一层

            case KeyEvent.KEYCODE_SETTINGS: //设置键
//                GlobalToast.show("设置键", Toast.LENGTH_LONG);
                break;

            case KeyEvent.KEYCODE_DPAD_DOWN:   //向下键

                /*    实际开发中有时候会触发两次，所以要判断一下按下时触发 ，松开按键时不触发
                 *    exp:KeyEvent.ACTION_UP
                 */
                if (event.getAction() == KeyEvent.ACTION_DOWN){

//                    GlobalToast.show("向下键", Toast.LENGTH_LONG);
                }

                break;

            case KeyEvent.KEYCODE_DPAD_UP:   //向上键
//                GlobalToast.show("向上键", Toast.LENGTH_LONG);
                if(player!=null){
                    player.showBottomView();

                }

                break;

            case     KeyEvent.KEYCODE_0:   //数字键0


                break;

            case KeyEvent.KEYCODE_DPAD_LEFT: //向左键
//                event.startTracking();
                if (event.getRepeatCount() == 0) {
                    isLongPressKey=false;
                    if((System.currentTimeMillis() - DOUBLE_CLICK_TIME)>600){
                        isDoublePressKey=false;
                    }else{
                        isDoublePressKey=true;
                    }
                    DOUBLE_CLICK_TIME = System.currentTimeMillis();
                }else{
                    isLongPressKey=true;
                    //这里开始显示进度条窗口//快进快退
                    kuaijinn(-1f);//左滑了，快退了
                }


                break;

            case KeyEvent.KEYCODE_DPAD_RIGHT:  //向右键
//                event.startTracking();
                    if (event.getRepeatCount() == 0) {
                    isLongPressKey=false;
                    if((System.currentTimeMillis() - DOUBLE_CLICK_TIME)>600){
                        isDoublePressKey=false;
                    }else{
                        isDoublePressKey=true;
                    }
                    DOUBLE_CLICK_TIME = System.currentTimeMillis();
                }else{
                    isLongPressKey=true;
                    //这里开始显示进度条窗口//快进快退
                        kuaijinn(2f);
                }

                break;

            case KeyEvent.KEYCODE_INFO:    //info键


                break;

            case KeyEvent.KEYCODE_PAGE_DOWN:     //向上翻页键
            case KeyEvent.KEYCODE_MEDIA_NEXT:

//                GlobalToast.show("向上翻页键", Toast.LENGTH_LONG);
                break;


            case KeyEvent.KEYCODE_PAGE_UP:     //向下翻页键
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
//                GlobalToast.show("向下翻页键", Toast.LENGTH_LONG);

                break;

            case KeyEvent.KEYCODE_VOLUME_UP:   //调大声音键
//                GlobalToast.show("调大声音键", Toast.LENGTH_LONG);

                break;

            case KeyEvent.KEYCODE_VOLUME_DOWN: //降低声音键
//                GlobalToast.show("降低声音键", Toast.LENGTH_LONG);

                break;
            case KeyEvent.KEYCODE_VOLUME_MUTE: //禁用声音
//                GlobalToast.show("禁用声音", Toast.LENGTH_LONG);
                //todo 需要验证
                if(player!=null){
                    boolean needmute=GSYVideoManager.instance().isNeedMute();
                    if(needmute){
                        GSYVideoManager.instance().setNeedMute(false);
                    }else{
                        GSYVideoManager.instance().setNeedMute(true);
                    }

                }

                break;
            case KeyEvent.KEYCODE_MENU://菜单键弹出列表
//                if(listview!=null){
//                    listview.setVisibility(View.VISIBLE);
//                }
                Intent intent=new Intent(this,ListDialogActivity.class);
                startActivityForResult(intent,1);
                break;

            default:
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode){
             case KeyEvent.KEYCODE_DPAD_LEFT: //向左键
                 if(isLongPressKey){
                     isLongPressKey=false;//长按结束
                     if(progressDoalogTimer!=null){
                         progressDoalogTimer.cancel();
                         progressDoalogTimer=null;
                         player.dissprogrssDialog2();
                     }
//                     GlobalToast.show("进入到长按抬起事件，总长度为"+LogUtils.localtotal, Toast.LENGTH_LONG);
//                     GSYVideoManager.instance().seekTo( LogUtils.localtotal );
                     player.seekTo(LogUtils.localtotal );
                 }
                 player.dissprogrssDialog2();
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:  //向右键
                //这里得到快进到的进度，并播放

                if(isLongPressKey){
                    isLongPressKey=false;//长按结束
                    if(progressDoalogTimer!=null){
                        progressDoalogTimer.cancel();
                        progressDoalogTimer=null;
                        player.dissprogrssDialog2();
                    }
//                    GlobalToast.show("进入到长按抬起事件，总长度为"+LogUtils.localtotal, Toast.LENGTH_LONG);
//                    GSYVideoManager.instance().seekTo( LogUtils.localtotal );
                    player.seekTo(LogUtils.localtotal );
                }
                player.dissprogrssDialog2();
                break;
        }
        return super.onKeyUp(keyCode, event);

    }

    //flag 标志左滑还是右滑,这里只能执行一次，左右滑动也是单例。
    public void kuaijinn(final float flag){
        if(isLongPressKey){
            //如果是长按事件
            if(progressDoalogTimer==null){
                progressDoalogTimer =new Timer();
                progressDoalogTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Message message=new Message();
                        message.what=0x001;
                        message.obj=flag;
                        mainHandler.sendMessage(message);
                    }
                },0,100);
            }
        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1&&resultCode==2){
            player.release();
//            player=findViewById(R.id.player);
            TextView textView= (TextView) player.getStatuView();
            textView.setVisibility(View.VISIBLE);
            playUrlbean playUrlbean= (com.chinamobile.tvplayerdemo.model.playUrlbean) data.getSerializableExtra("url");
            playUrl=playUrlbean.getPlayurl();
            contentID=playUrlbean.getContentId();
            //开始播放
            //查看解密服务是否打开
            boolean falg=preferences.getBoolean("DrmSwitch",true);
            LogUtils.i("开关为:"+falg);

            if(falg){
                mUDRM=new UDRM(this);
                String version = mUDRM.getUdrmVersion();//获取版本号
                /**
                 * 判断播放权限
                 */
                judgePermiss(playUrl);
            }else{
//                zhuanhuanUrl();
                //否则直接播放不用解密
                GlobalToast.show("UDRM开关未打开，无法解密视频",Toast.LENGTH_LONG);
                UDRMUrlSuccess(playUrl);
            };


        }
    }

    /**
     * 检测权限失败
     */
    public void checkPremissFail(String msg){
        TextView textView= (TextView) player.getStatuView();
        if(textView!=null){
            textView.setText(msg);
            Resources resource = (Resources) getBaseContext().getResources();
            ColorStateList csl = (ColorStateList) resource.getColorStateList(R.color.red);
            textView.setTextColor(csl);
        };


    }

    /**
     * 检查权限
     */
    private void judgePermiss(String url) {
        TextView textView= (TextView) player.getStatuView();
        if(textView!=null){
            textView.setText("开始检测权限...");
        };
//        mUDRM.setAgentType(1);//不知道这是啥玩意
        preferences = getSharedPreferences("TVset", Context.MODE_PRIVATE);//拿到共享参数
        editor = preferences.edit();
        //得到DRM服务器地址，这个地址应该在外面早早地设置
        String DrmServiceUrl = preferences.getString("udrmserviceurl", "https://10.2.40.94:443/udrmrsa/udrmGetLicense");
        operator = preferences.getString("uremserviceyys", "unitend");
        if(DrmServiceUrl.equals("")||operator.equals("")){
            Toast.makeText(this,"代理服务器或者运营商设置有误",Toast.LENGTH_LONG).show();
        }

        //格式化服务器地址
        DrmServiceUrl = DrmServiceUrl.replaceAll("：", ":");
        DrmServiceUrl = DrmServiceUrl.replaceAll("。", ".");
        if (!DrmServiceUrl.contains(":")) {
            DrmServiceUrl = DrmServiceUrl + ":443";
        }
        /**
         * 这些东西哪里出来的？固定的?·
         */
        drmUrl = getDrmUrl(url);//得到代理服务器地址drmUrl = "METHOD=SAMPLE-AES,VDECFORMAT=h264-f,KEYFORMATVERSIONS=1,KEYFORMAT=chinadrm,OPERATOR=unitend,MIMETYPE=application/vnd.unitend.drm,IV=000102030405060708090A0B0C0D0E0F,CONTENTID=20191025,URI=https://192.168.113.40:443/udrmrsa/udrmGetLicense"
        String macAddress=LogUtils.getMac(MainActivity.this);
        LogUtils.i("得到的mac地址为:"+macAddress);
//        String macAddress= "18-99-F5-BF-81-46";//这个mac地址未来需要获取
        String  deviceId  = "deviceTest";
        String userId="5787989";
        String auth = "***********";
        String AAA= "deviceId="+deviceId+"$$userId="+userId+"$$auth="+auth;//格式不变
        String udrmip =DrmServiceUrl;
        if(!TextUtils.isEmpty(deviceId) && !TextUtils.isEmpty(macAddress)&& !TextUtils.isEmpty(AAA) && !TextUtils.isEmpty(contentID)){
            mUDRM.UDRMSetDrmInfo(udrmip, macAddress,AAA,contentID,onUDRMListener);
            Log.i("test", "UDRMSetDrmInfo :" + " udrmip : "+ udrmip +",macAddress = "+ macAddress +" ,AAA = "+ AAA + ",contentID = "+contentID);
        }else{
            GlobalToast.show("获取权限时参数短缺",Toast.LENGTH_LONG);
            Log.i("test", "UDRMSetDrmInfo :" + " udrmip : "+ udrmip +",macAddress = "+ macAddress +" ,AAA = "+ AAA + ",contentID = "+contentID);
        }
//        //启动检测权限
        /**
         * mUDRM :解密播放器实例
         * playhandler:handler
         * drmUrl:服务器地址
         * username:用户名
         * passwed:密码
         * authenticationinfo：鉴权地址
         * macAddress：mac 地址
         * contentID：内容id
         * operator:运营商
         *
         */
        CheckPremissionRunnable checkPremissionRunnable=new CheckPremissionRunnable(mUDRM,mainHandler,drmUrl,username,passwd,authenticationinfo,macAddress,contentID,operator);
        Thread thread=new Thread(checkPremissionRunnable);
        thread.start();
    }
    private String getDrmUrl(String path) {
        String drmInfo = null;
        String str = preferences.getString("udrmserviceurl", "https://10.2.40.94:443/udrmrsa/udrmGetLicense");
        operator = preferences.getString("uremserviceyys", "unitend");

        Pattern p = Pattern.compile("\\s*|\t|\r|\n");
        Matcher m = p.matcher(path);
        path = m.replaceAll("").trim();

        if (str.equals("") || str == null)
            drmInfo = drminfo;
        else {
            if (!("".equals(path)) || path != null) {
                if (!(operator.equals("")) || operator != null) {
                    if (!"".equals(contentID) && null != contentID) {
                        drmInfo = "METHOD=SAMPLE-AES,VDECFORMAT=h264-f,KEYFORMATVERSIONS=1,KEYFORMAT=chinadrm,OPERATOR="
                                + operator
                                + ",MIMETYPE=application/vnd.unitend.drm,IV=000102030405060708090A0B0C0D0E0F,CONTENTID="
                                + contentID
                                + ",URI="
                                + str;
                    } else {
                        drmInfo = "METHOD=SAMPLE-AES,VDECFORMAT=h264-f,KEYFORMATVERSIONS=1,KEYFORMAT=chinadrm,OPERATOR="
                                + operator
                                + ",MIMETYPE=application/vnd.unitend.drm,IV=000102030405060708090A0B0C0D0E0F,CONTENTID=20160223,"
                                + ",URI="
                                + str;
                    }
                }
            }

        }
        return drmInfo;
    }



    /***
     * udrm监听事件
     */
    private OnUDRMListener onUDRMListener = new OnUDRMListener() {
        @Override
        public void onInfoListener(int uniqueId, int type, Object message) {
            if(type == -9999){
                Message msg = new Message();
                msg.obj = message;
                msg.what = LOG_MSG_CAT;
                mainHandler.sendMessage(msg);
            }
        }

        @Override
        public void onErrorListener(int uniqueId, int errorNO, String message) {
            Message msg = new Message();
            msg.arg1 = errorNO;
            msg.obj = message;
            msg.what = UDRM_DECRYPT_FAILED;
            mainHandler.sendMessage(msg);
        }

        @Override
        public void onEventListener(int uniqueId, int eventNO, String message) {

        }
    };
    //有权限转换url(转换的是加密的视频)
    public void zhuanhuanUrl() {
        TextView textView= (TextView) player.getStatuView();
        if(textView!=null){
            textView.setText("检测权限成功,开始转换代理url...");
            Resources resource = (Resources) getBaseContext().getResources();
            ColorStateList csl = (ColorStateList) resource.getColorStateList(R.color.wrning);
            textView.setTextColor(csl);
        };
        /*
         * 启动播放代理
         */
        LogUtils.i("准备播放的url为 "+playUrl);
        StartPlayRunnable startPlayRunnable=new StartPlayRunnable(this,mUDRM,playUrl,mainHandler);
        Thread thread=new Thread(startPlayRunnable);
        thread.start();
    }

    //转换url成功，开始播放
    public  void UDRMUrlSuccess(String url) {
        if(player.getCurrentState()==-1){

        }

        TextView textView= (TextView) player.getStatuView();
        if(textView!=null){
            textView.setText("转换成功，开始播放");
        };
        player.setViSIBLE(View.GONE);
        player.setStartAfterPrepared(true);
        player.setAutoFullWithSize(false);
        player.setShowFullAnimation(false);
        player.setNeedLockFull(true);
        player.setRotateViewAuto(false);
        player.setLockLand(false);
        //设置返回键
        player.setNeedShowWifiTip(false);
        player.setUp(url,true,"");
        player.startPlayLogic();//立即开始播放
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(player!=null){
            player.getCurrentPlayer().onVideoPause();
        }
    }
    @Override
    protected void onResume() {
        if(player!=null){
        player.getCurrentPlayer().onVideoResume(false);
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(player!=null) {
            player.getCurrentPlayer().release();
        }
        if(mUDRM!=null){
            mUDRM.stopPlayerAgent();
        }
    }
}

