package com.hujunfei.dinner;

import android.app.Activity;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Administrator on 2018/4/16.
 */

public class ClickListener2 extends Thread implements View.OnClickListener{

    EditText name;
    EditText password;
    Socket s;
    OutputStream out;
    InputStream in;
    private Thread th;
    BufferedReader read = null;
    Activity act;


    public ClickListener2(EditText name, EditText password,Activity act){
        this.name = name;
        this.password = password;
        this.act=act;
    }

    @Override
    public void onClick(View view) {
        // TODO Auto-generated method stub
        String name = this.name.getText().toString().trim();
        String password = this.password.getText().toString().trim();
        // int intport=Integer.parseInt(port);
        Log.v("name", name);
        Log.v("password", password);
		/*
		 * 如果线程的对象不是空
		 * 也就是一次只允许创建一个线程
		 */
//        if (th == null) {
        th = new Thread(this);
        th.start();
//        }
    }

    public void run(){
//        if(name.getText().toString().trim().equals("1")&&password.getText().toString().trim().equals("1")){
//            Intent intent = new Intent(act,LoginActivity.class);
//            act.startActivity(intent);
//
//        }
//        else{
//            Looper.prepare();
//            Toast.makeText(act,"账号密码错误", Toast.LENGTH_LONG).show();
//            Looper.loop();// 进入loop中的循环，查看消息队列
//        }
        try {
            Log.v("test", "运行到s赋值前");
//			s = new Socket("192.168.31.146", 8888);
            s = new Socket("172.20.10.2", 8888);
            Log.v("test", "运行到out赋值前");
            this.out = s.getOutputStream();
            this.in = s.getInputStream();
            Log.i("ClickListen", "连接服务器成功!!!");
            //告诉服务器自己是注册
            out.write("Register\r".getBytes());
            //传送想注册的账号密码给服务器

            out.flush();
            out.write((this.name.getText().toString().trim() + "|" + this.password.getText().toString().trim()+"\r").getBytes());
            System.out.println("发送了"+this.name.getText().toString().trim() + "|" + this.password.getText().toString().trim()+"\r");
            //读服务器传来的数据
            this.read = new BufferedReader(new InputStreamReader(this.in));
            String result=read.readLine();
            Log.v("服务器传来", result);
            if(result.toString().trim().equals("OK")){
                Intent intent = new Intent(act,MainActivity.class);
                Looper.prepare();
                Toast.makeText(act,"注册账号成功，欢迎登入", Toast.LENGTH_LONG).show();
                Looper.loop();// 进入loop中的循环，查看消息队列
                act.startActivity(intent);
            }else {
                Looper.prepare();
                Toast.makeText(act,"注册账号失败，该账号已被注册", Toast.LENGTH_LONG).show();
                Looper.loop();// 进入loop中的循环，查看消息队列
            }
        } catch (UnknownHostException e1) {
            Log.e("UnknownHostException", "error");
            e1.printStackTrace();
        } catch (IOException e1) {
            Log.e("IOException", "error");
            e1.printStackTrace();
        }
    }
}
