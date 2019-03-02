package com.ost.mobilesdk.security;

import android.util.Log;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.Impls.OstSecureKeyModelRepository;
import com.ost.mobilesdk.models.Impls.OstSessionKeyModelRepository;
import com.ost.mobilesdk.models.entities.OstSecureKey;
import com.ost.mobilesdk.security.impls.OstAndroidSecureStorage;
import com.ost.mobilesdk.security.impls.OstSdkCrypto;
import com.ost.mobilesdk.security.structs.OstSignWithMnemonicsStruct;
import com.ost.mobilesdk.utils.AsyncStatus;

import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.web3j.compat.Compat.UTF_8;

public class InternalKeyManager2 {
    private static OstSecureKeyModelRepository modelRepo = null;
    private static OstSecureKeyModelRepository getByteStorageRepo() {
        if ( null == modelRepo ) {
            modelRepo = new OstSecureKeyModelRepository();
        }
        return modelRepo;
    }

    private static OstSessionKeyModelRepository sessionModelRepository = null;
    private static OstSessionKeyModelRepository getSessionRepo() {
        if ( null == sessionModelRepository ) {
            sessionModelRepository = new OstSessionKeyModelRepository();
        }
        return sessionModelRepository;
    }

    private static final String TAG = "IKM";
    private static final String USER_PRESENCE_INFO_HASH_FOR_ = "pin_hash_for_";
    private static final String USER_DEVICE_INFO_FOR = "user_device_info_for_";
    private static final String ETHEREUM_KEY_FOR_ = "ethereum_key_for_";
    private static final String ETHEREUM_KEY_MNEMONICS_FOR_ = "ethereum_key_mnemonics_for_";


    private KeyMetaStruct mKeyMetaStruct;
    private String mUserId;
    InternalKeyManager2(String userId) {
        mUserId = userId;
        mKeyMetaStruct = getKeyMataStruct(userId);
        if (null == mKeyMetaStruct) {

            Log.d(TAG, String.format("Creating new Ost Secure key for userId : %s", userId));

            mKeyMetaStruct = new KeyMetaStruct();
            //Generate Api Key
            createApiKey();

            //Generate Device Key
            createDeviceKey();

            //Store the meta into Db.
            storeKeyMetaStruct();
        }
    }

    // region - Api Key Methods
    private String createApiKey() {
        // Create a private key.
        ECKeyPair ecKeyPair = OstSdkCrypto.getInstance().genECKey();
        String apiKeyAddress = Credentials.create(ecKeyPair).getAddress();

        //CheckSum as generated address is not CheckSum address
        apiKeyAddress = Keys.toChecksumAddress(apiKeyAddress);


        byte[] privateKey = null;
        byte[] encryptedKey;
        try {
            privateKey = ecKeyPair.getPrivateKey().toByteArray();
            encryptedKey = OstAndroidSecureStorage.getInstance(OstSdk.getContext(), mUserId).encrypt(privateKey);


            //Store the encrypted key.
            String apiKeyId = createEthKeyMetaId(apiKeyAddress);
            OstSecureKeyModelRepository metaRepository = getByteStorageRepo();
            Future<AsyncStatus> future = metaRepository.insertSecureKey(new OstSecureKey(apiKeyId, encryptedKey));

            try {
                future.get(10, TimeUnit.SECONDS);
            } catch (Exception e) {
                Log.e(TAG, String.format("%s while waiting for insertion in DB", e.getMessage()));
                return null;
            }

        } catch (Exception ex) {
            Log.e(TAG, "m_s_ikm_cak: Unexpected Exception", ex);
            return null;
        } finally {
            ecKeyPair = null;
            clearBytes(privateKey);
        }

        //Update key meta.
        setEthKeyMeta(apiKeyAddress);
        mKeyMetaStruct.apiAddress = apiKeyAddress;

        return apiKeyAddress;
    }

