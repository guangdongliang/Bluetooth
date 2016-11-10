package hust.example.bluetooth.Activity.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.ArrayList;

import hust.example.bluetooth.Activity.MainActivity;
import hust.example.bluetooth.R;

/**
 * Created by ll on 2016/11/8.
 */

public class BLScanFragment extends Fragment {
    private View view;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isFinished=true;
    private ListView lv;
    private MainActivity mainActivity;
    public ListDeviceAdapter deviceAdapter;
    public static BluetoothAdapter mBluetoothAdapter;
    public ArrayList<BluetoothDevice> found=new ArrayList<>();

    private android.os.Handler mHandler;

    private static final int REQUEST_ENABLE_BT = 1;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 5000;

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!found.contains(device)) {
                                found.add(device);
                                deviceAdapter.notifyDataSetChanged();
                            }

                        }
                    });
                }
            };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        mHandler = new Handler();


        deviceAdapter=new ListDeviceAdapter(this, getActivity());

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity=(MainActivity)activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        scanLeDevice(true);
        swipeRefreshLayout.setRefreshing(true);
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
                    scanLeDevice(true);
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
    public void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isFinished = true;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }, SCAN_PERIOD);
            found.clear();
            if(mainActivity.mConnectedDevice!=null){
                found.add(mainActivity.mConnectedDevice);
                deviceAdapter.notifyDataSetChanged();
            }
            if(mBluetoothAdapter==null)
                mBluetoothAdapter=mainActivity.mBluetoothAdapter;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            isFinished=false;
        } else {
            isFinished=true;
            swipeRefreshLayout.setRefreshing(false);
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }
}
