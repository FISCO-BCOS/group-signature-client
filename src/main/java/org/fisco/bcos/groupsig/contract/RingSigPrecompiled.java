package org.fisco.bcos.groupsig.contract;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bool;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.contract.Contract;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;

@SuppressWarnings("unchecked")
public class RingSigPrecompiled extends Contract {
    public static final String[] BINARY_ARRAY = {};

    public static final String BINARY =
            org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {};

    public static final String SM_BINARY =
            org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {
        "[{\"constant\":true,\"inputs\":[{\"name\":\"signature\",\"type\":\"string\"},{\"name\":\"message\",\"type\":\"string\"},{\"name\":\"paramInfo\",\"type\":\"string\"}],\"name\":\"ringSigVerify\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"},{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"selector\":[1083244397,863885139],\"stateMutability\":\"view\",\"type\":\"function\"}]"
    };

    public static final String ABI = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_RINGSIGVERIFY = "ringSigVerify";

    protected RingSigPrecompiled(String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public static String getABI() {
        return ABI;
    }

    public Tuple2<BigInteger, Boolean> ringSigVerify(
            String signature, String message, String paramInfo) throws ContractException {
        final Function function =
                new Function(
                        FUNC_RINGSIGVERIFY,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(signature),
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(message),
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(paramInfo)),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Int256>() {}, new TypeReference<Bool>() {}));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple2<BigInteger, Boolean>(
                (BigInteger) results.get(0).getValue(), (Boolean) results.get(1).getValue());
    }

    public static RingSigPrecompiled load(
            String contractAddress, Client client, CryptoKeyPair credential) {
        return new RingSigPrecompiled(contractAddress, client, credential);
    }

    public static RingSigPrecompiled deploy(Client client, CryptoKeyPair credential)
            throws ContractException {
        return deploy(
                RingSigPrecompiled.class,
                client,
                credential,
                getBinary(client.getCryptoSuite()),
                getABI(),
                null,
                null);
    }
}
