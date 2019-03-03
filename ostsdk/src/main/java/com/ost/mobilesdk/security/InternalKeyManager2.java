package com.ost.mobilesdk.security;

import android.util.Log;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.Impls.OstSecureKeyModelRepository;
import com.ost.mobilesdk.models.Impls.OstSessionKeyModelRepository;
import com.ost.mobilesdk.models.entities.OstSecureKey;
import com.ost.mobilesdk.models.entities.OstSessionKey;
import com.ost.mobilesdk.security.impls.OstAndroidSecureStorage;
import com.ost.mobilesdk.security.structs.OstSignWithMnemonicsStruct;
import com.ost.mobilesdk.utils.AsyncStatus;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.errors.OstErrors.ErrorCode;

import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.generators.SCrypt;
import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.MnemonicUtils;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.web3j.compat.Compat.UTF_8;

class InternalKeyManager2 {
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

    private static final String TAG = "OST_IKM";
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

    /**
     * Method to create and store new Api-Signer Key.
     * The private key is encrypted and stored in DB.
     */
    private void createApiKey() {
        ECKeyPair ecKeyPair;
        byte[] privateKey = null;
        byte[] encryptedKey = null;
        try {
            // Create a private key.
            ecKeyPair = generateECKeyPair();
            privateKey = ecKeyPair.getPrivateKey().toByteArray();
            encryptedKey = OstAndroidSecureStorage.getInstance(OstSdk.getContext(), mUserId).encrypt(privateKey);
            String apiKeyAddress = getKeyAddress(ecKeyPair);

            //Store the encrypted key.
            String apiKeyId = createEthKeyMetaId(apiKeyAddress);
            OstSecureKeyModelRepository metaRepository = getByteStorageRepo();
            Future<AsyncStatus> future = metaRepository.insertSecureKey(new OstSecureKey(apiKeyId, encryptedKey));

            //Wait for key to be stored.
            future.get(10, TimeUnit.SECONDS);

            //Update key meta.
            setEthKeyMeta(apiKeyAddress);
            mKeyMetaStruct.apiAddress = apiKeyAddress;
        } catch (Exception ex) {
            throw new OstError("km_ikm_cak_1", ErrorCode.FAILED_TO_GENERATE_ETH_KEY);
        } finally {
            ecKeyPair = null;
            clearBytes(privateKey);
            clearBytes(encryptedKey);
        }
    }

    /**
     * Generates signature for HTTP Api calls (ETH Personal Sign).
     * @param dataToSign - byte[] to sign.
     * @return Signature
     */
    String signBytesWithApiSigner(byte[] dataToSign) {
        //Get Api Key address and id.
        String apiKeyAddress = mKeyMetaStruct.getApiAddress();
        String apiKeyId = createEthKeyMetaId(apiKeyAddress);

        //Fetch and decrypt Api Key
        OstSecureKeyModelRepository metaRepository = getByteStorageRepo();

        OstSecureKey osk = null;
        byte[] key = null;
        ECKeyPair ecKeyPair;
        try {
            osk = metaRepository.getByKey(apiKeyId);
            key = OstAndroidSecureStorage.getInstance(OstSdk.getContext(), mUserId).decrypt(osk.getData());
            ecKeyPair = ECKeyPair.create(key);
            //Sign the data
            Sign.SignatureData signatureData = Sign.signPrefixedMessage(dataToSign, ecKeyPair);
            return signatureDataToString(signatureData);
        } catch (Exception ex) {
            Log.e(TAG, "m_s_ikm_sbwps: Unexpected Exception", ex);
            return null;
        } finally {
            clearBytes(key);
            ecKeyPair = null;
            osk = null;
        }
    }
    // endregion

    // region - Device Key Management
    byte[] getMnemonics(String address) {
        //Get the keyId metaId.
        String metaId = createMnemonicsMetaId(address);
        String mnemonicsIdentifier = mKeyMetaStruct.getEthKeyMnemonicsIdentifier(metaId);

        //Get the encrypted mnemonics
        OstSecureKeyModelRepository metaRepository = getByteStorageRepo();
        OstSecureKey ostSecureKey = metaRepository.getByKey(metaId);
        if (null == ostSecureKey) {
            return null;
        }

        //Decrypt it.
        byte[] decryptedMnemonics = OstAndroidSecureStorage.getInstance(OstSdk.getContext(), mnemonicsIdentifier).decrypt(ostSecureKey.getData());
        return decryptedMnemonics;
    }


