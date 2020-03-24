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
import org.fisco.bcos.web3j.tuples.generated.Tuple3;
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
public class TestRingSig extends Contract {
    public static String BINARY =
            "60806040526000600460006101000a81548160ff02191690831515021790555034801561002b57600080fd5b50604051610aec380380610aec8339810180604052810190808051820192919060200180518201929190602001805182019291905050506150056000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555082600190805190602001906100ba9291906100f1565b5081600290805190602001906100d19291906100f1565b5080600390805190602001906100e89291906100f1565b50505050610196565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061013257805160ff1916838001178555610160565b82800160010185558215610160579182015b8281111561015f578251825591602001919060010190610144565b5b50905061016d9190610171565b5090565b61019391905b8082111561018f576000816000905550600101610177565b5090565b90565b610947806101a56000396000f300608060405260043610610078576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680631991fcec1461007d578063534b680f146100ac578063c14877941461013c578063dd3ede221461016b578063f429e366146101fb578063f9bd817d1461028b575b600080fd5b34801561008957600080fd5b50610092610380565b604051808215151515815260200191505060405180910390f35b3480156100b857600080fd5b506100c1610397565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156101015780820151818401526020810190506100e6565b50505050905090810190601f16801561012e5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561014857600080fd5b50610151610439565b604051808215151515815260200191505060405180910390f35b34801561017757600080fd5b506101806106cd565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156101c05780820151818401526020810190506101a5565b50505050905090810190601f1680156101ed5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561020757600080fd5b5061021061076f565b6040518080602001828103825283818151815260200191508051906020019080838360005b83811015610250578082015181840152602081019050610235565b50505050905090810190601f16801561027d5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561029757600080fd5b5061037e600480360381019080803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290505050610811565b005b6000600460009054906101000a900460ff16905090565b606060028054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561042f5780601f106104045761010080835404028352916020019161042f565b820191906000526020600020905b81548152906001019060200180831161041257829003601f168201915b5050505050905090565b60008060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16634090ff6d6001600260036040518463ffffffff167c01000000000000000000000000000000000000000000000000000000000281526004018080602001806020018060200184810384528781815460018160011615610100020316600290048152602001915080546001816001161561010002031660029004801561053a5780601f1061050f5761010080835404028352916020019161053a565b820191906000526020600020905b81548152906001019060200180831161051d57829003601f168201915b50508481038352868181546001816001161561010002031660029004815260200191508054600181600116156101000203166002900480156105bd5780601f10610592576101008083540402835291602001916105bd565b820191906000526020600020905b8154815290600101906020018083116105a057829003601f168201915b50508481038252858181546001816001161561010002031660029004815260200191508054600181600116156101000203166002900480156106405780601f1061061557610100808354040283529160200191610640565b820191906000526020600020905b81548152906001019060200180831161062357829003601f168201915b50509650505050505050602060405180830381600087803b15801561066457600080fd5b505af1158015610678573d6000803e3d6000fd5b505050506040513d602081101561068e57600080fd5b8101908080519060200190929190505050600460006101000a81548160ff021916908315150217905550600460009054906101000a900460ff16905090565b606060038054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156107655780601f1061073a57610100808354040283529160200191610765565b820191906000526020600020905b81548152906001019060200180831161074857829003601f168201915b5050505050905090565b606060018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156108075780601f106107dc57610100808354040283529160200191610807565b820191906000526020600020905b8154815290600101906020018083116107ea57829003601f168201915b5050505050905090565b8260019080519060200190610827929190610876565b50816002908051906020019061083e929190610876565b508060039080519060200190610855929190610876565b506000600460006101000a81548160ff021916908315150217905550505050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106108b757805160ff19168380011785556108e5565b828001600101855582156108e5579182015b828111156108e45782518255916020019190600101906108c9565b5b5090506108f291906108f6565b5090565b61091891905b808211156109145760008160009055506001016108fc565b5090565b905600a165627a7a723058205d0a9bdcda732ec1c340398ff27ee2d7828adf99cd48203f0d113e65bc0d16d00029";

