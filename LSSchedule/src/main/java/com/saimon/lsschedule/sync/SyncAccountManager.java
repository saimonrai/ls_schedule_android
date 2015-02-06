package com.saimon.lsschedule.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;

import com.saimon.lsschedule.Constants;

/**
 * Created at 10:42 PM on 1/5/14
 * Copyright 2013 Poolsidelabs Inc.
 *
 * @author Saimon Rai
 */
public class SyncAccountManager {

    /**
     * Create a new dummy account for the sync adapter
     *
     * @param context The application context
     */
    public static Account createSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(Constants.ContentProvider.ACCOUNT,
                Constants.ContentProvider.ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
//            ContentResolver.setIsSyncable(newAccount, Constants.ContentProvider.AUTHORITY, 1);
//            // Turn on periodic sync
//            ContentResolver.addPeriodicSync(
//                    newAccount,
//                    Constants.ContentProvider.AUTHORITY,
//                    new Bundle(),
//                    1 * 60);
//            ContentResolver.setSyncAutomatically(newAccount, Constants.ContentProvider.AUTHORITY, true);
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
        }

        return newAccount;
    }

}
