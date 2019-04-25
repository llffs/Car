package com.example.car;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class RegisterActivity extends AppCompatActivity {

    private String realCode;
    ImageView iv_registeractivity_back;              //返回
    ImageView imageView;                               //验证码
    Button bt_registeractivity_register;            //注册按钮
    EditText email;                                    //邮箱
    EditText et_registeractivity_username;          //注册用户名
    EditText et_registeractivity_password1;         //注册密码
    EditText et_registeractivity_password2;       //确认密码
    EditText et_registeractivity_phoneCodes;        //输入验证码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        Bmob.initialize(this, "3238c409b127a913b011c099243c8769");

        imageView = (ImageView) findViewById(R.id.iv_registeractivity_showCode);
        iv_registeractivity_back = (ImageView) findViewById(R.id.iv_registeractivity_back);
        et_registeractivity_username = (EditText) findViewById(R.id.et_registeractivity_username);
        et_registeractivity_password1 = (EditText) findViewById(R.id.et_registeractivity_password1);
        et_registeractivity_password2 = (EditText) findViewById(R.id.et_registeractivity_password2);
        email = (EditText)findViewById(R.id.email);
        et_registeractivity_phoneCodes = (EditText) findViewById(R.id.et_registeractivity_phoneCodes);
        bt_registeractivity_register = (Button) findViewById(R.id.bt_registeractivity_register);


        imageView.setImageBitmap(Code.getInstance().createBitmap());
        realCode = Code.getInstance().getCode().toLowerCase();

        imageView.setOnClickListener(new View.OnClickListener() {

            //        验证码显示
            @Override
            public void onClick(View v) {
                imageView.setImageBitmap(Code.getInstance().createBitmap());
                realCode = Code.getInstance().getCode().toLowerCase();

            }
        });
        //返回登录界面
        iv_registeractivity_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //注册功能
        bt_registeractivity_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BmobUser user = new BmobUser();
                final String name = et_registeractivity_username.getText().toString().trim();
                String password1 = et_registeractivity_password1.getText().toString().trim();
                String password2 = et_registeractivity_password2.getText().toString().trim();
                String code = et_registeractivity_phoneCodes.getText().toString().trim();
                final String email1 = email.getText().toString().trim();

//                注册验证
                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(password1) && !TextUtils.isEmpty(email1) && !TextUtils.isEmpty(code)) {
                    Log.e("_______________", realCode);
                    Log.d("_______________", code);
                    if (password1.equals(password2)) {
                        if (code.equalsIgnoreCase(realCode)) {
                            //将用户名和密码加入到数据库中
                            user.setUsername(name);
                            user.setPassword(password1);
                            user.setEmail(email1);

                            user.signUp(new SaveListener<User>() {
                                @Override
                                public void done(User user, BmobException e) {
                                    if (e == null) {
                                        Toast.makeText(RegisterActivity.this, "注册成功，请到" + email1 +"邮箱中确认", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent();
                                        intent.setClass(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Log.e("注册失败", "原因：", e);
                                        Toast.makeText(RegisterActivity.this,"失败" + e.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(RegisterActivity.this, "验证码错误,注册失败", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(RegisterActivity.this, "两次密码不一致，请重新输入！", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(RegisterActivity.this, "未完善信息，注册失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
