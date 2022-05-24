package org.fisco.bcos.groupsig.contract;

import java.util.Arrays;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bool;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.contract.Contract;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;

@SuppressWarnings("unchecked")
public class GroupSigPrecompiled extends Contract {
    public static final String[] BINARY_ARRAY = {
        "608060405234801561001057600080fd5b506101e2806100206000396000f3fe608060405234801561001057600080fd5b506004361061002b5760003560e01c8063b2c8ac1e14610030575b600080fd5b61004861003e3660046100ff565b6000949350505050565b604051901515815260200160405180910390f35b634e487b7160e01b600052604160045260246000fd5b600082601f83011261008357600080fd5b813567ffffffffffffffff8082111561009e5761009e61005c565b604051601f8301601f19908116603f011681019082821181831017156100c6576100c661005c565b816040528381528660208588010111156100df57600080fd5b836020870160208301376000602085830101528094505050505092915050565b6000806000806080858703121561011557600080fd5b843567ffffffffffffffff8082111561012d57600080fd5b61013988838901610072565b9550602087013591508082111561014f57600080fd5b61015b88838901610072565b9450604087013591508082111561017157600080fd5b61017d88838901610072565b9350606087013591508082111561019357600080fd5b506101a087828801610072565b9150509295919450925056fea2646970667358221220659321b5f2c6db11b927483d03eaa60a0a809b721ca45d7855b6c57effd4956e64736f6c634300080b0033"
    };

    public static final String BINARY =
            org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {
        "608060405234801561001057600080fd5b506101e2806100206000396000f3fe608060405234801561001057600080fd5b506004361061002b5760003560e01c80639be9e39214610030575b600080fd5b61004861003e3660046100ff565b6000949350505050565b604051901515815260200160405180910390f35b63b95aa35560e01b600052604160045260246000fd5b600082601f83011261008357600080fd5b813567ffffffffffffffff8082111561009e5761009e61005c565b604051601f8301601f19908116603f011681019082821181831017156100c6576100c661005c565b816040528381528660208588010111156100df57600080fd5b836020870160208301376000602085830101528094505050505092915050565b6000806000806080858703121561011557600080fd5b843567ffffffffffffffff8082111561012d57600080fd5b61013988838901610072565b9550602087013591508082111561014f57600080fd5b61015b88838901610072565b9450604087013591508082111561017157600080fd5b61017d88838901610072565b9350606087013591508082111561019357600080fd5b506101a087828801610072565b9150509295919450925056fea26469706673582212200b4870090005b32cc81d759105d78436005b8facce9939f4952474f1801e5fd764736f6c634300080b0033"
    };

    public static final String SM_BINARY =
            org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {
        "[{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"string\",\"name\":\"signature\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"message\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"gpkInfo\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"paramInfo\",\"type\":\"string\"}],\"name\":\"groupSigVerify\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[2999495710,2615796626],\"stateMutability\":\"view\",\"type\":\"function\"}]"
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

    public Boolean groupSigVerify(
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
                        Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
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
