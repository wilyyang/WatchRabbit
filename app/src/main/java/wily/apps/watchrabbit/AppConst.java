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

    public static final String INTENT_EVAL_FRAG_EVALUATION_HABBIT = "evaluationHabbit";
    public static final String INTENT_EVAL_FRAG_ID = "hid";
    public static final String INTENT_EVAL_FRAG_TYPE = "type";
    public static final String INTENT_EVAL_FRAG_TITLE = "title";

    public static final String INTENT_EVAL_EVALUATION= "evaluation";
    public static final String INTENT_EVAL_HABBIT = "habbit";
    public static final String INTENT_EVAL_HABBIT_ID = "hid";
    public static final String INTENT_EVAL_HABBIT_TYPE = "type";
    public static final String INTENT_EVAL_HABBIT_TITLE = "title";
    public static final String INTENT_EVAL_HABBIT_DATE = "date";

    public static final int EVALUATION_30_DAYS = 30;
    public static final int EVALUATION_7_DAYS = 7;
}
