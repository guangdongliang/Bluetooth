package hust.example.bluetooth.Activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import hust.example.bluetooth.Activity.fragment.BLScanFragment;
import hust.example.bluetooth.BlueDatabase;
import hust.example.bluetooth.R;
import hust.example.bluetooth.bluetooth.BluetoothLeService;
import hust.example.bluetooth.bluetooth.SampleGattAttributes;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    static final int REQUEST_ENABLE_BT = 3;
    public BluetoothAdapter mBluetoothAdapter;
    public List<BluetoothGattService> mGattServices = new ArrayList<BluetoothGattService>();
    private static final String TAG="MainActivity";
    public BluetoothLeService mBluetoothLeService;
    public String mConnectedDeviceName="" ;
    public boolean mConnected=false;
    public BluetoothDevice mConnectedDevice;
    public final static String MESSAGE="MESSAGE";
    public BlueDatabase database;

    public final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                broadcastToast("蓝牙服务初始化失败");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
    private void broadcastToast(final String message){
        final Intent intent = new Intent(message);
        intent.putExtra(MESSAGE,message);
        sendBroadcast(intent);
    }
    /*public void showText(String str) {
        Log.d("第三次", "第三次");
        String messages= database.queryMessages(getIntent().getStringExtra("address"));
        messages = messages + "/" + str;
        database.updateMessages(getIntent().getStringExtra("address"), messages);
    }*/
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setDefaultFragment();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    protected void onDestroy() {
        super.onDestroy();
        if(mBluetoothLeService!=null&&mServiceConnection!=null){
            unbindService(mServiceConnection);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            showToast("蓝牙不可用");
        }
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            showToast("蓝牙不可用");
            return;
        }
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        if(mBluetoothLeService==null){
            Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
            bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        }
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }
    protected final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnectedDeviceName=intent.getStringExtra("mConnectedDeviceName");
                mConnected=intent.getBooleanExtra("mConnected",false);
                mConnectedDevice=intent.getParcelableExtra("mConnectedDevice");
                // TODO: 2016/11/8 ,添加deviceAdapter.notifyDataSetChanged();
                showToast("连接成功");
                //syncTime();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnectedDeviceName=intent.getStringExtra("mConnectedDeviceName");
                mConnected=intent.getBooleanExtra("mConnected",false);
                mConnectedDevice=intent.getParcelableExtra("mConnectedDevice");
                // TODO: 2016/11/8 添加deviceAdapter.notifyDataSetChanged();//updateUI(false);
                showToast("蓝牙断开");
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                mGattServices = mBluetoothLeService.getSupportedGattServices();
                setNotificationForReceiveCharacteristic();
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                handleReceiveMessage(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }else if(MESSAGE.equals(action)){
                showToast(intent.getStringExtra(MESSAGE));
            }
        }
    };





    public static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(MESSAGE);
        return intentFilter;
    }
    private void setNotificationForReceiveCharacteristic(){

        BluetoothGattCharacteristic characteristic = SampleGattAttributes.getReceiveCharacteristic(mGattServices);
        if (characteristic != null) {
            mBluetoothLeService.setCharacteristicNotification(characteristic, true);
        }
    }
    public void updateUI(boolean bool){

    }
    public void syncTime(){}
    void showToast(String str) {
        Toast.makeText(this, str + "第一个", Toast.LENGTH_SHORT).show();
    }
    void handleReceiveMessage(String str){}
    private void setDefaultFragment()
    {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        BLScanFragment blScanFragment=new BLScanFragment();
        transaction.replace(R.id.id_content, blScanFragment);
        transaction.commit();
    }
}
