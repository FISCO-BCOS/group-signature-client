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
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.fisco.bcos.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version none.
 */
@SuppressWarnings("unchecked")
public class TestRingSig extends Contract {
    public static final String BINARY = "608060405234801561001057600080fd5b50604051610af4380380610af48339810180604052810190808051820192919060200180518201929190602001805182019291905050506150056000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550826001908051906020019061009f9291906100d6565b5081600290805190602001906100b69291906100d6565b5080600390805190602001906100cd9291906100d6565b5050505061017b565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061011757805160ff1916838001178555610145565b82800160010185558215610145579182015b82811115610144578251825591602001919060010190610129565b5b5090506101529190610156565b5090565b61017891905b8082111561017457600081600090555060010161015c565b5090565b90565b61096a8061018a6000396000f300608060405260043610610078576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806326d647311461007d57806332af2edb1461017257806340ce7a3b146102025780634e9f4d8214610231578063582b10ab146102c1578063fc735e9914610351575b600080fd5b34801561008957600080fd5b50610170600480360381019080803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290505050610380565b005b34801561017e57600080fd5b506101876103e5565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156101c75780820151818401526020810190506101ac565b50505050905090810190601f1680156101f45780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561020e57600080fd5b50610217610487565b604051808215151515815260200191505060405180910390f35b34801561023d57600080fd5b5061024661049e565b6040518080602001828103825283818151815260200191508051906020019080838360005b8381101561028657808201518184015260208101905061026b565b50505050905090810190601f1680156102b35780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b3480156102cd57600080fd5b506102d6610540565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156103165780820151818401526020810190506102fb565b50505050905090810190601f1680156103435780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561035d57600080fd5b506103666105e2565b604051808215151515815260200191505060405180910390f35b8260019080519060200190610396929190610899565b5081600290805190602001906103ad929190610899565b5080600390805190602001906103c4929190610899565b506000600460006101000a81548160ff021916908315150217905550505050565b606060028054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561047d5780601f106104525761010080835404028352916020019161047d565b820191906000526020600020905b81548152906001019060200180831161046057829003601f168201915b5050505050905090565b6000600460009054906101000a900460ff16905090565b606060038054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156105365780601f1061050b57610100808354040283529160200191610536565b820191906000526020600020905b81548152906001019060200180831161051957829003601f168201915b5050505050905090565b606060018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156105d85780601f106105ad576101008083540402835291602001916105d8565b820191906000526020600020905b8154815290600101906020018083116105bb57829003601f168201915b5050505050905090565b6000806000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16634090ff6d6001600260036040518463ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808060200180602001806020018481038452878181546001816001161561010002031660029004815260200191508054600181600116156101000203166002900480156106e45780601f106106b9576101008083540402835291602001916106e4565b820191906000526020600020905b8154815290600101906020018083116106c757829003601f168201915b50508481038352868181546001816001161561010002031660029004815260200191508054600181600116156101000203166002900480156107675780601f1061073c57610100808354040283529160200191610767565b820191906000526020600020905b81548152906001019060200180831161074a57829003601f168201915b50508481038252858181546001816001161561010002031660029004815260200191508054600181600116156101000203166002900480156107ea5780601f106107bf576101008083540402835291602001916107ea565b820191906000526020600020905b8154815290600101906020018083116107cd57829003601f168201915b50509650505050505050602060405180830381600087803b15801561080e57600080fd5b505af1158015610822573d6000803e3d6000fd5b505050506040513d602081101561083857600080fd5b810190808051906020019092919050505090508015610875576001600460006101000a81548160ff02191690831515021790555060019150610895565b6000600460006101000a81548160ff021916908315150217905550600091505b5090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106108da57805160ff1916838001178555610908565b82800160010185558215610908579182015b828111156109075782518255916020019190600101906108ec565b5b5090506109159190610919565b5090565b61093b91905b8082111561093757600081600090555060010161091f565b5090565b905600a165627a7a723058201e51dcd34d1741907d741471403768fe2db7144edf44844681d6f35d495b9ce70029";

