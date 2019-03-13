package com.ost.mobilesdk.ecKeyInteracts;

import android.util.Log;

import com.ost.mobilesdk.ecKeyInteracts.structs.SignedTransactionStruct;
import com.ost.mobilesdk.models.entities.OstRule;
import com.ost.mobilesdk.models.entities.OstSession;
import com.ost.mobilesdk.models.entities.OstToken;
import com.ost.mobilesdk.models.entities.OstUser;
import com.ost.mobilesdk.network.OstApiClient;
import com.ost.mobilesdk.utils.CommonUtils;
import com.ost.mobilesdk.utils.EIP1077;
import com.ost.mobilesdk.utils.PricerRule;
import com.ost.mobilesdk.utils.TokenHolder;
import com.ost.mobilesdk.utils.TokenRules;
import com.ost.mobilesdk.workflows.errors.OstError;
import com.ost.mobilesdk.workflows.errors.OstErrors;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static com.ost.mobilesdk.workflows.errors.OstErrors.ErrorCode;


public class OstTransactionSigner {
    private static final String TAG = "OstTransactionSigner";
    private static final String DIRECT_TRANSFER = "direct transfer";
    private static final String PRICER = "pricer";
    private static final String COUNTRY_CODE_USD = "USD";
    private static final String DECIMAL_EXPONENT = "decimals";
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
        String spendingBtAmountInWei = BigInteger.ZERO.toString();
        ruleName = ruleName.toLowerCase();

        switch (ruleName) {
            case DIRECT_TRANSFER:
                Log.i(TAG, "Building call data");
                callData = new TokenRules().getTransactionExecutableData(tokenHolderAddresses, amounts);
                rawCallData = new TokenRules().getTransactionRawCallData(tokenHolderAddresses, amounts);
                spendingBtAmountInWei = new TokenRules().calDirectTransferSpendingLimit(amounts);
                break;
            case PRICER:
                Log.i(TAG, "Fetch price points");
                double pricePointOSTtoUSD;
                int decimalExponent;
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
                    pricePointOSTtoUSD = pricePointObject.getDouble(COUNTRY_CODE_USD);
                    decimalExponent = pricePointObject.getInt(DECIMAL_EXPONENT);

                } catch (Exception e) {
                    OstError ostError = new OstError("km_ts_st_7",
                            OstErrors.ErrorCode.PRICE_POINTS_API_FAILED);
                    throw ostError;
                }
                Log.i(TAG, "Building call data");

                BigInteger weiPricePoint = convertPricePointFromEthToWei(pricePointOSTtoUSD, decimalExponent);

                OstToken ostToken = OstToken.getById(mTokenId);
                if (null == ostToken) {
                    throw new OstError("km_ts_st_8",
                            ErrorCode.TOKEN_API_FAILED);
                }
                String conversionFactor = ostToken.getConversionFactor();
                if (null == conversionFactor) {
                    throw new OstError("km_ts_st_9",
                            ErrorCode.INSUFFICIENT_DATA);
                }
                String btDecimalsString = ostToken.getBtDecimals();
                if (null == btDecimalsString) {
                    throw new OstError("km_ts_st_10",
                            ErrorCode.INSUFFICIENT_DATA);
                }
                int btDecimals = Integer.parseInt(btDecimalsString);


                BigInteger fiatMultiplier = calFiatMultiplier(pricePointOSTtoUSD, decimalExponent, conversionFactor, btDecimals);

                callData = new PricerRule().getPriceTxnExecutableData(user.getTokenHolderAddress(),
                        tokenHolderAddresses, amounts, COUNTRY_CODE_USD, weiPricePoint);
                rawCallData = new PricerRule().getPricerTransactionRawCallData(user.getTokenHolderAddress(),
                        tokenHolderAddresses, amounts, COUNTRY_CODE_USD, weiPricePoint);
                spendingBtAmountInWei = new PricerRule().calDirectTransferSpendingLimit(amounts, fiatMultiplier);
                break;
            default:
                OstError ostError = new OstError("km_ts_st_11",
                        OstErrors.ErrorCode.UNKNOWN_RULE_NAME);
                throw ostError;

        }

        String ruleAddress = getRuleAddressFor(ruleName);
        if (null == ruleAddress) {
            OstError ostError = new OstError("km_ts_st_1", OstErrors.ErrorCode.RULE_NOT_FOUND);
            throw ostError;
        }

        OstSession activeSession = user.getActiveSession(spendingBtAmountInWei);
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

        return new SignedTransactionStruct(activeSession, ruleAddress, rawCallData,
                callData, signature);
    }

    private BigInteger calFiatMultiplier(double pricePointOSTtoUSD,
                                         int decimalExponent,
                                         String conversionFactor,
                                         int btDecimals) {
        // weiDecimal = OstToUsd * 10^decimalExponent
        BigDecimal bigDecimal = new BigDecimal(String.valueOf(pricePointOSTtoUSD));
        BigDecimal toWeiMultiplier = new BigDecimal(10).pow(decimalExponent);
        BigDecimal weiDecimal = bigDecimal.multiply(toWeiMultiplier);

        // bigDecimalWeiDecimal = weiDecimal * conversionFactor
        BigDecimal bigDecimalConversionFactor = new BigDecimal(String.valueOf(conversionFactor));
        BigDecimal bigDecimalWeiDecimal = weiDecimal.multiply(bigDecimalConversionFactor);

        // toBtWeiMultiplier = 10^btDecimal
        BigDecimal toBtWeiMultiplier = new BigDecimal(10).pow(btDecimals);

        // multiplierForFiat = toBtWeiMultiplier / bigDecimalWeiDecimal
        BigDecimal multiplierForFiat = toBtWeiMultiplier.divideToIntegralValue(bigDecimalWeiDecimal);

        return multiplierForFiat.toBigInteger();
    }


    private BigInteger convertPricePointFromEthToWei(double pricePointUSDtoOST, int decimalExponent) {
        BigDecimal bigDecimal = new BigDecimal(String.valueOf(pricePointUSDtoOST));
        BigDecimal toWeiMultiplier = new BigDecimal(10).pow(decimalExponent);
        BigDecimal weiDecimal = bigDecimal.multiply(toWeiMultiplier);
        BigInteger weiInteger = weiDecimal.toBigInteger();

        return weiInteger;
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