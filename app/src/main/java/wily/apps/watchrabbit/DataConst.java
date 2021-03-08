package wily.apps.watchrabbit;

public final class DataConst {
    private DataConst(){}

    public static final String TAG = "WatchRabbit";

    public static final String GROUP_KEY_HABBIT_NOTI = "wily.apps.watchrabbit.WatchRabbit";

    public static final int TYPE_HABBIT_CHECK = 1;
    public static final int TYPE_HABBIT_TIMER = 2;

    public static final int MODE_MODIFY_ADD = 0;
    public static final int MODE_MODIFY_UPDATE = 1;

    public static final String HABBIT_ID = "habbit_id";
    public static final String HABBIT_TITLE = "title";
    public static final String HABBIT_TYPE = "type";
    public static final String HABBIT_PRIORITY = "priority";
    public static final String HABBIT_ACTIVE = "active";
    public static final String HABBIT_DELETE_LIST = "delete_list";
    public static final String HABBIT_STATE = "state";

    public static final int HABBIT_STATE_CHECK = 1001;
    public static final int HABBIT_STATE_TIMER_START = 2001;
    public static final int HABBIT_STATE_TIMER_STOP = 2002;
}
