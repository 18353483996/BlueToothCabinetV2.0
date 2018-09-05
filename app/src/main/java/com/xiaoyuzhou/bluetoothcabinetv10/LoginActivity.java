package com.xiaoyuzhou.bluetoothcabinetv10;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private String register_username;
    private String register_password;
    private static String username;
    private static String password;
    private static Boolean remember_flag;
    private SharedPreferences pref;

    private TextView tv_agree;
    private TextView tv_forget;
    private TextView tv_register;
    private Button btn_login;
    private EditText et_login_username;
    private EditText et_login_password;
    private CheckBox cb_remember;

    final SpannableStringBuilder agree_style = new SpannableStringBuilder();
    final SpannableStringBuilder forget_style = new SpannableStringBuilder();
    final SpannableStringBuilder register_style = new SpannableStringBuilder();

    @Override
    protected void onCreate (Bundle savedInstanceState) {

        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );

        tv_agree = findViewById( R.id.tv_agree );
        tv_forget = findViewById( R.id.tv_forget );
        tv_register = findViewById( R.id.tv_register );
        btn_login = findViewById( R.id.btn_login );
        et_login_username = findViewById( R.id.et_userName );
        et_login_password = findViewById( R.id.et_password );
        cb_remember = findViewById( R.id.cb_checkbox );

        pref = getSharedPreferences( "data", MODE_PRIVATE );

        //设置文字
        agree_style.append( "登录即代表已经仔细阅读使用说明" );
        forget_style.append( "忘记密码?" );
        register_style.append( "新用户注册" );

        //设置使用说明文字点击事件
        ClickableSpan agree_clickableSpan = new ClickableSpan() {
            @Override
            public void onClick (View widget) {
                AlertDialog.Builder dialog = new AlertDialog.Builder( LoginActivity.this );
                dialog.setTitle( "505智能锁使用说明" );
                dialog.setCancelable( false );
                dialog.setMessage( "这是505自主开发的实验室管理系统，目前实现智能货柜开锁功能(带权限识别)，实验室留宿人员申请正在开发...，" +
                        "使用前请确保已经联系管理员注册用户账号和密码，" +
                        "本软件处于测试阶段，出现Bug敬请谅解，" +
                        "进入主界面后请先点击打开蓝牙，再点击搜索蓝牙设备，在下面的" +
                        "选项中发现BluetoothCabinet直接点击，第一个按钮提示连接是否成功，之后使用可直接点击搜索已配对设备进行连接" );
                dialog.setPositiveButton( "同意", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick (DialogInterface dialog, int which) {

                    }
                } );
                dialog.show();
            }
        };
        //设置忘记密码文字点击事件
        ClickableSpan forget_clickableSpan = new ClickableSpan() {
            @Override
            public void onClick (View widget) {
                Toast.makeText( LoginActivity.this, "忘记密码", Toast.LENGTH_SHORT ).show();
            }
        };
        //设置注册新用户文字点击事件
        ClickableSpan register_clickableSpan = new ClickableSpan() {
            @Override
            public void onClick (View widget) {
                Intent register_intent = new Intent( LoginActivity.this, RegisterActivity.class );
                startActivity( register_intent );
            }
        };

        //设置部分文字可响应点击事件，通过setSpan方法给到TextView
        agree_style.setSpan( agree_clickableSpan, 11, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
        tv_agree.setText( agree_style );
        forget_style.setSpan( forget_clickableSpan, 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
        tv_forget.setText( agree_style );
        register_style.setSpan( register_clickableSpan, 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
        tv_register.setText( agree_style );

        //配置给TextView
        tv_agree.setMovementMethod( LinkMovementMethod.getInstance() );
        tv_agree.setText( agree_style );
        tv_forget.setMovementMethod( LinkMovementMethod.getInstance() );
        tv_forget.setText( forget_style );
        tv_register.setMovementMethod( LinkMovementMethod.getInstance() );
        tv_register.setText( register_style );

        //登录按钮的点击事件
        btn_login.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick (View v) {

                //获取用户输入的用户名和密码
                username = et_login_username.getText().toString();
                password = et_login_password.getText().toString();
                //获取记住密码CheckBox的状态
                remember_flag = cb_remember.isChecked();
                //获取之前用户注册好的用户名和密码
                register_username = pref.getString( "username", "  " );
                register_password = pref.getString( "password", "  " );

                //如果用户名和密码都符合
                if (register_username.equals( username ) && register_password.equals( password )) {
                    if (remember_flag) {
                        SharedPreferences.Editor editor = getSharedPreferences( "data", MODE_PRIVATE ).edit();
                        editor.putBoolean( "remember_flag", remember_flag );
                        editor.apply();
                    }
                    //登录成功，进入主控制界面
                    Intent features_intent = new Intent( LoginActivity.this, FeaturesActivity.class );
                    startActivity( features_intent );
                } else {
                    //否则弹出提示
                    Toast.makeText( LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT ).show();
                }

            }
        } );

    }

    @Override
    protected void onResume () {
        super.onResume();
        //Activity重新启动的时候看一下复选框有没有被选中,如果选中了记住密码选项就在界面上显示用户名和密码
        remember_flag = pref.getBoolean( "remember_flag", false );
        System.out.println( "On Resume" + remember_flag + "" );
        if (remember_flag) {
            String username = pref.getString( "username", "" );
            String password = pref.getString( "password", "" );
            cb_remember.setChecked( true );
            et_login_username.setText( username );
            et_login_password.setText( password );

        }
    }

}

