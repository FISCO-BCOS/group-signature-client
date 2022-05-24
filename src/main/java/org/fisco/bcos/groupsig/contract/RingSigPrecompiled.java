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
public class RingSigPrecompiled extends Contract {
    public static final String[] BINARY_ARRAY = {
        "608060405234801561001057600080fd5b506101bc806100206000396000f3fe608060405234801561001057600080fd5b506004361061002b5760003560e01c80634090ff6d14610030575b600080fd5b61004761003e3660046100fe565b60009392505050565b604051901515815260200160405180910390f35b634e487b7160e01b600052604160045260246000fd5b600082601f83011261008257600080fd5b813567ffffffffffffffff8082111561009d5761009d61005b565b604051601f8301601f19908116603f011681019082821181831017156100c5576100c561005b565b816040528381528660208588010111156100de57600080fd5b836020870160208301376000602085830101528094505050505092915050565b60008060006060848603121561011357600080fd5b833567ffffffffffffffff8082111561012b57600080fd5b61013787838801610071565b9450602086013591508082111561014d57600080fd5b61015987838801610071565b9350604086013591508082111561016f57600080fd5b5061017c86828701610071565b915050925092509256fea2646970667358221220a9a746bedeb23fbf62c5ce995a27ac2249d0783bd77c4fb18cdca8c640948d3264736f6c634300080b0033"
    };

    public static final String BINARY =
            org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {
        "608060405234801561001057600080fd5b506101bc806100206000396000f3fe608060405234801561001057600080fd5b506004361061002b5760003560e01c8063337dd75314610030575b600080fd5b61004761003e3660046100fe565b60009392505050565b604051901515815260200160405180910390f35b63b95aa35560e01b600052604160045260246000fd5b600082601f83011261008257600080fd5b813567ffffffffffffffff8082111561009d5761009d61005b565b604051601f8301601f19908116603f011681019082821181831017156100c5576100c561005b565b816040528381528660208588010111156100de57600080fd5b836020870160208301376000602085830101528094505050505092915050565b60008060006060848603121561011357600080fd5b833567ffffffffffffffff8082111561012b57600080fd5b61013787838801610071565b9450602086013591508082111561014d57600080fd5b61015987838801610071565b9350604086013591508082111561016f57600080fd5b5061017c86828701610071565b915050925092509256fea264697066735822122073b106d3bf20a81c3dbf6dc9f3271003c26bb3fd8ceabe82d70b15355bf7689b64736f6c634300080b0033"
    };

    public static final String SM_BINARY =
            org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {
        "[{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"string\",\"name\":\"signature\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"message\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"paramInfo\",\"type\":\"string\"}],\"name\":\"ringSigVerify\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[1083244397,863885139],\"stateMutability\":\"view\",\"type\":\"function\"}]"
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

    public Boolean ringSigVerify(String signature, String message, String paramInfo)
            throws ContractException {
        final Function function =
                new Function(
                        FUNC_RINGSIGVERIFY,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(signature),
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(message),
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(paramInfo)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
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
