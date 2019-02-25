package com.ost.mobilesdk;

public interface OstConstants {
    String RESPONSE_SUCCESS = "success";
    String RESPONSE_DATA = "data";
    int THREAD_POOL_SIZE = 5;
    String SALT = "salt";
    String RESULT_TYPE = "result_type";
    String SCRYPT_SALT = "scrypt_salt";
    String BLOCK_HEIGHT = "block_height";
    String BLOCK_TIME = "block_time";
    long SESSION_BUFFER_TIME = 60 * 60;
    int BLOCK_GENERATION_TIME = 3; //Todo:: Will come from config
    String DATA_DEFINATION = "data_defination";
    String USER_ID = "user_id";
    String DEVICE_ADDRESS = "device_address";
    int MAX_PIN_LIMIT = 3;
    String RULE_NAME = "rule_name";
    String TOKEN_ID = "token_id";
    String RULE_PARAMETERS = "rule_parameters";
    String AMOUNTS = "amounts";
    String ADDRESSES = "addresses";
    String METHOD = "method";
    String PARAMETERS = "parameters";


    //ToDo: Move this to OstWorkflows.java (Create one)
    enum WORKFLOW_TYPE {
        UNKNOWN,
        REGISTER_DEVICE,
        ACTIVATE_USER,
        ADD_DEVICE,
        PERFORM,
        GET_PAPER_WALLET,
        ADD_SESSION,
        EXECUTE_TRANSACTION
    }
}