    public static final String ABI = "[{\"constant\":false,\"inputs\":[{\"name\":\"new_sig\",\"type\":\"string\"},{\"name\":\"new_message\",\"type\":\"string\"},{\"name\":\"new_param_info\",\"type\":\"string\"}],\"name\":\"update_sig_data\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"get_message\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"get_verify_result\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"get_param_info\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"get_sig\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"verify\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"name\":\"_sig\",\"type\":\"string\"},{\"name\":\"_message\",\"type\":\"string\"},{\"name\":\"_param_info\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"}]";

    public static final String FUNC_UPDATE_SIG_DATA = "update_sig_data";

    public static final String FUNC_GET_MESSAGE = "get_message";

    public static final String FUNC_GET_VERIFY_RESULT = "get_verify_result";

    public static final String FUNC_GET_PARAM_INFO = "get_param_info";

    public static final String FUNC_GET_SIG = "get_sig";

    public static final String FUNC_VERIFY = "verify";

    @Deprecated
    protected TestRingSig(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected TestRingSig(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected TestRingSig(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected TestRingSig(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<TransactionReceipt> update_sig_data(String new_sig, String new_message, String new_param_info) {
        final Function function = new Function(
                FUNC_UPDATE_SIG_DATA, 
                Arrays.<Type>asList(new org.fisco.bcos.web3j.abi.datatypes.Utf8String(new_sig), 
                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(new_message), 
                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(new_param_info)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public void update_sig_data(String new_sig, String new_message, String new_param_info, TransactionSucCallback callback) {
        final Function function = new Function(
                FUNC_UPDATE_SIG_DATA, 
                Arrays.<Type>asList(new org.fisco.bcos.web3j.abi.datatypes.Utf8String(new_sig), 
                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(new_message), 
                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(new_param_info)), 
                Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String update_sig_dataSeq(String new_sig, String new_message, String new_param_info) {
        final Function function = new Function(
                FUNC_UPDATE_SIG_DATA, 
                Arrays.<Type>asList(new org.fisco.bcos.web3j.abi.datatypes.Utf8String(new_sig), 
                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(new_message), 
                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(new_param_info)), 
                Collections.<TypeReference<?>>emptyList());
        return createTransactionSeq(function);
    }

    public RemoteCall<String> get_message() {
        final Function function = new Function(FUNC_GET_MESSAGE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<Boolean> get_verify_result() {
        final Function function = new Function(FUNC_GET_VERIFY_RESULT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<String> get_param_info() {
        final Function function = new Function(FUNC_GET_PARAM_INFO, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> get_sig() {
        final Function function = new Function(FUNC_GET_SIG, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> verify() {
        final Function function = new Function(
                FUNC_VERIFY, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public void verify(TransactionSucCallback callback) {
        final Function function = new Function(
                FUNC_VERIFY, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String verifySeq() {
        final Function function = new Function(
                FUNC_VERIFY, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return createTransactionSeq(function);
    }

    @Deprecated
    public static TestRingSig load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new TestRingSig(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static TestRingSig load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new TestRingSig(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static TestRingSig load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new TestRingSig(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static TestRingSig load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new TestRingSig(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<TestRingSig> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, String _sig, String _message, String _param_info) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.fisco.bcos.web3j.abi.datatypes.Utf8String(_sig), 
                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(_message), 
                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(_param_info)));
        return deployRemoteCall(TestRingSig.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
    }

    public static RemoteCall<TestRingSig> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, String _sig, String _message, String _param_info) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.fisco.bcos.web3j.abi.datatypes.Utf8String(_sig), 
                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(_message), 
                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(_param_info)));
        return deployRemoteCall(TestRingSig.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<TestRingSig> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String _sig, String _message, String _param_info) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.fisco.bcos.web3j.abi.datatypes.Utf8String(_sig), 
                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(_message), 
                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(_param_info)));
        return deployRemoteCall(TestRingSig.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<TestRingSig> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String _sig, String _message, String _param_info) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.fisco.bcos.web3j.abi.datatypes.Utf8String(_sig), 
                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(_message), 
                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(_param_info)));
        return deployRemoteCall(TestRingSig.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }
}
