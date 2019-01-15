package com.ost.ostsdk.models.entities;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.ost.ostsdk.OstSdk;
import com.ost.ostsdk.models.Impls.ModelFactory;
import com.ost.ostsdk.models.Impls.SecureKeyModelRepository;
import com.ost.ostsdk.models.TaskCallback;
import com.ost.ostsdk.models.UserModel;
import com.ost.ostsdk.utils.KeyGenProcess;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.web3j.crypto.RawTransaction;

import java.math.BigInteger;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class MultiSigWalletTest {

    private static Context mAppContext;

    static UserModel mUserRepository;

    @BeforeClass
    public static void setUp() {
        mAppContext = InstrumentationRegistry.getTargetContext();
        OstSdk.init(mAppContext);
        cleanDB();
    }

    private static void cleanDB() {
        new SecureKeyModelRepository().deleteAll(null);
        ModelFactory.getUserModel().deleteAllUsers(null);
        ModelFactory.getMultiSigWalletModel().deleteAllMultiSigWallets(null);
        ModelFactory.getMultiSigModel().deleteAllMultiSigs(null);
        ModelFactory.getTokenHolderModel().deleteAllTokenHolders(null);
        ModelFactory.getRuleModel().deleteAllRules(null);
    }

    @Test
    public void testMultiSigWalletSigning() throws Exception {

        // Create User
        User user = insertUserData("1", "1", "1", "1");

        // Create SecureKey
        String walletAddress = new KeyGenProcess().execute("1");

        // Create Multi Sig
        MultiSig multiSig = insertMultiSig(user.getId(), "1", "1");

        TokenHolder tokenHolder = insertTokenHolder(user.getId(), "1");

        // Create MultiSigWallet
        MultiSigWallet multiSigWallet = insertMultiSigWallet(multiSig.getId(), "1", walletAddress, "1");

        user = updateUserData(user, multiSig, tokenHolder);

        user = OstSdk.getUser("1");
        multiSig = user.getMultiSig();
        multiSigWallet = multiSig.getDeviceMultiSigWallet();

        MultiSigWallet.Transaction transaction = new MultiSigWallet.Transaction(new BigInteger(multiSig.getNonce()),
                new BigInteger("100000"), new BigInteger("100000"), "0xF281e85a0B992efA5fda4f52b35685dC5Ee67BEa", new BigInteger("0"), "0x");


        Assert.assertEquals(204,
                multiSigWallet.signTransaction((RawTransaction) transaction, user.getId()).length());
    }

    private User updateUserData(User user, MultiSig multiSig, TokenHolder tokenHolder) throws InterruptedException {
        user.setMultiSigId(multiSig.getId());
        user.setTokenHolderId(tokenHolder.getId());

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        ModelFactory.getUserModel().update(user, new TaskCallback() {
            @Override
            public void onSuccess() {
                countDownLatch.countDown();
            }
        });

        countDownLatch.await(5, TimeUnit.SECONDS);
        return user;
    }

    private MultiSigWallet insertMultiSigWallet(String parentId, String multiSigWalletId, String walletAddress, String multiSigId) throws JSONException, InterruptedException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(BaseEntity.PARENT_ID, parentId);
        jsonObject.put(BaseEntity.ID, multiSigWalletId);
        jsonObject.put(MultiSigWallet.ADDRESS, walletAddress);
        jsonObject.put(MultiSigWallet.MULTI_SIG_ID, multiSigId);
        jsonObject.put(MultiSigWallet.STATUS, MultiSigWallet.CREATED_STATUS);

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        MultiSigWallet multiSigWallet = ModelFactory.getMultiSigWalletModel().initMultiSigWallet(jsonObject, new TaskCallback() {
            @Override
            public void onSuccess() {
                countDownLatch.countDown();
            }
        });

        countDownLatch.await(5, TimeUnit.SECONDS);

        return multiSigWallet;
    }

    private MultiSig insertMultiSig(String parentId, String multiSigId, String tokenHolderId) throws JSONException, InterruptedException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(BaseEntity.PARENT_ID, parentId);
        jsonObject.put(BaseEntity.ID, multiSigId);
        jsonObject.put(MultiSig.ADDRESS, "0x2901239");
        jsonObject.put(MultiSig.TOKEN_HOLDER_ID, tokenHolderId);
        jsonObject.put(MultiSig.REQUIREMENT, 1);
        jsonObject.put(MultiSig.AUTHORIZE_SESSION_CALL_PREFIX, "callPrefix");
        jsonObject.put(MultiSig.NONCE, "1");
        jsonObject.put(MultiSig.USER_ID, "123");

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        MultiSig multiSig = ModelFactory.getMultiSigModel().initMultiSig(jsonObject, new TaskCallback() {
            @Override
            public void onSuccess() {
                countDownLatch.countDown();
            }
        });

        countDownLatch.await(5, TimeUnit.SECONDS);

        return multiSig;
    }


    private User insertUserData(String parentId, String userId, String tokenHolderId, String multiSigId) throws JSONException, InterruptedException {
        JSONObject userObj = new JSONObject();

        userObj.put(User.PARENT_ID, parentId);
        userObj.put(User.ID, userId);
        userObj.put(User.TOKEN_ID, "1");
        userObj.put(User.NAME, "user");
        userObj.put(User.TOKEN_HOLDER_ID, tokenHolderId);
        userObj.put(User.MULTI_SIG_ID, multiSigId);

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        User user = OstSdk.initUser(userObj, new TaskCallback() {
            @Override
            public void onSuccess() {
                countDownLatch.countDown();
            }
        });

        countDownLatch.await(5, TimeUnit.SECONDS);

        return user;
    }

    private TokenHolder insertTokenHolder(String parentId, String tokenHolderId) throws JSONException, InterruptedException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(BaseEntity.PARENT_ID, parentId);
        jsonObject.put(BaseEntity.ID, tokenHolderId);
        jsonObject.put(TokenHolder.ADDRESS, "0x2901239");
        jsonObject.put(TokenHolder.REQUIREMENTS, 1);
        jsonObject.put(TokenHolder.USER_ID, parentId);
        jsonObject.put(TokenHolder.EXECUTE_RULE_CALL_PREFIX, "callPrefix");

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        TokenHolder tokenHolder = ModelFactory.getTokenHolderModel().initTokenHolder(jsonObject, new TaskCallback() {
            @Override
            public void onSuccess() {
                countDownLatch.countDown();
            }
        });

        countDownLatch.await(5, TimeUnit.SECONDS);

        return tokenHolder;
    }
}
