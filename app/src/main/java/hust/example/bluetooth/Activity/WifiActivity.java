package hust.example.bluetooth.Activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;
import android.widget.Toast;

import hust.example.bluetooth.Activity.fragment.BLScanFragment;
import hust.example.bluetooth.Activity.fragment.WifiScanFragment;
import hust.example.bluetooth.R;
import hust.example.bluetooth.bluetooth.BluetoothLeService;

/**
 * Created by ll on 2016/11/9.
 */

public class WifiActivity extends AppCompatActivity {
    public String mConnectedDeviceName="";
    public WifiManager wifiManager;
    public boolean mConnected=false;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setDefaultFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver,new IntentFilter(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    protected final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION.equals(action)) {
                SupplicantState state=intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
                if(state==SupplicantState.COMPLETED){
                    Toast.makeText(WifiActivity.this,"wifi已连接",Toast.LENGTH_LONG).show();
                    mConnectedDeviceName=wifiManager.getConnectionInfo().getSSID();
                    mConnected=true;
                }else if(state==SupplicantState.DISCONNECTED){
                    Toast.makeText(WifiActivity.this,"wifi已断开",Toast.LENGTH_LONG).show();
                    mConnectedDeviceName="";
                    mConnected=false;
                }
            }
        }
    };
    private void setDefaultFragment()
    {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        WifiScanFragment wifiScanFragment=new WifiScanFragment();
        transaction.replace(R.id.id_content, wifiScanFragment);
        transaction.commit();
    }
}
