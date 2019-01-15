package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.models.EconomyModel;
import com.ost.ostsdk.models.MultiSigModel;
import com.ost.ostsdk.models.MultiSigWalletModel;
import com.ost.ostsdk.models.RuleModel;
import com.ost.ostsdk.models.TokenHolderModel;
import com.ost.ostsdk.models.TokenHolderSessionModel;
import com.ost.ostsdk.models.UserModel;

public class ModelFactory {

    private static volatile TokenHolderSessionModelRepository TOKEN_HOLDER_SESSION_MODEL_INSTANCE;
    private static volatile MultiSigWalletModelRepository MULTI_SIG_WALLET_MODEL_INSTANCE;
    private static volatile MultiSigModel MULTI_SIG_MODEL_INSTANCE;
    private static volatile UserModel USER_MODEL_INSTANCE;
    private static volatile RuleModel RULE_MODEL_INSTANCE;
    private static volatile EconomyModel ECONOMY_MODEL_INSTANCE;
    private static volatile TokenHolderModel TOKEN_HOLDER_MODEL_INSTANCE;



    public static UserModel getUserModel() {
        if (USER_MODEL_INSTANCE == null) {
            synchronized (UserModelRepository.class) {
                if (USER_MODEL_INSTANCE == null) {
                    USER_MODEL_INSTANCE = new UserModelRepository();
                }
            }
        }
        return USER_MODEL_INSTANCE;
    }

    public static RuleModel getRuleModel() {
        if (RULE_MODEL_INSTANCE == null) {
            synchronized (RuleModelRepository.class) {
                if (RULE_MODEL_INSTANCE == null) {
                    RULE_MODEL_INSTANCE = new RuleModelRepository();
                }
            }
        }
        return RULE_MODEL_INSTANCE;
    }

    public static EconomyModel getEconomyModel() {
        if (ECONOMY_MODEL_INSTANCE == null) {
            synchronized (EconomyModelRepository.class) {
                if (ECONOMY_MODEL_INSTANCE == null) {
                    ECONOMY_MODEL_INSTANCE = new EconomyModelRepository();
                }
            }
        }
        return ECONOMY_MODEL_INSTANCE;
    }

    public static TokenHolderModel getTokenHolderModel() {
        if (TOKEN_HOLDER_MODEL_INSTANCE == null) {
            synchronized (TokenHolderModelRepository.class) {
                if (TOKEN_HOLDER_MODEL_INSTANCE == null) {
                    TOKEN_HOLDER_MODEL_INSTANCE = new TokenHolderModelRepository();
                }
            }
        }
        return TOKEN_HOLDER_MODEL_INSTANCE;
    }


    public static MultiSigModel getMultiSigModel() {
        if (MULTI_SIG_MODEL_INSTANCE == null) {
            synchronized (MultiSigModelRepository.class) {
                if (MULTI_SIG_MODEL_INSTANCE == null) {
                    MULTI_SIG_MODEL_INSTANCE = new MultiSigModelRepository();
                }
            }
        }
        return MULTI_SIG_MODEL_INSTANCE;
    }

    public static MultiSigWalletModel getMultiSigWalletModel() {
        if (MULTI_SIG_WALLET_MODEL_INSTANCE == null) {
            synchronized (MultiSigModelRepository.class) {
                if (MULTI_SIG_WALLET_MODEL_INSTANCE == null) {
                    MULTI_SIG_WALLET_MODEL_INSTANCE = new MultiSigWalletModelRepository();
                }
            }
        }
        return MULTI_SIG_WALLET_MODEL_INSTANCE;

    }

    public static TokenHolderSessionModel getTokenHolderSession() {
        if (TOKEN_HOLDER_SESSION_MODEL_INSTANCE == null) {
            synchronized (TokenHolderSessionModelRepository.class) {
                if (TOKEN_HOLDER_SESSION_MODEL_INSTANCE == null) {
                    TOKEN_HOLDER_SESSION_MODEL_INSTANCE = new TokenHolderSessionModelRepository();
                }
            }
        }
        return TOKEN_HOLDER_SESSION_MODEL_INSTANCE;

    }
}