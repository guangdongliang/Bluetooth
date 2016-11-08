package hust.example.bluetooth.bluetooth;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.HashMap;
import java.util.List;

/**
 * Created by 相信小东 on 2016/5/9.
 */
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    public static String TRANSLATE_DATA_SERVICE = "0000ffe5-0000-1000-8000-00805f9b34fb";
    public static String RECEIVE_DATA_SERVICE = "0000ffe0-0000-1000-8000-00805f9b34fb";
    public static String TRANSLATE_DATA_CHARACTERISTIC= "0000ffe9-0000-1000-8000-00805f9b34fb";
    public static String RECEIVE_DATA_CHARACTERISTIC= "0000ffe4-0000-1000-8000-00805f9b34fb";

    static {
        // Sample Services.
        attributes.put("0000ffe5-0000-1000-8000-00805f9b34fb", "Translate Data Service");
        attributes.put("0000ffe0-0000-1000-8000-00805f9b34fb", "Receive Data Service");
        // Sample Characteristics.
        // attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
        attributes.put("0000ffe9-0000-1000-8000-00805f9b34fb", "Translate Data Characteristic");
        attributes.put("0000ffe4-0000-1000-8000-00805f9b34fb", "Receive Data Characteristic");


    }


    public static BluetoothGattCharacteristic
    getTranslateCharacteristic(List<BluetoothGattService> gattServices) {
        BluetoothGattCharacteristic characteristic = null;
        if (gattServices != null) {
            for (BluetoothGattService gattService : gattServices) {
                String uuid = gattService.getUuid().toString();
                if (uuid.equals(SampleGattAttributes.TRANSLATE_DATA_SERVICE)) {

                    List<BluetoothGattCharacteristic> gattCharacteristics =
                            gattService.getCharacteristics();


                    // Loops through available Characteristics.
                    for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                        uuid = gattCharacteristic.getUuid().toString();
                        if (uuid.equals(SampleGattAttributes.TRANSLATE_DATA_CHARACTERISTIC)) {
                            characteristic = gattCharacteristic;
                        }
                    }
                }
            }
        }
        return characteristic;
    }


    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }


    public static BluetoothGattCharacteristic
    getReceiveCharacteristic(List<BluetoothGattService> gattServices) {
        BluetoothGattCharacteristic characteristic = null;
        if (gattServices != null) {
            for (BluetoothGattService gattService : gattServices) {
                String uuid = gattService.getUuid().toString();
                if (uuid.equals(SampleGattAttributes.RECEIVE_DATA_SERVICE)) {

                    List<BluetoothGattCharacteristic> gattCharacteristics =
                            gattService.getCharacteristics();


                    // Loops through available Characteristics.
                    for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                        uuid = gattCharacteristic.getUuid().toString();
                        if (uuid.equals(SampleGattAttributes.RECEIVE_DATA_CHARACTERISTIC)) {
                            characteristic = gattCharacteristic;
                        }
                    }
                }
            }
        }
        return characteristic;
    }

}
