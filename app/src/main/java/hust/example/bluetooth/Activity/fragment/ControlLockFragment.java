package hust.example.bluetooth.Activity.fragment;


import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import hust.example.bluetooth.Activity.MainActivity;
import hust.example.bluetooth.R;
import hust.example.bluetooth.bluetooth.BluetoothLeService;
import hust.example.bluetooth.bluetooth.SampleGattAttributes;

public class ControlLockFragment extends Fragment {

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
    private String mDeviceName;
    private MainActivity mainActivity;
    final SimpleDateFormat sDateFormat = new SimpleDateFormat("MM-dd hh:mm:ss");


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity=(MainActivity)getActivity();
        mDeviceAddress=getArguments().getString("mDeviceAddress");
        mDeviceName=getArguments().getString("mDeviceName");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.content_control_lock,container,false);
        return view;
    }

    void sendMessage(String message, char type) {
        // Check that we're actually connected before trying anything


        if (mainActivity.mConnectedDeviceName.equals("")) {
            showToast("未连接装置");
            return;
        }
        BluetoothGattCharacteristic characteristic =SampleGattAttributes.getTranslateCharacteristic(mainActivity.mGattServices);
        Log.d("消息",getWrapperData(message,'@').toString());
        characteristic.setValue(getWrapperData(message,type));
        mainActivity.mBluetoothLeService.writeCharacteristic(characteristic);
        showToast(Arrays.toString(getWrapperData(message,'@')));
    }

    /**
     * 将字符串包装成指定类型type的字符数组
     *
     * @param str  要被包装的字符串
     * @param type 数组首字节的值，对应自定义的数据类型
     *             ‘-’：同步时间
     *             ‘@’：传输命令
     *             ‘#’：传输数据
     * @return 返回都以'$'结尾的字符数组
     */
    private byte[] getWrapperData(String str, char type) {

        int arrayLen = 20;
        byte[] desArray = new byte[arrayLen];
        byte[] srcArray;
        srcArray = str.getBytes();

        int i = 0;
        if (srcArray.length < arrayLen - 1) {
            for (; i < srcArray.length; i++) {

                desArray[i + 1] = srcArray[i];
            }
        }
        desArray[0] = (byte) type;
        desArray[i + 1] = '$';

        return desArray;
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
        isViewCreated=true;
        listView.setAdapter(messageArrayAdapter);
/*        if (application.mBluetoothLeService != null) {
            final boolean result = application.mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }*/
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    private void initView(){
        final EditText editText=(EditText)view.findViewById(R.id.edit1);
        lock5=(Button)view.findViewById(R.id.lock5);
        lock5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(editText.getText().toString(),'@');
                editText.setText("");
            }
        });
        listView=(ListView)view.findViewById(R.id.lv);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.bluetooth_chat, menu);
        if(mDeviceName.equals(mainActivity.mConnectedDeviceName)){
            menu.getItem(0).setIcon(R.drawable.secure);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        this.item=item;
        if(item.getItemId()==R.id.secure_connect_scan&&mainActivity.mConnected==false) {
            mainActivity.mBluetoothLeService.connect(mDeviceAddress);
            Log.d("进行连接","连接");
            return true;
        }
        if(item.getItemId()==R.id.secure_connect_scan&&mainActivity.mConnected==true){
            mainActivity.mBluetoothLeService.disconnect();
            Log.d("中断连接", "中断连接");
        }
        return false;
    }
    void showToast(String text){
        Toast.makeText(mainActivity,text,Toast.LENGTH_LONG).show();
    }
    public void updateUI(String str){
        messageArrayAdapter.add(sDateFormat.format(new Date()+"  "+str));
    }
}
