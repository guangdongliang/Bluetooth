package hust.example.bluetooth.Activity.fragment;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import hust.example.bluetooth.Activity.WifiActivity;
import hust.example.bluetooth.R;

/**
 * Created by ll on 2016/11/9.
 */

public class WifiListDeviceAdapter extends BaseAdapter {
    final String connectted="已连接";
    final String notConnectted="未连接";
    WifiScanFragment fragment;
    WifiActivity wifiActivity;
    LayoutInflater mLayoutInflater;
    public WifiListDeviceAdapter(WifiScanFragment fragment, Context context) {
        this.wifiActivity=(WifiActivity)context;
        this.fragment=fragment;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if(fragment.found!=null)
            return fragment.found.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(fragment.found==null)
            return null;
        ViewHolder viewHolder=null;
        if(viewHolder==null)
        {
            viewHolder=new ViewHolder();
            convertView=mLayoutInflater.inflate(R.layout.wifi_item,null);
            viewHolder.name=(TextView)convertView.findViewById(R.id.device_name);
            viewHolder.state=(TextView)convertView.findViewById(R.id.device_state);
            viewHolder.deviceNumber=(TextView)convertView.findViewById(R.id.device_lock_number);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder=(ViewHolder)convertView.getTag();
        }
        viewHolder.name.setText(fragment.found.get(position).SSID);
        viewHolder.state.setText(fragment.found.get(position).SSID.equals(wifiActivity.mConnectedDeviceName) ? connectted : notConnectted);
        viewHolder.deviceNumber.setText("授权锁：");
        convertView.setClickable(true);
        convertView.requestFocusFromTouch();
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("收到点击", "小米");
                FragmentManager fm = wifiActivity.getSupportFragmentManager();
                Bundle args=new Bundle();
                args.putParcelable("con",createWifiInfo(fragment.found.get(position).SSID,"",1));
                FragmentTransaction transaction = fm.beginTransaction();
                ControlLockFragment lockFragment=new ControlLockFragment();
                lockFragment.setArguments(args);
                transaction.replace(R.id.id_content,lockFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        return convertView;
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }

    public final class ViewHolder{
        public TextView name;
        public TextView state;
        public TextView deviceNumber;
    }
    public WifiConfiguration createWifiInfo(String SSID, String Password, int Type)
    {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        WifiConfiguration tempConfig = this.IsExsits(SSID);
        if(tempConfig != null) {
            wifiActivity.wifiManager.removeNetwork(tempConfig.networkId);
        }

        if(Type == 1) //WIFICIPHER_NOPASS
        {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if(Type == 2) //WIFICIPHER_WEP
        {
            config.hiddenSSID = true;
            config.wepKeys[0]= "\""+Password+"\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if(Type == 3) //WIFICIPHER_WPA
        {
            config.preSharedKey = "\""+Password+"\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    private WifiConfiguration IsExsits(String SSID)
    {
        List<WifiConfiguration> existingConfigs = wifiActivity.wifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs)
        {
            if (existingConfig.SSID.equals("\""+SSID+"\""))
            {
                return existingConfig;
            }
        }
        return null;
    }
}
