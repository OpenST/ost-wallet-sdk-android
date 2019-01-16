package com.ost.ostsdk.models.entities;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.ost.ostsdk.OstSdk;
import com.ost.ostsdk.models.Impls.OstModelFactory;
import com.ost.ostsdk.models.Impls.OstSecureKeyModelRepository;
import com.ost.ostsdk.models.OstTaskCallback;
import com.ost.ostsdk.models.OstUserModel;
import com.ost.ostsdk.utils.KeyGenProcess;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigInteger;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class OstSessionTest {

    private static Context mAppContext;

    static OstUserModel mUserRepository;

    @BeforeClass
    public static void setUp() {
        mAppContext = InstrumentationRegistry.getTargetContext();
        OstSdk.init(mAppContext);
        cleanDB();
    }

    private static void cleanDB() {
        new OstSecureKeyModelRepository().deleteAll(null);
        OstModelFactory.getUserModel().deleteAllUsers(null);
        OstModelFactory.getMultiSigWalletModel().deleteAllMultiSigWallets(null);
        OstModelFactory.getMultiSigModel().deleteAllMultiSigs(null);
        OstModelFactory.getTokenHolderModel().deleteAllTokenHolders(null);
        OstModelFactory.getRuleModel().deleteAllRules(null);
        OstModelFactory.getTokenHolderSession().deleteAllTokenHolderSessions(null);
    }

    @Test
    public void testTokenHolderSessionSigning() throws Exception {

        // Create OstUser
        OstUser ostUser = insertUserData("1", "1", "1", "1");

        // Create OstSecureKey
        String walletAddress = new KeyGenProcess().execute("1");

        OstTokenHolder ostTokenHolder = insertTokenHolder(ostUser.getId(), "1", "1");

        // Create Multi Sig
        OstDeviceManager ostDeviceManager = insertMultiSig(ostUser.getId(), "1", "1");

        //Create OstSession
        OstSession ostSession = insertTokenHolderSession(ostTokenHolder.getId(), "1", walletAddress);

        ostUser = updateUserData(ostUser, ostDeviceManager, ostTokenHolder);

        ostUser = OstSdk.getUser("1");
        ostTokenHolder = ostUser.getTokenHolder();
        ostSession = ostTokenHolder.getDeviceTokenHolderSession();

        JSONObject jsonObject = new OstSession.Transaction("0x5a85a1E5a749A76dDf378eC2A0a2Ac310ca86Ba8", "0xF281e85a0B992efA5fda4f52b35685dC5Ee67BEa")
                .setValue(new BigInteger("1"))
                .setGasPrice(new BigInteger("0"))
                .setGas(new BigInteger("0"))
                .setTxnCallPrefix("0x0")
                .setData("0xF281e85a0B992efA5fda4f52b35685dC5Ee67BEa")
                .setNonce(new BigInteger("1"))
                .toJSONObject();

        String signature = ostSession.signTransaction(jsonObject, ostUser.getId());

        Assert.assertEquals(132, signature.length());
    }

    private OstUser updateUserData(OstUser ostUser, OstDeviceManager ostDeviceManager, OstTokenHolder ostTokenHolder) throws InterruptedException {
        ostUser.setMultiSigId(ostDeviceManager.getId());
        ostUser.setTokenHolderId(ostTokenHolder.getId());

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        OstModelFactory.getUserModel().update(ostUser, new OstTaskCallback() {
            @Override
            public void onSuccess() {
                countDownLatch.countDown();
            }
        });

        countDownLatch.await(5, TimeUnit.SECONDS);
        return ostUser;
    }

    private OstSession insertTokenHolderSession(String parentId, String tokenHolderSessionId, String walletAddress) throws JSONException, InterruptedException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(OstBaseEntity.PARENT_ID, parentId);
        jsonObject.put(OstBaseEntity.ID, tokenHolderSessionId);
        jsonObject.put(OstSession.ADDRESS, walletAddress);
        jsonObject.put(OstSession.NONCE, "1");
        jsonObject.put(OstSession.SPENDING_LIMIT, "10000");
        jsonObject.put(OstSession.REDEMPTION_LIMIT, "10000");
        jsonObject.put(OstSession.EXPIRY_TIME, "100");
        jsonObject.put(OstSession.BLOCK_HEIGHT, "100");
        jsonObject.put(OstSession.TOKEN_HOLDER_ID, parentId);
        jsonObject.put(OstDevice.STATUS, OstDevice.CREATED_STATUS);

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        OstSession ostSession = OstModelFactory.getTokenHolderSession().initTokenHolderSession(jsonObject, new OstTaskCallback() {
            @Override
            public void onSuccess() {
                countDownLatch.countDown();
            }
        });

        countDownLatch.await(5, TimeUnit.SECONDS);

        return ostSession;
    }

    private OstDeviceManager insertMultiSig(String parentId, String multiSigId, String tokenHolderId) throws JSONException, InterruptedException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(OstBaseEntity.PARENT_ID, parentId);
        jsonObject.put(OstBaseEntity.ID, multiSigId);
        jsonObject.put(OstDeviceManager.ADDRESS, "0x2901239");
        jsonObject.put(OstDeviceManager.TOKEN_HOLDER_ID, tokenHolderId);
        jsonObject.put(OstDeviceManager.REQUIREMENT, 1);
        jsonObject.put(OstDeviceManager.AUTHORIZE_SESSION_CALL_PREFIX, "callPrefix");
        jsonObject.put(OstDeviceManager.NONCE, "1");
        jsonObject.put(OstDeviceManager.USER_ID, "123");

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        OstDeviceManager ostDeviceManager = OstModelFactory.getMultiSigModel().initMultiSig(jsonObject, new OstTaskCallback() {
            @Override
            public void onSuccess() {
                countDownLatch.countDown();
            }
        });

        countDownLatch.await(5, TimeUnit.SECONDS);

        return ostDeviceManager;
    }


    private OstUser insertUserData(String parentId, String userId, String tokenHolderId, String multiSigId) throws JSONException, InterruptedException {
        JSONObject userObj = new JSONObject();

        userObj.put(OstUser.PARENT_ID, parentId);
        userObj.put(OstUser.ID, userId);
        userObj.put(OstUser.TOKEN_ID, "1");
        userObj.put(OstUser.NAME, "ostUser");
        userObj.put(OstUser.TOKEN_HOLDER_ID, tokenHolderId);
        userObj.put(OstUser.MULTI_SIG_ID, multiSigId);

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        OstUser ostUser = OstSdk.initUser(userObj, new OstTaskCallback() {
            @Override
            public void onSuccess() {
                countDownLatch.countDown();
            }
        });

        countDownLatch.await(5, TimeUnit.SECONDS);

        return ostUser;
    }

    private OstTokenHolder insertTokenHolder(String parentId, String param, String userId) throws JSONException, InterruptedException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(OstBaseEntity.PARENT_ID, parentId);
        jsonObject.put(OstBaseEntity.ID, param);
        jsonObject.put(OstTokenHolder.ADDRESS, "0x2901239");
        jsonObject.put(OstTokenHolder.REQUIREMENTS, 1);
        jsonObject.put(OstTokenHolder.USER_ID, userId);
        jsonObject.put(OstTokenHolder.EXECUTE_RULE_CALL_PREFIX, "callPrefix");

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        OstTokenHolder ostTokenHolder = OstModelFactory.getTokenHolderModel().initTokenHolder(jsonObject, new OstTaskCallback() {
            @Override
            public void onSuccess() {
                countDownLatch.countDown();
            }
        });

        countDownLatch.await(5, TimeUnit.SECONDS);

        return ostTokenHolder;
    }
}
