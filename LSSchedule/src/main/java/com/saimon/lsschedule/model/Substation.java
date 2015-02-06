package com.saimon.lsschedule.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created at 6:17 PM on 1/1/14
 * Copyright 2013 Poolsidelabs Inc.
 *
 * @author Saimon Rai
 */
public class Substation {
    private int id;
    private String name;
    @SerializedName("name_nep")
    private String nameInNepali;
    @SerializedName("in_ktm")
    private boolean inKtm;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNameInNepali() {
        return nameInNepali;
    }

    public boolean isInKtm() {
        return inKtm;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNameInNepali(String nameInNepali) {
        this.nameInNepali = nameInNepali;
    }

    public void setInKtm(boolean inKtm) {
        this.inKtm = inKtm;
    }
}
