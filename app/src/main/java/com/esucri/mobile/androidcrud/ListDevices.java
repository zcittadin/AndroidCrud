package com.esucri.mobile.androidcrud;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;

public class ListDevices extends ListActivity {

    private BluetoothAdapter bluetoothAdapter = null;

    static String MAC_ADDRESS = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayAdapter<String> arrayDevices = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if(!pairedDevices.isEmpty()) {
            for (BluetoothDevice bd : pairedDevices) {
                String deviceName = bd.getName();
                String deviceMac = bd.getAddress();
                arrayDevices.add(deviceName + "\n" + deviceMac);
            }
        }
        setListAdapter(arrayDevices);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String generalInfo = ((TextView) v).getText().toString();
        String mac = generalInfo.substring(generalInfo.length() - 17);
        Intent returnMac = new Intent();
        returnMac.putExtra(MAC_ADDRESS, mac);
        setResult(RESULT_OK, returnMac);
        finish();
    }
}