    private void createDeviceKey() {
        //Create mnemonics and encrypt it.
        String mnemonics = OstSdkCrypto.getInstance().genMnemonics();
        byte[] encryptedMnemonics = OstAndroidSecureStorage.getInstance(OstSdk.getContext(), mUserId).encrypt(mnemonics.getBytes());


        //Create ECKeyPair and encrypt it.
        ECKeyPair ecKeyPair = OstSdkCrypto.getInstance().genECKeyFromMnemonics(mnemonics);
        String deviceAddress = Credentials.create(ecKeyPair).getAddress();

        //CheckSum as generated address is not CheckSum address
        deviceAddress = Keys.toChecksumAddress(deviceAddress);

        byte[] privateKey = null;
        byte[] encryptedKey;
        try {
            privateKey = ecKeyPair.getPrivateKey().toByteArray();
            encryptedKey = OstAndroidSecureStorage.getInstance(OstSdk.getContext(), mUserId).encrypt(privateKey);

            OstSecureKeyModelRepository metaRepository = getByteStorageRepo();

            //Store encrypted mnemonics
            String mnemonicsMetaId = createMnemonicsMetaId(deviceAddress);
            OstSecureKey mnemonicsStorageBytes = new OstSecureKey(mnemonicsMetaId, encryptedMnemonics);
            Future<AsyncStatus> future1 = metaRepository.insertSecureKey(mnemonicsStorageBytes);

            //Store encrypted Device key.
            String deviceAddressMetaId = createEthKeyMetaId(deviceAddress);
            Future<AsyncStatus> future2 = metaRepository.insertSecureKey(new OstSecureKey(deviceAddressMetaId, encryptedKey));
        } catch (Exception ex) {
            Log.e(TAG, "m_s_ikm_cdk: Unexpected Exception", ex);
            return;
        } finally {
            ecKeyPair = null;
            mnemonics = null;
            clearBytes(privateKey);
        }

        // Update meta.
        setMnemonicsMeta(deviceAddress);
        setEthKeyMeta(deviceAddress);
        mKeyMetaStruct.deviceAddress = deviceAddress;
    }

    String signBytesWithApiSigner(byte[] dataToSign) {
        //Get Api Key address and id.
        String apiKeyAddress = mKeyMetaStruct.getApiAddress();
        String apiKeyId = createEthKeyMetaId(apiKeyAddress);

        //Fetch and decrypt Api Key
        OstSecureKeyModelRepository metaRepository = getByteStorageRepo();
        OstSecureKey osk = metaRepository.getByKey(apiKeyId);

        byte[] key = null;
        ECKeyPair ecKeyPair;
        try {
            key = OstAndroidSecureStorage.getInstance(OstSdk.getContext(), mUserId).decrypt(osk.getData());
            ecKeyPair = ECKeyPair.create(key);
        } catch (Exception ex) {
            Log.e(TAG, "m_s_ikm_sbwps: Unexpected Exception", ex);
            return null;
        } finally {
            clearBytes(key);
        }

        //Sign the data
        Sign.SignatureData signatureData = Sign.signPrefixedMessage(dataToSign, ecKeyPair);
        ecKeyPair = null;
        return signatureDataToString(signatureData);
    }




    // endregion


    //region - KeyMetaStruct Methods

    static KeyMetaStruct srtuct = null;
    static KeyMetaStruct getKeyMataStruct(String userId) {

        if ( null != srtuct ) {
            return srtuct;
        }
        OstSecureKeyModelRepository metaRepository = getByteStorageRepo();
        String userMetaId = createUserMataId(userId);
        OstSecureKey ostSecureKey = metaRepository.getByKey(userMetaId);
        if (null == ostSecureKey) {
            return null;
        }
        srtuct = createObjectFromBytes(ostSecureKey.getData());
        return srtuct;
    }


