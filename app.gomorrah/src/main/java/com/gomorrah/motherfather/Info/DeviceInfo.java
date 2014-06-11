package com.gomorrah.motherfather.Info;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Get and format device info
 */
public class DeviceInfo {
    protected final Context context;
    protected final String template = "Build: %s [os:%s]<br/>" +
            "Device: %s [model:%s, product:%s]<br/>" + "Battery: %.2f%%<br/>" + "Cpu: %.2f%%";

    protected String os;
    protected String release;
    protected String device;
    protected String model;
    protected String product;

    protected float battery;
    protected float cpu;

    protected String help;

    public DeviceInfo(Context context) {
        this.context = context;
        this.os = System.getProperty("os.version");
        this.release = Build.VERSION.RELEASE;
        this.device = Build.DEVICE;
        this.model = Build.MODEL;
        this.product = Build.PRODUCT;
        this.battery = (new Battery(context)).getLevel();
        this.cpu = (new CpuUsage()).getCurrent();
        this.help = "Get existing SMS list via GET '/sms' and send new SMS" +
                "via POST '/sms' with 'phone' and 'message' as params.";
    }

    /**
     * @return String representation
     */
    @Override public String toString() {
        return String.format(
            template,
            os, release, device, model, product, battery, cpu
        );
    }

    /**
     * @return Json representation
     */
    public String toJson() {
       JSONObject info = new JSONObject();
        try {
            info.put("os", os);
            info.put("release", release);
            info.put("device", device);
            info.put("model", model);
            info.put("product", product);
            info.put("battery", battery);
            info.put("cpu", cpu);
            info.put("help", help);
        } catch (JSONException e) {
            Log.e("json", e.getMessage());
        }

        return info.toString();
    }
}
