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
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple4;
import org.fisco.bcos.sdk.v3.contract.Contract;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;

@SuppressWarnings("unchecked")
public class TestGroupSig extends Contract {
    public static final String[] BINARY_ARRAY = {
        "60806040526000600560006101000a81548160ff02191690831515021790555034801561002b57600080fd5b50604051610d4b380380610d4b833981018060405281019080805182019291906020018051820192919060200180518201929190602001805182019291905050506150046000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555083600190805190602001906100c4929190610113565b5082600290805190602001906100db929190610113565b5081600390805190602001906100f2929190610113565b508060049080519060200190610109929190610113565b50505050506101b8565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061015457805160ff1916838001178555610182565b82800160010185558215610182579182015b82811115610181578251825591602001919060010190610166565b5b50905061018f9190610193565b5090565b6101b591905b808211156101b1576000816000905550600101610199565b5090565b90565b610b84806101c76000396000f300608060405260043610610083576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680630dbdbcc11461008857806362f557d1146101185780637c49b25b146101475780639616440f146101d75780639ec3eee314610267578063e53d6c00146102f7578063eb36769914610432575b600080fd5b34801561009457600080fd5b5061009d610461565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156100dd5780820151818401526020810190506100c2565b50505050905090810190601f16801561010a5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561012457600080fd5b5061012d610503565b604051808215151515815260200191505060405180910390f35b34801561015357600080fd5b5061015c610839565b6040518080602001828103825283818151815260200191508051906020019080838360005b8381101561019c578082015181840152602081019050610181565b50505050905090810190601f1680156101c95780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b3480156101e357600080fd5b506101ec6108db565b6040518080602001828103825283818151815260200191508051906020019080838360005b8381101561022c578082015181840152602081019050610211565b50505050905090810190601f1680156102595780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561027357600080fd5b5061027c61097d565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156102bc5780820151818401526020810190506102a1565b50505050905090810190601f1680156102e95780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561030357600080fd5b50610430600480360381019080803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290505050610a1f565b005b34801561043e57600080fd5b50610447610a9c565b604051808215151515815260200191505060405180910390f35b606060048054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156104f95780601f106104ce576101008083540402835291602001916104f9565b820191906000526020600020905b8154815290600101906020018083116104dc57829003601f168201915b5050505050905090565b600080600090506000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663b2c8ac1e60016002600360046040518563ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808060200180602001806020018060200185810385528981815460018160011615610100020316600290048152602001915080546001816001161561010002031660029004801561060f5780601f106105e45761010080835404028352916020019161060f565b820191906000526020600020905b8154815290600101906020018083116105f257829003601f168201915b50508581038452888181546001816001161561010002031660029004815260200191508054600181600116156101000203166002900480156106925780601f1061066757610100808354040283529160200191610692565b820191906000526020600020905b81548152906001019060200180831161067557829003601f168201915b50508581038352878181546001816001161561010002031660029004815260200191508054600181600116156101000203166002900480156107155780601f106106ea57610100808354040283529160200191610715565b820191906000526020600020905b8154815290600101906020018083116106f857829003601f168201915b50508581038252868181546001816001161561010002031660029004815260200191508054600181600116156101000203166002900480156107985780601f1061076d57610100808354040283529160200191610798565b820191906000526020600020905b81548152906001019060200180831161077b57829003601f168201915b5050985050505050505050506040805180830381600087803b1580156107bd57600080fd5b505af11580156107d1573d6000803e3d6000fd5b505050506040513d60408110156107e757600080fd5b810190808051906020019092919080519060200190929190505050600560008291906101000a81548160ff0219169083151502179055508192505050600560009054906101000a900460ff1691505090565b606060038054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156108d15780601f106108a6576101008083540402835291602001916108d1565b820191906000526020600020905b8154815290600101906020018083116108b457829003601f168201915b5050505050905090565b606060018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156109735780601f1061094857610100808354040283529160200191610973565b820191906000526020600020905b81548152906001019060200180831161095657829003601f168201915b5050505050905090565b606060028054600181600116156101000203166002900480601f016020809104026020016040519081016040528092919081815260200182805460018160011615610100020316600290048015610a155780601f106109ea57610100808354040283529160200191610a15565b820191906000526020600020905b8154815290600101906020018083116109f857829003601f168201915b5050505050905090565b8360019080519060200190610a35929190610ab3565b508260029080519060200190610a4c929190610ab3565b508160039080519060200190610a63929190610ab3565b508060049080519060200190610a7a929190610ab3565b506000600560006101000a81548160ff02191690831515021790555050505050565b6000600560009054906101000a900460ff16905090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10610af457805160ff1916838001178555610b22565b82800160010185558215610b22579182015b82811115610b21578251825591602001919060010190610b06565b5b509050610b2f9190610b33565b5090565b610b5591905b80821115610b51576000816000905550600101610b39565b5090565b905600a165627a7a7230582071bd12589fad2c47f7232f6ba7f0c568047ac35ebde821c132ea671eaab28a4f0029"
    };

