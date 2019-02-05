package com.ost.ostsdk.security;

import com.ost.ostsdk.OstSdk;
import com.ost.ostsdk.models.Impls.OstSecureKeyModelRepository;
import com.ost.ostsdk.models.OstTaskCallback;
import com.ost.ostsdk.models.entities.OstSecureKey;
import com.ost.ostsdk.security.impls.OstAndroidSecureStorage;
import com.ost.ostsdk.security.impls.OstSdkCrypto;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.MnemonicUtils;
import org.web3j.crypto.Sign;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class OstKeyManager {
    private static final String USER_DEVICE_INFO_FOR = "user_device_info_for_";
    private static final String ETHEREUM_KEY_FOR_ = "ethereum_key_for_";
    private static final String ETHEREUM_KEY_MNEMONICS_FOR_ = "ethereum_key_mnemonics_for_";
    private final OstSecureKeyModelRepository mOstSecureKeyModel;
    private final String mUserId;
    private KeyMetaStruct mKeyMetaStruct;

    public OstKeyManager(String userId) {
        this.mUserId = userId;
        mOstSecureKeyModel = new OstSecureKeyModelRepository();
        OstSecureKey ostSecureKey = mOstSecureKeyModel.getByKey(USER_DEVICE_INFO_FOR + userId);
        if (null == ostSecureKey) {
            String address = genAndStoreKey(mUserId);
            mKeyMetaStruct = new KeyMetaStruct(address);
            mKeyMetaStruct.addEthKeyIdentifier(ETHEREUM_KEY_FOR_ + address, mUserId);
            mOstSecureKeyModel.initSecureKey(USER_DEVICE_INFO_FOR + userId, createBytesFromObject(mKeyMetaStruct));
        } else {
            mKeyMetaStruct = createObjectFromBytes(ostSecureKey.getData());
        }
    }

    public String createKey() {
        String address = genAndStoreKey(mUserId);
        mKeyMetaStruct.addEthKeyIdentifier(ETHEREUM_KEY_FOR_ + address, mUserId);
        mOstSecureKeyModel.initSecureKey(USER_DEVICE_INFO_FOR + mUserId, createBytesFromObject(mKeyMetaStruct));
        return address;
    }

    public String getApiKeyAddress() {
        return mKeyMetaStruct.getApiAddress();
    }

    String createKeyWithMnemonic() {
        String mnemonics = OstSdkCrypto.getInstance().genMnemonics(mUserId);

        ECKeyPair ecKeyPair = OstSdkCrypto.getInstance().genECKeyFromMnemonics(mnemonics, mUserId);
        String address = Credentials.create(ecKeyPair).getAddress();

        storeKeyAndMnemonics(ecKeyPair ,address, mnemonics);
        return address;
    }


    public String createHDKey(byte[] seed) {
        String mnemonics = MnemonicUtils.generateMnemonic(seed);

        ECKeyPair ecKeyPair = OstSdkCrypto.getInstance().genECKeyFromMnemonics(mnemonics, mUserId);
        String address = Credentials.create(ecKeyPair).getAddress();

        storeKeyAndMnemonics(ecKeyPair, address, mnemonics);
        return address;
    }

    public static Sign.SignatureData sign(byte[] apiKey, byte[] data, String identifier) {
        byte[] decryptedKey = OstAndroidSecureStorage.getInstance(OstSdk.getContext(), identifier).decrypt(apiKey);
        ECKeyPair ecKeyPair = ECKeyPair.create(decryptedKey);

        return Sign.signMessage(data, ecKeyPair, false);
    }

    public Sign.SignatureData sign(String address, byte[] data) {
        OstSecureKey ostSecureKey = mOstSecureKeyModel.getByKey(ETHEREUM_KEY_FOR_ + address);
        if (null == ostSecureKey) {
            return null;
        }
        byte[] decryptedKey = OstAndroidSecureStorage.getInstance(OstSdk.getContext(), mUserId).decrypt(ostSecureKey.getData());
        ECKeyPair ecKeyPair = ECKeyPair.create(decryptedKey);
        return Sign.signMessage(data, ecKeyPair, false);
    }

    String[] getMnemonics(String address) {
        String identifier = mKeyMetaStruct.getEthKeyMnemonicsIdentifier(address);
        OstSecureKey ostSecureKey = mOstSecureKeyModel.getByKey(ETHEREUM_KEY_MNEMONICS_FOR_ + address);
        if (null == ostSecureKey) {
            return null;
        }
        byte[] decryptedMnemonics = OstAndroidSecureStorage.getInstance(OstSdk.getContext(), mUserId).decrypt(ostSecureKey.getData());
        String mnemonics = new String(decryptedMnemonics);
        return mnemonics.split(" ");
    }

    public OstApiSigner getApiSigner() {
        OstSecureKey osk = mOstSecureKeyModel.getByKey(ETHEREUM_KEY_FOR_ + getApiKeyAddress());
        byte[] key = OstAndroidSecureStorage.getInstance(OstSdk.getContext(), mUserId).decrypt(osk.getData());
        return new OstApiSigner(key);
    }

    byte[] createBytesFromObject(KeyMetaStruct object) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(object);
            out.flush();
            byte[] bytes = bos.toByteArray();
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return null;
    }

    KeyMetaStruct createObjectFromBytes(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            Object o = in.readObject();
            return (KeyMetaStruct) o;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return null;
    }

    boolean hasAddress(String address) {
        return mKeyMetaStruct.getEthKeyIdentifier(ETHEREUM_KEY_FOR_ + address) != null;
    }

    private String genAndStoreKey(String identifier) {
        ECKeyPair ecKeyPair = OstSdkCrypto.getInstance().genECKey(identifier);
        String address = Credentials.create(ecKeyPair).getAddress();
        byte[] privateKey = ecKeyPair.getPrivateKey().toByteArray();
        byte[] encryptedKey = OstAndroidSecureStorage.getInstance(OstSdk.getContext(), mUserId).encrypt(privateKey);

        CountDownLatch countDownLatch = new CountDownLatch(1);

        mOstSecureKeyModel.insertSecureKey(new OstSecureKey(ETHEREUM_KEY_FOR_ + address, encryptedKey), new OstTaskCallback() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                countDownLatch.countDown();
            }
        });

        try {
            countDownLatch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return address;
    }

    private void storeKeyAndMnemonics(ECKeyPair ecKeyPair, String address , String mnemonics) {

        byte[] privateKey = ecKeyPair.getPrivateKey().toByteArray();
        byte[] encryptedKey = OstAndroidSecureStorage.getInstance(OstSdk.getContext(), mUserId).encrypt(privateKey);
        byte[] encryptedMnemonics = OstAndroidSecureStorage.getInstance(OstSdk.getContext(), mUserId).encrypt(mnemonics.getBytes());

        CountDownLatch countDownLatch = new CountDownLatch(2);
        mOstSecureKeyModel.insertSecureKey(new OstSecureKey(ETHEREUM_KEY_MNEMONICS_FOR_ + address, encryptedMnemonics), new OstTaskCallback() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                countDownLatch.countDown();
            }
        });
        mOstSecureKeyModel.insertSecureKey(new OstSecureKey(ETHEREUM_KEY_FOR_ + address, encryptedKey), new OstTaskCallback() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                countDownLatch.countDown();
            }
        });

        mKeyMetaStruct.addEthKeyMnemonicsIdentifier(ETHEREUM_KEY_MNEMONICS_FOR_ + address, mUserId);
        mKeyMetaStruct.addEthKeyIdentifier(ETHEREUM_KEY_FOR_ + address, mUserId);

        mOstSecureKeyModel.initSecureKey(USER_DEVICE_INFO_FOR + mUserId, createBytesFromObject(mKeyMetaStruct));

        try {
            countDownLatch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class KeyMetaStruct implements Serializable {
        private final String apiAddress;
        private HashMap<String, String> ethKeyMetaMapping = new HashMap<>();
        private HashMap<String, String> ethKeyMnemonicsMetaMapping = new HashMap<>();

        KeyMetaStruct(String apiAddress) {
            this.apiAddress = apiAddress;
        }

        String getApiAddress() {
            return apiAddress;
        }

        String getEthKeyIdentifier(String address) {
            return ethKeyMetaMapping.get(address);
        }

        String getEthKeyMnemonicsIdentifier(String address) {
            return ethKeyMnemonicsMetaMapping.get(address);
        }

        void addEthKeyIdentifier(String address, String identifier) {
            ethKeyMetaMapping.put(address, identifier);
        }

        void addEthKeyMnemonicsIdentifier(String address, String identifier) {
            ethKeyMnemonicsMetaMapping.put(address, identifier);
        }
    }
}