package com.saimon.lsschedule.model;

import java.util.List;

/**
 * Created at 6:17 PM on 1/1/14
 * Copyright 2013 Poolsidelabs Inc.
 *
 * @author Saimon Rai
 */
public class GroupContainer {

    private List<Group> groups;
    private List<Substation> substations;
    private List<Area> areas;

    public List<Group> getGroups() {
        return groups;
    }

    public List<Substation> getSubstations() {
        return substations;
    }

    public List<Area> getAreas() {
        return areas;
    }

}