    public static final String BINARY =
            org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {
        "60806040526000600560006101000a81548160ff02191690831515021790555034801561002b57600080fd5b50604051610d4b380380610d4b833981018060405281019080805182019291906020018051820192919060200180518201929190602001805182019291905050506150046000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555083600190805190602001906100c4929190610113565b5082600290805190602001906100db929190610113565b5081600390805190602001906100f2929190610113565b508060049080519060200190610109929190610113565b50505050506101b8565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061015457805160ff1916838001178555610182565b82800160010185558215610182579182015b82811115610181578251825591602001919060010190610166565b5b50905061018f9190610193565b5090565b6101b591905b808211156101b1576000816000905550600101610199565b5090565b90565b610b84806101c76000396000f300608060405260043610610083576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806353f8014e14610088578063793f2386146100b75780638c9ba3b214610147578063cf7edfcf146101d7578063d72d1ab714610206578063e413d03f14610296578063ea58d40414610326575b600080fd5b34801561009457600080fd5b5061009d610461565b604051808215151515815260200191505060405180910390f35b3480156100c357600080fd5b506100cc610797565b6040518080602001828103825283818151815260200191508051906020019080838360005b8381101561010c5780820151818401526020810190506100f1565b50505050905090810190601f1680156101395780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561015357600080fd5b5061015c610839565b6040518080602001828103825283818151815260200191508051906020019080838360005b8381101561019c578082015181840152602081019050610181565b50505050905090810190601f1680156101c95780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b3480156101e357600080fd5b506101ec6108db565b604051808215151515815260200191505060405180910390f35b34801561021257600080fd5b5061021b6108f2565b6040518080602001828103825283818151815260200191508051906020019080838360005b8381101561025b578082015181840152602081019050610240565b50505050905090810190601f1680156102885780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b3480156102a257600080fd5b506102ab610994565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156102eb5780820151818401526020810190506102d0565b50505050905090810190601f1680156103185780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561033257600080fd5b5061045f600480360381019080803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509192919290505050610a36565b005b600080600090506000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16639be9e39260016002600360046040518563ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808060200180602001806020018060200185810385528981815460018160011615610100020316600290048152602001915080546001816001161561010002031660029004801561056d5780601f106105425761010080835404028352916020019161056d565b820191906000526020600020905b81548152906001019060200180831161055057829003601f168201915b50508581038452888181546001816001161561010002031660029004815260200191508054600181600116156101000203166002900480156105f05780601f106105c5576101008083540402835291602001916105f0565b820191906000526020600020905b8154815290600101906020018083116105d357829003601f168201915b50508581038352878181546001816001161561010002031660029004815260200191508054600181600116156101000203166002900480156106735780601f1061064857610100808354040283529160200191610673565b820191906000526020600020905b81548152906001019060200180831161065657829003601f168201915b50508581038252868181546001816001161561010002031660029004815260200191508054600181600116156101000203166002900480156106f65780601f106106cb576101008083540402835291602001916106f6565b820191906000526020600020905b8154815290600101906020018083116106d957829003601f168201915b5050985050505050505050506040805180830381600087803b15801561071b57600080fd5b505af115801561072f573d6000803e3d6000fd5b505050506040513d604081101561074557600080fd5b810190808051906020019092919080519060200190929190505050600560008291906101000a81548160ff0219169083151502179055508192505050600560009054906101000a900460ff1691505090565b606060018054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561082f5780601f106108045761010080835404028352916020019161082f565b820191906000526020600020905b81548152906001019060200180831161081257829003601f168201915b5050505050905090565b606060028054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156108d15780601f106108a6576101008083540402835291602001916108d1565b820191906000526020600020905b8154815290600101906020018083116108b457829003601f168201915b5050505050905090565b6000600560009054906101000a900460ff16905090565b606060038054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561098a5780601f1061095f5761010080835404028352916020019161098a565b820191906000526020600020905b81548152906001019060200180831161096d57829003601f168201915b5050505050905090565b606060048054600181600116156101000203166002900480601f016020809104026020016040519081016040528092919081815260200182805460018160011615610100020316600290048015610a2c5780601f10610a0157610100808354040283529160200191610a2c565b820191906000526020600020905b815481529060010190602001808311610a0f57829003601f168201915b5050505050905090565b8360019080519060200190610a4c929190610ab3565b508260029080519060200190610a63929190610ab3565b508160039080519060200190610a7a929190610ab3565b508060049080519060200190610a91929190610ab3565b506000600560006101000a81548160ff02191690831515021790555050505050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10610af457805160ff1916838001178555610b22565b82800160010185558215610b22579182015b82811115610b21578251825591602001919060010190610b06565b5b509050610b2f9190610b33565b5090565b610b5591905b80821115610b51576000816000905550600101610b39565b5090565b905600a165627a7a723058205e3aba20c3ab4271017fc0e485066835fdab8a30ae8add59f10772f86d006e150029"
    };

