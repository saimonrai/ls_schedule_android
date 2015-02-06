package com.saimon.lsschedule.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created at 7:10 PM on 12/29/13
 * Copyright 2013 Poolsidelabs Inc.
 *
 * @author Saimon Rai
 */
public final class LSContract {

    protected static interface GroupColumns {
        String NAME = "name";
        String NAME_NEP = "name_nep";
    }

    protected static interface SubstationColumns {
        String NAME = "name";
        String NAME_NEP = "name_nep";
        String IN_KTM = "in_ktm";
    }

    protected static interface AreaColumns {
        String NAME = "name";
        String NAME_NEP = "name_nep";
        String GROUP_ID = "group_id";
        String SUBSTATION_ID = "substation_id";
        String IN_KTM = "in_ktm";
    }

    protected static interface ScheduleColumns {
        String GROUP_ID = "group_id";
        String WEEKDAY = "weekday";
        String START_TIME = "start_time";
        String END_TIME = "end_time";
    }

    static final String CONTENT_AUTHORITY = "com.saimon.lsschedule.provider";
    static final Uri BASE_CONTENT_URI = new Uri.Builder()
            .scheme("content")
            .authority(CONTENT_AUTHORITY)
            .build();

    static final String PATH_GROUPS = "groups";
    static final String PATH_SUBSTATIONS = "substations";
    static final String PATH_AREAS = "areas";
    static final String PATH_SCHEDULES = "schedules";

    static final String PATH_WITH_SUBSTATION = "with_substations";

    public static final class Group implements BaseColumns, GroupColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_GROUPS).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.ls.groups";
        public static final String CONTENT_ITEM_TYPE = CONTENT_TYPE;
    }

    public static final class Substation implements BaseColumns, SubstationColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SUBSTATIONS).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.ls.substations";
        public static final String CONTENT_ITEM_TYPE = CONTENT_TYPE;
    }

    public static final class Area implements BaseColumns, AreaColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_AREAS).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.ls.areas";
        public static final String CONTENT_ITEM_TYPE = CONTENT_TYPE;

        public static Uri buildWithSubstationsUri() {
            return CONTENT_URI.buildUpon().appendPath(PATH_WITH_SUBSTATION).build();
        }
    }

    public static final class Schedule implements BaseColumns, ScheduleColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SCHEDULES).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.ls.schedules";
        public static final String CONTENT_ITEM_TYPE = CONTENT_TYPE;
    }

}
