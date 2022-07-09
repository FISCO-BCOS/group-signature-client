package org.fisco.bcos.groupsig.contract;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.abi.FunctionEncoder;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bool;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple3;
import org.fisco.bcos.sdk.v3.contract.Contract;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;

@SuppressWarnings("unchecked")
public class TestRingSig extends Contract {
    public static final String[] BINARY_ARRAY = {
        "60806040526000600460006101000a81548160ff02191690831515021790555034801561002b57600080fd5b50604051610b03380380610b038339810180604052810190808051820192919060200180518201929190602001805182019291905050506150056000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555082600190805190602001906100ba9291906100f1565b5081600290805190602001906100d19291906100f1565b5080600390805190602001906100e89291906100f1565b50505050610196565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061013257805160ff1916838001178555610160565b82800160010185558215610160579182015b8281111561015f578251825591602001919060010190610144565b5b50905061016d9190610171565b5090565b61019391905b8082111561018f576000816000905550600101610177565b5090565b90565b61095e806101a56000396000f300608060405260043610610078576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680631991fcec1461007d578063534b680f146100ac578063c14877941461013c578063dd3ede221461016b578063f429e366146101fb578063f9bd817d1461028b575b600080fd5b34801561008957600080fd5b50610092610380565b604051808215151515815260200191505060405180910390f35b3480156100b857600080fd5b506100c1610397565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156101015780820151818401526020810190506100e6565b50505050905090810190601f16801561012e5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561014857600080fd5b50610151610439565b604051808215151515815260200191505060405180910390f35b34801561017757600080fd5b506101806106e4565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156101c05780820151818401526020810190506101a5565b50505050905090810190601f1680156101ed5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561020757600080fd5b50610210610786565b6040518080602001828103825283818151815260200191508051906020019080838360005b83811015610250578082015181840152602081019050610235565b50505050905090810190601f16801561027d5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561029757600080fd5b5061037e600480360381019080803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290505050610828565b005b6000600460009054906101000a900460ff16905090565b606060028054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561042f5780601f106104045761010080835404028352916020019161042f565b820191906000526020600020905b81548152906001019060200180831161041257829003601f168201915b5050505050905090565b600080600090506000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16634090ff6d6001600260036040518463ffffffff167c01000000000000000000000000000000000000000000000000000000000281526004018080602001806020018060200184810384528781815460018160011615610100020316600290048152602001915080546001816001161561010002031660029004801561053f5780601f106105145761010080835404028352916020019161053f565b820191906000526020600020905b81548152906001019060200180831161052257829003601f168201915b50508481038352868181546001816001161561010002031660029004815260200191508054600181600116156101000203166002900480156105c25780601f10610597576101008083540402835291602001916105c2565b820191906000526020600020905b8154815290600101906020018083116105a557829003601f168201915b50508481038252858181546001816001161561010002031660029004815260200191508054600181600116156101000203166002900480156106455780601f1061061a57610100808354040283529160200191610645565b820191906000526020600020905b81548152906001019060200180831161062857829003601f168201915b505096505050505050506040805180830381600087803b15801561066857600080fd5b505af115801561067c573d6000803e3d6000fd5b505050506040513d604081101561069257600080fd5b810190808051906020019092919080519060200190929190505050600460008291906101000a81548160ff0219169083151502179055508192505050600460009054906101000a900460ff1691505090565b606060038054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561077c5780601f106107515761010080835404028352916020019161077c565b820191906000526020600020905b81548152906001019060200180831161075f57829003601f168201915b5050505050905090565b606060018054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561081e5780601f106107f35761010080835404028352916020019161081e565b820191906000526020600020905b81548152906001019060200180831161080157829003601f168201915b5050505050905090565b826001908051906020019061083e92919061088d565b50816002908051906020019061085592919061088d565b50806003908051906020019061086c92919061088d565b506000600460006101000a81548160ff021916908315150217905550505050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106108ce57805160ff19168380011785556108fc565b828001600101855582156108fc579182015b828111156108fb5782518255916020019190600101906108e0565b5b509050610909919061090d565b5090565b61092f91905b8082111561092b576000816000905550600101610913565b5090565b905600a165627a7a72305820a2748fb909859d650fa2273d7940b021357f6c02f953067f2a42c2cae416177d0029"
    };

