package com.xiaoyuzhou.bluetoothcabinetv10;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    private EditText et_register_username;
    private EditText et_resister_password;
    private EditText et_resister_password_check;
    private EditText et_register_manager;
    private Button btn_register;
    private Spinner s_choose_permissiom;

    private ArrayAdapter<String> adapter = null;
    private static final String [] user_permission ={"普通用户","销售部","办公室","设备部"};
    private static String user_premission;


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_register );

        et_register_username=findViewById( R.id.et_registerusername );
        et_resister_password=findViewById( R.id.et_registerpassword );
        et_resister_password_check=findViewById( R.id.et_registerpasswordcheck );
        et_register_manager=findViewById( R.id.et_register_manager );
        btn_register=findViewById( R.id.btn_register );
        s_choose_permissiom=findViewById( R.id.s_choose_premission );

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,user_permission);
        //设置下拉列表风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //将适配器添加到spinner中去
        s_choose_permissiom.setAdapter(adapter);
        s_choose_permissiom.setVisibility(View.VISIBLE);//设置默认显示

        //设置下拉列表选择点击事件
        s_choose_permissiom.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected (AdapterView<?> parent, View view, int position, long id) {
                user_premission=((TextView)view).getText().toString();
            }

            @Override
            public void onNothingSelected (AdapterView<?> parent) {

            }
        } );




        btn_register.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick (View v) {

                String username=et_register_username.getText().toString();
                String password=et_resister_password.getText().toString();
                String passwordcheck=et_resister_password_check.getText().toString();
                String manager_password=et_register_manager.getText().toString();

                if(manager_password.equals( "505tech" )){
                    if(!password.equals( passwordcheck )){
                        Toast.makeText( RegisterActivity.this,"密码不匹配，请重新输入密码" ,Toast.LENGTH_SHORT).show();
                        et_resister_password.setText( "" );
                        et_resister_password_check.setText( "" );
                    }else{
                        SharedPreferences.Editor editor =getSharedPreferences( "data" ,MODE_PRIVATE).edit();
                        editor.putString( "username",username );
                        editor.putString( "password",password );
                        editor.putString( "permission",user_premission );
                        editor.apply();
                        Toast.makeText( RegisterActivity.this,"注册成功",Toast.LENGTH_LONG ).show();
                    }
                }else{
                    Toast.makeText( RegisterActivity.this,"请联系管理员进行注册",Toast.LENGTH_LONG ).show();
                }



            }
        } );


    }
}