    /**
     * Method to create and store new Device Key (Multi-Sig owner key).
     * The private key is encrypted and stored in DB.
     */
    private void createDeviceKey() {
        byte[] mnemonics = null;
        byte[] privateKey = null;
        ECKeyPair ecKeyPair;

        byte[] encryptedMnemonics;
        byte[] encryptedKey;
        try {
            //Create mnemonics and encrypt it.
            mnemonics = generateMnemonics();
            encryptedMnemonics = OstAndroidSecureStorage.getInstance(OstSdk.getContext(), mUserId).encrypt(mnemonics);


            //Create ECKeyPair and encrypt it.
            ecKeyPair = generateECKeyPairWithMnemonics(mnemonics);
            String deviceAddress = getKeyAddress(ecKeyPair);

            // Clear the mnemonics - set to null to avoid double clearing.
            clearBytes(mnemonics);
            mnemonics = null;

            // Get private key and encrypt it.
            privateKey = ecKeyPair.getPrivateKey().toByteArray();
            encryptedKey = OstAndroidSecureStorage.getInstance(OstSdk.getContext(), mUserId).encrypt(privateKey);

            // Clear the privateKey - set to null to avoid double clearing.
            clearBytes(privateKey);
            privateKey = null;

            OstSecureKeyModelRepository metaRepository = getByteStorageRepo();

            //Store encrypted mnemonics
            String mnemonicsMetaId = createMnemonicsMetaId(deviceAddress);
            OstSecureKey mnemonicsStorageBytes = new OstSecureKey(mnemonicsMetaId, encryptedMnemonics);
            Future<AsyncStatus> future1 = metaRepository.insertSecureKey(mnemonicsStorageBytes);

            //Store encrypted Device key.
            String deviceAddressMetaId = createEthKeyMetaId(deviceAddress);
            Future<AsyncStatus> future2 = metaRepository.insertSecureKey(new OstSecureKey(deviceAddressMetaId, encryptedKey));

            // @Dev: Shouldn't we wait here for future1 & future2 ?
            // We are not clearing the encryptedKey & encryptedMnemonics - for now.
            // Although: These are less of a worry as they are encrypted anyway.

            // Update meta.
            setMnemonicsMeta(deviceAddress);
            setEthKeyMeta(deviceAddress);
            mKeyMetaStruct.deviceAddress = deviceAddress;

        } catch (Exception ex) {
            Log.e(TAG, "m_s_ikm_cdk: Unexpected Exception");
            return;
        } finally {
            ecKeyPair = null;
            clearBytes(mnemonics);
            clearBytes(privateKey);
        }
    }

    /**
     * Sign messageHash using the current device key of the user.
     * @param messageHash - Hex String to sign.
     * @return signature
     */
    String signWithDeviceKey(String messageHash) {
        byte[] data = Numeric.hexStringToByteArray(messageHash);
        return signWithDeviceKey(data);

        // @Dev: don't worry about data here
        // Its just bytes of messageHash.
    }

    /**
     * Sign data using the current device key of the user.
     * @param data byte[] of Hex String (messageHash) to sign.
     * @return signature
     */
    private String signWithDeviceKey(byte[] data) {
        //Get the keyId.
        String deviceAddress = mKeyMetaStruct.deviceAddress;
        String ethKeyId = createEthKeyMetaId(deviceAddress);

        //Get the encrypted key
        OstSecureKeyModelRepository metaRepository = getByteStorageRepo();
        OstSecureKey ostSecureKey = metaRepository.getByKey(ethKeyId);
        if (null == ostSecureKey) {
            return null;
        }

        //Decrypt it.
        byte[] decryptedKey = null;
        ECKeyPair ecKeyPair;
        try {
            decryptedKey = OstAndroidSecureStorage.getInstance(OstSdk.getContext(), mUserId).decrypt(ostSecureKey.getData());
            ecKeyPair = ECKeyPair.create(decryptedKey);

            //Sign the data.
            Sign.SignatureData signatureData = Sign.signMessage(data, ecKeyPair, false);
            return signatureDataToString(signatureData);
        } catch (Throwable th) {
            //Silence it.
            Log.e(TAG, "m_s_ikm_swdk_2: Unexpected Exception");
            return null;
        } finally {
            clearBytes(decryptedKey);
            ecKeyPair = null;
        }
    }

    //endregion


