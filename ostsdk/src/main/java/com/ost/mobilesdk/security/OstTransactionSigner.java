package com.ost.mobilesdk.security;

import android.util.Log;

import com.ost.mobilesdk.models.entities.OstRule;
import com.ost.mobilesdk.models.entities.OstSession;
import com.ost.mobilesdk.models.entities.OstToken;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.network.OstApiClient;
import com.ost.mobilesdk.security.structs.SignedTransactionStruct;
import com.ost.mobilesdk.utils.CommonUtils;
import com.ost.mobilesdk.utils.EIP1077;
import com.ost.mobilesdk.utils.PricerRule;
import com.ost.mobilesdk.utils.TokenHolder;
import com.ost.mobilesdk.utils.TokenRules;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.errors.OstErrors;

import org.json.JSONObject;

import java.math.BigInteger;
import java.util.List;

import static com.ost.mobilesdk.workflows.errors.OstErrors.ErrorCode;


public class OstTransactionSigner {
    private static final String TAG = "OstTransactionSigner";
    private static final String DIRECT_TRANSFER = "Direct Transfer";
    private static final String PRICER = "Pricer";
    private static final String COUNTRY_CODE_USD = "USD";
    private final String mUserId;
    private final String mTokenId;

    public OstTransactionSigner(String userId) {
        mUserId = userId;
        mTokenId = OstUser.getById(userId).getTokenId();
    }

    public SignedTransactionStruct getSignedTransaction(String ruleName, List<String> tokenHolderAddresses, List<String> amounts) {
        OstUser user = OstUser.getById(mUserId);

        tokenHolderAddresses = new CommonUtils().toCheckSumAddresses(tokenHolderAddresses);

        String callData = null;
        String rawCallData = null;

        switch (ruleName) {
            case DIRECT_TRANSFER:
                Log.i(TAG, "Building call data");
                callData = new TokenRules().getTransactionExecutableData(tokenHolderAddresses, amounts);
                rawCallData = new TokenRules().getTransactionRawCallData(tokenHolderAddresses, amounts);
                break;
            case PRICER:
                Log.i(TAG, "Fetch price points");
                double pricePointUSDtoOST;
                OstApiClient ostApiClient = new OstApiClient(mUserId);
                try {
                    CommonUtils commonUtils = new CommonUtils();
                    JSONObject jsonObject = ostApiClient.getPricePoints();
                    if (!commonUtils.isValidResponse(jsonObject)) {
                        OstError ostError = new OstError("km_ts_st_5",
                                OstErrors.ErrorCode.PRICE_POINTS_API_FAILED);
                        throw ostError;
                    }
                    JSONObject pricePointObject = commonUtils.parseObjectResponseForKey(jsonObject, "OST");
                    if (null == pricePointObject) {
                        OstError ostError = new OstError("km_ts_st_6",
                                OstErrors.ErrorCode.PRICE_POINTS_API_FAILED);
                        throw ostError;
                    }
                    pricePointUSDtoOST = pricePointObject.getDouble(COUNTRY_CODE_USD);

                } catch (Exception e) {
                    OstError ostError = new OstError("km_ts_st_7",
                            OstErrors.ErrorCode.PRICE_POINTS_API_FAILED);
                    throw ostError;
                }
                Log.i(TAG, "Building call data");

                BigInteger weiPricePoint = convertPricePointFromEthToWei(pricePointUSDtoOST);

                callData = new PricerRule().getPriceTxnExecutableData(user.getTokenHolderAddress(),
                        tokenHolderAddresses, amounts, COUNTRY_CODE_USD, weiPricePoint);
                rawCallData = new PricerRule().getPricerTransactionRawCallData(user.getTokenHolderAddress(),
                        tokenHolderAddresses, amounts, COUNTRY_CODE_USD, weiPricePoint);
                break;
            default:
                OstError ostError = new OstError("km_ts_st_8",
                        OstErrors.ErrorCode.UNKNOWN_RULE_NAME);
                throw ostError;

        }

        String ruleAddress = getRuleAddressFor(ruleName);
        if (null == ruleAddress) {
            OstError ostError = new OstError("km_ts_st_1", OstErrors.ErrorCode.RULE_NOT_FOUND);
            throw ostError;
        }

        OstSession activeSession = user.getActiveSession();
        if (null == activeSession) {
            OstError ostError = new OstError("km_ts_st_2", OstErrors.ErrorCode.NO_SESSION_FOUND);
            throw ostError;
        }

        String signerSessionAddress = activeSession.getAddress();

        Log.i(TAG, "Creating transaction hash to sign");
        String eip1077TxnHash = createEIP1077TxnHash(callData, ruleAddress, activeSession.getNonce());
        if (null == eip1077TxnHash) {
            OstError ostError = new OstError("km_ts_st_3", ErrorCode.EIP1077_FAILED);
            throw ostError;
        }

        Log.i(TAG, "Signing Transaction using session");
        InternalKeyManager ikm = new InternalKeyManager(mUserId);
        String signature = ikm.signWithSession(signerSessionAddress, eip1077TxnHash);

        if (null == signature) {
            OstError ostError = new OstError("km_ts_st_4", ErrorCode.FAILED_TO_SIGN_DATA);
            throw ostError;
        }

        return new SignedTransactionStruct(activeSession, user.getTokenHolderAddress(), rawCallData,
                callData, signature);
    }

