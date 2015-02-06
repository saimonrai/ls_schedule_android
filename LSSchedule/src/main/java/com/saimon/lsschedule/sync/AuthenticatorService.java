package com.saimon.lsschedule.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created at 5:28 PM on 12/29/13
 * Copyright 2013 Poolsidelabs Inc.
 *
 * @author Saimon Rai
 */
public class AuthenticatorService extends Service {

    private Authenticator mAuthenticator;

    @Override
    public void onCreate() {
        super.onCreate();
        mAuthenticator = new Authenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }

}
