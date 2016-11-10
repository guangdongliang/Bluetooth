package hust.example.bluetooth;

/**
 * Created by ll on 2016/11/9.
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class SocThread extends Thread {
    private String ip = "192.168.4.1";
    private int port =333;
    private String TAG = "socket thread";
    private int timeout = 100;

    public Socket client = null;
    PrintWriter out;
    EspWifiAdminSimple esp;
    BufferedReader in;
    public boolean isRun = true;
    Handler inHandler;
    Handler outHandler;
    Context ctx;
    private String TAG1 = "===Send===";
    SharedPreferences sp;

    public SocThread(Handler handlerin, Handler handlerout, Context context) {
        inHandler = handlerin;
        outHandler = handlerout;
        ctx = context;
        MyLog.i(TAG, "创建线程socket");
        esp=new EspWifiAdminSimple(context);
    }

    /**
     * 连接socket服务器
     */
    public void conn()
    {
        new Thread()
        {
            @Override
            public void run() {
                // TODO Auto-generated method stub

                try {
                    Log.i(TAG, "连接中……");
                    client = new Socket(ip, port);
                    client.setSoTimeout(timeout);// 设置阻塞时间
                    MyLog.i(TAG, "连接成功");
                    in = new BufferedReader(new InputStreamReader(
                            client.getInputStream()));
                    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                            client.getOutputStream())), true);
                    MyLog.i(TAG, "输入输出流获取成功");
                } catch (UnknownHostException e) {
                    MyLog.i(TAG, "连接错误UnknownHostException 重新获取");
                    e.printStackTrace();
                } catch (IOException e) {
                    MyLog.i(TAG, "连接服务器io错误");
                    e.printStackTrace();
                } catch (Exception e) {
                    MyLog.i(TAG, "连接服务器错误Exception" + e.getMessage());
                    e.printStackTrace();
                }

            }

        }.start();
    }

    /**
     * 实时接受数据
     */
    @Override
    public void run() {
        String line = "";
        while (isRun) {
            try {
                if (client != null) {
                    MyLog.i(TAG, "2.检测数据");
                    while ((line = in.readLine()) != null) {
                        MyLog.i(TAG, "3.getdata" + line + " len=" + line.length());
                        MyLog.i(TAG, "4.start set Message");
                        Message msg = inHandler.obtainMessage();
                        msg.obj = line;
                        inHandler.sendMessage(msg);// 结果返回给UI处理
                        MyLog.i(TAG1, "5.send to handler");
                    }

                } else {
                    MyLog.i(TAG, "没有可用连接");
                }
            } catch (Exception e) {
                MyLog.i(TAG, "数据接收错误" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送数据
     *
     * @param mess
     */
    public void send(String mess) {
        try {
            if (client != null) {
                MyLog.i(TAG1, "发送" + mess + "至"
                        + client.getInetAddress().getHostAddress() + ":"
                        + String.valueOf(client.getPort()));
                out.println(mess+"$");
                out.flush();
                MyLog.i(TAG1, "发送成功");
                Message msg = outHandler.obtainMessage();
                msg.obj = mess;
                msg.what = 1;
                outHandler.sendMessage(msg);// 结果返回给UI处理
            } else {
                MyLog.i(TAG, "client 不存在");
                Message msg = outHandler.obtainMessage();
                msg.obj = mess;
                msg.what = 0;
                outHandler.sendMessage(msg);// 结果返回给UI处理
                MyLog.i(TAG, "连接不存在重新连接");
            }

        } catch (Exception e) {
            MyLog.i(TAG1, "send error");
            e.printStackTrace();
        } finally {
            MyLog.i(TAG1, "发送完毕");

        }
    }

    /**
     * 关闭连接
     */
    public void close() {
        try {
            if (client != null) {
                MyLog.i(TAG, "close in");
                in.close();
                MyLog.i(TAG, "close out");
                out.close();
                MyLog.i(TAG, "close client");
                client.close();
            }
        } catch (Exception e) {
            MyLog.i(TAG, "close err");
            e.printStackTrace();
        }

    }
    public boolean checkWifi()
    {
        if(!esp.isWifiConnected())
        {
            Toast.makeText(ctx, "请连接wifi", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!(esp.getWifiConnectedSsid().contains("AI-THINKER")))
        {
            Toast.makeText(ctx, "请连接wifi模块", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}

