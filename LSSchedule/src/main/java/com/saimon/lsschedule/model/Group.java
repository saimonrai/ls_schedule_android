package com.saimon.lsschedule.model;

import android.database.Cursor;

import com.google.gson.annotations.SerializedName;
import com.saimon.lsschedule.provider.LSContract;

/**
 * Created at 6:10 PM on 1/1/14
 * Copyright 2013 Poolsidelabs Inc.
 *
 * @author Saimon Rai
 */
public class Group {
    private int id;
    private String name;
    @SerializedName("name_nep")
    private String nameInNepali;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNameInNepali() {
        return nameInNepali;
    }

    public static Group fromContentValues(Cursor cursor) {
        Group group = new Group();
        group.id = cursor.getInt(cursor.getColumnIndexOrThrow(LSContract.Group._ID));
        group.name = cursor.getString(cursor.getColumnIndexOrThrow(LSContract.Group.NAME));
        group.nameInNepali = cursor.getString(cursor.getColumnIndexOrThrow(LSContract.Group.NAME_NEP));
        return group;
    }

}
