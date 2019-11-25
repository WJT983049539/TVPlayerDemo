package com.chinamobile.tvplayerdemo.view.customview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinamobile.tvplayerdemo.R;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import androidx.annotation.Nullable;

public class TvPlayerView extends StandardGSYVideoPlayer {
    private TextView playstaus;
    private ImageView nextimage;
    private ImageView lastimage;
    private ImageView thumbb;
    private ImageView stop;
    public TvPlayerView(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public TvPlayerView(Context context) {
        super(context);
    }

    public TvPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public int getLayoutId() {
        return R.layout.tv_video;
    }
    @Override
    protected void touchSurfaceMoveFullLogic(float absDeltaX, float absDeltaY) {
        super.touchSurfaceMoveFullLogic(absDeltaX, absDeltaY);
        //不给触摸快进，如果需要，屏蔽下方代码即可
//        mChangePosition = false;

        //不给触摸音量，如果需要，屏蔽下方代码即可
        mChangeVolume = false;

        //不给触摸亮度，如果需要，屏蔽下方代码即可
        mBrightness = false;
    }

    /**
     * x 小于0等于快退
     * x 大于0 等于快进
     */
    public void showProgressDialog2(float deltaX1, String seekTime1, int seekTimePosition1, String totalTime1, int totalTimeDuration1){
        showProgressDialog(deltaX1, seekTime1, seekTimePosition1, totalTime1, totalTimeDuration1);
    }
    public void dissprogrssDialog2(){
        dismissProgressDialog();
    }

   public void clickStartButton(){
        clickStartIcon();
   }

    @Override
    protected void updateStartImage() {
        if(mStartButton instanceof ImageView) {
            ImageView imageView = (ImageView) mStartButton;
            if (mCurrentState == CURRENT_STATE_PLAYING) {
                imageView.setImageResource(com.shuyu.gsyvideoplayer.R.drawable.video_click_pause_selector);
            } else if (mCurrentState == CURRENT_STATE_ERROR) {
                imageView.setImageResource(com.shuyu.gsyvideoplayer.R.drawable.video_click_play_selector);
            } else {
                imageView.setImageResource(com.shuyu.gsyvideoplayer.R.drawable.video_click_play_selector);
            }
        }
    }
    @Override
    protected void init(Context context) {
        super.init(context);
        playstaus=findViewById(R.id.playstaus);
        thumbb=findViewById(R.id.thumbb);
        stop=findViewById(R.id.stop);
        stop.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    stop.setBackgroundColor(android.graphics.Color.parseColor("#48ccdf"));
                }else{
                    stop.setBackgroundColor(android.graphics.Color.parseColor("#00000000"));
                }
            }
        });
        setViewShowState(mStartButton, INVISIBLE);//先把开始按钮隐藏了


    }
    public View getThunbView(){
        if(thumbb!=null){
            return thumbb;
        }
        return null;
    }
    public View getStopView(){
        if(stop!=null){
            return stop;
        }
        return null;
    }
    @Override
    protected void hideAllWidget() {
        super.hideAllWidget();
    }
    public View getStatuView(){
        if(playstaus!=null){
            return playstaus;
        }
        return null;
    }
    public void setViSIBLE(int viv){
        if(playstaus!=null){
            playstaus.setVisibility(viv);
        }
    }
    //显示底部view
    public void showBottomView(){
        changeUiToPlayingShow();
        getStartButton().requestFocus();
    }
    //暂停的时候
    @Override
    protected void changeUiToPauseShow() {
        super.changeUiToPauseShow();
        getStopView().setFocusable(true);
        getStartButton().requestFocus();
    }
}
