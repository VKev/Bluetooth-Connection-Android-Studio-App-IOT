package com.example.bluetoothconnection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "BluetoothHelper";
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 2;
    private static final int REQUEST_BLUETOOTH_CONNECT_PERMISSION = 3;

    private EditText editDate;
    private TextView textView;
    private Button send;

    private EditText editCustomString;
    private TextView customStringSuccess;
    private Button sendString;

    private Spinner modeSpinner;
    private String selectedMode;

    private Button scanBluetooth;

    private BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    List<BluetoothDevice> pairedDevices;
    private BluetoothSocket btSocket ;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        editDate = (EditText) findViewById(R.id.dateInput);
        textView = (TextView) findViewById(R.id.textView_dateSuccess);
        sendString = (Button) findViewById(R.id.button_dateSend);


        editCustomString = (EditText) findViewById(R.id.customStringInput);
        customStringSuccess = (TextView) findViewById(R.id.textView_customStringSuccess);
        send = (Button) findViewById(R.id.button_customStringSend);

        scanBluetooth = (Button) findViewById(R.id.button_scan);

        modeSpinner = findViewById(R.id.modeSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.mode_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeSpinner.setAdapter(adapter);
        modeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item
                selectedMode = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optionally handle when nothing is selected
            }
        });

        Button confirmModeButton = findViewById(R.id.confirmModeButton);
        confirmModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the mode confirmation
                handleModeConfirmation();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dateTimeString = editDate.getText().toString();
                SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                try {
                    Date dateTime = inputFormat.parse(dateTimeString);
                    // Do something with the datetime, e.g., display it in a Toast or log it
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    String formattedDateTime = outputFormat.format(dateTime);
                    textView.setText("Send successfully!");
                    textView.setTextColor(Color.GREEN);

                } catch (Exception e) {
                    textView.setText("Wrong format!");
                    textView.setTextColor(Color.RED);
                }
            }
        });

        sendString.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String customString = editCustomString.getText().toString();
                customStringSuccess.setText("Send successfully!");
            }
        });

        scanBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothHelper btHelper = new BluetoothHelper();
                if (btAdapter != null) {
                    pairedDevices = btHelper.getPairedDevices(btAdapter);
                    LinearLayout deviceListLayout = findViewById(R.id.deviceListLayout);
                    deviceListLayout.removeAllViews();
                    for (BluetoothDevice device : pairedDevices) {
                        addButtonToDeviceList(deviceListLayout, device);
                    }
                } else {
                    Log.d(TAG, "Failed to obtain Bluetooth adapter.");
                }
            }
        });
    }

    private void handleModeConfirmation() {
        // Use the selectedMode variable as needed
        TextView modeSelectionResult = findViewById(R.id.textView_modeSuccess);
        modeSelectionResult.setText("Selected Mode: " + selectedMode);
    }

    private void addButtonToDeviceList(LinearLayout layout, BluetoothDevice device) {
        Button button = new Button(this);
        button.setText(device.getName());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(MainActivity.this, "Clicked device: " + device.getName(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "CLick");
                int count=0;
                do {
                    try {
                        btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                        btSocket.connect();
                        // Connection successful, handle further actions (e.g., data transfer)
                        Log.d(TAG, "" + btSocket.isConnected());
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to connect to device: " + e.getMessage());
                    }
                    count++;
                }while (!btSocket.isConnected() && count <5);
            }
        });
        layout.addView(button);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (btSocket != null) {
            try {
                btSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error while closing socket: " + e.getMessage());
            }
        }
    }

}