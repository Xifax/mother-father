package com.gomorrah.motherfather.sms;

import android.telephony.SmsManager;

/**
 * SMS Abstraction
 */
public class Sms {

    protected SmsManager sms;
    protected String phone;
    protected String message;

    /**
     * Create new SMS
     * @param phone Recipient
     * @param message SMS contents
     */
    public Sms(String phone, String message) {
        this.phone = phone;
        this.message = message;
        this.sms = SmsManager.getDefault();
    }

    /**
     * Send SMS
     */
    public void send() throws IllegalArgumentException {
       sms.sendTextMessage(phone, null, message, null, null);
    }

}
