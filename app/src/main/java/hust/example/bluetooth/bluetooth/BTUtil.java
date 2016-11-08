package hust.example.bluetooth.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ll on 2016/11/8.
 */

public class BTUtil {
    public static BluetoothAdapter mBluetoothAdapter;
    public static List<BluetoothGattService> mGattServices = new ArrayList<BluetoothGattService>();
    public WeakReference mBluetoothLeService;
    public BTUtil(BluetoothLeService mBluetoothLeService) {
        this.mBluetoothLeService = new WeakReference(mBluetoothLeService);
    }

    public boolean sendMessage(String message, char type){
        BluetoothGattCharacteristic characteristic =SampleGattAttributes.getTranslateCharacteristic(mGattServices);
        Log.d("消息",getWrapperData(message,'@').toString());
        characteristic.setValue(getWrapperData(message,type));
        BluetoothLeService tem=(BluetoothLeService)mBluetoothLeService.get();
        if(tem==null)return false;
        tem.writeCharacteristic(characteristic);
        return true;
        //Toast.makeText(this, Arrays.toString(getWrapperData(message,'@')),Toast.LENGTH_SHORT).show();
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
    private  byte[] getWrapperData(String str, char type) {

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
}
