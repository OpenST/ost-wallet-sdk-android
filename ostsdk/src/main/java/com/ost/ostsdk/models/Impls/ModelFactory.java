package com.ost.ostsdk.models.Impls;

import com.ost.ostsdk.models.EconomyModel;
import com.ost.ostsdk.models.RuleModel;
import com.ost.ostsdk.models.UserModel;

public class ModelFactory {

    private static volatile UserModel USER_MODEL_INSTANCE;
    private static volatile RuleModel RULE_MODEL_INSTANCE;
    private static volatile EconomyModel ECONOMY_MODEL_INSTANCE;


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


}