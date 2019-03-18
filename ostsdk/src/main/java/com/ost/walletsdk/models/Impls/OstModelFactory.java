/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.models.Impls;

import com.ost.walletsdk.models.OstDeviceManagerModel;
import com.ost.walletsdk.models.OstDeviceManagerOperationModel;
import com.ost.walletsdk.models.OstDeviceModel;
import com.ost.walletsdk.models.OstRuleModel;
import com.ost.walletsdk.models.OstSessionModel;
import com.ost.walletsdk.models.OstTokenHolderModel;
import com.ost.walletsdk.models.OstTokenModel;
import com.ost.walletsdk.models.OstTransactionModel;
import com.ost.walletsdk.models.OstUserModel;

public class OstModelFactory {

    private static volatile OstDeviceManagerOperationModel DEVICE_MANAGER_OPERATION_MODEL_INSTANCE;
    private static volatile OstTransactionModel TRANSACTION_MODEL_INSTANCE;
    private static volatile OstSessionModel SESSION_MODEL_INSTANCE;
    private static volatile OstDeviceModel DEVICE_MODEL_INSTANCE;
    private static volatile OstDeviceManagerModel DEVICE_MANAGER_MODEL_INSTANCE;
    private static volatile OstUserModel USER_MODEL_INSTANCE;
    private static volatile OstRuleModel RULE_MODEL_INSTANCE;
    private static volatile OstTokenModel TOKEN_MODEL_INSTANCE;
    private static volatile OstTokenHolderModel TOKEN_HOLDER_MODEL_INSTANCE;



    public static OstUserModel getUserModel() {
        if (USER_MODEL_INSTANCE == null) {
            synchronized (OstUserModelRepository.class) {
                if (USER_MODEL_INSTANCE == null) {
                    USER_MODEL_INSTANCE = new OstUserModelRepository();
                }
            }
        }
        return USER_MODEL_INSTANCE;
    }

    public static OstRuleModel getRuleModel() {
        if (RULE_MODEL_INSTANCE == null) {
            synchronized (OstRuleModelRepository.class) {
                if (RULE_MODEL_INSTANCE == null) {
                    RULE_MODEL_INSTANCE = new OstRuleModelRepository();
                }
            }
        }
        return RULE_MODEL_INSTANCE;
    }

    public static OstTokenModel getTokenModel() {
        if (TOKEN_MODEL_INSTANCE == null) {
            synchronized (OstTokenModelRepository.class) {
                if (TOKEN_MODEL_INSTANCE == null) {
                    TOKEN_MODEL_INSTANCE = new OstTokenModelRepository();
                }
            }
        }
        return TOKEN_MODEL_INSTANCE;
    }

    public static OstTokenHolderModel getTokenHolderModel() {
        if (TOKEN_HOLDER_MODEL_INSTANCE == null) {
            synchronized (OstTokenHolderModelRepository.class) {
                if (TOKEN_HOLDER_MODEL_INSTANCE == null) {
                    TOKEN_HOLDER_MODEL_INSTANCE = new OstTokenHolderModelRepository();
                }
            }
        }
        return TOKEN_HOLDER_MODEL_INSTANCE;
    }


    public static OstDeviceManagerModel getDeviceManagerModel() {
        if (DEVICE_MANAGER_MODEL_INSTANCE == null) {
            synchronized (OstDeviceManagerModelRepository.class) {
                if (DEVICE_MANAGER_MODEL_INSTANCE == null) {
                    DEVICE_MANAGER_MODEL_INSTANCE = new OstDeviceManagerModelRepository();
                }
            }
        }
        return DEVICE_MANAGER_MODEL_INSTANCE;
    }

    public static OstDeviceModel getDeviceModel() {
        if (DEVICE_MODEL_INSTANCE == null) {
            synchronized (OstDeviceManagerModelRepository.class) {
                if (DEVICE_MODEL_INSTANCE == null) {
                    DEVICE_MODEL_INSTANCE = new OstDeviceModelRepository();
                }
            }
        }
        return DEVICE_MODEL_INSTANCE;

    }

    public static OstSessionModel getSessionModel() {
        if (SESSION_MODEL_INSTANCE == null) {
            synchronized (OstSessionModelRepository.class) {
                if (SESSION_MODEL_INSTANCE == null) {
                    SESSION_MODEL_INSTANCE = new OstSessionModelRepository();
                }
            }
        }
        return SESSION_MODEL_INSTANCE;

    }

    public static OstTransactionModel getTransactionModel() {
        if (TRANSACTION_MODEL_INSTANCE == null) {
            synchronized (OstTransactionModelRepository.class) {
                if (TRANSACTION_MODEL_INSTANCE == null) {
                    TRANSACTION_MODEL_INSTANCE = new OstTransactionModelRepository();
                }
            }
        }
        return TRANSACTION_MODEL_INSTANCE;
    }

    public static OstDeviceManagerOperationModel getDeviceManagerOperationModel() {
        if (DEVICE_MANAGER_OPERATION_MODEL_INSTANCE == null) {
            synchronized (OstTransactionModelRepository.class) {
                if (DEVICE_MANAGER_OPERATION_MODEL_INSTANCE == null) {
                    DEVICE_MANAGER_OPERATION_MODEL_INSTANCE = new OstDeviceManagerOperationModelRepository();
                }
            }
        }
        return DEVICE_MANAGER_OPERATION_MODEL_INSTANCE;
    }

}