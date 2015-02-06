package com.saimon.lsschedule.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created at 6:17 PM on 1/1/14
 * Copyright 2013 Poolsidelabs Inc.
 *
 * @author Saimon Rai
 */
public class Area {
    private int id;
    private String name;
    @SerializedName("name_nep")
    private String nameInNepali;
    @SerializedName("group_id")
    private int groupId;
    @SerializedName("substation_id")
    private int substationId;
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

    public int getGroupId() {
        return groupId;
    }

    public int getSubstationId() {
        return substationId;
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

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public void setSubstationId(int substationId) {
        this.substationId = substationId;
    }

    public void setInKtm(boolean inKtm) {
        this.inKtm = inKtm;
    }
}