    public static final String ABI =
            "[{\"constant\":true,\"inputs\":[],\"name\":\"get_ring_verify_result\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"get_ring_message\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"verify_ring_sig\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"get_ring_param_info\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"get_ring_sig\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"new_sig\",\"type\":\"string\"},{\"name\":\"new_message\",\"type\":\"string\"},{\"name\":\"new_param_info\",\"type\":\"string\"}],\"name\":\"update_ring_sig_data\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"name\":\"_sig\",\"type\":\"string\"},{\"name\":\"_message\",\"type\":\"string\"},{\"name\":\"_param_info\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"}]";

    public static final TransactionDecoder transactionDecoder = new TransactionDecoder(ABI, BINARY);

    public static final String FUNC_GET_RING_VERIFY_RESULT = "get_ring_verify_result";

    public static final String FUNC_GET_RING_MESSAGE = "get_ring_message";

    public static final String FUNC_VERIFY_RING_SIG = "verify_ring_sig";

    public static final String FUNC_GET_RING_PARAM_INFO = "get_ring_param_info";

    public static final String FUNC_GET_RING_SIG = "get_ring_sig";

    public static final String FUNC_UPDATE_RING_SIG_DATA = "update_ring_sig_data";

    @Deprecated
    protected TestRingSig(
            String contractAddress,
            Web3j web3j,
            Credentials credentials,
            BigInteger gasPrice,
            BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected TestRingSig(
            String contractAddress,
            Web3j web3j,
            Credentials credentials,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected TestRingSig(
            String contractAddress,
            Web3j web3j,
            TransactionManager transactionManager,
            BigInteger gasPrice,
            BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected TestRingSig(
            String contractAddress,
            Web3j web3j,
            TransactionManager transactionManager,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static TransactionDecoder getTransactionDecoder() {
        return transactionDecoder;
    }

    public RemoteCall<Boolean> get_ring_verify_result() {
        final Function function =
                new Function(
                        FUNC_GET_RING_VERIFY_RESULT,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<String> get_ring_message() {
        final Function function =
                new Function(
                        FUNC_GET_RING_MESSAGE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> verify_ring_sig() {
        final Function function =
                new Function(
                        FUNC_VERIFY_RING_SIG,
                        Arrays.<Type>asList(),
                        Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public void verify_ring_sig(TransactionSucCallback callback) {
        final Function function =
                new Function(
                        FUNC_VERIFY_RING_SIG,
                        Arrays.<Type>asList(),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String verify_ring_sigSeq() {
        final Function function =
                new Function(
                        FUNC_VERIFY_RING_SIG,
                        Arrays.<Type>asList(),
                        Collections.<TypeReference<?>>emptyList());
        return createTransactionSeq(function);
    }

    public Tuple1<Boolean> getVerify_ring_sigOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_VERIFY_RING_SIG,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        ;
        return new Tuple1<Boolean>((Boolean) results.get(0).getValue());
    }

    public RemoteCall<String> get_ring_param_info() {
        final Function function =
                new Function(
                        FUNC_GET_RING_PARAM_INFO,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> get_ring_sig() {
        final Function function =
                new Function(
                        FUNC_GET_RING_SIG,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> update_ring_sig_data(
            String new_sig, String new_message, String new_param_info) {
        final Function function =
                new Function(
                        FUNC_UPDATE_RING_SIG_DATA,
                        Arrays.<Type>asList(
                                new Utf8String(new_sig),
                                new Utf8String(new_message),
                                new Utf8String(new_param_info)),
                        Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public void update_ring_sig_data(
            String new_sig,
            String new_message,
            String new_param_info,
            TransactionSucCallback callback) {
        final Function function =
                new Function(
                        FUNC_UPDATE_RING_SIG_DATA,
                        Arrays.<Type>asList(
                                new Utf8String(new_sig),
                                new Utf8String(new_message),
                                new Utf8String(new_param_info)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String update_ring_sig_dataSeq(
            String new_sig, String new_message, String new_param_info) {
        final Function function =
                new Function(
                        FUNC_UPDATE_RING_SIG_DATA,
                        Arrays.<Type>asList(
                                new Utf8String(new_sig),
                                new Utf8String(new_message),
                                new Utf8String(new_param_info)),
                        Collections.<TypeReference<?>>emptyList());
        return createTransactionSeq(function);
    }

    public Tuple3<String, String, String> getUpdate_ring_sig_dataInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_UPDATE_RING_SIG_DATA,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Utf8String>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        ;
        return new Tuple3<String, String, String>(
                (String) results.get(0).getValue(),
                (String) results.get(1).getValue(),
                (String) results.get(2).getValue());
    }

    @Deprecated
    public static TestRingSig load(
            String contractAddress,
            Web3j web3j,
            Credentials credentials,
            BigInteger gasPrice,
            BigInteger gasLimit) {
        return new TestRingSig(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static TestRingSig load(
            String contractAddress,
            Web3j web3j,
            TransactionManager transactionManager,
            BigInteger gasPrice,
            BigInteger gasLimit) {
        return new TestRingSig(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static TestRingSig load(
            String contractAddress,
            Web3j web3j,
            Credentials credentials,
            ContractGasProvider contractGasProvider) {
        return new TestRingSig(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static TestRingSig load(
            String contractAddress,
            Web3j web3j,
            TransactionManager transactionManager,
            ContractGasProvider contractGasProvider) {
        return new TestRingSig(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<TestRingSig> deploy(
            Web3j web3j,
            Credentials credentials,
            ContractGasProvider contractGasProvider,
            String _sig,
            String _message,
            String _param_info) {
        String encodedConstructor =
                FunctionEncoder.encodeConstructor(
                        Arrays.<Type>asList(
                                new Utf8String(_sig),
                                new Utf8String(_message),
                                new Utf8String(_param_info)));
        return deployRemoteCall(
                TestRingSig.class,
                web3j,
                credentials,
                contractGasProvider,
                BINARY,
                encodedConstructor);
    }

    public static RemoteCall<TestRingSig> deploy(
            Web3j web3j,
            TransactionManager transactionManager,
            ContractGasProvider contractGasProvider,
            String _sig,
            String _message,
            String _param_info) {
        String encodedConstructor =
                FunctionEncoder.encodeConstructor(
                        Arrays.<Type>asList(
                                new Utf8String(_sig),
                                new Utf8String(_message),
                                new Utf8String(_param_info)));
        return deployRemoteCall(
                TestRingSig.class,
                web3j,
                transactionManager,
                contractGasProvider,
                BINARY,
                encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<TestRingSig> deploy(
            Web3j web3j,
            Credentials credentials,
            BigInteger gasPrice,
            BigInteger gasLimit,
            String _sig,
            String _message,
            String _param_info) {
        String encodedConstructor =
                FunctionEncoder.encodeConstructor(
                        Arrays.<Type>asList(
                                new Utf8String(_sig),
                                new Utf8String(_message),
                                new Utf8String(_param_info)));
        return deployRemoteCall(
                TestRingSig.class,
                web3j,
                credentials,
                gasPrice,
                gasLimit,
                BINARY,
                encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<TestRingSig> deploy(
            Web3j web3j,
            TransactionManager transactionManager,
            BigInteger gasPrice,
            BigInteger gasLimit,
            String _sig,
            String _message,
            String _param_info) {
        String encodedConstructor =
                FunctionEncoder.encodeConstructor(
                        Arrays.<Type>asList(
                                new Utf8String(_sig),
                                new Utf8String(_message),
                                new Utf8String(_param_info)));
        return deployRemoteCall(
                TestRingSig.class,
                web3j,
                transactionManager,
                gasPrice,
                gasLimit,
                BINARY,
                encodedConstructor);
    }
}
