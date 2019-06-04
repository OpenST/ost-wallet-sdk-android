/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.ecKeyInteracts;

import android.util.Log;

import com.ost.walletsdk.OstConfigs;
import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.ecKeyInteracts.structs.SignedTransactionStruct;
import com.ost.walletsdk.models.entities.OstSession;
import com.ost.walletsdk.models.entities.OstToken;
import com.ost.walletsdk.models.entities.OstUser;
import com.ost.walletsdk.network.OstApiClient;
import com.ost.walletsdk.utils.CommonUtils;
import com.ost.walletsdk.utils.EIP1077;
import com.ost.walletsdk.utils.PricerRule;
import com.ost.walletsdk.utils.TokenHolder;
import com.ost.walletsdk.utils.TokenRules;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static com.ost.walletsdk.workflows.errors.OstErrors.ErrorCode;


public class OstTransactionSigner {
    private static final String TAG = "OstTransactionSigner";
    private static final String DIRECT_TRANSFER = OstSdk.RULE_NAME_DIRECT_TRANSFER;
    private static final String PRICER =OstSdk.RULE_NAME_PRICER;
    private static final String DECIMAL_EXPONENT = "decimals";
    private final String mUserId;
    private final String mTokenId;

    public OstTransactionSigner(String userId) {
        mUserId = userId;
        mTokenId = OstUser.getById(userId).getTokenId();
    }

    public SignedTransactionStruct getSignedTransaction(String ruleName,
                                                        List<String> tokenHolderAddresses,
                                                        List<String> amounts,
                                                        String ruleAddress) {
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
                JSONObject pricePointApiResponse = ostApiClient.getPricePoints();
                try {
                    CommonUtils commonUtils = new CommonUtils();
                    if (!commonUtils.isValidResponse(pricePointApiResponse)) {
                        throw OstError.ApiResponseError("km_ts_st_5", "getPricePoints", pricePointApiResponse);
                    }
                    JSONObject pricePointObject = commonUtils.parseObjectResponseForKey(pricePointApiResponse, OstSdk.getToken(user.getTokenId()).getBaseToken());
                    if (null == pricePointObject) {
                        throw OstError.ApiResponseError("km_ts_st_6", "getPricePoints", pricePointApiResponse);
                    }
                    pricePointOSTtoUSD = pricePointObject.getDouble(OstConfigs.getInstance().PRICE_POINT_CURRENCY_SYMBOL);
                    decimalExponent = pricePointObject.getInt(DECIMAL_EXPONENT);

                } catch (Throwable e) {
                    OstError ostError;
                    if ( e instanceof OstError ) {
                        ostError = (OstError) e;
                    } else {
                        ostError = OstError.ApiResponseError("km_ts_st_7", "getPricePoints", pricePointApiResponse);
                    }
                    throw ostError;
                }
                Log.i(TAG, "Building call data");

                BigInteger weiPricePoint = convertPricePointFromEthToWei(pricePointOSTtoUSD, decimalExponent);

                OstToken ostToken = OstToken.getById(mTokenId);
                if (null == ostToken) {
                    throw new OstError("km_ts_st_8",
                            ErrorCode.INVALID_TOKEN_ID);
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


                BigDecimal fiatMultiplier = calFiatMultiplier(pricePointOSTtoUSD, decimalExponent, conversionFactor, btDecimals);

                callData = new PricerRule().getPriceTxnExecutableData(user.getTokenHolderAddress(),
                        tokenHolderAddresses, amounts, OstConfigs.getInstance().PRICE_POINT_CURRENCY_SYMBOL, weiPricePoint);
                rawCallData = new PricerRule().getPricerTransactionRawCallData(user.getTokenHolderAddress(),
                        tokenHolderAddresses, amounts, OstConfigs.getInstance().PRICE_POINT_CURRENCY_SYMBOL, weiPricePoint);
                spendingBtAmountInWei = new PricerRule().calDirectTransferSpendingLimit(amounts, fiatMultiplier);
                break;
            default:
                OstError ostError = new OstError("km_ts_st_11",
                        OstErrors.ErrorCode.RULE_NOT_FOUND);
                throw ostError;

        }

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
            OstError ostError = new OstError("km_ts_st_3", ErrorCode.SDK_ERROR);
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

    private BigDecimal calFiatMultiplier(double oneOstToUsd,
                                         int usdDecimalExponent,
                                         String oneOstToBT,
                                         int btDecimalExponent) {
        // weiDecimal = OstToUsd * 10^decimalExponent
        BigDecimal bigDecimal = new BigDecimal(String.valueOf(oneOstToUsd));
        BigDecimal toWeiMultiplier = new BigDecimal(10).pow(usdDecimalExponent);
        BigDecimal usdWeiDecimalDenominator = bigDecimal.multiply(toWeiMultiplier);

        // toBtWeiMultiplier = 10^btDecimal
        BigDecimal toBtWeiMultiplier = new BigDecimal(10).pow(btDecimalExponent);

        // btInWeiNumerator = conversionFactorOstToPin * toBtWeiMultiplier
        BigDecimal conversionFactorOstToPin = new BigDecimal(String.valueOf(oneOstToBT));
        BigDecimal btInWeiNumerator = conversionFactorOstToPin.multiply(toBtWeiMultiplier);

        // multiplierForFiat = btInWeiNumerator / usdWeiDecimalDenominator
        BigDecimal multiplierForFiat = btInWeiNumerator.divideToIntegralValue(usdWeiDecimalDenominator);

        return multiplierForFiat;
    }


    private BigInteger convertPricePointFromEthToWei(double pricePointUSDtoOST, int decimalExponent) {
        BigDecimal bigDecimal = new BigDecimal(String.valueOf(pricePointUSDtoOST));
        BigDecimal toWeiMultiplier = new BigDecimal(10).pow(decimalExponent);
        BigDecimal weiDecimal = bigDecimal.multiply(toWeiMultiplier);
        BigInteger weiInteger = weiDecimal.toBigInteger();

        return weiInteger;
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