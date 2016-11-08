package hust.example.bluetooth;



import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import hust.example.bluetooth.Activity.fragment.ControlLockFragment;
import hust.example.bluetooth.Activity.MainActivity;
import hust.example.bluetooth.Activity.fragment.BLScanFragment;

/**
 * Created by 相信小东 on 2016/5/3.
 * 列出所有装置的adapter
 */
public class ListDeviceAdapter extends BaseAdapter {
    final String connectted="已连接";
    final String notConnectted="未连接";
    private LayoutInflater mLayoutInflater;
    private BLScanFragment fragment;
    private MainActivity mainActivity;
    public ListDeviceAdapter(BLScanFragment fragment, Context context) {
        this.fragment=fragment;
        mainActivity=(MainActivity)context;
        this.mLayoutInflater =LayoutInflater.from(context);
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
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(fragment.found==null)
            return null;
       ViewHolder viewHolder=null;
        if(viewHolder==null)
        {
            viewHolder=new ViewHolder();
            convertView=mLayoutInflater.inflate(R.layout.device_list_view,null);
            viewHolder.name=(TextView)convertView.findViewById(R.id.device_name);
            viewHolder.state=(TextView)convertView.findViewById(R.id.device_state);
            viewHolder.deviceNumber=(TextView)convertView.findViewById(R.id.device_lock_number);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder=(ViewHolder)convertView.getTag();
        }
        viewHolder.name.setText(fragment.found.get(position).getName());
        viewHolder.state.setText(fragment.found.get(position).getName().equals(mainActivity.mConnectedDeviceName) ? connectted : notConnectted);
        viewHolder.deviceNumber.setText("授权锁：");
        convertView.setClickable(true);
        convertView.requestFocusFromTouch();
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.scanLeDevice(false);
                Log.d("收到点击", "小米");
                FragmentManager fm = mainActivity.getSupportFragmentManager();
                Bundle args=new Bundle();
                args.putString("mDeviceAddress",fragment.found.get(position).getAddress());
                args.putString("mDeviceName",fragment.found.get(position).getName());
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
    public final class ViewHolder{
        public TextView name;
        public TextView state;
        public TextView deviceNumber;
    }

}
