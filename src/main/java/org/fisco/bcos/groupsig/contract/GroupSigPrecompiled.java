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
public class GroupSigPrecompiled extends Contract {
    public static final String[] BINARY_ARRAY = {};

    public static final String BINARY =
            org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {};

    public static final String SM_BINARY =
            org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {
        "[{\"constant\":true,\"inputs\":[{\"name\":\"signature\",\"type\":\"string\"},{\"name\":\"message\",\"type\":\"string\"},{\"name\":\"gpkInfo\",\"type\":\"string\"},{\"name\":\"paramInfo\",\"type\":\"string\"}],\"name\":\"groupSigVerify\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"},{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"selector\":[2999495710,2615796626],\"stateMutability\":\"view\",\"type\":\"function\"}]"
    };

    public static final String ABI = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_GROUPSIGVERIFY = "groupSigVerify";

    protected GroupSigPrecompiled(String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public static String getABI() {
        return ABI;
    }

    public Tuple2<BigInteger, Boolean> groupSigVerify(
            String signature, String message, String gpkInfo, String paramInfo)
            throws ContractException {
        final Function function =
                new Function(
                        FUNC_GROUPSIGVERIFY,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(signature),
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(message),
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(gpkInfo),
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(paramInfo)),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Int256>() {}, new TypeReference<Bool>() {}));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple2<BigInteger, Boolean>(
                (BigInteger) results.get(0).getValue(), (Boolean) results.get(1).getValue());
    }

    public static GroupSigPrecompiled load(
            String contractAddress, Client client, CryptoKeyPair credential) {
        return new GroupSigPrecompiled(contractAddress, client, credential);
    }

    public static GroupSigPrecompiled deploy(Client client, CryptoKeyPair credential)
            throws ContractException {
        return deploy(
                GroupSigPrecompiled.class,
                client,
                credential,
                getBinary(client.getCryptoSuite()),
                getABI(),
                null,
                null);
    }
}
