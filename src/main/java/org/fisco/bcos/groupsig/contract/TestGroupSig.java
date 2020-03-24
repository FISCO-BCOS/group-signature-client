package org.fisco.bcos.groupsig.contract;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.channel.client.TransactionSucCallback;
import org.fisco.bcos.web3j.abi.FunctionEncoder;
import org.fisco.bcos.web3j.abi.FunctionReturnDecoder;
import org.fisco.bcos.web3j.abi.TypeReference;
import org.fisco.bcos.web3j.abi.datatypes.Bool;
import org.fisco.bcos.web3j.abi.datatypes.Function;
import org.fisco.bcos.web3j.abi.datatypes.Type;
import org.fisco.bcos.web3j.abi.datatypes.Utf8String;
import org.fisco.bcos.web3j.crypto.Credentials;
import org.fisco.bcos.web3j.protocol.Web3j;
import org.fisco.bcos.web3j.protocol.core.RemoteCall;
import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.fisco.bcos.web3j.tuples.generated.Tuple1;
import org.fisco.bcos.web3j.tuples.generated.Tuple4;
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
public class TestGroupSig extends Contract {
    public static String BINARY =
            "60806040526000600560006101000a81548160ff02191690831515021790555034801561002b57600080fd5b50604051610d34380380610d34833981018060405281019080805182019291906020018051820192919060200180518201929190602001805182019291905050506150046000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555083600190805190602001906100c4929190610113565b5082600290805190602001906100db929190610113565b5081600390805190602001906100f2929190610113565b508060049080519060200190610109929190610113565b50505050506101b8565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061015457805160ff1916838001178555610182565b82800160010185558215610182579182015b82811115610181578251825591602001919060010190610166565b5b50905061018f9190610193565b5090565b6101b591905b808211156101b1576000816000905550600101610199565b5090565b90565b610b6d806101c76000396000f300608060405260043610610083576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680630dbdbcc11461008857806362f557d1146101185780637c49b25b146101475780639616440f146101d75780639ec3eee314610267578063e53d6c00146102f7578063eb36769914610432575b600080fd5b34801561009457600080fd5b5061009d610461565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156100dd5780820151818401526020810190506100c2565b50505050905090810190601f16801561010a5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561012457600080fd5b5061012d610503565b604051808215151515815260200191505060405180910390f35b34801561015357600080fd5b5061015c610822565b6040518080602001828103825283818151815260200191508051906020019080838360005b8381101561019c578082015181840152602081019050610181565b50505050905090810190601f1680156101c95780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b3480156101e357600080fd5b506101ec6108c4565b6040518080602001828103825283818151815260200191508051906020019080838360005b8381101561022c578082015181840152602081019050610211565b50505050905090810190601f1680156102595780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561027357600080fd5b5061027c610966565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156102bc5780820151818401526020810190506102a1565b50505050905090810190601f1680156102e95780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561030357600080fd5b50610430600480360381019080803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290505050610a08565b005b34801561043e57600080fd5b50610447610a85565b604051808215151515815260200191505060405180910390f35b606060048054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156104f95780601f106104ce576101008083540402835291602001916104f9565b820191906000526020600020905b8154815290600101906020018083116104dc57829003601f168201915b5050505050905090565b60008060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663b2c8ac1e60016002600360046040518563ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808060200180602001806020018060200185810385528981815460018160011615610100020316600290048152602001915080546001816001161561010002031660029004801561060a5780601f106105df5761010080835404028352916020019161060a565b820191906000526020600020905b8154815290600101906020018083116105ed57829003601f168201915b505085810384528881815460018160011615610100020316600290048152602001915080546001816001161561010002031660029004801561068d5780601f106106625761010080835404028352916020019161068d565b820191906000526020600020905b81548152906001019060200180831161067057829003601f168201915b50508581038352878181546001816001161561010002031660029004815260200191508054600181600116156101000203166002900480156107105780601f106106e557610100808354040283529160200191610710565b820191906000526020600020905b8154815290600101906020018083116106f357829003601f168201915b50508581038252868181546001816001161561010002031660029004815260200191508054600181600116156101000203166002900480156107935780601f1061076857610100808354040283529160200191610793565b820191906000526020600020905b81548152906001019060200180831161077657829003601f168201915b505098505050505050505050602060405180830381600087803b1580156107b957600080fd5b505af11580156107cd573d6000803e3d6000fd5b505050506040513d60208110156107e357600080fd5b8101908080519060200190929190505050600560006101000a81548160ff021916908315150217905550600560009054906101000a900460ff16905090565b606060038054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156108ba5780601f1061088f576101008083540402835291602001916108ba565b820191906000526020600020905b81548152906001019060200180831161089d57829003601f168201915b5050505050905090565b606060018054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561095c5780601f106109315761010080835404028352916020019161095c565b820191906000526020600020905b81548152906001019060200180831161093f57829003601f168201915b5050505050905090565b606060028054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156109fe5780601f106109d3576101008083540402835291602001916109fe565b820191906000526020600020905b8154815290600101906020018083116109e157829003601f168201915b5050505050905090565b8360019080519060200190610a1e929190610a9c565b508260029080519060200190610a35929190610a9c565b508160039080519060200190610a4c929190610a9c565b508060049080519060200190610a63929190610a9c565b506000600560006101000a81548160ff02191690831515021790555050505050565b6000600560009054906101000a900460ff16905090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10610add57805160ff1916838001178555610b0b565b82800160010185558215610b0b579182015b82811115610b0a578251825591602001919060010190610aef565b5b509050610b189190610b1c565b5090565b610b3e91905b80821115610b3a576000816000905550600101610b22565b5090565b905600a165627a7a72305820ce234f408cf3e96ad39fef5c436c820fd3dbdd4be317700cbab185e185ef129e0029";