    private BigInteger convertPricePointFromEthToWei(double pricePointUSDtoOST) {
        int pricePointDoubleLength = String.valueOf(pricePointUSDtoOST).length();
        int pricePointInteger = (int) (pricePointUSDtoOST * Math.pow(10, pricePointDoubleLength));
        String pricePointString = String.valueOf(pricePointInteger);

        BigInteger exponentToMakeWei = new BigInteger("10").pow(18 - pricePointDoubleLength);
        BigInteger interimBigInteger = new BigInteger(pricePointString).multiply(exponentToMakeWei);

        return interimBigInteger;
    }

    private String getRuleAddressFor(String directTransfer) {
        OstToken ostToken = OstToken.getById(mTokenId);
        OstRule[] ostRules = ostToken.getAllRules();
        for (int i = 0; i < ostRules.length; i++) {
            if (directTransfer.equalsIgnoreCase(ostRules[i].getName())) {
                return ostRules[i].getAddress();
            }
        }
        return null;
    }

    /**
     * from: tokenHolderAddress,
     * to: ruleContractAddress,
     * value: 0,
     * gasPrice: 0,
     * gas: 0,
     * data: methodEncodedAbi,
     * nonce: keyNonce,
     * callPrefix: callPrefix
     *
     * @param keyNonce
     * @return
     */
    private String createEIP1077TxnHash(String callData, String contractAddress, int keyNonce) {
        JSONObject jsonObject;
        String txnHash;
        try {
            OstUser ostUser = OstUser.getById(mUserId);
            String tokenHolderAddress = ostUser.getTokenHolderAddress();
            jsonObject = new EIP1077.TransactionBuilder()
                    .setTo(contractAddress)
                    .setFrom(tokenHolderAddress)
                    .setCallPrefix(new TokenHolder().get_EXECUTABLE_CALL_PREFIX())
                    .setData(callData)
                    .setNonce(String.valueOf(keyNonce))
                    .build();
            txnHash = new EIP1077(jsonObject).toEIP1077TransactionHash();
        } catch (Exception e) {
            Log.e(TAG, "Exception while creating EIP1077 Hash");
            return null;
        }
        return txnHash;
    }

    private String createRawCallData(String ruleName, List<String> tokenHolderAddresses, List<String> amounts) {
        if (ruleName.equalsIgnoreCase(DIRECT_TRANSFER)) {
            tokenHolderAddresses = new CommonUtils().toCheckSumAddresses(tokenHolderAddresses);
            return new TokenRules().getTransactionRawCallData(tokenHolderAddresses, amounts);
        }
        return null;
    }
}