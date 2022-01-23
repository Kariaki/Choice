package com.kariaki.choice.ui.registrationandlogin;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;

public class DetectCountryCode {

    Activity activity;

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Activity getActivity() {
        return activity;
    }

    TelephonyManager tm = (TelephonyManager)activity.getSystemService(Context.TELEPHONY_SERVICE);
    String countryCodeValue = tm.getNetworkCountryIso();
}