    public static final String BINARY =
            org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {
        "60806040526000600460006101000a81548160ff02191690831515021790555034801561002b57600080fd5b50604051610b03380380610b038339810180604052810190808051820192919060200180518201929190602001805182019291905050506150056000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555082600190805190602001906100ba9291906100f1565b5081600290805190602001906100d19291906100f1565b5080600390805190602001906100e89291906100f1565b50505050610196565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061013257805160ff1916838001178555610160565b82800160010185558215610160579182015b8281111561015f578251825591602001919060010190610144565b5b50905061016d9190610171565b5090565b61019391905b8082111561018f576000816000905550600101610177565b5090565b90565b61095e806101a56000396000f300608060405260043610610078576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680638119c37c1461007d5780638895a21c146101725780639ee1ba7f14610202578063a103f0c014610231578063ba5a5d38146102c1578063cb75874b146102f0575b600080fd5b34801561008957600080fd5b50610170600480360381019080803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290505050610380565b005b34801561017e57600080fd5b506101876103e5565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156101c75780820151818401526020810190506101ac565b50505050905090810190601f1680156101f45780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561020e57600080fd5b50610217610487565b604051808215151515815260200191505060405180910390f35b34801561023d57600080fd5b5061024661049e565b6040518080602001828103825283818151815260200191508051906020019080838360005b8381101561028657808201518184015260208101905061026b565b50505050905090810190601f1680156102b35780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b3480156102cd57600080fd5b506102d6610540565b604051808215151515815260200191505060405180910390f35b3480156102fc57600080fd5b506103056107eb565b6040518080602001828103825283818151815260200191508051906020019080838360005b8381101561034557808201518184015260208101905061032a565b50505050905090810190601f1680156103725780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b826001908051906020019061039692919061088d565b5081600290805190602001906103ad92919061088d565b5080600390805190602001906103c492919061088d565b506000600460006101000a81548160ff021916908315150217905550505050565b606060038054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561047d5780601f106104525761010080835404028352916020019161047d565b820191906000526020600020905b81548152906001019060200180831161046057829003601f168201915b5050505050905090565b6000600460009054906101000a900460ff16905090565b606060028054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156105365780601f1061050b57610100808354040283529160200191610536565b820191906000526020600020905b81548152906001019060200180831161051957829003601f168201915b5050505050905090565b600080600090506000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663337dd7536001600260036040518463ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808060200180602001806020018481038452878181546001816001161561010002031660029004815260200191508054600181600116156101000203166002900480156106465780601f1061061b57610100808354040283529160200191610646565b820191906000526020600020905b81548152906001019060200180831161062957829003601f168201915b50508481038352868181546001816001161561010002031660029004815260200191508054600181600116156101000203166002900480156106c95780601f1061069e576101008083540402835291602001916106c9565b820191906000526020600020905b8154815290600101906020018083116106ac57829003601f168201915b505084810382528581815460018160011615610100020316600290048152602001915080546001816001161561010002031660029004801561074c5780601f106107215761010080835404028352916020019161074c565b820191906000526020600020905b81548152906001019060200180831161072f57829003601f168201915b505096505050505050506040805180830381600087803b15801561076f57600080fd5b505af1158015610783573d6000803e3d6000fd5b505050506040513d604081101561079957600080fd5b810190808051906020019092919080519060200190929190505050600460008291906101000a81548160ff0219169083151502179055508192505050600460009054906101000a900460ff1691505090565b606060018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156108835780601f1061085857610100808354040283529160200191610883565b820191906000526020600020905b81548152906001019060200180831161086657829003601f168201915b5050505050905090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106108ce57805160ff19168380011785556108fc565b828001600101855582156108fc579182015b828111156108fb5782518255916020019190600101906108e0565b5b509050610909919061090d565b5090565b61092f91905b8082111561092b576000816000905550600101610913565b5090565b905600a165627a7a723058200f2f3a0aac3596388828b414e2a09634aa06929c2d9a392d492595ee313ff3b40029"
    };

