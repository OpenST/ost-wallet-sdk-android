package com.ost.mobilesdk.security;

import android.text.TextUtils;
import android.util.Log;

import com.ost.mobilesdk.OstConstants;
import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.Impls.OstSecureKeyModelRepository;
import com.ost.mobilesdk.models.Impls.OstSessionKeyModelRepository;
import com.ost.mobilesdk.models.entities.OstSecureKey;
import com.ost.mobilesdk.models.entities.OstSessionKey;
import com.ost.mobilesdk.models.entities.OstUser;
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

import java.security.interfaces.ECKey;
import java.util.HashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

class InternalKeyManager {
    private static OstSecureKeyModelRepository modelRepo = null;

    private static final String TAG = "IKM";
    private static final String USER_PRESENCE_INFO_HASH_FOR_ = "pin_hash_for_";
    private static final String USER_DEVICE_INFO_FOR = "user_device_info_for_";
    private static final String ETHEREUM_KEY_FOR_ = "ethereum_key_for_";
    private static final String ETHEREUM_KEY_MNEMONICS_FOR_ = "ethereum_key_mnemonics_for_";


    private KeyMetaStruct mKeyMetaStruct;
    private String mUserId;
    InternalKeyManager(String userId) {
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

    //region Device Key Methods
    private void createDeviceKey() {
        //Create mnemonics and encrypt it.
        String mnemonics = OstSdkCrypto.getInstance().genMnemonics();
        byte[] encryptedMnemonics = OstAndroidSecureStorage.getInstance(OstSdk.getContext(), mUserId).encrypt(mnemonics.getBytes());


        //Create ECKeyPair and encrypt it.
        ECKeyPair ecKeyPair = OstSdkCrypto.getInstance().genECKeyFromMnemonics(mnemonics);
        String deviceAddress = Credentials.create(ecKeyPair).getAddress();
        byte[] privateKey = ecKeyPair.getPrivateKey().toByteArray();
        byte[] encryptedKey = OstAndroidSecureStorage.getInstance(OstSdk.getContext(), mUserId).encrypt(privateKey);
        ecKeyPair = null;
        privateKey = null;
        mnemonics = null;

        OstSecureKeyModelRepository metaRepository = modelRepo;

        //Store encrypted mnemonics
        String mnemonicsMetaId = createMnemonicsMetaId(deviceAddress);
        OstSecureKey mnemonicsStorageBytes = new OstSecureKey(mnemonicsMetaId, encryptedMnemonics);
        Future<AsyncStatus> future1 = metaRepository.insertSecureKey(mnemonicsStorageBytes);

        //Store encrypted Device key.
        String deviceAddressMetaId = createEthKeyMetaId(deviceAddress);
        Future<AsyncStatus> future2 = metaRepository.insertSecureKey(new OstSecureKey(deviceAddressMetaId, encryptedKey));

        // Update meta.
        setMnemonicsMeta(deviceAddress);
        setEthKeyMeta(deviceAddress);
        mKeyMetaStruct.deviceAddress = deviceAddress;
    }

    String[] getMnemonics(String address) {
        //Get the keyId.
        String mnemonicsMetaId = mKeyMetaStruct.getEthKeyMnemonicsIdentifier(address);

        //Get the encrypted mnemonics
        OstSecureKeyModelRepository metaRepository = modelRepo;
        OstSecureKey ostSecureKey = metaRepository.getByKey(mnemonicsMetaId);
        if (null == ostSecureKey) {
            return null;
        }

        //Decrypt it.
        byte[] decryptedMnemonics = OstAndroidSecureStorage.getInstance(OstSdk.getContext(), mUserId).decrypt(ostSecureKey.getData());
        String mnemonics = new String(decryptedMnemonics);
        return mnemonics.split(" ");
    }


    String signWithDeviceKey(byte[] data) {
        //Get the keyId.
        String deviceAddress = mKeyMetaStruct.deviceAddress;
        String ethKeyId = mKeyMetaStruct.getEthKeyIdentifier(deviceAddress);

        //Get the encrypted key
        OstSecureKeyModelRepository metaRepository = modelRepo;
        OstSecureKey ostSecureKey = metaRepository.getByKey(ethKeyId);
        if (null == ostSecureKey) {
            return null;
        }

        //Decrypt it.
        byte[] decryptedKey = OstAndroidSecureStorage.getInstance(OstSdk.getContext(), mUserId).decrypt(ostSecureKey.getData());
        ECKeyPair ecKeyPair = ECKeyPair.create(decryptedKey);

        //Sign the data.
        Sign.SignatureData signatureData = Sign.signMessage(data, ecKeyPair, false);
        ecKeyPair = null;

        //return the signature.
        return Numeric.toHexString(signatureData.getR()) + Numeric.cleanHexPrefix(Numeric.toHexString(signatureData.getS())) + String.format("%02x", (signatureData.getV()));
    }

    //endregion

    // region - Api Key Methods
    private String createApiKey() {
        // Create a private key.
        ECKeyPair ecKeyPair = OstSdkCrypto.getInstance().genECKey();
        String apiKeyAddress = Credentials.create(ecKeyPair).getAddress();
        byte[] privateKey = ecKeyPair.getPrivateKey().toByteArray();
        ecKeyPair = null;

        //Encrypt the private key.
        byte[] encryptedKey = OstAndroidSecureStorage.getInstance(OstSdk.getContext(), mUserId).encrypt(privateKey);
        privateKey = null;

        //Store the encrypted key.
        String apiKeyId = createEthKeyMetaId(apiKeyAddress);
        OstSecureKeyModelRepository metaRepository = modelRepo;
        Future<AsyncStatus> future = metaRepository.insertSecureKey(new OstSecureKey(apiKeyId, encryptedKey));

        try {
            future.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            Log.e(TAG, String.format("%s while waiting for insertion in DB", e.getMessage()));
            return null;
        }

        //Update key meta.
        setEthKeyMeta(apiKeyAddress);
        mKeyMetaStruct.apiAddress = apiKeyAddress;

        return apiKeyAddress;
    }

    Sign.SignatureData signBytesWithApiSigner(byte[] dataToSign) {
        //Get Api Key address and id.
        String apiKeyAddress = mKeyMetaStruct.getApiAddress();
        String apiKeyId = createEthKeyMetaId(apiKeyAddress);

        //Fetch and decrypt Api Key
        OstSecureKeyModelRepository metaRepository = modelRepo;
        OstSecureKey osk = metaRepository.getByKey(apiKeyId);
        byte[] key = OstAndroidSecureStorage.getInstance(OstSdk.getContext(), mUserId).decrypt(osk.getData());
        ECKeyPair ecKeyPair = ECKeyPair.create(key);
        key = null;

        //Sign the data
        Sign.SignatureData signatureData = Sign.signPrefixedMessage(dataToSign, ecKeyPair);
        ecKeyPair = null;
        return signatureData;
    }
    //endregion

    //region - Validate Pin

    /**
     * Validates user passphrase
     * @param passphrasePrefix - Password Prefix Provided my the consumer application
     * @param userPassphrase - Pin entered by user. Min length 6.
     * @param scriptSalt - SCript salt provided by Kit.
     * @return true if provided inputs can prove userPassphrase validity.
     */
    boolean validateUserPassphrase(String passphrasePrefix, String userPassphrase, String scriptSalt) {
        if ( !areRecoveryPassphraseInputsValid(passphrasePrefix, userPassphrase, scriptSalt) ) {
            userPassphraseInvalidated();
            return false;
        }

        if ( !isUserPassphraseValidationAllowed() ) {
            return false;
        }


        OstUser ostUser = OstUser.getById(mUserId);
        String recoveryOwnerAddress = ostUser.getRecoveryOwnerAddress();
        if ( validateWithUserPresenceInfo(passphrasePrefix, userPassphrase, scriptSalt, recoveryOwnerAddress) ) {
            userPassphraseValidated();
            return true;
        }

        boolean isValid = validateByCreatingRecoveryOwner(passphrasePrefix, userPassphrase, scriptSalt,recoveryOwnerAddress);
        if ( isValid ) {
            // Let update user presence.
            userPassphraseValidated();
            storeUserPresenceInfoInDb(passphrasePrefix, userPassphrase, scriptSalt, recoveryOwnerAddress);
        } else {
            userPassphraseInvalidated();
        }

        return isValid;
    }

    /**
     * Validate user passphrase by creating recovery address
     * @param passphrasePrefix - Password Prefix Provided my the consumer application
     * @param userPassphrase - Pin entered by user. Min length 6.
     * @param scriptSalt - SCript salt provided by Kit.
     * @param recoveryOwnerAddress - User's registered recovery key address.
     * @return true if inputs are able to generate ECKeyPair which has same ethereum address is same as recoveryOwnerAddress.
     */
    private boolean validateByCreatingRecoveryOwner(String passphrasePrefix, String userPassphrase, String scriptSalt, String recoveryOwnerAddress) {
        ECKeyPair ecKeyPair = createRecoveryECKey(passphrasePrefix, userPassphrase, scriptSalt);
        String expectedRecoveryOwnerAddress = Credentials.create(ecKeyPair).getAddress();
        return expectedRecoveryOwnerAddress.equalsIgnoreCase(recoveryOwnerAddress);
    }
    //endregion

    //region - User Presence Info - Information that can prove the presence of user via valid pin.
    private String createUserPresenceInfoHash(String passphrasePrefix, String userPassphrase, String scriptSalt, String recoveryOwnerAddress) throws Exception {
        if ( !areRecoveryPassphraseInputsValid(passphrasePrefix, userPassphrase, scriptSalt) ) {
            return null;
        }
        String pinString = String.format("%s%s%s%s%s", passphrasePrefix, userPassphrase, scriptSalt, recoveryOwnerAddress, mUserId);
        return new SoliditySha3().soliditySha3(pinString);
    }

    /**
     * Helper method to create id for User Presence Info.
     * @param recoveryOwnerAddress
     * @return
     */
    private String getUserPresenceInfoId(String recoveryOwnerAddress) {
        return USER_PRESENCE_INFO_HASH_FOR_ + recoveryOwnerAddress;
    }

    /**
     * Generates and stores encrypted user presence Info Hash in Database.
     * @param passphrasePrefix - Password Prefix Provided my the consumer application
     * @param userPassphrase - Passphrase provided by user.
     * @param scriptSalt - SCript salt provided by Kit.
     * @param recoveryOwnerAddress - User's registered recovery key address.
     * @return false if exceptions occurred while generating/storing UserPresenceInfoHash.
     */
    boolean storeUserPresenceInfoInDb(String passphrasePrefix, String userPassphrase, String scriptSalt, String recoveryOwnerAddress) {
        if ( !areRecoveryPassphraseInputsValid(passphrasePrefix, userPassphrase, scriptSalt) ) {
            return false;
        }

        String userPresenceInfoHash = null;
        try {
            userPresenceInfoHash = createUserPresenceInfoHash(passphrasePrefix, userPassphrase, scriptSalt, recoveryOwnerAddress);
        } catch (Exception e) {
            return false;
        }

        byte[] encrypted = OstAndroidSecureStorage.getInstance(OstSdk.getContext(), mUserId).encrypt(userPresenceInfoHash.getBytes());

        String userPresenceInfoId = getUserPresenceInfoId(recoveryOwnerAddress);

        Future<AsyncStatus> future = modelRepo
                .insertSecureKey(new OstSecureKey(userPresenceInfoId, encrypted));

        try {
            future.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            Log.e(TAG, String.format("%s while waiting for insertion in DB", e.getMessage()));
            return false;
        }
        return true;
    }

    /**
     *
     * @param passphrasePrefix - Password Prefix Provided my the consumer application
     * @param userPassphrase - Pin entered by user. Min length 6.
     * @param scriptSalt - SCript salt provided by Kit.
     * @param recoveryOwnerAddress - User's registered recovery key address.
     * @return true if inputs are able to generate user presence info associated with recoveryOwnerAddress
     */
    private boolean validateWithUserPresenceInfo(String passphrasePrefix, String userPassphrase, String scriptSalt, String recoveryOwnerAddress) {
        if ( !areRecoveryPassphraseInputsValid(passphrasePrefix, userPassphrase, scriptSalt) ) {
            return false;
        }

        // Compute Provided User Presence Info.
        String providedUserPresenceInfoHash;
        try {
            providedUserPresenceInfoHash = createUserPresenceInfoHash(passphrasePrefix, userPassphrase, scriptSalt, recoveryOwnerAddress);
        } catch (Exception e) {
            return false;
        }

        // Get User Presence Info from DB
        String userPresenceInfoId = getUserPresenceInfoId(recoveryOwnerAddress);
        OstSecureKey ostSecureKey = new OstSecureKeyModelRepository().getByKey(userPresenceInfoId);
        if (null == ostSecureKey) {
            return false;
        }

        // Decrypt the User Presence Info.
        byte[] encryptedData = ostSecureKey.getData();
        byte[] decryptedData = OstAndroidSecureStorage.getInstance(OstSdk.getContext(), mUserId).decrypt(encryptedData);
        String expectedUserPresenceInfoHash = new String(decryptedData);

        // Compare
        return providedUserPresenceInfoHash.equalsIgnoreCase(expectedUserPresenceInfoHash);
    }
    //endregion

    // region - Passphrase Locker Class


    class PassphraseValidationLocker {
        private long lastInvalidAttemptTimestamp;
        private long lastValidAttemptTimestamp;
        private int retryCount;

        /**
         * Use this method to check if validation is allowed.
         * @return
         */
        private boolean isValidationAllowed() {
            if ( retryCount < MaxRetryCount ) {
                return true;
            }
            long now = System.currentTimeMillis();
            long elapsedLockDuration = now - lastInvalidAttemptTimestamp;
            if ( elapsedLockDuration <= LockDuration ) {
                return false;
            }
            return true;
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
            if ( retryCount > 3 && wasValidationAllowed) {
                retryCount = 1;
            }
        }

        /**
         * Use this method to check if validation is needed.
         * @return
         */
        private boolean isValidationNeeded() {
            //If passphrase validation is not allowed, return true.
            if ( !isValidationAllowed() ) {
                return true;
            }

            long now = System.currentTimeMillis();
            long elapsedLockDuration = now - lastValidAttemptTimestamp;
            if ( elapsedLockDuration > UnlockedDuration ) {
                return true;
            }
            return false;
        }
    }
    // endregion

    // region - Passphrase Locker Utility Methods

    private static int MaxRetryCount = 3;
    //To-Do: Get these from configs/constants
    private static long LockDuration = (10 * 60 * 60 * 1000);
    private static long UnlockedDuration = 0;

    // A static hash-map that stores instances of PassphraseValidationLocker
    private static HashMap<String,PassphraseValidationLocker> lockers = new HashMap<>();
    /**
     * A Factory method to get In-Memory PassphraseValidationLocker for a given user-id
     * @param userId Id of the user.
     * @return
     */
    private PassphraseValidationLocker getLockerFor(String userId) {
        PassphraseValidationLocker locker = lockers.get(userId);
        if ( null == locker ) {
            locker = new PassphraseValidationLocker();
            lockers.put(userId, locker);
        }
        return locker;
    }

    boolean isUserPassphraseValidationAllowed() {
        PassphraseValidationLocker locker = getLockerFor(mUserId);
        return locker.isValidationAllowed();
    }
    private void userPassphraseValidated() {
        PassphraseValidationLocker locker = getLockerFor(mUserId);
        locker.validated();
    }
    private void userPassphraseInvalidated() {
        PassphraseValidationLocker locker = getLockerFor(mUserId);
        locker.invalidated();
    }
    boolean isUserPassphraseValidationNeeded() {
        PassphraseValidationLocker locker = getLockerFor(mUserId);
        return locker.isValidationNeeded();
    }
    // endregion

    //region - Session Key Methods
    String signWithSessionKey(String sessionAddress, String hashToSign) {
        OstSessionKey ostSessionKey = new OstSessionKeyModelRepository().getByKey(sessionAddress);
        if (null == ostSessionKey) {
            return null;
        }
        byte[] sessionKey = OstAndroidSecureStorage.getInstance(OstSdk.getContext(), mUserId).decrypt(ostSessionKey.getData());
        ECKeyPair ecKeyPair = ECKeyPair.create(sessionKey);
        Sign.SignatureData signatureData = Sign.signMessage(Numeric.hexStringToByteArray(hashToSign), ecKeyPair, false);
        String signature = Numeric.toHexString(signatureData.getR()) + Numeric.cleanHexPrefix(Numeric.toHexString(signatureData.getS())) + String.format("%02x", (signatureData.getV()));
        ecKeyPair = null;
        return signature;
    }

    String createSessionKey() {
        ECKeyPair ecKeyPair = OstSdkCrypto.getInstance().genECKey();
        String address = Credentials.create(ecKeyPair).getAddress();
        byte[] privateKey = ecKeyPair.getPrivateKey().toByteArray();
        byte[] encryptedKey = OstAndroidSecureStorage.getInstance(OstSdk.getContext(), mUserId).encrypt(privateKey);
        privateKey = null;
        ecKeyPair = null;

        Future<AsyncStatus> future = new OstSessionKeyModelRepository().insertSessionKey(new OstSessionKey(address, encryptedKey));

        try {
            future.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            Log.e(TAG, String.format("%s while waiting for insertion in DB", e.getMessage()));
            return null;
        }

        return address;
    }
    //endregion

    //region - Recovery Key Methods
    /**
     * Sign Data with recovery owner key generated using inputs.
     * @param passphrasePrefix - Password Prefix Provided my the consumer application. Min length 30.
     * @param userPassphrase - Pin entered by user. Min length 6.
     * @param scriptSalt - SCript salt provided by Kit.
     * @param hexStringToSign - hexString to sign using recovery key.
     * @return Signature
     */
    String signDataWithRecoveryKey(String passphrasePrefix, String userPassphrase, String scriptSalt, String hexStringToSign) {
        if ( !areRecoveryPassphraseInputsValid(passphrasePrefix, userPassphrase, scriptSalt) ) {
            return null;
        }
        if ( !isUserPassphraseValidationAllowed() ) {
            //Do not throw. Just return null.
            return null;
        }

        OstUser ostUser = OstUser.getById(mUserId);
        String recoveryOwnerAddress = ostUser.getRecoveryOwnerAddress();

        // Generate ecKeyPair.
        ECKeyPair ecKeyPair = createRecoveryECKey(passphrasePrefix, userPassphrase, scriptSalt);

        // Validate recoveryOwnerAddress.
        String expectedRecoveryOwnerAddress = Credentials.create(ecKeyPair).getAddress();
        if ( !expectedRecoveryOwnerAddress.equalsIgnoreCase(recoveryOwnerAddress) ) {
            // Note that user passphrase is invalid.
            userPassphraseInvalidated();

            //Do not throw. Just return null.
            return null;
        }

        // Note that user passphrase is valid.
        userPassphraseValidated();

        // Sign the data.
        Sign.SignatureData signatureData = Sign.signMessage(Numeric.hexStringToByteArray(hexStringToSign), ecKeyPair, false);
        String signature = Numeric.toHexString(signatureData.getR()) + Numeric.cleanHexPrefix(Numeric.toHexString(signatureData.getS())) + String.format("%02x", (signatureData.getV()));
        ecKeyPair = null;
        return signature;
    }

    /**
     * Returns ECKeyPair generated using inputs. This method does not check the validity of inputs.
     * @param passphrasePrefix - Password Prefix Provided my the consumer application. Min length 30.
     * @param userPassphrase - Pin entered by user. Min length 6.
     * @param scriptSalt - SCript salt provided by Kit.
     * @return ECKeyPair
     */
    ECKeyPair createRecoveryECKey(String passphrasePrefix, String userPassphrase, String scriptSalt) {
        if ( !areRecoveryPassphraseInputsValid(passphrasePrefix, userPassphrase, scriptSalt) ) {
            return null;
        }
        String pinString = String.format("%s%s%s", passphrasePrefix, userPassphrase, mUserId);
        byte[] seed = OstSdkCrypto.getInstance().genSCryptKey(pinString.getBytes(), scriptSalt.getBytes());
        ECKeyPair ecKeyPair = OstSdkCrypto.getInstance().genHDKey(seed);
        return ecKeyPair;
    }

    String getRecoveryKeyAddressUsing(String passphrasePrefix, String userPassphrase, String scriptSalt) {
        if ( !areRecoveryPassphraseInputsValid(passphrasePrefix, userPassphrase, scriptSalt) ) {
            return null;
        }
        ECKeyPair ecKeyPair = createRecoveryECKey(passphrasePrefix, userPassphrase, scriptSalt);
        String recoveryOwnerAddress = Credentials.create(ecKeyPair).getAddress();
        return recoveryOwnerAddress;
    }

    boolean areRecoveryPassphraseInputsValid(String passphrasePrefix, String userPassphrase, String scriptSalt) {
        return InternalKeyManager.sanityCheckRecoveryPassphraseInputsValidity(passphrasePrefix, userPassphrase, scriptSalt);
    }

    static boolean sanityCheckRecoveryPassphraseInputsValidity(String passphrasePrefix, String userPassphrase, String scriptSalt) {
        if ( TextUtils.isEmpty(passphrasePrefix) || passphrasePrefix.length() < OstConstants.RECOVERY_PHRASE_PREFIX_MIN_LENGTH) {
            return false;
        }

        if ( TextUtils.isEmpty(userPassphrase) || userPassphrase.length() < OstConstants.RECOVERY_PHRASE_USER_INPUT_MIN_LENGTH) {
            return false;
        }

        if ( TextUtils.isEmpty(scriptSalt) || scriptSalt.length() < OstConstants.RECOVERY_PHRASE_SCRYPT_SALT_MIN_LENGTH) {
            return false;
        }
        return true;
    }
    //endregion

    //region - KeyMetaStruct Methods

    static KeyMetaStruct srtuct = null;
    static KeyMetaStruct getKeyMataStruct(String userId) {
        if ( null == modelRepo ) {
            modelRepo = new OstSecureKeyModelRepository();
        }

        if ( null != srtuct ) {
            return srtuct;
        }
        OstSecureKeyModelRepository metaRepository = modelRepo;
        String userMetaId = createUserMataId(userId);
        OstSecureKey ostSecureKey = metaRepository.getByKey(userMetaId);
        if (null == ostSecureKey) {
            return null;
        }
        srtuct = createObjectFromBytes(ostSecureKey.getData());
        return srtuct;
    }


    static KeyMetaStruct createObjectFromBytes(byte[] bytes) {
        if ( null == modelRepo ) {
            modelRepo = new OstSecureKeyModelRepository();
        }

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
        if ( null == modelRepo ) {
            modelRepo = new OstSecureKeyModelRepository();
        }

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
        OstSecureKeyModelRepository metaRepository = modelRepo;
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

}