    public static final String ABI =
            "[{\"constant\":true,\"inputs\":[],\"name\":\"get_group_pbc_param\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"verify_group_sig\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"get_group_gpk_info\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"get_group_sig\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"get_group_message\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"new_sig\",\"type\":\"string\"},{\"name\":\"new_message\",\"type\":\"string\"},{\"name\":\"new_gpk_info\",\"type\":\"string\"},{\"name\":\"new_pbc_param_info\",\"type\":\"string\"}],\"name\":\"update_group_sig_data\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"get_group_verify_result\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"name\":\"_sig\",\"type\":\"string\"},{\"name\":\"_message\",\"type\":\"string\"},{\"name\":\"_gpk_info\",\"type\":\"string\"},{\"name\":\"_pbc_param_info\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"}]";

    public static final TransactionDecoder transactionDecoder = new TransactionDecoder(ABI, BINARY);

    public static final String FUNC_GET_GROUP_PBC_PARAM = "get_group_pbc_param";

    public static final String FUNC_VERIFY_GROUP_SIG = "verify_group_sig";

    public static final String FUNC_GET_GROUP_GPK_INFO = "get_group_gpk_info";

    public static final String FUNC_GET_GROUP_SIG = "get_group_sig";

    public static final String FUNC_GET_GROUP_MESSAGE = "get_group_message";

    public static final String FUNC_UPDATE_GROUP_SIG_DATA = "update_group_sig_data";

    public static final String FUNC_GET_GROUP_VERIFY_RESULT = "get_group_verify_result";

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

    public static TransactionDecoder getTransactionDecoder() {
        return transactionDecoder;
    }

    public RemoteCall<String> get_group_pbc_param() {
        final Function function =
                new Function(
                        FUNC_GET_GROUP_PBC_PARAM,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> verify_group_sig() {
        final Function function =
                new Function(
                        FUNC_VERIFY_GROUP_SIG,
                        Arrays.<Type>asList(),
                        Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public void verify_group_sig(TransactionSucCallback callback) {
        final Function function =
                new Function(
                        FUNC_VERIFY_GROUP_SIG,
                        Arrays.<Type>asList(),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String verify_group_sigSeq() {
        final Function function =
                new Function(
                        FUNC_VERIFY_GROUP_SIG,
                        Arrays.<Type>asList(),
                        Collections.<TypeReference<?>>emptyList());
        return createTransactionSeq(function);
    }

    public Tuple1<Boolean> getVerify_group_sigOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_VERIFY_GROUP_SIG,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        ;
        return new Tuple1<Boolean>((Boolean) results.get(0).getValue());
    }

    public RemoteCall<String> get_group_gpk_info() {
        final Function function =
                new Function(
                        FUNC_GET_GROUP_GPK_INFO,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> get_group_sig() {
        final Function function =
                new Function(
                        FUNC_GET_GROUP_SIG,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> get_group_message() {
        final Function function =
                new Function(
                        FUNC_GET_GROUP_MESSAGE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> update_group_sig_data(
            String new_sig, String new_message, String new_gpk_info, String new_pbc_param_info) {
        final Function function =
                new Function(
                        FUNC_UPDATE_GROUP_SIG_DATA,
                        Arrays.<Type>asList(
                                new Utf8String(new_sig),
                                new Utf8String(new_message),
                                new Utf8String(new_gpk_info),
                                new Utf8String(new_pbc_param_info)),
                        Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public void update_group_sig_data(
            String new_sig,
            String new_message,
            String new_gpk_info,
            String new_pbc_param_info,
            TransactionSucCallback callback) {
        final Function function =
                new Function(
                        FUNC_UPDATE_GROUP_SIG_DATA,
                        Arrays.<Type>asList(
                                new Utf8String(new_sig),
                                new Utf8String(new_message),
                                new Utf8String(new_gpk_info),
                                new Utf8String(new_pbc_param_info)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String update_group_sig_dataSeq(
            String new_sig, String new_message, String new_gpk_info, String new_pbc_param_info) {
        final Function function =
                new Function(
                        FUNC_UPDATE_GROUP_SIG_DATA,
                        Arrays.<Type>asList(
                                new Utf8String(new_sig),
                                new Utf8String(new_message),
                                new Utf8String(new_gpk_info),
                                new Utf8String(new_pbc_param_info)),
                        Collections.<TypeReference<?>>emptyList());
        return createTransactionSeq(function);
    }

    public Tuple4<String, String, String, String> getUpdate_group_sig_dataInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_UPDATE_GROUP_SIG_DATA,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Utf8String>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        ;
        return new Tuple4<String, String, String, String>(
                (String) results.get(0).getValue(),
                (String) results.get(1).getValue(),
                (String) results.get(2).getValue(),
                (String) results.get(3).getValue());
    }

    public RemoteCall<Boolean> get_group_verify_result() {
        final Function function =
                new Function(
                        FUNC_GET_GROUP_VERIFY_RESULT,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
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
                                new Utf8String(_sig),
                                new Utf8String(_message),
                                new Utf8String(_gpk_info),
                                new Utf8String(_pbc_param_info)));
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
                                new Utf8String(_sig),
                                new Utf8String(_message),
                                new Utf8String(_gpk_info),
                                new Utf8String(_pbc_param_info)));
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
                                new Utf8String(_sig),
                                new Utf8String(_message),
                                new Utf8String(_gpk_info),
                                new Utf8String(_pbc_param_info)));
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
                                new Utf8String(_sig),
                                new Utf8String(_message),
                                new Utf8String(_gpk_info),
                                new Utf8String(_pbc_param_info)));
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
