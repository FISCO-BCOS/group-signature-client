package org.fisco.bcos.groupsig.contract;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.fisco.bcos.channel.client.TransactionSucCallback;
import org.fisco.bcos.web3j.abi.FunctionEncoder;
import org.fisco.bcos.web3j.abi.TypeReference;
import org.fisco.bcos.web3j.abi.datatypes.Bool;
import org.fisco.bcos.web3j.abi.datatypes.Function;
import org.fisco.bcos.web3j.abi.datatypes.Type;
import org.fisco.bcos.web3j.abi.datatypes.Utf8String;
import org.fisco.bcos.web3j.crypto.Credentials;
import org.fisco.bcos.web3j.protocol.Web3j;
import org.fisco.bcos.web3j.protocol.core.RemoteCall;
import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.fisco.bcos.web3j.tx.Contract;
import org.fisco.bcos.web3j.tx.TransactionManager;
import org.fisco.bcos.web3j.tx.gas.ContractGasProvider;

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
public class TestGroupSig extends Contract {
    public static final String BINARY =
            "608060405234801561001057600080fd5b50604051610d3c380380610d3c833981018060405281019080805182019291906020018051820192919060200180518201929190602001805182019291905050506150046000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555083600190805190602001906100a99291906100f8565b5082600290805190602001906100c09291906100f8565b5081600390805190602001906100d79291906100f8565b5080600490805190602001906100ee9291906100f8565b505050505061019d565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061013957805160ff1916838001178555610167565b82800160010185558215610167579182015b8281111561016657825182559160200191906001019061014b565b5b5090506101749190610178565b5090565b61019a91905b8082111561019657600081600090555060010161017e565b5090565b90565b610b90806101ac6000396000f300608060405260043610610083576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806332af2edb1461008857806340ce7a3b14610118578063582b10ab146101475780636ca8c949146101d75780638bd6b49b14610267578063fc735e99146102f7578063ffbc12f914610326575b600080fd5b34801561009457600080fd5b5061009d610461565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156100dd5780820151818401526020810190506100c2565b50505050905090810190601f16801561010a5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561012457600080fd5b5061012d610503565b604051808215151515815260200191505060405180910390f35b34801561015357600080fd5b5061015c61051a565b6040518080602001828103825283818151815260200191508051906020019080838360005b8381101561019c578082015181840152602081019050610181565b50505050905090810190601f1680156101c95780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b3480156101e357600080fd5b506101ec6105bc565b6040518080602001828103825283818151815260200191508051906020019080838360005b8381101561022c578082015181840152602081019050610211565b50505050905090810190601f1680156102595780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561027357600080fd5b5061027c61065e565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156102bc5780820151818401526020810190506102a1565b50505050905090810190601f1680156102e95780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561030357600080fd5b5061030c610700565b604051808215151515815260200191505060405180910390f35b34801561033257600080fd5b5061045f600480360381019080803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290505050610a42565b005b606060028054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156104f95780601f106104ce576101008083540402835291602001916104f9565b820191906000526020600020905b8154815290600101906020018083116104dc57829003601f168201915b5050505050905090565b6000600560009054906101000a900460ff16905090565b606060018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156105b25780601f10610587576101008083540402835291602001916105b2565b820191906000526020600020905b81548152906001019060200180831161059557829003601f168201915b5050505050905090565b606060048054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156106545780601f1061062957610100808354040283529160200191610654565b820191906000526020600020905b81548152906001019060200180831161063757829003601f168201915b5050505050905090565b606060038054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156106f65780601f106106cb576101008083540402835291602001916106f6565b820191906000526020600020905b8154815290600101906020018083116106d957829003601f168201915b5050505050905090565b6000806000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663b2c8ac1e60016002600360046040518563ffffffff167c010000000000000000000000000000000000000000000000000000000002815260040180806020018060200180602001806020018581038552898181546001816001161561010002031660029004815260200191508054600181600116156101000203166002900480156108085780601f106107dd57610100808354040283529160200191610808565b820191906000526020600020905b8154815290600101906020018083116107eb57829003601f168201915b505085810384528881815460018160011615610100020316600290048152602001915080546001816001161561010002031660029004801561088b5780601f106108605761010080835404028352916020019161088b565b820191906000526020600020905b81548152906001019060200180831161086e57829003601f168201915b505085810383528781815460018160011615610100020316600290048152602001915080546001816001161561010002031660029004801561090e5780601f106108e35761010080835404028352916020019161090e565b820191906000526020600020905b8154815290600101906020018083116108f157829003601f168201915b50508581038252868181546001816001161561010002031660029004815260200191508054600181600116156101000203166002900480156109915780601f1061096657610100808354040283529160200191610991565b820191906000526020600020905b81548152906001019060200180831161097457829003601f168201915b505098505050505050505050602060405180830381600087803b1580156109b757600080fd5b505af11580156109cb573d6000803e3d6000fd5b505050506040513d60208110156109e157600080fd5b810190808051906020019092919050505090508015610a1e576001600560006101000a81548160ff02191690831515021790555060019150610a3e565b6000600560006101000a81548160ff021916908315150217905550600091505b5090565b8360019080519060200190610a58929190610abf565b508260029080519060200190610a6f929190610abf565b508160039080519060200190610a86929190610abf565b508060049080519060200190610a9d929190610abf565b506000600560006101000a81548160ff02191690831515021790555050505050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10610b0057805160ff1916838001178555610b2e565b82800160010185558215610b2e579182015b82811115610b2d578251825591602001919060010190610b12565b5b509050610b3b9190610b3f565b5090565b610b6191905b80821115610b5d576000816000905550600101610b45565b5090565b905600a165627a7a72305820547c08deab690fab88081581476826acdd270b7cf008bf85c112d31086df7ebd0029";

