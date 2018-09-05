package com.xiaoyuzhou.bluetoothcabinetv10;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class FeaturesActivity extends AppCompatActivity {

    private TextView tv_OpenCabinet;
    private TextView tv_OvernightApplication;
    private final SpannableStringBuilder OpenCabinet_style = new SpannableStringBuilder();
    private final SpannableStringBuilder OvernightApplication_style = new SpannableStringBuilder();
    private SharedPreferences pref;
    private String UserPermssion;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_features );

        pref = getSharedPreferences( "data", MODE_PRIVATE );
        tv_OpenCabinet=findViewById( R.id.tv_OpenCabinet );
        tv_OvernightApplication=findViewById( R.id.tv_OvernightApplication );

        OpenCabinet_style.append( "智能货柜管理" );
        OvernightApplication_style.append( "实验室留宿申请" );

        //设置智能货柜管理文字点击事件
        ClickableSpan OpenCabinet_clickableSpan=new ClickableSpan() {
            @Override
            public void onClick (View widget) {

                //获取之前用户注册好的用户名和密码
                UserPermssion = pref.getString( "permission", "没有获取到" );
                System.out.println("用户的权限是"+UserPermssion);
                if(UserPermssion.equals( "销售部" )||UserPermssion.equals( "设备部" )||UserPermssion.equals( "办公室" )){
                    //进入智能货柜管理界面
                    Intent features_intent = new Intent( FeaturesActivity.this, MainActivity.class );
                    startActivity( features_intent );
                }else{
                    Toast.makeText( FeaturesActivity.this,"对不起，你没有此权限",Toast.LENGTH_SHORT ).show();
                }


            }
        };

        //设置实验室留宿申请文字点击事件
        ClickableSpan OvernightApplication_clickableSpan=new ClickableSpan(){

            @Override
            public void onClick (View widget) {
                Toast.makeText( FeaturesActivity.this,"此项功能正在开发",Toast.LENGTH_SHORT ).show();
            }
        };



        //设置部分文字可响应点击事件，通过setSpan方法给到TextView
        OpenCabinet_style.setSpan( OpenCabinet_clickableSpan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
        tv_OpenCabinet.setText(OpenCabinet_style);
        OvernightApplication_style.setSpan( OvernightApplication_clickableSpan, 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
        tv_OvernightApplication.setText(OvernightApplication_style);
        //配置给TextView
        tv_OpenCabinet.setMovementMethod( LinkMovementMethod.getInstance() );
        tv_OpenCabinet.setText( OpenCabinet_style );
        tv_OvernightApplication.setMovementMethod( LinkMovementMethod.getInstance() );
        tv_OvernightApplication.setText( OvernightApplication_style );

    }
}
