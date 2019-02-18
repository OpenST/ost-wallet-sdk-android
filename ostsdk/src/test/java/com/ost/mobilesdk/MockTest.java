package com.ost.mobilesdk;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.ObjectMapperFactory;
import org.web3j.protocol.core.methods.response.AbiDefinition;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MockTest {
    @Test
    public void testMockToken() {

        try {
            File abiFile = new File("src/test/resources/MockToken.abi");
            List<AbiDefinition> functionDefinitions = loadContractDefinition(abiFile);
            AbiDefinition func = null;
            for (AbiDefinition abiDefinition: functionDefinitions) {
                if ( "approve".equals(abiDefinition.getName()) ) {
                    func = abiDefinition;
                }
            }
            List<AbiDefinition.NamedType> namedType = func.getInputs();
            for (AbiDefinition.NamedType namedType1 : namedType) {
                namedType1.getType();
            }

            Function function = new Function(
                    func.getName(),  // function we're calling
                    Arrays.asList(new Address("0x7e68ae93145b393c59e0422978d41f858d88da90"), new Uint256(new BigInteger("100000000000000000000"))),  // Parameters to pass as Solidity Types
                    Collections.<TypeReference<?>>emptyList());

            String encodeFunction = FunctionEncoder.encode(function);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testGnosisSafe() {

        try {
            File abiFile = new File("src/test/resources/GnosisSafe.abi");
            List<AbiDefinition> functionDefinitions = loadContractDefinition(abiFile);
            List<String> functionNames = new ArrayList<>();
            AbiDefinition func = null;
            for (AbiDefinition abiDefinition: functionDefinitions) {
                System.out.println("MockTest :: Function Name :" + abiDefinition.getName());
                functionNames.add(abiDefinition.getName());
            }
            System.out.print("MockTest :" + functionNames);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    static List<AbiDefinition> loadContractDefinition(File absFile)
            throws IOException {
        ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
        AbiDefinition[] abiDefinition = objectMapper.readValue(absFile, AbiDefinition[].class);
        return Arrays.asList(abiDefinition);
    }
}