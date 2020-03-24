package org.fisco.bcos.groupsig.contract;

import java.math.BigInteger;
import java.util.Arrays;
import org.fisco.bcos.web3j.abi.TypeReference;
import org.fisco.bcos.web3j.abi.datatypes.Bool;
import org.fisco.bcos.web3j.abi.datatypes.Function;
import org.fisco.bcos.web3j.abi.datatypes.Type;
import org.fisco.bcos.web3j.crypto.Credentials;
import org.fisco.bcos.web3j.protocol.Web3j;
import org.fisco.bcos.web3j.protocol.core.RemoteCall;
import org.fisco.bcos.web3j.tx.Contract;
import org.fisco.bcos.web3j.tx.TransactionManager;
import org.fisco.bcos.web3j.tx.gas.ContractGasProvider;
import org.fisco.bcos.web3j.tx.txdecode.TransactionDecoder;

/**
 * Auto generated code.
 *
 * <p><strong>Do not modify!</strong>
 *
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.fisco.bcos.web3j.codegen.SolidityFunctionWrapperGenerator in the <a
 * href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version none.
 */
@SuppressWarnings("unchecked")
public class RingSigPrecompiled extends Contract {
    public static String BINARY = "";

    public static final String ABI =
            "[{\"constant\":true,\"inputs\":[{\"name\":\"signature\",\"type\":\"string\"},{\"name\":\"message\",\"type\":\"string\"},{\"name\":\"paramInfo\",\"type\":\"string\"}],\"name\":\"ringSigVerify\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"}]";

    public static final TransactionDecoder transactionDecoder = new TransactionDecoder(ABI, BINARY);

    public static final String FUNC_RINGSIGVERIFY = "ringSigVerify";

    @Deprecated
    protected RingSigPrecompiled(
            String contractAddress,
            Web3j web3j,
            Credentials credentials,
            BigInteger gasPrice,
            BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected RingSigPrecompiled(
            String contractAddress,
            Web3j web3j,
            Credentials credentials,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected RingSigPrecompiled(
            String contractAddress,
            Web3j web3j,
            TransactionManager transactionManager,
            BigInteger gasPrice,
            BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected RingSigPrecompiled(
            String contractAddress,
            Web3j web3j,
            TransactionManager transactionManager,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static TransactionDecoder getTransactionDecoder() {
        return transactionDecoder;
    }

    public RemoteCall<Boolean> ringSigVerify(String signature, String message, String paramInfo) {
        final Function function =
                new Function(
                        FUNC_RINGSIGVERIFY,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(signature),
                                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(message),
                                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(paramInfo)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    @Deprecated
    public static RingSigPrecompiled load(
            String contractAddress,
            Web3j web3j,
            Credentials credentials,
            BigInteger gasPrice,
            BigInteger gasLimit) {
        return new RingSigPrecompiled(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static RingSigPrecompiled load(
            String contractAddress,
            Web3j web3j,
            TransactionManager transactionManager,
            BigInteger gasPrice,
            BigInteger gasLimit) {
        return new RingSigPrecompiled(
                contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static RingSigPrecompiled load(
            String contractAddress,
            Web3j web3j,
            Credentials credentials,
            ContractGasProvider contractGasProvider) {
        return new RingSigPrecompiled(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static RingSigPrecompiled load(
            String contractAddress,
            Web3j web3j,
            TransactionManager transactionManager,
            ContractGasProvider contractGasProvider) {
        return new RingSigPrecompiled(
                contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<RingSigPrecompiled> deploy(
            Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(
                RingSigPrecompiled.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<RingSigPrecompiled> deploy(
            Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(
                RingSigPrecompiled.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<RingSigPrecompiled> deploy(
            Web3j web3j,
            TransactionManager transactionManager,
            ContractGasProvider contractGasProvider) {
        return deployRemoteCall(
                RingSigPrecompiled.class,
                web3j,
                transactionManager,
                contractGasProvider,
                BINARY,
                "");
    }

    @Deprecated
    public static RemoteCall<RingSigPrecompiled> deploy(
            Web3j web3j,
            TransactionManager transactionManager,
            BigInteger gasPrice,
            BigInteger gasLimit) {
        return deployRemoteCall(
                RingSigPrecompiled.class,
                web3j,
                transactionManager,
                gasPrice,
                gasLimit,
                BINARY,
                "");
    }
}