    //region - Session Key Methods
    /**
     * Create a new session key for the user.
     * @return Address of session key
     */
    String createSessionKey() {
        ECKeyPair ecKeyPair = null;
        byte[] privateKey = null;
        byte[] encryptedKey = null;
        try {
            ecKeyPair = generateECKeyPair();
            String address = getKeyAddress(ecKeyPair);

            //Fetch the private key and encrypt it.
            privateKey = ecKeyPair.getPrivateKey().toByteArray();
            encryptedKey = OstAndroidSecureStorage.getInstance(OstSdk.getContext(), mUserId).encrypt(privateKey);
            clearBytes(privateKey);
            ecKeyPair = null;

            //Store the encrypted key.
            Future<AsyncStatus> future = new OstSessionKeyModelRepository().insertSessionKey(new OstSessionKey(address, encryptedKey));
            future.get(10, TimeUnit.SECONDS);

            return address;
        } catch (Exception e) {
            Log.e(TAG, String.format("%s while waiting for insertion in DB", e.getMessage()));
            return null;
        } finally {
            clearBytes(privateKey);
            clearBytes(encryptedKey);
            ecKeyPair = null;
        }
    }

    /**
     * Method to check if an encrypted copy of session key with the given address.
     * @param sessionAddress
     * @return true if encrypted copy of session key with the given address is present.
     */
    boolean canSignWithSession(String sessionAddress) {
        sessionAddress = Keys.toChecksumAddress(sessionAddress);
        OstSessionKey ostSessionKey = getSessionRepo().getByKey(sessionAddress);
        return  ( null != ostSessionKey );
    }

    /**
     * Method to sign with Session keys.
     * @param sessionAddress
     * @param hashToSign
     * @return
     */
    String signWithSession(String sessionAddress, String hashToSign) {
        OstSessionKey ostSessionKey = null;
        byte[] sessionKey = null;
        ECKeyPair ecKeyPair = null;

        try {
            ostSessionKey = new OstSessionKeyModelRepository().getByKey(sessionAddress);
            if (null == ostSessionKey) {
                return null;
            }
            sessionKey = OstAndroidSecureStorage.getInstance(OstSdk.getContext(), mUserId).decrypt(ostSessionKey.getData());
            ecKeyPair = ECKeyPair.create(sessionKey);
            Sign.SignatureData signatureData = Sign.signMessage(Numeric.hexStringToByteArray(hashToSign), ecKeyPair, false);
            return signatureDataToString(signatureData);
        } catch (Throwable th) {
            //Silence it.
            Log.e(TAG, "m_s_ikm_sws_1: Unexpected Exception");
            return null;
        } finally {
            ecKeyPair = null;
            clearBytes(sessionKey);
        }
    }
    //endregion

    //region - KeyMetaStruct Methods

