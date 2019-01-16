package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.models.OstDeviceManagerModel;
import com.ost.ostsdk.models.OstDeviceModel;
import com.ost.ostsdk.models.OstRuleModel;
import com.ost.ostsdk.models.OstTokenHolderModel;
import com.ost.ostsdk.models.OstSessionModel;
import com.ost.ostsdk.models.OstTokenModel;
import com.ost.ostsdk.models.OstUserModel;

public class OstModelFactory {

    private static volatile OstSessionModelRepository TOKEN_HOLDER_SESSION_MODEL_INSTANCE;
    private static volatile OstDeviceModelRepository MULTI_SIG_WALLET_MODEL_INSTANCE;
    private static volatile OstDeviceManagerModel MULTI_SIG_MODEL_INSTANCE;
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


    public static OstDeviceManagerModel getMultiSigModel() {
        if (MULTI_SIG_MODEL_INSTANCE == null) {
            synchronized (OstDeviceManagerModelRepository.class) {
                if (MULTI_SIG_MODEL_INSTANCE == null) {
                    MULTI_SIG_MODEL_INSTANCE = new OstDeviceManagerModelRepository();
                }
            }
        }
        return MULTI_SIG_MODEL_INSTANCE;
    }

    public static OstDeviceModel getMultiSigWalletModel() {
        if (MULTI_SIG_WALLET_MODEL_INSTANCE == null) {
            synchronized (OstDeviceManagerModelRepository.class) {
                if (MULTI_SIG_WALLET_MODEL_INSTANCE == null) {
                    MULTI_SIG_WALLET_MODEL_INSTANCE = new OstDeviceModelRepository();
                }
            }
        }
        return MULTI_SIG_WALLET_MODEL_INSTANCE;

    }

    public static OstSessionModel getTokenHolderSession() {
        if (TOKEN_HOLDER_SESSION_MODEL_INSTANCE == null) {
            synchronized (OstSessionModelRepository.class) {
                if (TOKEN_HOLDER_SESSION_MODEL_INSTANCE == null) {
                    TOKEN_HOLDER_SESSION_MODEL_INSTANCE = new OstSessionModelRepository();
                }
            }
        }
        return TOKEN_HOLDER_SESSION_MODEL_INSTANCE;

    }
}