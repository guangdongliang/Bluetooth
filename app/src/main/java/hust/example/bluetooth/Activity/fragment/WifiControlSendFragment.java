package hust.example.bluetooth.Activity.fragment;

import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.Date;

import hust.example.bluetooth.Activity.MainActivity;
import hust.example.bluetooth.Activity.WifiActivity;
import hust.example.bluetooth.EspWifiAdminSimple;
import hust.example.bluetooth.MyLog;
import hust.example.bluetooth.R;
import hust.example.bluetooth.SocThread;

/**
 * Created by ll on 2016/11/9.
 */

public class WifiControlSendFragment extends Fragment {
    private static final String TAG="ControlLockFragment";
    private View view;
    private ImageView imgLock1,imgLock2,imgLock3;
    private ToggleButton lock1,lock2,lock3,lock4;
    private Button lock5;
    private ListView listView;
    private String [] lock_right;
    private boolean isViewCreated=false;
    private MenuItem item;
    private String messages;
    private ArrayAdapter<String> messageArrayAdapter;
    private String mDeviceAddress;
    private WifiActivity wifiActivity;
    private WifiConfiguration con;
    EspWifiAdminSimple simple;
    private SocThread socketThread;
    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                MyLog.i(TAG, "mhandler接收到msg=" + msg.what);
                if (msg.obj != null) {
                    String s = msg.obj.toString();
                    if (s.trim().length() > 0) {
                        MyLog.i(TAG, "mhandler接收到obj=" + s);
                        MyLog.i(TAG, "开始更新UI");
                        String date = sDateFormat.format(new java.util.Date());
                        messageArrayAdapter.add(date+"  "+s);
                        MyLog.i(TAG, "更新UI完毕");
                    } else {
                        Log.i(TAG, "没有数据返回不更新");
                    }
                }
            } catch (Exception ee) {
                MyLog.i(TAG, "加载过程出现异常");
                ee.printStackTrace();
            }
        }
    };
    Handler mhandlerSend = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                MyLog.i(TAG, "mhandlerSend接收到msg.what=" + msg.what);
                if (msg.what == 1) {
                    Toast.makeText(wifiActivity, "发送成功",Toast.LENGTH_SHORT).show();
                    messageArrayAdapter.add(sDateFormat.format(new Date())+"  "+msg.obj);
                } else {
                    messageArrayAdapter.add(sDateFormat.format(new Date())+"  "+msg.obj+"  "+"发送失败");
                    Toast.makeText(wifiActivity, "发送失败",Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ee) {
                MyLog.i(TAG, "加载过程出现异常");
                ee.printStackTrace();
            }
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wifiActivity=(WifiActivity)getActivity();
        con=getArguments().getParcelable("con");
        simple=new EspWifiAdminSimple(wifiActivity);
        startSocket();
    }
    public void startSocket() {
        socketThread = new SocThread(mhandler, mhandlerSend, wifiActivity);
        socketThread.start();
    }
    private void stopSocket() {
        socketThread.isRun = false;
        socketThread.close();
        socketThread = null;
        MyLog.i(TAG, "Socket已终止");
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        view=inflater.inflate(R.layout.content_control_lock,container,false);
        listView=(ListView)view.findViewById(R.id.lv);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
        isViewCreated=true;
        messageArrayAdapter=new ArrayAdapter<String>(wifiActivity,R.layout.message);
        listView.setAdapter(messageArrayAdapter);
    }
    private void initView(){
        final EditText editText=(EditText)view.findViewById(R.id.edit1);
        lock5=(Button)view.findViewById(R.id.lock5);
        lock5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                socketThread.send(editText.getText().toString());
                editText.setText("");
            }
        });
        listView=(ListView)view.findViewById(R.id.lv);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.bluetooth_chat, menu);
        if(con.SSID.equals(wifiActivity.mConnectedDeviceName)){
            menu.getItem(0).setIcon(R.drawable.wifi);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        this.item=item;
        if(item.getItemId()==R.id.secure_connect_scan&&wifiActivity.mConnected==false) {
            if(!socketThread.checkWifi())return false;
            simple.addNetwork(con);
            Log.d("进行连接","连接");
            socketThread.conn();
            return true;
        }
        if(item.getItemId()==R.id.secure_connect_scan&&wifiActivity.mConnected==true){
            wifiActivity.wifiManager.disconnect();
            Log.d("中断连接", "中断连接");
            stopSocket();
        }
        return false;
    }
}