    public static final String SM_BINARY =
            org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {
        "[{\"conflictFields\":[{\"kind\":4,\"value\":[4]}],\"constant\":true,\"inputs\":[],\"name\":\"get_group_pbc_param\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"selector\":[230538433,3826503743],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0}],\"constant\":false,\"inputs\":[],\"name\":\"verify_group_sig\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"selector\":[1660245969,1408762190],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[3]}],\"constant\":true,\"inputs\":[],\"name\":\"get_group_gpk_info\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"selector\":[2085204571,3610057399],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[1]}],\"constant\":true,\"inputs\":[],\"name\":\"get_group_sig\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"selector\":[2518041615,2034180998],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[2]}],\"constant\":true,\"inputs\":[],\"name\":\"get_group_message\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"selector\":[2663640803,2359010226],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[1]},{\"kind\":4,\"value\":[2]},{\"kind\":4,\"value\":[3]},{\"kind\":4,\"value\":[4]},{\"kind\":4,\"value\":[5]}],\"constant\":false,\"inputs\":[{\"name\":\"new_sig\",\"type\":\"string\"},{\"name\":\"new_message\",\"type\":\"string\"},{\"name\":\"new_gpk_info\",\"type\":\"string\"},{\"name\":\"new_pbc_param_info\",\"type\":\"string\"}],\"name\":\"update_group_sig_data\",\"outputs\":[],\"payable\":false,\"selector\":[3846007808,3931689988],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[5]}],\"constant\":true,\"inputs\":[],\"name\":\"get_group_verify_result\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"selector\":[3946215065,3481198543],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"name\":\"_sig\",\"type\":\"string\"},{\"name\":\"_message\",\"type\":\"string\"},{\"name\":\"_gpk_info\",\"type\":\"string\"},{\"name\":\"_pbc_param_info\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"}]"
    };

    public static final String ABI = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_GET_GROUP_PBC_PARAM = "get_group_pbc_param";

    public static final String FUNC_VERIFY_GROUP_SIG = "verify_group_sig";

    public static final String FUNC_GET_GROUP_GPK_INFO = "get_group_gpk_info";

    public static final String FUNC_GET_GROUP_SIG = "get_group_sig";

    public static final String FUNC_GET_GROUP_MESSAGE = "get_group_message";

    public static final String FUNC_UPDATE_GROUP_SIG_DATA = "update_group_sig_data";

    public static final String FUNC_GET_GROUP_VERIFY_RESULT = "get_group_verify_result";

    protected TestGroupSig(String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public static String getABI() {
        return ABI;
    }

    public String get_group_pbc_param() throws ContractException {
        final Function function =
                new Function(
                        FUNC_GET_GROUP_PBC_PARAM,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public TransactionReceipt verify_group_sig() {
        final Function function =
                new Function(
                        FUNC_VERIFY_GROUP_SIG,
                        Arrays.<Type>asList(),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return executeTransaction(function);
    }

    public String verify_group_sig(TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_VERIFY_GROUP_SIG,
                        Arrays.<Type>asList(),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForVerify_group_sig() {
        final Function function =
                new Function(
                        FUNC_VERIFY_GROUP_SIG,
                        Arrays.<Type>asList(),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return createSignedTransaction(function);
    }

    public Tuple1<Boolean> getVerify_group_sigOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_VERIFY_GROUP_SIG,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<Boolean>((Boolean) results.get(0).getValue());
    }

    public String get_group_gpk_info() throws ContractException {
        final Function function =
                new Function(
                        FUNC_GET_GROUP_GPK_INFO,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public String get_group_sig() throws ContractException {
        final Function function =
                new Function(
                        FUNC_GET_GROUP_SIG,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public String get_group_message() throws ContractException {
        final Function function =
                new Function(
                        FUNC_GET_GROUP_MESSAGE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public TransactionReceipt update_group_sig_data(
            String new_sig, String new_message, String new_gpk_info, String new_pbc_param_info) {
        final Function function =
                new Function(
                        FUNC_UPDATE_GROUP_SIG_DATA,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(new_sig),
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(new_message),
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(new_gpk_info),
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(
                                        new_pbc_param_info)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return executeTransaction(function);
    }

    public String update_group_sig_data(
            String new_sig,
            String new_message,
            String new_gpk_info,
            String new_pbc_param_info,
            TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_UPDATE_GROUP_SIG_DATA,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(new_sig),
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(new_message),
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(new_gpk_info),
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(
                                        new_pbc_param_info)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForUpdate_group_sig_data(
            String new_sig, String new_message, String new_gpk_info, String new_pbc_param_info) {
        final Function function =
                new Function(
                        FUNC_UPDATE_GROUP_SIG_DATA,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(new_sig),
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(new_message),
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(new_gpk_info),
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(
                                        new_pbc_param_info)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return createSignedTransaction(function);
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
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple4<String, String, String, String>(
                (String) results.get(0).getValue(),
                (String) results.get(1).getValue(),
                (String) results.get(2).getValue(),
                (String) results.get(3).getValue());
    }

    public Boolean get_group_verify_result() throws ContractException {
        final Function function =
                new Function(
                        FUNC_GET_GROUP_VERIFY_RESULT,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public static TestGroupSig load(
            String contractAddress, Client client, CryptoKeyPair credential) {
        return new TestGroupSig(contractAddress, client, credential);
    }

    public static TestGroupSig deploy(
            Client client,
            CryptoKeyPair credential,
            String _sig,
            String _message,
            String _gpk_info,
            String _pbc_param_info)
            throws ContractException {
        byte[] encodedConstructor =
                FunctionEncoder.encodeConstructor(
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(_sig),
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(_message),
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(_gpk_info),
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(
                                        _pbc_param_info)));
        return deploy(
                TestGroupSig.class,
                client,
                credential,
                getBinary(client.getCryptoSuite()),
                getABI(),
                encodedConstructor,
                null);
    }
}
