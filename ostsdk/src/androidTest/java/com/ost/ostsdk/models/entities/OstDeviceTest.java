package com.ost.ostsdk.models.entities;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.ost.ostsdk.OstSdk;
import com.ost.ostsdk.models.Impls.OstModelFactory;
import com.ost.ostsdk.models.Impls.OstSecureKeyModelRepository;
import com.ost.ostsdk.models.OstUserModel;
import com.ost.ostsdk.utils.KeyGenProcess;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.web3j.crypto.RawTransaction;

import java.math.BigInteger;

@RunWith(AndroidJUnit4.class)
public class OstDeviceTest {

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
        OstModelFactory.getUserModel().deleteAllUsers();
        OstModelFactory.getDeviceModel().deleteAllMultiSigWallets();
        OstModelFactory.getDeviceManagerModel().deleteAllMultiSigs();
        OstModelFactory.getTokenHolderModel().deleteAllTokenHolders();
        OstModelFactory.getRuleModel().deleteAllRules();
    }

    @Test
    public void testMultiSigWalletSigning() throws Exception {

        // Create OstUser
        OstUser ostUser = insertUserData("1", "1", "1", "1");

        // Create OstSecureKey
        String walletAddress = new KeyGenProcess().execute("1");

        // Create Multi Sig
        OstDeviceManager ostDeviceManager = insertMultiSig(ostUser.getId(), "1", "1");

        OstTokenHolder ostTokenHolder = insertTokenHolder(ostUser.getId(), "1");

        // Create OstDevice
        OstDevice ostDevice = insertMultiSigWallet(ostDeviceManager.getId(), "1", walletAddress, "1");

        ostUser = updateUserData(ostUser, ostDeviceManager, ostTokenHolder);

        ostUser = OstSdk.getUser("1");
        ostDeviceManager = ostUser.getMultiSig();
        ostDevice = ostDeviceManager.getDeviceMultiSigWallet();

        OstDevice.Transaction transaction = new OstDevice.Transaction(new BigInteger(ostDeviceManager.getNonce()),
                new BigInteger("100000"), new BigInteger("100000"), "0xF281e85a0B992efA5fda4f52b35685dC5Ee67BEa", new BigInteger("0"), "0x");


        Assert.assertEquals(204,
                ostDevice.signTransaction((RawTransaction) transaction, ostUser.getId()).length());
    }

    private OstUser updateUserData(OstUser ostUser, OstDeviceManager ostDeviceManager, OstTokenHolder ostTokenHolder) throws InterruptedException {
//        ostUser.setMultiSigId(ostDeviceManager.getId());
//        ostUser.setTokenHolderId(ostTokenHolder.getId());

//        final CountDownLatch countDownLatch = new CountDownLatch(1);

        OstModelFactory.getUserModel().update(ostUser);

//        countDownLatch.await(5, TimeUnit.SECONDS);
        return ostUser;
    }

    private OstDevice insertMultiSigWallet(String parentId, String multiSigWalletId, String walletAddress, String multiSigId) throws JSONException, InterruptedException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(OstBaseEntity.PARENT_ID, parentId);
        jsonObject.put(OstBaseEntity.ID, multiSigWalletId);
        jsonObject.put(OstDevice.ADDRESS, walletAddress);
        jsonObject.put(OstDevice.MULTI_SIG_ID, multiSigId);
        jsonObject.put(OstDevice.STATUS, OstDevice.CREATED_STATUS);

//        final CountDownLatch countDownLatch = new CountDownLatch(1);

        OstDevice ostDevice = OstModelFactory.getDeviceModel().initMultiSigWallet(jsonObject);

//        countDownLatch.await(5, TimeUnit.SECONDS);

        return ostDevice;
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

//        final CountDownLatch countDownLatch = new CountDownLatch(1);

        OstDeviceManager ostDeviceManager = OstModelFactory.getDeviceManagerModel().initMultiSig(jsonObject);

//        countDownLatch.await(5, TimeUnit.SECONDS);

        return ostDeviceManager;
    }


    private OstUser insertUserData(String parentId, String userId, String tokenHolderId, String multiSigId) throws JSONException, InterruptedException {
        JSONObject userObj = new JSONObject();

        userObj.put(OstUser.PARENT_ID, parentId);
        userObj.put(OstUser.ID, userId);
        userObj.put(OstUser.TOKEN_ID, "1");
        userObj.put(OstUser.NAME, "ostUser");
        userObj.put(OstUser.TOKEN_HOLDER_ADDRESS, tokenHolderId);
        userObj.put(OstUser.DEVICE_MANAGER_ADDRESS, multiSigId);

//        final CountDownLatch countDownLatch = new CountDownLatch(1);

        OstUser ostUser = OstSdk.initUser(userObj);

//        countDownLatch.await(5, TimeUnit.SECONDS);

        return ostUser;
    }

    private OstTokenHolder insertTokenHolder(String parentId, String tokenHolderId) throws JSONException, InterruptedException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(OstBaseEntity.PARENT_ID, parentId);
        jsonObject.put(OstBaseEntity.ID, tokenHolderId);
        jsonObject.put(OstTokenHolder.ADDRESS, "0x2901239");
//        jsonObject.put(OstTokenHolder.REQUIREMENTS, 1);
        jsonObject.put(OstTokenHolder.USER_ID, parentId);
//        jsonObject.put(OstTokenHolder.EXECUTE_RULE_CALL_PREFIX, "callPrefix");

//        final CountDownLatch countDownLatch = new CountDownLatch(1);

        OstTokenHolder ostTokenHolder = OstModelFactory.getTokenHolderModel().insert(OstTokenHolder.parse(jsonObject));
//        countDownLatch.await(5, TimeUnit.SECONDS);

        return ostTokenHolder;
    }
}
