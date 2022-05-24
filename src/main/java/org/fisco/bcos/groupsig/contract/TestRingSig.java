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
        "60806040526004805460ff191690553480156200001b57600080fd5b50604051620008d9380380620008d98339810160408190526200003e9162000214565b600080546001600160a01b03191661500590811790915583516200006a906001906020870190620000a1565b50825162000080906002906020860190620000a1565b50815162000096906003906020850190620000a1565b5050505050620002e2565b828054620000af90620002a5565b90600052602060002090601f016020900481019282620000d357600085556200011e565b82601f10620000ee57805160ff19168380011785556200011e565b828001600101855582156200011e579182015b828111156200011e57825182559160200191906001019062000101565b506200012c92915062000130565b5090565b5b808211156200012c576000815560010162000131565b634e487b7160e01b600052604160045260246000fd5b600082601f8301126200016f57600080fd5b81516001600160401b03808211156200018c576200018c62000147565b604051601f8301601f19908116603f01168101908282118183101715620001b757620001b762000147565b81604052838152602092508683858801011115620001d457600080fd5b600091505b83821015620001f85785820183015181830184015290820190620001d9565b838211156200020a5760008385830101525b9695505050505050565b6000806000606084860312156200022a57600080fd5b83516001600160401b03808211156200024257600080fd5b62000250878388016200015d565b945060208601519150808211156200026757600080fd5b62000275878388016200015d565b935060408601519150808211156200028c57600080fd5b506200029b868287016200015d565b9150509250925092565b600181811c90821680620002ba57607f821691505b60208210811415620002dc57634e487b7160e01b600052602260045260246000fd5b50919050565b6105e780620002f26000396000f3fe608060405234801561001057600080fd5b50600436106100625760003560e01c80631991fcec14610067578063534b680f14610083578063c148779414610098578063dd3ede22146100a0578063f429e366146100a8578063f9bd817d146100b0575b600080fd5b60045460ff165b60405190151581526020015b60405180910390f35b61008b6100c5565b60405161007a91906102ea565b61006e610157565b61008b6101e8565b61008b6101f7565b6100c36100be3660046103e2565b610206565b005b6060600280546100d49061046a565b80601f01602080910402602001604051908101604052809291908181526020018280546101009061046a565b801561014d5780601f106101225761010080835404028352916020019161014d565b820191906000526020600020905b81548152906001019060200180831161013057829003601f168201915b5050505050905090565b60008054604051634090ff6d60e01b81526001600160a01b0390911690634090ff6d9061018f90600190600290600390600401610545565b602060405180830381865afa1580156101ac573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906101d09190610588565b6004805460ff1916911515918217905560ff16919050565b6060600380546100d49061046a565b6060600180546100d49061046a565b8251610219906001906020860190610251565b50815161022d906002906020850190610251565b508051610241906003906020840190610251565b50506004805460ff191690555050565b82805461025d9061046a565b90600052602060002090601f01602090048101928261027f57600085556102c5565b82601f1061029857805160ff19168380011785556102c5565b828001600101855582156102c5579182015b828111156102c55782518255916020019190600101906102aa565b506102d19291506102d5565b5090565b5b808211156102d157600081556001016102d6565b600060208083528351808285015260005b81811015610317578581018301518582016040015282016102fb565b81811115610329576000604083870101525b50601f01601f1916929092016040019392505050565b634e487b7160e01b600052604160045260246000fd5b600082601f83011261036657600080fd5b813567ffffffffffffffff808211156103815761038161033f565b604051601f8301601f19908116603f011681019082821181831017156103a9576103a961033f565b816040528381528660208588010111156103c257600080fd5b836020870160208301376000602085830101528094505050505092915050565b6000806000606084860312156103f757600080fd5b833567ffffffffffffffff8082111561040f57600080fd5b61041b87838801610355565b9450602086013591508082111561043157600080fd5b61043d87838801610355565b9350604086013591508082111561045357600080fd5b5061046086828701610355565b9150509250925092565b600181811c9082168061047e57607f821691505b6020821081141561049f57634e487b7160e01b600052602260045260246000fd5b50919050565b8054600090600181811c90808316806104bf57607f831692505b60208084108214156104e157634e487b7160e01b600052602260045260246000fd5b838852602088018280156104fc576001811461050d57610538565b60ff19871682528282019750610538565b60008981526020902060005b8781101561053257815484820152908601908401610519565b83019850505b5050505050505092915050565b60608152600061055860608301866104a5565b828103602084015261056a81866104a5565b9050828103604084015261057e81856104a5565b9695505050505050565b60006020828403121561059a57600080fd5b815180151581146105aa57600080fd5b939250505056fea264697066735822122039226386c4603363e78b062588b865c261b1e341c0b2c499d35f4df6daf4ea8a64736f6c634300080b0033"
    };

    public static final String BINARY =
            org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {
        "60806040526004805460ff191690553480156200001b57600080fd5b50604051620008dd380380620008dd8339810160408190526200003e9162000214565b600080546001600160a01b03191661500590811790915583516200006a906001906020870190620000a1565b50825162000080906002906020860190620000a1565b50815162000096906003906020850190620000a1565b5050505050620002e2565b828054620000af90620002a5565b90600052602060002090601f016020900481019282620000d357600085556200011e565b82601f10620000ee57805160ff19168380011785556200011e565b828001600101855582156200011e579182015b828111156200011e57825182559160200191906001019062000101565b506200012c92915062000130565b5090565b5b808211156200012c576000815560010162000131565b63b95aa35560e01b600052604160045260246000fd5b600082601f8301126200016f57600080fd5b81516001600160401b03808211156200018c576200018c62000147565b604051601f8301601f19908116603f01168101908282118183101715620001b757620001b762000147565b81604052838152602092508683858801011115620001d457600080fd5b600091505b83821015620001f85785820183015181830184015290820190620001d9565b838211156200020a5760008385830101525b9695505050505050565b6000806000606084860312156200022a57600080fd5b83516001600160401b03808211156200024257600080fd5b62000250878388016200015d565b945060208601519150808211156200026757600080fd5b62000275878388016200015d565b935060408601519150808211156200028c57600080fd5b506200029b868287016200015d565b9150509250925092565b600181811c90821680620002ba57607f821691505b60208210811415620002dc5763b95aa35560e01b600052602260045260246000fd5b50919050565b6105eb80620002f26000396000f3fe608060405234801561001057600080fd5b50600436106100625760003560e01c80638119c37c146100675780638895a21c1461007c5780639ee1ba7f1461009a578063a103f0c0146100b1578063ba5a5d38146100b9578063cb75874b146100c1575b600080fd5b61007a610075366004610391565b6100c9565b005b610084610114565b6040516100919190610419565b60405180910390f35b60045460ff165b6040519015158152602001610091565b6100846101a6565b6100a16101b5565b610084610246565b82516100dc906001906020860190610255565b5081516100f0906002906020850190610255565b508051610104906003906020840190610255565b50506004805460ff191690555050565b6060600380546101239061046e565b80601f016020809104026020016040519081016040528092919081815260200182805461014f9061046e565b801561019c5780601f106101715761010080835404028352916020019161019c565b820191906000526020600020905b81548152906001019060200180831161017f57829003601f168201915b5050505050905090565b6060600280546101239061046e565b6000805460405163337dd75360e01b81526001600160a01b039091169063337dd753906101ed90600190600290600390600401610549565b602060405180830381865afa15801561020a573d6000803e3d6000fd5b505050506040513d601f19601f8201168201806040525081019061022e919061058c565b6004805460ff1916911515918217905560ff16919050565b6060600180546101239061046e565b8280546102619061046e565b90600052602060002090601f01602090048101928261028357600085556102c9565b82601f1061029c57805160ff19168380011785556102c9565b828001600101855582156102c9579182015b828111156102c95782518255916020019190600101906102ae565b506102d59291506102d9565b5090565b5b808211156102d557600081556001016102da565b63b95aa35560e01b600052604160045260246000fd5b600082601f83011261031557600080fd5b813567ffffffffffffffff80821115610330576103306102ee565b604051601f8301601f19908116603f01168101908282118183101715610358576103586102ee565b8160405283815286602085880101111561037157600080fd5b836020870160208301376000602085830101528094505050505092915050565b6000806000606084860312156103a657600080fd5b833567ffffffffffffffff808211156103be57600080fd5b6103ca87838801610304565b945060208601359150808211156103e057600080fd5b6103ec87838801610304565b9350604086013591508082111561040257600080fd5b5061040f86828701610304565b9150509250925092565b600060208083528351808285015260005b818110156104465785810183015185820160400152820161042a565b81811115610458576000604083870101525b50601f01601f1916929092016040019392505050565b600181811c9082168061048257607f821691505b602082108114156104a35763b95aa35560e01b600052602260045260246000fd5b50919050565b8054600090600181811c90808316806104c357607f831692505b60208084108214156104e55763b95aa35560e01b600052602260045260246000fd5b8388526020880182801561050057600181146105115761053c565b60ff1987168252828201975061053c565b60008981526020902060005b878110156105365781548482015290860190840161051d565b83019850505b5050505050505092915050565b60608152600061055c60608301866104a9565b828103602084015261056e81866104a9565b9050828103604084015261058281856104a9565b9695505050505050565b60006020828403121561059e57600080fd5b815180151581146105ae57600080fd5b939250505056fea2646970667358221220e303333422cf611c87f8dec48a49f5220e1b9e7385d7a63baf9538d425c1f8c264736f6c634300080b0033"
    };

    public static final String SM_BINARY =
            org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {
        "[{\"inputs\":[{\"internalType\":\"string\",\"name\":\"_sig\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"_message\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"_param_info\",\"type\":\"string\"}],\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"conflictFields\":[{\"kind\":4,\"value\":[2]}],\"inputs\":[],\"name\":\"get_ring_message\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"selector\":[1397450767,2701390016],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[3]}],\"inputs\":[],\"name\":\"get_ring_param_info\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"selector\":[3711884834,2291507740],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[1]}],\"inputs\":[],\"name\":\"get_ring_sig\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"selector\":[4096385894,3413477195],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[4]}],\"inputs\":[],\"name\":\"get_ring_verify_result\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[428997868,2665593471],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[1]},{\"kind\":4,\"value\":[2]},{\"kind\":4,\"value\":[3]},{\"kind\":4,\"value\":[4]}],\"inputs\":[{\"internalType\":\"string\",\"name\":\"new_sig\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"new_message\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"new_param_info\",\"type\":\"string\"}],\"name\":\"update_ring_sig_data\",\"outputs\":[],\"selector\":[4189946237,2165949308],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0}],\"inputs\":[],\"name\":\"verify_ring_sig\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[3242751892,3126484280],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"
    };

    public static final String ABI = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_GET_RING_MESSAGE = "get_ring_message";

    public static final String FUNC_GET_RING_PARAM_INFO = "get_ring_param_info";

    public static final String FUNC_GET_RING_SIG = "get_ring_sig";

    public static final String FUNC_GET_RING_VERIFY_RESULT = "get_ring_verify_result";

    public static final String FUNC_UPDATE_RING_SIG_DATA = "update_ring_sig_data";

    public static final String FUNC_VERIFY_RING_SIG = "verify_ring_sig";

    protected TestRingSig(String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public static String getABI() {
        return ABI;
    }

    public String get_ring_message() throws ContractException {
        final Function function =
                new Function(
                        FUNC_GET_RING_MESSAGE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
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

    public Boolean get_ring_verify_result() throws ContractException {
        final Function function =
                new Function(
                        FUNC_GET_RING_VERIFY_RESULT,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
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
