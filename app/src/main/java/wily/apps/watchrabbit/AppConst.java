package wily.apps.watchrabbit;

public final class AppConst {
    private AppConst(){}

    public static final String TAG = "WatchRabbit";

    public static final int HABBIT_MODIFY_MODE_ADD = 0;
    public static final int HABBIT_MODIFY_MODE_UPDATE = 1;

    public static final String INTENT_SERVICE_HABBIT_ID = "habbit_id";
    public static final String INTENT_SERVICE_TITLE = "title";
    public static final String INTENT_SERVICE_TYPE = "type";
    public static final String INTENT_SERVICE_PRIORITY = "priority";
    public static final String INTENT_SERVICE_ACTIVE = "active";
    public static final String INTENT_SERVICE_DELETE_LIST = "delete_list";
    public static final String INTENT_SERVICE_STATE = "state";

    public static final String INTENT_HABBIT_FRAG_ID = "id";
    public static final String INTENT_HABBIT_FRAG_UPDATE = "update";
}