    public static final String ABI =
            "[{\"constant\":true,\"inputs\":[],\"name\":\"get_message\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"get_verify_result\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"get_sig\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"get_pbc_param\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"get_gpk_info\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"verify\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"new_sig\",\"type\":\"string\"},{\"name\":\"new_message\",\"type\":\"string\"},{\"name\":\"new_gpk_info\",\"type\":\"string\"},{\"name\":\"new_pbc_param_info\",\"type\":\"string\"}],\"name\":\"update_sig_data\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"name\":\"_sig\",\"type\":\"string\"},{\"name\":\"_message\",\"type\":\"string\"},{\"name\":\"_gpk_info\",\"type\":\"string\"},{\"name\":\"_pbc_param_info\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"}]";

    public static final String FUNC_GET_MESSAGE = "get_message";

    public static final String FUNC_GET_VERIFY_RESULT = "get_verify_result";

    public static final String FUNC_GET_SIG = "get_sig";

    public static final String FUNC_GET_PBC_PARAM = "get_pbc_param";

    public static final String FUNC_GET_GPK_INFO = "get_gpk_info";

    public static final String FUNC_VERIFY = "verify";

    public static final String FUNC_UPDATE_SIG_DATA = "update_sig_data";

    @Deprecated
    protected TestGroupSig(
            String contractAddress,
            Web3j web3j,
            Credentials credentials,
            BigInteger gasPrice,
            BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected TestGroupSig(
            String contractAddress,
            Web3j web3j,
            Credentials credentials,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected TestGroupSig(
            String contractAddress,
            Web3j web3j,
            TransactionManager transactionManager,
            BigInteger gasPrice,
            BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected TestGroupSig(
            String contractAddress,
            Web3j web3j,
            TransactionManager transactionManager,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<String> get_message() {
        final Function function =
                new Function(
                        FUNC_GET_MESSAGE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<Boolean> get_verify_result() {
        final Function function =
                new Function(
                        FUNC_GET_VERIFY_RESULT,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<String> get_sig() {
        final Function function =
                new Function(
                        FUNC_GET_SIG,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> get_pbc_param() {
        final Function function =
                new Function(
                        FUNC_GET_PBC_PARAM,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> get_gpk_info() {
        final Function function =
                new Function(
                        FUNC_GET_GPK_INFO,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> verify() {
        final Function function =
                new Function(
                        FUNC_VERIFY,
                        Arrays.<Type>asList(),
                        Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public void verify(TransactionSucCallback callback) {
        final Function function =
                new Function(
                        FUNC_VERIFY,
                        Arrays.<Type>asList(),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String verifySeq() {
        final Function function =
                new Function(
                        FUNC_VERIFY,
                        Arrays.<Type>asList(),
                        Collections.<TypeReference<?>>emptyList());
        return createTransactionSeq(function);
    }

    public RemoteCall<TransactionReceipt> update_sig_data(
            String new_sig, String new_message, String new_gpk_info, String new_pbc_param_info) {
        final Function function =
                new Function(
                        FUNC_UPDATE_SIG_DATA,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(new_sig),
                                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(new_message),
                                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(new_gpk_info),
                                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(
                                        new_pbc_param_info)),
                        Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public void update_sig_data(
            String new_sig,
            String new_message,
            String new_gpk_info,
            String new_pbc_param_info,
            TransactionSucCallback callback) {
        final Function function =
                new Function(
                        FUNC_UPDATE_SIG_DATA,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(new_sig),
                                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(new_message),
                                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(new_gpk_info),
                                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(
                                        new_pbc_param_info)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String update_sig_dataSeq(
            String new_sig, String new_message, String new_gpk_info, String new_pbc_param_info) {
        final Function function =
                new Function(
                        FUNC_UPDATE_SIG_DATA,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(new_sig),
                                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(new_message),
                                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(new_gpk_info),
                                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(
                                        new_pbc_param_info)),
                        Collections.<TypeReference<?>>emptyList());
        return createTransactionSeq(function);
    }

    @Deprecated
    public static TestGroupSig load(
            String contractAddress,
            Web3j web3j,
            Credentials credentials,
            BigInteger gasPrice,
            BigInteger gasLimit) {
        return new TestGroupSig(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static TestGroupSig load(
            String contractAddress,
            Web3j web3j,
            TransactionManager transactionManager,
            BigInteger gasPrice,
            BigInteger gasLimit) {
        return new TestGroupSig(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static TestGroupSig load(
            String contractAddress,
            Web3j web3j,
            Credentials credentials,
            ContractGasProvider contractGasProvider) {
        return new TestGroupSig(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static TestGroupSig load(
            String contractAddress,
            Web3j web3j,
            TransactionManager transactionManager,
            ContractGasProvider contractGasProvider) {
        return new TestGroupSig(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<TestGroupSig> deploy(
            Web3j web3j,
            Credentials credentials,
            ContractGasProvider contractGasProvider,
            String _sig,
            String _message,
            String _gpk_info,
            String _pbc_param_info) {
        String encodedConstructor =
                FunctionEncoder.encodeConstructor(
                        Arrays.<Type>asList(
                                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(_sig),
                                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(_message),
                                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(_gpk_info),
                                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(
                                        _pbc_param_info)));
        return deployRemoteCall(
                TestGroupSig.class,
                web3j,
                credentials,
                contractGasProvider,
                BINARY,
                encodedConstructor);
    }

    public static RemoteCall<TestGroupSig> deploy(
            Web3j web3j,
            TransactionManager transactionManager,
            ContractGasProvider contractGasProvider,
            String _sig,
            String _message,
            String _gpk_info,
            String _pbc_param_info) {
        String encodedConstructor =
                FunctionEncoder.encodeConstructor(
                        Arrays.<Type>asList(
                                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(_sig),
                                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(_message),
                                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(_gpk_info),
                                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(
                                        _pbc_param_info)));
        return deployRemoteCall(
                TestGroupSig.class,
                web3j,
                transactionManager,
                contractGasProvider,
                BINARY,
                encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<TestGroupSig> deploy(
            Web3j web3j,
            Credentials credentials,
            BigInteger gasPrice,
            BigInteger gasLimit,
            String _sig,
            String _message,
            String _gpk_info,
            String _pbc_param_info) {
        String encodedConstructor =
                FunctionEncoder.encodeConstructor(
                        Arrays.<Type>asList(
                                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(_sig),
                                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(_message),
                                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(_gpk_info),
                                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(
                                        _pbc_param_info)));
        return deployRemoteCall(
                TestGroupSig.class,
                web3j,
                credentials,
                gasPrice,
                gasLimit,
                BINARY,
                encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<TestGroupSig> deploy(
            Web3j web3j,
            TransactionManager transactionManager,
            BigInteger gasPrice,
            BigInteger gasLimit,
            String _sig,
            String _message,
            String _gpk_info,
            String _pbc_param_info) {
        String encodedConstructor =
                FunctionEncoder.encodeConstructor(
                        Arrays.<Type>asList(
                                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(_sig),
                                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(_message),
                                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(_gpk_info),
                                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(
                                        _pbc_param_info)));
        return deployRemoteCall(
                TestGroupSig.class,
                web3j,
                transactionManager,
                gasPrice,
                gasLimit,
                BINARY,
                encodedConstructor);
    }
}