    public static final String SM_BINARY =
            org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {
        "[{\"conflictFields\":[{\"kind\":4,\"value\":[4]}],\"constant\":true,\"inputs\":[],\"name\":\"get_ring_verify_result\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"selector\":[428997868,2665593471],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[2]}],\"constant\":true,\"inputs\":[],\"name\":\"get_ring_message\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"selector\":[1397450767,2701390016],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0}],\"constant\":false,\"inputs\":[],\"name\":\"verify_ring_sig\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"selector\":[3242751892,3126484280],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[3]}],\"constant\":true,\"inputs\":[],\"name\":\"get_ring_param_info\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"selector\":[3711884834,2291507740],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[1]}],\"constant\":true,\"inputs\":[],\"name\":\"get_ring_sig\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"selector\":[4096385894,3413477195],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[1]},{\"kind\":4,\"value\":[2]},{\"kind\":4,\"value\":[3]},{\"kind\":4,\"value\":[4]}],\"constant\":false,\"inputs\":[{\"name\":\"new_sig\",\"type\":\"string\"},{\"name\":\"new_message\",\"type\":\"string\"},{\"name\":\"new_param_info\",\"type\":\"string\"}],\"name\":\"update_ring_sig_data\",\"outputs\":[],\"payable\":false,\"selector\":[4189946237,2165949308],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"name\":\"_sig\",\"type\":\"string\"},{\"name\":\"_message\",\"type\":\"string\"},{\"name\":\"_param_info\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"}]"
    };

    public static final String ABI = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_GET_RING_VERIFY_RESULT = "get_ring_verify_result";

    public static final String FUNC_GET_RING_MESSAGE = "get_ring_message";

    public static final String FUNC_VERIFY_RING_SIG = "verify_ring_sig";

    public static final String FUNC_GET_RING_PARAM_INFO = "get_ring_param_info";

    public static final String FUNC_GET_RING_SIG = "get_ring_sig";

    public static final String FUNC_UPDATE_RING_SIG_DATA = "update_ring_sig_data";

    protected TestRingSig(String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public static String getABI() {
        return ABI;
    }

    public Boolean get_ring_verify_result() throws ContractException {
        final Function function =
                new Function(
                        FUNC_GET_RING_VERIFY_RESULT,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public String get_ring_message() throws ContractException {
        final Function function =
                new Function(
                        FUNC_GET_RING_MESSAGE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public TransactionReceipt verify_ring_sig() {
        final Function function =
                new Function(
                        FUNC_VERIFY_RING_SIG,
                        Arrays.<Type>asList(),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return executeTransaction(function);
    }

    public String verify_ring_sig(TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_VERIFY_RING_SIG,
                        Arrays.<Type>asList(),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForVerify_ring_sig() {
        final Function function =
                new Function(
                        FUNC_VERIFY_RING_SIG,
                        Arrays.<Type>asList(),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return createSignedTransaction(function);
    }

    public Tuple1<Boolean> getVerify_ring_sigOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_VERIFY_RING_SIG,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<Boolean>((Boolean) results.get(0).getValue());
    }

    public String get_ring_param_info() throws ContractException {
        final Function function =
                new Function(
                        FUNC_GET_RING_PARAM_INFO,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public String get_ring_sig() throws ContractException {
        final Function function =
                new Function(
                        FUNC_GET_RING_SIG,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public TransactionReceipt update_ring_sig_data(
            String new_sig, String new_message, String new_param_info) {
        final Function function =
                new Function(
                        FUNC_UPDATE_RING_SIG_DATA,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(new_sig),
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(new_message),
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(
                                        new_param_info)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return executeTransaction(function);
    }

    public String update_ring_sig_data(
            String new_sig,
            String new_message,
            String new_param_info,
            TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_UPDATE_RING_SIG_DATA,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(new_sig),
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(new_message),
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(
                                        new_param_info)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForUpdate_ring_sig_data(
            String new_sig, String new_message, String new_param_info) {
        final Function function =
                new Function(
                        FUNC_UPDATE_RING_SIG_DATA,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(new_sig),
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(new_message),
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(
                                        new_param_info)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return createSignedTransaction(function);
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
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple3<String, String, String>(
                (String) results.get(0).getValue(),
                (String) results.get(1).getValue(),
                (String) results.get(2).getValue());
    }

    public static TestRingSig load(
            String contractAddress, Client client, CryptoKeyPair credential) {
        return new TestRingSig(contractAddress, client, credential);
    }

    public static TestRingSig deploy(
            Client client,
            CryptoKeyPair credential,
            String _sig,
            String _message,
            String _param_info)
            throws ContractException {
        byte[] encodedConstructor =
                FunctionEncoder.encodeConstructor(
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(_sig),
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(_message),
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(_param_info)));
        return deploy(
                TestRingSig.class,
                client,
                credential,
                getBinary(client.getCryptoSuite()),
                getABI(),
                encodedConstructor,
                null);
    }
}
