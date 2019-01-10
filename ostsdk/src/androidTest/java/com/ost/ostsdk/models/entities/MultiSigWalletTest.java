package com.ost.ostsdk.models.entities;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.ost.ostsdk.OstSdk;
import com.ost.ostsdk.models.Impls.ModelFactory;
import com.ost.ostsdk.models.TaskCallback;
import com.ost.ostsdk.models.UserModel;

import org.json.JSONArray;
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
public class MultiSigWalletTest {

    private static Context mAppContext;

    static UserModel mUserRepository;

    @BeforeClass
    public static void setUp() {
        mAppContext = InstrumentationRegistry.getTargetContext();
        OstSdk.init(mAppContext);
    }

    @Test
    public void testMultiSigWalletSigning() throws Exception {
        insertUserData(1);
        //new InitialDeviceProvisioningFlow().init("1");
        insertTokenHolder(1);
        insertMultiSig(1);
        insertMultiSigWallet(1);
        // Create User
        // Create Multi Sig
        // Create SecureKey
        // Create MultiSigWallet
        User user = OstSdk.getUser("1");
        MultiSig multiSig = user.getMultiSig();
        MultiSigWallet multiSigWallet = multiSig.getDeviceMultiSigWallet();
        MultiSigWallet.Transaction transaction = (MultiSigWallet.Transaction) MultiSigWallet.Transaction.createTransaction(new BigInteger(multiSigWallet.getNonce()),
                new BigInteger("100000"), new BigInteger("100000"), "0xF281e85a0B992efA5fda4f52b35685dC5Ee67BEa", "0x");
        Assert.assertEquals("", multiSigWallet.signTransaction(transaction));
    }

    private MultiSigWallet insertMultiSigWallet(int param) throws JSONException, InterruptedException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(BaseEntity.ID, String.valueOf(param));
        jsonObject.put(MultiSigWallet.ADDRESS, "0x2901239");
        jsonObject.put(MultiSigWallet.MULTI_SIG_ID, "123");
        jsonObject.put(MultiSigWallet.STATUS, "status");
        jsonObject.put(MultiSigWallet.NONCE, "1");

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        MultiSigWallet multiSigWallet = ModelFactory.getMultiSigWallet().initMultiSigWallet(jsonObject, new TaskCallback() {
            @Override
            public void onSuccess() {
                countDownLatch.countDown();
            }
        });

        countDownLatch.await(5, TimeUnit.SECONDS);

        return multiSigWallet;
    }

    private MultiSig insertMultiSig(int param) throws JSONException, InterruptedException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(BaseEntity.ID, String.valueOf(param));
        jsonObject.put(MultiSig.ADDRESS, "0x2901239");
        jsonObject.put(MultiSig.TOKEN_HOLDER_ID, "123");
        jsonObject.put(MultiSig.REQUIREMENT, 1);
        jsonObject.put(MultiSig.AUTHORIZE_SESSION_CALL_PREFIX, "callPrefix");
        JSONArray jsonArray = new JSONArray();
        jsonArray.put("0x2389234234");
        jsonObject.put(MultiSig.WALLETS, jsonArray);
        jsonObject.put(MultiSig.USER_ID, "123");

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        MultiSig multiSig = ModelFactory.getMultiSig().initMultiSig(jsonObject, new TaskCallback() {
            @Override
            public void onSuccess() {
                countDownLatch.countDown();
            }
        });

        countDownLatch.await(5, TimeUnit.SECONDS);

        return multiSig;
    }


    private User insertUserData(int param) throws JSONException, InterruptedException {
        JSONObject userObj = new JSONObject();

        userObj.put(User.ID, String.valueOf(param));
        userObj.put(User.ECONOMY_ID, "1");
        userObj.put(User.NAME, "user");
        userObj.put(User.TOKEN_HOLDER_ID, "1");

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

    private TokenHolder insertTokenHolder(int param) throws JSONException, InterruptedException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(BaseEntity.ID, "ID");
        jsonObject.put(TokenHolder.ADDRESS, "0x2901239");
        jsonObject.put(TokenHolder.REQUIREMENTS, 1);
        jsonObject.put(TokenHolder.USER_ID, "123");
        JSONArray jsonArray = new JSONArray();
        jsonArray.put("0x12345678");
        jsonObject.put(TokenHolder.SESSIONS, jsonArray);
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
