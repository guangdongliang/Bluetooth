package hust.example.bluetooth.Activity.fragment;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import hust.example.bluetooth.Activity.MainActivity;
import hust.example.bluetooth.Activity.WifiActivity;
import hust.example.bluetooth.R;

/**
 * Created by ll on 2016/11/9.
 */

public class WifiScanFragment extends Fragment {
    private View view;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isFinished=true;
    private ListView lv;
    public WifiListDeviceAdapter deviceAdapter;
    public List<ScanResult> found;
    private BroadcastReceiver wifiReciever;
    private WifiActivity wifiActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceAdapter=new WifiListDeviceAdapter(this,wifiActivity);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        wifiActivity=(WifiActivity)context;
        wifiReciever = new WiFiScanReceiver();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.scan_fragment,container,false);
        lv=(ListView)view.findViewById(R.id.lv);
        lv.setAdapter(deviceAdapter);
        swipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.swipe_container);
        //swipeRefreshLayout.setColorSchemeColors(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isFinished) {
                    wifiActivity.wifiManager.startScan();
                }
            }
        });
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0)
                    swipeRefreshLayout.setEnabled(true);
                else swipeRefreshLayout.setEnabled(false);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (wifiActivity.wifiManager == null) {
            // Device does not support Wi-Fi
            Toast.makeText(wifiActivity, "Oop! Your device does not support Wi-Fi",
                    Toast.LENGTH_SHORT).show();
            return;

        } else {
            IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            wifiActivity.registerReceiver(wifiReciever, filter);
            if (!wifiActivity.wifiManager.isWifiEnabled()) {

                Toast.makeText(wifiActivity, "Wi-Fi is turned on." +
                                "\n" + "Scanning for access points...",
                        Toast.LENGTH_SHORT).show();

                wifiActivity.wifiManager.setWifiEnabled(true);

            } else {
                Toast.makeText(wifiActivity, "Wi-Fi is already turned on." +
                                "\n" + "Scanning for access points...",
                        Toast.LENGTH_SHORT).show();
            }
            wifiActivity.wifiManager.startScan();

        }
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        wifiActivity.unregisterReceiver(wifiReciever);
    }

    class WiFiScanReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
                found = wifiActivity.wifiManager.getScanResults();
            }
            swipeRefreshLayout.setRefreshing(false);
            deviceAdapter.notifyDataSetChanged();
        }
    }
}
