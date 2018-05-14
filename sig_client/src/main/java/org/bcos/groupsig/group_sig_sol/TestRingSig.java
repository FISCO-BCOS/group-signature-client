package org.bcos.groupsig.group_sig_sol;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Future;
import org.bcos.channel.client.TransactionSucCallback;
import org.bcos.web3j.abi.FunctionEncoder;
import org.bcos.web3j.abi.TypeReference;
import org.bcos.web3j.abi.datatypes.Function;
import org.bcos.web3j.abi.datatypes.Type;
import org.bcos.web3j.abi.datatypes.Utf8String;
import org.bcos.web3j.crypto.Credentials;
import org.bcos.web3j.protocol.Web3j;
import org.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.bcos.web3j.tx.Contract;
import org.bcos.web3j.tx.TransactionManager;

/**
 * Auto generated code.<br>
 * <strong>Do not modify!</strong><br>
 * Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>, or {@link org.bcos.web3j.codegen.SolidityFunctionWrapperGenerator} to update.
 *
 * <p>Generated with web3j version none.
 */
public final class TestRingSig extends Contract {
    private static final String BINARY = "6060604052341561000f57600080fd5b604051610d3c380380610d3c83398101604052808051820191906020018051820191906020018051820191905050826000908051906020019061005392919061008a565b50816001908051906020019061006a92919061008a565b50806002908051906020019061008192919061008a565b5050505061012f565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106100cb57805160ff19168380011785556100f9565b828001600101855582156100f9579182015b828111156100f85782518255916020019190600101906100dd565b5b509050610106919061010a565b5090565b61012c91905b80821115610128576000816000905550600101610110565b5090565b90565b610bfe8061013e6000396000f30060606040523615610076576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806326d647311461007b57806332af2edb1461015e57806340ce7a3b146101ec5780634e9f4d821461027a578063582b10ab14610308578063fc735e9914610396575b600080fd5b341561008657600080fd5b61015c600480803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509190803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509190803590602001908201803590602001908080601f01602080910402602001604051908101604052809392919081815260200183838082843782019150505050505091905050610424565b005b341561016957600080fd5b6101716104ba565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156101b1578082015181840152602081019050610196565b50505050905090810190601f1680156101de5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34156101f757600080fd5b6101ff610562565b6040518080602001828103825283818151815260200191508051906020019080838360005b8381101561023f578082015181840152602081019050610224565b50505050905090810190601f16801561026c5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b341561028557600080fd5b61028d61060a565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156102cd5780820151818401526020810190506102b2565b50505050905090810190601f1680156102fa5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b341561031357600080fd5b61031b6106b2565b6040518080602001828103825283818151815260200191508051906020019080838360005b8381101561035b578082015181840152602081019050610340565b50505050905090810190601f1680156103885780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34156103a157600080fd5b6103a961075a565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156103e95780820151818401526020810190506103ce565b50505050905090810190601f1680156104165780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b826000908051906020019061043a929190610b19565b508160019080519060200190610451929190610b19565b508060029080519060200190610468929190610b19565b506040805190810160405280600481526020017f6e756c6c00000000000000000000000000000000000000000000000000000000815250600390805190602001906104b4929190610b19565b50505050565b6104c2610b99565b60018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156105585780601f1061052d57610100808354040283529160200191610558565b820191906000526020600020905b81548152906001019060200180831161053b57829003601f168201915b5050505050905090565b61056a610b99565b60038054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156106005780601f106105d557610100808354040283529160200191610600565b820191906000526020600020905b8154815290600101906020018083116105e357829003601f168201915b5050505050905090565b610612610b99565b60028054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156106a85780601f1061067d576101008083540402835291602001916106a8565b820191906000526020600020905b81548152906001019060200180831161068b57829003601f168201915b5050505050905090565b6106ba610b99565b60008054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156107505780601f1061072557610100808354040283529160200191610750565b820191906000526020600020905b81548152906001019060200180831161073357829003601f168201915b5050505050905090565b610762610b99565b61076a610b99565b600061094560008054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156108055780601f106107da57610100808354040283529160200191610805565b820191906000526020600020905b8154815290600101906020018083116107e857829003601f168201915b505050505060018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156108a05780601f10610875576101008083540402835291602001916108a0565b820191906000526020600020905b81548152906001019060200180831161088357829003601f168201915b505050505060028054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561093b5780601f106109105761010080835404028352916020019161093b565b820191906000526020600020905b81548152906001019060200180831161091e57829003601f168201915b5050505050610a9e565b8092508193505050610955610afc565b81141561097b578160039080519060200190610972929190610b19565b50819250610a99565b610983610b0a565b811415610a145760326040518059106109995750595b9080825280602002602001820160405250600390805190602001906109bf929190610b19565b506040805190810160405280601f81526020017f65746863616c6c2052696e67536967206e6f7420696d706c656d656e7465640081525060039080519060200190610a0b929190610b19565b50819250610a99565b6040805190810160405280600181526020017f300000000000000000000000000000000000000000000000000000000000000081525060039080519060200190610a5f929190610b19565b506040805190810160405280600181526020017f300000000000000000000000000000000000000000000000000000000000000081525092505b505090565b610aa6610b99565b6000610ab0610b99565b6000806002604051805910610ac25750595b9080825280602002602001820160405250925062066671915060008060008060008a8c8e8a8a2f9050828194509450505050935093915050565b600080600090508091505090565b60008061270f90508091505090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10610b5a57805160ff1916838001178555610b88565b82800160010185558215610b88579182015b82811115610b87578251825591602001919060010190610b6c565b5b509050610b959190610bad565b5090565b602060405190810160405280600081525090565b610bcf91905b80821115610bcb576000816000905550600101610bb3565b5090565b905600a165627a7a72305820929f43c112421b1f1b88ba03e9d0581b1e48ff2e174d81c0090fa6b7888828c80029";