    static KeyMetaStruct createObjectFromBytes(byte[] bytes) {

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

    static byte[] createBytesFromObject(KeyMetaStruct object) {

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

    private boolean storeKeyMetaStruct() {
        OstSecureKeyModelRepository metaRepository = getByteStorageRepo();
        String userMetaId = createUserMataId(mUserId);
        Future<AsyncStatus> future = metaRepository.insertSecureKey
                (new OstSecureKey(userMetaId, createBytesFromObject(mKeyMetaStruct)));
        try {
            future.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            Log.e(TAG, String.format("%s while waiting for insertion in DB", e.getMessage()));
            return false;
        }
        return true;
    }

    private static String createUserMataId(String userId) {
        return USER_DEVICE_INFO_FOR + userId;
    }

    private String createEthKeyMetaId(String address) {
        return ETHEREUM_KEY_FOR_ + address;
    }

    private String createMnemonicsMetaId(String address) {
        return ETHEREUM_KEY_MNEMONICS_FOR_ + address;
    }

    private void setEthKeyMeta(String address) {
        String addressIdentifier = createEthKeyMetaId(address);
        String keyStoreIdentifier = mUserId;
        mKeyMetaStruct.ethKeyMetaMapping.put(addressIdentifier, keyStoreIdentifier);
    }

    private void setMnemonicsMeta(String address) {
        String addressIdentifier = createMnemonicsMetaId(address);
        String keyStoreIdentifier = mUserId;
        mKeyMetaStruct.ethKeyMnemonicsMetaMapping.put(addressIdentifier, keyStoreIdentifier);
    }
    //endregion


    // region - Passphrase Locker Class
    static class PassphraseValidationLocker {
        private static int MaxRetryCount = 3;
        private static long LockDuration = (10 * 60 * 60 * 1000);
        private static long UnlockedDuration = 0;

        private long lastInvalidAttemptTimestamp;
        private long lastValidAttemptTimestamp;
        private int retryCount;

        /**
         * Use this method to check if validation is allowed.
         *
         * @return
         */
        private boolean isValidationAllowed() {
            if (retryCount < MaxRetryCount) {
                return true;
            }
            long now = System.currentTimeMillis();
            long elapsedLockDuration = now - lastInvalidAttemptTimestamp;
            return elapsedLockDuration > LockDuration;
        }

        /**
         * Call this method to acknowledge validation.
         */
        private void validated() {
            //To-Do: Update them in DB
            lastValidAttemptTimestamp = System.currentTimeMillis();
            lastInvalidAttemptTimestamp = 0;
            retryCount = 0;
        }

        /**
         * Call this method to acknowledge invalidation.
         */
        private void invalidated() {
            //To-Do: Update them in DB
            boolean wasValidationAllowed = isValidationAllowed();

            lastValidAttemptTimestamp = 0;
            lastInvalidAttemptTimestamp = System.currentTimeMillis();
            retryCount += 1;
            if (retryCount > 3 && wasValidationAllowed) {
                retryCount = 1;
            }
        }

        /**
         * Use this method to check if validation is needed.
         *
         * @return
         */
        private boolean isValidationNeeded() {
            //If passphrase validation is not allowed, return true.
            if (!isValidationAllowed()) {
                return true;
            }

            long now = System.currentTimeMillis();
            long elapsedLockDuration = now - lastValidAttemptTimestamp;
            return elapsedLockDuration > UnlockedDuration;
        }
    }
    //endregion


    // region - Passphrase Locker Utility Methods

    // A static hash-map that stores instances of PassphraseValidationLocker
    private static HashMap<String,InternalKeyManager2.PassphraseValidationLocker> lockers = new HashMap<>();
    /**
     * A Factory method to get In-Memory PassphraseValidationLocker for a given user-id
     * @param userId Id of the user.
     * @return A simple time-based, In-Memory lock mechanism.
     */
    private InternalKeyManager2.PassphraseValidationLocker getLockerFor(String userId) {
        InternalKeyManager2.PassphraseValidationLocker locker = lockers.get(userId);
        if ( null == locker ) {
            locker = new InternalKeyManager2.PassphraseValidationLocker();
            lockers.put(userId, locker);
        }
        return locker;
    }

    boolean isUserPassphraseValidationAllowed() {
        InternalKeyManager2.PassphraseValidationLocker locker = getLockerFor(mUserId);
        return locker.isValidationAllowed();
    }
    private void userPassphraseValidated() {
        InternalKeyManager2.PassphraseValidationLocker locker = getLockerFor(mUserId);
        locker.validated();
    }
    private void userPassphraseInvalidated() {
        InternalKeyManager2.PassphraseValidationLocker locker = getLockerFor(mUserId);
        locker.invalidated();
    }
    boolean isUserPassphraseValidationNeeded() {
        InternalKeyManager2.PassphraseValidationLocker locker = getLockerFor(mUserId);
        return locker.isValidationNeeded();
    }
    // endregion


    // region - External Mnemonics Signing Method
    public static final int HARDENED_BIT= 0x80000000;
    public static final int[] HD_DERIVATION_PATH_FIRST_CHILD = (new int[]{44 | HARDENED_BIT,60 | HARDENED_BIT,0|HARDENED_BIT,0,0});

    void sign(OstSignWithMnemonicsStruct ostSignWithMnemonicsStruct) {
        String messageHash = ostSignWithMnemonicsStruct.getMessageHash();
        byte[] mnemonics = ostSignWithMnemonicsStruct.getMnemonics();
        if ( null == messageHash || null == mnemonics) {
            return;
        }

        byte[] dataToSign =  Numeric.hexStringToByteArray(messageHash);
        byte[] seed = null;
        String signature = null;
        String signerAddress = null;
        Bip32ECKeyPair hdMasterKey = null;
        Bip32ECKeyPair deriveKeyPair = null;
        try {
            //Create Seed
            seed = generateSeedFromMnemonicBytes(mnemonics,null);

            //Create hdMasterKey
            hdMasterKey = Bip32ECKeyPair.generateKeyPair(seed);
            deriveKeyPair = Bip32ECKeyPair.deriveKeyPair(hdMasterKey,HD_DERIVATION_PATH_FIRST_CHILD );
            signerAddress = Credentials.create(deriveKeyPair).getAddress();

            Sign.SignatureData signatureData = Sign.signMessage(dataToSign, deriveKeyPair, false);
            signature = signatureDataToString( signatureData );

        } catch (Exception ex) {
            //Mute the exceptions
            ex.printStackTrace();
            return;
        } finally {
            //Clean out mnemonics
            clearBytes(mnemonics);

            //Clean the seed.
            clearBytes(seed);

            hdMasterKey = null;
            deriveKeyPair = null;
        }

        ostSignWithMnemonicsStruct.setSignature(signature);
        ostSignWithMnemonicsStruct.setSigner(signerAddress);
    }

    private static final int SEED_ITERATIONS = 2048;
    private static final int SEED_KEY_SIZE = 512;

    /**
     * To create a binary seed from the mnemonic, we use the PBKDF2 function with a
     * mnemonic sentence (in UTF-8 NFKD) used as the password and the string "mnemonic"
     * + passphrase (again in UTF-8 NFKD) used as the salt. The iteration count is set
     * to 2048 and HMAC-SHA512 is used as the pseudo-random function. The length of the
     * derived key is 512 bits (= 64 bytes).
     *
     * @param mnemonicBytes Byte array (Charset UTF_8 encoded) of the input mnemonic which should be 128-160 bits in length containing
     *                 only valid words.
     * @param passphrase The passphrase which will be used as part of salt for PBKDF2
     *                   function
     * @return Byte array representation of the generated seed
     */
    public static byte[] generateSeedFromMnemonicBytes(byte[] mnemonicBytes, String passphrase) {
        passphrase = passphrase == null ? "" : passphrase;

        String salt = String.format("mnemonic%s", passphrase);
        PKCS5S2ParametersGenerator gen = new PKCS5S2ParametersGenerator(new SHA512Digest());
        gen.init(mnemonicBytes, salt.getBytes(UTF_8), SEED_ITERATIONS);

        return ((KeyParameter) gen.generateDerivedParameters(SEED_KEY_SIZE)).getKey();
    }
    //endregion



    // region - internal utilities
    static String signatureDataToString(Sign.SignatureData signatureData) {
        return Numeric.toHexString(signatureData.getR()) + Numeric.cleanHexPrefix(Numeric.toHexString(signatureData.getS())) + String.format("%02x",(signatureData.getV()));
    }

    private static final byte[] nonSecret = ("BYTES_CLEARED_" + String.valueOf((int) (System.currentTimeMillis()))  ).getBytes();
    private static void clearBytes(byte[] secret) {
        if ( null == secret ) { return; }
        for (int i = 0; i < secret.length; i++) {
            secret[i] = nonSecret[i % nonSecret.length];
        }
    }
    //endregion

}
