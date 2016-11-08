package hust.example.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import hust.example.bluetooth.Activity.MainActivity;

/**
 * Created by 相信小东 on 2016/5/3.
 */
public class BluetoothControl {
    private Set<BluetoothDevice> bondedDevices;
    private Set<BluetoothDevice> foundDevice;
    private BluetoothAdapter bluetoothAdapter;
    private MainActivity activity;
    final private IntentFilter intentFilter;
    public BroadcastReceiver receiver;
    BluetoothDevice connectedDevice;

    public BluetoothControl(MainActivity context) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter().getDefaultAdapter();
        foundDevice = new HashSet<BluetoothDevice>();
        bondedDevices = new HashSet<BluetoothDevice>();
        this.activity = context;
        searchConnectedDevice();
        intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context1, Intent intent) {
                BluetoothDevice de = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction()) && de != null) {
                    Log.d("发现目标", de.getName());
                    foundDevice.add(de);

                } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(intent.getAction()) && de != null)
                    bondedDevices.add(de);
                //activity.deviceAdapter.notifyDataSetChanged();
            }
        };
        context.registerReceiver(receiver, intentFilter);

    }

    public Set<BluetoothDevice> getBondedDevices() {
        return bondedDevices;
    }

    public Set<BluetoothDevice> getFoundDevice() {
        return foundDevice;
    }

    public BluetoothDevice getConnectedDevice() {
        return connectedDevice;
    }

    public void refresh() {
        if (bluetoothAdapter != null) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivity(intent);
        }
        foundDevice.clear();
        bondedDevices.clear();
        bluetoothAdapter.startDiscovery();
    }

    public void connect(final BluetoothDevice device) throws IOException {

        new Runnable() {
            @Override
            public void run() {
                //final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
                final String SPP_UUID = "00001106-0000-1000-8000-00805F9B34FB";
                UUID uuid = UUID.fromString(SPP_UUID);
                BluetoothSocket socket = null;
                try {
                    socket = device.createRfcommSocketToServiceRecord(uuid);
                    if(bluetoothAdapter.isDiscovering())
                        bluetoothAdapter.cancelDiscovery();
                    socket.connect();
                    connectedDevice = socket.getRemoteDevice();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.run();

    }

    private void searchConnectedDevice()
    {
        int a2dp = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP);
        int headset = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET);
        int health = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEALTH);
        int flag = -1;
        if (a2dp == BluetoothProfile.STATE_CONNECTED) {
            flag = a2dp;
        } else if (headset == BluetoothProfile.STATE_CONNECTED) {
            flag = headset;
        } else if (health == BluetoothProfile.STATE_CONNECTED) {
            flag = health;
        }
        if (flag != -1) {
            bluetoothAdapter.getProfileProxy(activity, new BluetoothProfile.ServiceListener() {
                @Override
                public void onServiceDisconnected(int profile) {
                }
                @Override
                public void onServiceConnected(int profile, BluetoothProfile proxy) {
                    List<BluetoothDevice> mDevices = proxy.getConnectedDevices();
                    if (mDevices != null && mDevices.size() > 0) {
                        for (BluetoothDevice device : mDevices) {
                            Log.i("W","device name:"+ device.getName());
                            connectedDevice=device;
                        }
                    } else {
                        Log.i("W","mDevices is null");
                    }
                }
            }, flag);
        }
    }
}
