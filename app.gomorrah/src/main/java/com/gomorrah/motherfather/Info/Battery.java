package com.gomorrah.motherfather.Info;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

/**
 * Get battery info.
 */
public class Battery {

    protected Context context;

    public Battery(Context context) { this.context = context; }

    public float getLevel() {
        Intent batteryIntent = context.registerReceiver(
            null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        );
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        // Error checking that probably isn't needed but I added just in case.
        if(level == -1 || scale == -1) {
            return 50.0f;
        }

        return ((float)level / (float)scale) * 100.0f;
    }
}
