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
    int HASH_RATE = 3;
    String DATA_DEFINATION = "data_defination";
    String USER_ID = "user_id";
    String DEVICE_ADDRESS = "device_address";


    //ToDo: Move this to OstWorkflows.java (Create one)
    enum WORKFLOW_TYPE {
        UNKNOWN,
        REGISTER_DEVICE,
        ACTIVATE_USER,
    }
}