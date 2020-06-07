package com.example.infobateria;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    TextView result;
    ProgressBar progressbar;
    private BroadcastReceiver batteryInfoReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateBatteryData(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadBatteryInfo();
        TextView resultT = findViewById(R.id.textview);
        result = resultT;

        ProgressBar progressbarT = findViewById(R.id.progressBar);
        progressbar = progressbarT;
//

    }

    private void loadBatteryInfo() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);

        registerReceiver(batteryInfoReciver, intentFilter);
    }

    private void updateBatteryData(Intent intent) {
        boolean present = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false);
        if (present) {
            StringBuilder batteryInfo = new StringBuilder();
            int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
            batteryInfo.append("\n");
            batteryInfo.append("Stan baterii: " + health).append("\n\n");

            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            if (level != -1 && scale != -1) {
                int batteryPct = (int) ((level / (float) scale) * 100f);
                batteryInfo.append("Poziom naładowania: " + batteryPct).append("\n\n");
                progressbar.setProgress(batteryPct);
            }

            int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
            batteryInfo.append("Czy Podłączona: " + plugged).append("\n\n");

            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            batteryInfo.append("Status ładowania: " + status).append("\n\n");



            int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
            batteryInfo.append("Napięcie: " + voltage).append(" mV\n\n");

            long capacity = getBatteryCapacity();
            batteryInfo.append("Pojemność akumulatora: " + capacity).append(" mAh\n\n");

            if (intent.getExtras() != null) {
                String technology = intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);
                batteryInfo.append("Technologia: " + technology).append("\n\n");
            }


            int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
            if (temperature > 0) {
                batteryInfo.append("Temperatura: " + ((float) temperature / 10f)).append("*C\n\n");
            }

            //Toast.makeText(MainActivity.this,batteryInfo, Toast.LENGTH_LONG).show();

            result.setText(batteryInfo);

        } else {
            //Toast.makeText(MainActivity.this,"No Battery Present", Toast.LENGTH_LONG).show();
            result.setText("Brak Baterii / Błąd Baterii");
        }
    }

    private long getBatteryCapacity() {
        if (Build.VERSION.SDK_INT > -Build.VERSION_CODES.LOLLIPOP) {
            BatteryManager mBatteryManager = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);
            Long chargeCounter = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
            Long capacity = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

            if (chargeCounter != null && capacity != null) {
                long value = (long) (((float) chargeCounter / (float) capacity) * 100f);
                return value;
            }

        }
        return Long.parseLong(null);
    }

}
