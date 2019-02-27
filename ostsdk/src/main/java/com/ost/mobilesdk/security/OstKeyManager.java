package com.ost.mobilesdk.security;

import android.util.Log;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.Impls.OstSecureKeyModelRepository;
import com.ost.mobilesdk.models.Impls.OstSessionKeyModelRepository;
import com.ost.mobilesdk.models.entities.OstSecureKey;
import com.ost.mobilesdk.models.entities.OstSessionKey;
import com.ost.mobilesdk.security.impls.OstAndroidSecureStorage;
import com.ost.mobilesdk.security.impls.OstSdkCrypto;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.utils.SoliditySha3;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class OstKeyManager {
    private static final String TAG = "OstKeyManager";

    private static final String USER_DEVICE_INFO_FOR = "user_device_info_for_";
    private static final String ETHEREUM_KEY_FOR_ = "ethereum_key_for_";
    private static final String ETHEREUM_KEY_MNEMONICS_FOR_ = "ethereum_key_mnemonics_for_";
    private static final String PIN_HASH_FOR = "pin_hash_for_";
    private final OstSecureKeyModelRepository mOstSecureKeyModel;
    private final String mUserId;
    private KeyMetaStruct mKeyMetaStruct;

    public OstKeyManager(String userId) {
        this.mUserId = userId;
        mOstSecureKeyModel = new OstSecureKeyModelRepository();
        OstSecureKey ostSecureKey = mOstSecureKeyModel.getByKey(USER_DEVICE_INFO_FOR + userId);
        if (null == ostSecureKey) {

            Log.d(TAG, String.format("Creating new Ost Secure key for userId : %s", userId));

            String apiAddress = genAndStoreKey();

            String mnemonics = OstSdkCrypto.getInstance().genMnemonics();

            ECKeyPair ecKeyPair = OstSdkCrypto.getInstance().genECKeyFromMnemonics(mnemonics);
            String deviceAddress = Credentials.create(ecKeyPair).getAddress();

            mKeyMetaStruct = new KeyMetaStruct(apiAddress, deviceAddress);

            storeKeyAndMnemonics(ecKeyPair, apiAddress, deviceAddress, mnemonics);

            storeKeyMetaStruct();
        } else {

            Log.d(TAG, String.format("Got existing secure key meta struct for userId : %s", userId));

            mKeyMetaStruct = createObjectFromBytes(ostSecureKey.getData());
            Log.d(TAG, "List" + mKeyMetaStruct.ethKeyMetaMapping.keySet());
        }
    }

    public static String sign(String eip712Hash, ECKeyPair ecKeyPair) {
        Sign.SignatureData signatureData = Sign.signMessage(Numeric.hexStringToByteArray(eip712Hash), ecKeyPair, false);
        return Numeric.toHexString(signatureData.getR()) + Numeric.cleanHexPrefix(Numeric.toHexString(signatureData.getS())) + String.format("%02x", (signatureData.getV()));
    }

    public String getApiKeyAddress() {
        return mKeyMetaStruct.getApiAddress();
    }

    public String createHDKeyAddress(byte[] seed) {

        ECKeyPair ecKeyPair = OstSdkCrypto.getInstance().genHDKey(seed);
        String address = Credentials.create(ecKeyPair).getAddress();

        return address;
    }

    public static Sign.SignatureData sign(byte[] apiKey, byte[] data, String identifier) {
        byte[] decryptedKey = OstAndroidSecureStorage.getInstance(OstSdk.getContext(), identifier).decrypt(apiKey);
        ECKeyPair ecKeyPair = ECKeyPair.create(decryptedKey);

        return Sign.signMessage(data, ecKeyPair, false);
    }

    public String sign(String address, byte[] data) {
        OstSecureKey ostSecureKey = mOstSecureKeyModel.getByKey(ETHEREUM_KEY_FOR_ + address);
        if (null == ostSecureKey) {
            return null;
        }
        byte[] decryptedKey = OstAndroidSecureStorage.getInstance(OstSdk.getContext(), mUserId).decrypt(ostSecureKey.getData());
        ECKeyPair ecKeyPair = ECKeyPair.create(decryptedKey);
        Sign.SignatureData signatureData = Sign.signMessage(data, ecKeyPair, false);
        return Numeric.toHexString(signatureData.getR()) + Numeric.cleanHexPrefix(Numeric.toHexString(signatureData.getS())) + String.format("%02x", (signatureData.getV()));
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
            Log.e(TAG, "IOException " + e.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "Class not found exception " + e.getMessage());
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

    public String signUsingSessionKey(String sessionAddress, String hashToSign) {
        OstSessionKey ostSessionKey = new OstSessionKeyModelRepository().getByKey(sessionAddress);
        if (null == ostSessionKey) {
            return null;
        }
        byte[] sessionKey = OstAndroidSecureStorage.getInstance(OstSdk.getContext(), mUserId).decrypt(ostSessionKey.getData());
        Sign.SignatureData signatureData = Sign.signMessage(Numeric.hexStringToByteArray(hashToSign), ECKeyPair.create(sessionKey), false);
        return Numeric.toHexString(signatureData.getR()) + Numeric.cleanHexPrefix(Numeric.toHexString(signatureData.getS())) + String.format("%02x", (signatureData.getV()));
    }

    public String createSessionKey() {
        ECKeyPair ecKeyPair = OstSdkCrypto.getInstance().genECKey();
        String address = Credentials.create(ecKeyPair).getAddress();
        byte[] privateKey = ecKeyPair.getPrivateKey().toByteArray();
        byte[] encryptedKey = OstAndroidSecureStorage.getInstance(OstSdk.getContext(), mUserId).encrypt(privateKey);

        Future<AsyncStatus> future = new OstSessionKeyModelRepository().insertSessionKey(new OstSessionKey(address, encryptedKey));

        try {
            future.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            Log.e(TAG, String.format("%s while waiting for insertion in DB", e.getMessage()));
            return null;
        }

        return address;
    }

    public boolean hasAddress(String address) {
        return mKeyMetaStruct.getEthKeyIdentifier(ETHEREUM_KEY_FOR_ + address) != null;
    }

    private String genAndStoreKey() {
        ECKeyPair ecKeyPair = OstSdkCrypto.getInstance().genECKey();
        String address = Credentials.create(ecKeyPair).getAddress();
        byte[] privateKey = ecKeyPair.getPrivateKey().toByteArray();
        byte[] encryptedKey = OstAndroidSecureStorage.getInstance(OstSdk.getContext(), mUserId).encrypt(privateKey);

        Future<AsyncStatus> future = mOstSecureKeyModel.insertSecureKey(new OstSecureKey(ETHEREUM_KEY_FOR_ + address, encryptedKey));

        try {
            future.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            Log.e(TAG, String.format("%s while waiting for insertion in DB", e.getMessage()));
            return null;
        }

        return address;
    }

    private void storeKeyAndMnemonics(ECKeyPair ecKeyPair, String apiAddress, String address, String mnemonics) {

        byte[] privateKey = ecKeyPair.getPrivateKey().toByteArray();
        byte[] encryptedKey = OstAndroidSecureStorage.getInstance(OstSdk.getContext(), mUserId).encrypt(privateKey);
        byte[] encryptedMnemonics = OstAndroidSecureStorage.getInstance(OstSdk.getContext(), mUserId).encrypt(mnemonics.getBytes());

        Future<AsyncStatus> future1 = mOstSecureKeyModel.insertSecureKey(new OstSecureKey(ETHEREUM_KEY_MNEMONICS_FOR_ + address, encryptedMnemonics));
        Future<AsyncStatus> future2 = mOstSecureKeyModel.insertSecureKey(new OstSecureKey(ETHEREUM_KEY_FOR_ + address, encryptedKey));

        mKeyMetaStruct.addEthKeyMnemonicsIdentifier(ETHEREUM_KEY_MNEMONICS_FOR_ + address, mUserId);
        mKeyMetaStruct.addEthKeyIdentifier(ETHEREUM_KEY_FOR_ + address, mUserId);
        mKeyMetaStruct.addEthKeyIdentifier(ETHEREUM_KEY_FOR_ + apiAddress, mUserId);
    }

    private boolean storeKeyMetaStruct() {
        Future<AsyncStatus> future = mOstSecureKeyModel.insertSecureKey
                (new OstSecureKey(USER_DEVICE_INFO_FOR + mUserId, createBytesFromObject(mKeyMetaStruct)));
        try {
            future.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            Log.e(TAG, String.format("%s while waiting for insertion in DB", e.getMessage()));
            return false;
        }
        return true;
    }

    public String getDeviceAddress() {
        return mKeyMetaStruct.getDeviceAddress();
    }

    public String[] getMnemonics() {
        String deviceAddress = getDeviceAddress();
        return getMnemonics(deviceAddress);
    }

    public boolean storePinHash(String pin, String appSalt) {

        String pinString = String.format("%s%s%s", appSalt, pin, mUserId);

        String pinHash = null;
        try {
            pinHash = new SoliditySha3().soliditySha3(pinString);
        } catch (Exception e) {
            Log.e(TAG, "Exception while hashing recovery key string", e);
            return false;
        }

        byte[] encrypted = OstAndroidSecureStorage.getInstance(OstSdk.getContext(), mUserId).encrypt(pinHash.getBytes());

        Future<AsyncStatus> future = new OstSecureKeyModelRepository().insertSecureKey
                (new OstSecureKey(PIN_HASH_FOR + mUserId, encrypted));
        try {
            future.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            Log.e(TAG, String.format("%s while waiting for insertion in DB", e.getMessage()));
            return false;
        }
        return true;
    }

    public boolean validatePin(String pin, String appSalt) {
        String pinString = String.format("%s%s%s", appSalt, pin, mUserId);

        String testPinHash = null;
        try {
            testPinHash = new SoliditySha3().soliditySha3(pinString);
        } catch (Exception e) {
            Log.e(TAG, "Exception while hashing recovery key string", e);
            return false;
        }

        //get Hash from db
        OstSecureKey ostSecureKey = new OstSecureKeyModelRepository().getByKey(PIN_HASH_FOR + mUserId);
        byte[] encryptedData = ostSecureKey.getData();
        byte[] decryptedData = OstAndroidSecureStorage.getInstance(OstSdk.getContext(), mUserId).decrypt(encryptedData);
        String expectedPinHash = new String(decryptedData);
        //Compare
        boolean isValid = testPinHash.equalsIgnoreCase(expectedPinHash);

        return isValid;
    }

    public String signUsingSeed(byte[] seed, String eip712Hash) {
        ECKeyPair ecKeyPair = OstSdkCrypto.getInstance().genHDKey(seed);
        return sign(eip712Hash, ecKeyPair);
    }

    static class KeyMetaStruct implements Serializable {
        private static final long serialVersionUID = 129348938L;
        private final String apiAddress;
        private final String deviceAddress;
        private HashMap<String, String> ethKeyMetaMapping = new HashMap<>();
        private HashMap<String, String> ethKeyMnemonicsMetaMapping = new HashMap<>();

        KeyMetaStruct(String apiAddress, String deviceAddress) {
            this.apiAddress = apiAddress;
            this.deviceAddress = deviceAddress;
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

        public String getDeviceAddress() {
            return deviceAddress;
        }
    }
}