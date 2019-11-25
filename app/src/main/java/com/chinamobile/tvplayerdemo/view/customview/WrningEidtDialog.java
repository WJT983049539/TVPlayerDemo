package com.chinamobile.tvplayerdemo.view.customview;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chinamobile.tvplayerdemo.R;

import androidx.annotation.NonNull;

/**
 * 提示dialog
 * @auther wjt
 * @date 2019/5/14
 */
public class WrningEidtDialog extends Dialog

{
    private Button yes;//确定按钮
    private Button no;//取消按钮
    private TextView titleTV;//消息标题文本
    private TextView message;//url
    private String titleStr;//从外界设置的title文本
    private String messageStr;//从外界设置的消息文本

    //确定文本和取消文本的显示的内容
    private String yesStr, noStr;
    private onNoOnclickListener noOnclickListener;//取消按钮被点击了的监听器
    private onYesOnclickListener yesOnclickListener;//确定按钮被点击了的监听器


    public WrningEidtDialog(@NonNull Context context) {
        super(context, R.style.MyDialog);
    }

    /**
     * 设置取消按钮的显示内容和监听
     *
     * @param str
     * @param onNoOnclickListener
     */
    public void setNoOnclickListener(String str, onNoOnclickListener onNoOnclickListener) {
        if (str != null) {
            noStr = str;
        }
        this.noOnclickListener = onNoOnclickListener;
    }

    /**
     * 设置确定按钮的显示内容和监听
     *
     * @param str
     * @param yesOnclickListener
     */
    public void setYesOnclickListener(String str, onYesOnclickListener yesOnclickListener) {
        if (str != null) {
            yesStr = str;
        }
        this.yesOnclickListener = yesOnclickListener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_wrning_dialog);
        //空白处不能取消动画
        setCanceledOnTouchOutside(false);

        //初始化界面控件
        initView();

        //初始化界面数据
        initData();
        //初始化界面控件的事件
        initEvent();
    }


    /**
     * 初始化界面控件
     */
    private void initView() {
        yes = findViewById(R.id.ed_yes);
        no = findViewById(R.id.ed_no);
        titleTV = (TextView) findViewById(R.id.ed_title);
        message = findViewById(R.id.ed_message);
        yes.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    yes.setTextColor(Color.parseColor("#ffffff"));
                }else{
                    yes.setTextColor(Color.parseColor("#606064"));
                }
            }
        });
        no.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    no.setTextColor(Color.parseColor("#ffffff"));
                }else{
                    no.setTextColor(Color.parseColor("#606064"));
                }
            }
        });
    }

    /**
     * 初始化界面控件的显示数据
     */
    private void initData() {
        //如果用户自定了title和message
        if (titleStr != null) {
            titleTV.setText(titleStr);
        }
        //如果设置按钮文字
        if (yesStr != null) {
            yes.setText(yesStr);
        }
        if (noStr != null) {
            no.setText(noStr);
        }
    }

    /**
     * 初始化界面的确定和取消监听
     */
    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yesOnclickListener != null) {
                    yesOnclickListener.onYesOnclick();
                }
            }
        });
        //设置取消按钮被点击后，向外界提供监听
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noOnclickListener != null) {
                    noOnclickListener.onNoClick();
                }
            }
        });
    }


    /**
     * 从外界Activity为Dialog设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        titleStr = title;
    }

    /**
     * 从外界Activity为Dialog设置message
     *
     */
    public String getMessageStr(){
        String str="";
     if(message.getText()!=null){
         str= message.getText().toString();
     }
     return str;
    }

    //取消按钮监听事件
    public interface onNoOnclickListener {
        public void onNoClick();
    }

    //确定按钮监听事件
    public interface onYesOnclickListener {
        public void onYesOnclick();
    }
    public void setMEssage(String messagess){
        if(message!=null){
            message.setText(messagess);
        }
    }

}