    private static KeyMetaStruct keyMetaStruct = null;
    static KeyMetaStruct getKeyMataStruct(String userId) {

        if ( null != keyMetaStruct) {
            return keyMetaStruct;
        }
        OstSecureKeyModelRepository metaRepository = getByteStorageRepo();
        String userMetaId = createUserMataId(userId);
        OstSecureKey ostSecureKey = metaRepository.getByKey(userMetaId);
        if (null == ostSecureKey) {
            return null;
        }
        keyMetaStruct = createObjectFromBytes(ostSecureKey.getData());
        return keyMetaStruct;
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


    // region - External Mnemonics Signing Method
    void sign(OstSignWithMnemonicsStruct ostSignWithMnemonicsStruct) {
        String messageHash = ostSignWithMnemonicsStruct.getMessageHash();
        byte[] mnemonics = ostSignWithMnemonicsStruct.getMnemonics();
        if ( null == messageHash || null == mnemonics) {
            return;
        }

        byte[] dataToSign =  Numeric.hexStringToByteArray(messageHash);
        String signature = null;
        String signerAddress = null;
        Bip32ECKeyPair ecKeyPair = null;
        try {
            //Create ecKeyPair
            ecKeyPair = generateECKeyPairWithMnemonics(mnemonics);
            signerAddress = getKeyAddress(ecKeyPair);

            Sign.SignatureData signatureData = Sign.signMessage(dataToSign, ecKeyPair, false);
            signature = signatureDataToString( signatureData );

        } catch (Exception ex) {
            //Mute the exceptions
            ex.printStackTrace();
            return;
        } finally {
            //Clean out mnemonics
            clearBytes(mnemonics);

            ecKeyPair = null;
        }

        ostSignWithMnemonicsStruct.setSignature(signature);
        ostSignWithMnemonicsStruct.setSigner(signerAddress);
    }
    //endregion


    //region - Key Generators
    private ECKeyPair generateECKeyPair() {
        byte[] mnemonics = null;
        try {
            mnemonics = generateMnemonics();
            return generateECKeyPairWithMnemonics(mnemonics);
        } catch (Throwable th ) {
            throw new OstError("ikm_geckp_1", ErrorCode.FAILED_TO_GENERATE_ETH_KEY);
        } finally {
            clearBytes(mnemonics);
        }
    }

    private byte[] generateMnemonics() {
        byte[] initialEntropy = new byte[16];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(initialEntropy);
        return MnemonicUtils.generateMnemonic(initialEntropy).getBytes(UTF_8);
    }

    private static final int HARDENED_BIT= 0x80000000;
    private static final int[] HD_DERIVATION_PATH_FIRST_CHILD = (new int[]{44 | HARDENED_BIT,60 | HARDENED_BIT, HARDENED_BIT, 0, 0});
    private Bip32ECKeyPair generateECKeyPairWithMnemonics(byte[] mnemonics) {
        byte[] seed = null;
        Bip32ECKeyPair hdMasterKey = null;
        try {
            seed = generateSeedFromMnemonicBytes(mnemonics, "");
            hdMasterKey = Bip32ECKeyPair.generateKeyPair(seed);
            return Bip32ECKeyPair.deriveKeyPair(hdMasterKey,HD_DERIVATION_PATH_FIRST_CHILD );
        } catch (Throwable th ){
            throw  new OstError("ikm_geckp_2", ErrorCode.FAILED_TO_GENERATE_ETH_KEY);
        } finally {
            clearBytes(seed);
            hdMasterKey = null;
        }
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


    //region - Passphrase based Recovery Key

    /**
     * Create recovery key for given passphrase and salt.
     * Make sure it is outside try/catch to avoid multiple clearBytes.
     * But, devs must still set ecKeyPair = null in finally block.
     * Also, the caller must ensure that userPassphrase is not null and not wiped before calling to avoid exceptions.
     *
     * @param userPassphrase - Passphrase of the user.
     * @param salt - Salt of recovery key.
     * @return - Recovery Key. You need handle it properly.
     */
    private ECKeyPair createRecoveryKey(UserPassphrase userPassphrase, byte[] salt) {
        int SCryptMemoryCost = 2;
        int SCryptBlockSize = 2;
        int SCryptParallelization = 2;
        int SCryptKeyLength = 32;


        if ( null == userPassphrase || userPassphrase.isWiped() ) {
            throw new IllegalArgumentException();
        }

        byte[] seed = null;
        byte[] passphrase = null;
        try{
            passphrase = userPassphrase.getPassphrase();
            seed = SCrypt.generate(passphrase, salt, SCryptMemoryCost, SCryptBlockSize, SCryptParallelization, SCryptKeyLength);
            return Bip32ECKeyPair.generateKeyPair(seed);
        } catch (Throwable th) {
            //Suppress Error.
            throw new OstError("c_ikm_crk_1", ErrorCode.RECOVERY_KEY_GENERATION_FAILED);
        } finally {
            //Clear the seed.
            clearBytes(seed);

            //Wipe user passphrase.
            userPassphrase.wipe();

            //Make UserPassphrase unusable.
            clearBytes(passphrase);

            //Make sure salt is unusable.
            clearBytes(salt);
        }
    }

    String getRecoveryAddress(UserPassphrase userPassphrase, byte[] salt) {
        //Let createRecoveryKey be out side of try/catch.
        ECKeyPair ecKeyPair = null;
        if ( null == userPassphrase || userPassphrase.isWiped() ) {
            throw new OstError("c_ikm_gra_1", ErrorCode.INVALID_USER_PASSPHRASE);
        }

        try {
            ecKeyPair = createRecoveryKey(userPassphrase,salt);
            return getKeyAddress(ecKeyPair);
        } catch(Throwable th) {
            //Suppress Error.
            throw new OstError("c_ikm_gra_2", ErrorCode.RECOVERY_KEY_GENERATION_FAILED);
        } finally {
            ecKeyPair = null;
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


    // region - internal utilities
    private static String signatureDataToString(Sign.SignatureData signatureData) {
        return Numeric.toHexString(signatureData.getR()) + Numeric.cleanHexPrefix(Numeric.toHexString(signatureData.getS())) + String.format("%02x",(signatureData.getV()));
    }

    private static final byte[] nonSecret = ("BYTES_CLEARED_" + String.valueOf((int) (System.currentTimeMillis()))  ).getBytes();
    private static void clearBytes(byte[] secret) {
        if ( null == secret ) { return; }
        for (int i = 0; i < secret.length; i++) {
            secret[i] = nonSecret[i % nonSecret.length];
        }
    }

    private String getKeyAddress(ECKeyPair keyPair) {
        Credentials credentials = null;
        try {
            credentials = Credentials.create(keyPair);
            String address = credentials.getAddress();
            return Keys.toChecksumAddress(address);
        } finally {
            credentials =null;
        }
    }


    //endregion


}