    public static final String ABI = "[{\"constant\":false,\"inputs\":[{\"name\":\"new_sig\",\"type\":\"string\"},{\"name\":\"new_message\",\"type\":\"string\"},{\"name\":\"new_param_info\",\"type\":\"string\"}],\"name\":\"update_sig_data\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"get_message\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"get_verify_result\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"get_param_info\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"get_sig\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"verify\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"name\":\"_sig\",\"type\":\"string\"},{\"name\":\"_message\",\"type\":\"string\"},{\"name\":\"_param_info\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"}]";

    private TestRingSig(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, Boolean isInitByName) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit, isInitByName);
    }

    private TestRingSig(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, Boolean isInitByName) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit, isInitByName);
    }

    private TestRingSig(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit, false);
    }

    private TestRingSig(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit, false);
    }

    public Future<TransactionReceipt> update_sig_data(Utf8String new_sig, Utf8String new_message, Utf8String new_param_info) {
        Function function = new Function("update_sig_data", Arrays.<Type>asList(new_sig, new_message, new_param_info), Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    public void update_sig_data(Utf8String new_sig, Utf8String new_message, Utf8String new_param_info, TransactionSucCallback callback) {
        Function function = new Function("update_sig_data", Arrays.<Type>asList(new_sig, new_message, new_param_info), Collections.<TypeReference<?>>emptyList());
        executeTransactionAsync(function, callback);
    }

    public Future<Utf8String> get_message() {
        Function function = new Function("get_message", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<Utf8String> get_verify_result() {
        Function function = new Function("get_verify_result", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<Utf8String> get_param_info() {
        Function function = new Function("get_param_info", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<Utf8String> get_sig() {
        Function function = new Function("get_sig", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<TransactionReceipt> verify() {
        Function function = new Function("verify", Arrays.<Type>asList(), Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    public void verify(TransactionSucCallback callback) {
        Function function = new Function("verify", Arrays.<Type>asList(), Collections.<TypeReference<?>>emptyList());
        executeTransactionAsync(function, callback);
    }

    public static Future<TestRingSig> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, BigInteger initialWeiValue, Utf8String _sig, Utf8String _message, Utf8String _param_info) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(_sig, _message, _param_info));
        return deployAsync(TestRingSig.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor, initialWeiValue);
    }

    public static Future<TestRingSig> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, BigInteger initialWeiValue, Utf8String _sig, Utf8String _message, Utf8String _param_info) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(_sig, _message, _param_info));
        return deployAsync(TestRingSig.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor, initialWeiValue);
    }

    public static TestRingSig load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new TestRingSig(contractAddress, web3j, credentials, gasPrice, gasLimit, false);
    }

    public static TestRingSig load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new TestRingSig(contractAddress, web3j, transactionManager, gasPrice, gasLimit, false);
    }

    public static TestRingSig loadByName(String contractName, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new TestRingSig(contractName, web3j, credentials, gasPrice, gasLimit, true);
    }

    public static TestRingSig loadByName(String contractName, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new TestRingSig(contractName, web3j, transactionManager, gasPrice, gasLimit, true);
    }
}
