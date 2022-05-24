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
        "60806040526005805460ff191690553480156200001b57600080fd5b50604051620009a3380380620009a38339810160408190526200003e916200022b565b600080546001600160a01b03191661500490811790915584516200006a906001906020880190620000b8565b50835162000080906002906020870190620000b8565b50825162000096906003906020860190620000b8565b508151620000ac906004906020850190620000b8565b50505050505062000321565b828054620000c690620002e4565b90600052602060002090601f016020900481019282620000ea576000855562000135565b82601f106200010557805160ff191683800117855562000135565b8280016001018555821562000135579182015b828111156200013557825182559160200191906001019062000118565b506200014392915062000147565b5090565b5b8082111562000143576000815560010162000148565b634e487b7160e01b600052604160045260246000fd5b600082601f8301126200018657600080fd5b81516001600160401b0380821115620001a357620001a36200015e565b604051601f8301601f19908116603f01168101908282118183101715620001ce57620001ce6200015e565b81604052838152602092508683858801011115620001eb57600080fd5b600091505b838210156200020f5785820183015181830184015290820190620001f0565b83821115620002215760008385830101525b9695505050505050565b600080600080608085870312156200024257600080fd5b84516001600160401b03808211156200025a57600080fd5b620002688883890162000174565b955060208701519150808211156200027f57600080fd5b6200028d8883890162000174565b94506040870151915080821115620002a457600080fd5b620002b28883890162000174565b93506060870151915080821115620002c957600080fd5b50620002d88782880162000174565b91505092959194509250565b600181811c90821680620002f957607f821691505b602082108114156200031b57634e487b7160e01b600052602260045260246000fd5b50919050565b61067280620003316000396000f3fe608060405234801561001057600080fd5b506004361061007d5760003560e01c80639616440f1161005b5780639616440f146100c05780639ec3eee3146100c8578063e53d6c00146100d0578063eb367699146100e557600080fd5b80630dbdbcc11461008257806362f557d1146100a05780637c49b25b146100b8575b600080fd5b61008a6100f0565b604051610097919061033b565b60405180910390f35b6100a8610182565b6040519015158152602001610097565b61008a610215565b61008a610224565b61008a610233565b6100e36100de366004610433565b610242565b005b60055460ff166100a8565b6060600480546100ff906104e0565b80601f016020809104026020016040519081016040528092919081815260200182805461012b906104e0565b80156101785780601f1061014d57610100808354040283529160200191610178565b820191906000526020600020905b81548152906001019060200180831161015b57829003601f168201915b5050505050905090565b60008054604051635964560f60e11b81526001600160a01b039091169063b2c8ac1e906101bc9060019060029060039060049081016105bb565b602060405180830381865afa1580156101d9573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906101fd9190610613565b6005805460ff1916911515918217905560ff16919050565b6060600380546100ff906104e0565b6060600180546100ff906104e0565b6060600280546100ff906104e0565b83516102559060019060208701906102a2565b5082516102699060029060208601906102a2565b50815161027d9060039060208501906102a2565b5080516102919060049060208401906102a2565b50506005805460ff19169055505050565b8280546102ae906104e0565b90600052602060002090601f0160209004810192826102d05760008555610316565b82601f106102e957805160ff1916838001178555610316565b82800160010185558215610316579182015b828111156103165782518255916020019190600101906102fb565b50610322929150610326565b5090565b5b808211156103225760008155600101610327565b600060208083528351808285015260005b818110156103685785810183015185820160400152820161034c565b8181111561037a576000604083870101525b50601f01601f1916929092016040019392505050565b634e487b7160e01b600052604160045260246000fd5b600082601f8301126103b757600080fd5b813567ffffffffffffffff808211156103d2576103d2610390565b604051601f8301601f19908116603f011681019082821181831017156103fa576103fa610390565b8160405283815286602085880101111561041357600080fd5b836020870160208301376000602085830101528094505050505092915050565b6000806000806080858703121561044957600080fd5b843567ffffffffffffffff8082111561046157600080fd5b61046d888389016103a6565b9550602087013591508082111561048357600080fd5b61048f888389016103a6565b945060408701359150808211156104a557600080fd5b6104b1888389016103a6565b935060608701359150808211156104c757600080fd5b506104d4878288016103a6565b91505092959194509250565b600181811c908216806104f457607f821691505b6020821081141561051557634e487b7160e01b600052602260045260246000fd5b50919050565b8054600090600181811c908083168061053557607f831692505b602080841082141561055757634e487b7160e01b600052602260045260246000fd5b838852602088018280156105725760018114610583576105ae565b60ff198716825282820197506105ae565b60008981526020902060005b878110156105a85781548482015290860190840161058f565b83019850505b5050505050505092915050565b6080815260006105ce608083018761051b565b82810360208401526105e0818761051b565b905082810360408401526105f4818661051b565b90508281036060840152610608818561051b565b979650505050505050565b60006020828403121561062557600080fd5b8151801515811461063557600080fd5b939250505056fea2646970667358221220a53cd0e4a4b9143860950fd7bfa6be9af792beb85e7bba0b42f88a6691e687ad64736f6c634300080b0033"
    };

    public static final String BINARY =
            org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {
        "60806040526005805460ff191690553480156200001b57600080fd5b506040516200099f3803806200099f8339810160408190526200003e916200022b565b600080546001600160a01b03191661500490811790915584516200006a906001906020880190620000b8565b50835162000080906002906020870190620000b8565b50825162000096906003906020860190620000b8565b508151620000ac906004906020850190620000b8565b50505050505062000321565b828054620000c690620002e4565b90600052602060002090601f016020900481019282620000ea576000855562000135565b82601f106200010557805160ff191683800117855562000135565b8280016001018555821562000135579182015b828111156200013557825182559160200191906001019062000118565b506200014392915062000147565b5090565b5b8082111562000143576000815560010162000148565b63b95aa35560e01b600052604160045260246000fd5b600082601f8301126200018657600080fd5b81516001600160401b0380821115620001a357620001a36200015e565b604051601f8301601f19908116603f01168101908282118183101715620001ce57620001ce6200015e565b81604052838152602092508683858801011115620001eb57600080fd5b600091505b838210156200020f5785820183015181830184015290820190620001f0565b83821115620002215760008385830101525b9695505050505050565b600080600080608085870312156200024257600080fd5b84516001600160401b03808211156200025a57600080fd5b620002688883890162000174565b955060208701519150808211156200027f57600080fd5b6200028d8883890162000174565b94506040870151915080821115620002a457600080fd5b620002b28883890162000174565b93506060870151915080821115620002c957600080fd5b50620002d88782880162000174565b91505092959194509250565b600181811c90821680620002f957607f821691505b602082108114156200031b5763b95aa35560e01b600052602260045260246000fd5b50919050565b61066e80620003316000396000f3fe608060405234801561001057600080fd5b506004361061007d5760003560e01c8063cf7edfcf1161005b578063cf7edfcf146100bc578063d72d1ab7146100c7578063e413d03f146100cf578063ea58d404146100d757600080fd5b806353f8014e14610082578063793f23861461009f5780638c9ba3b2146100b4575b600080fd5b61008a6100ec565b60405190151581526020015b60405180910390f35b6100a761017f565b6040516100969190610337565b6100a7610211565b60055460ff1661008a565b6100a7610220565b6100a761022f565b6100ea6100e536600461042f565b61023e565b005b60008054604051634df4f1c960e11b81526001600160a01b0390911690639be9e392906101269060019060029060039060049081016105b7565b602060405180830381865afa158015610143573d6000803e3d6000fd5b505050506040513d601f19601f82011682018060405250810190610167919061060f565b6005805460ff1916911515918217905560ff16919050565b60606001805461018e906104dc565b80601f01602080910402602001604051908101604052809291908181526020018280546101ba906104dc565b80156102075780601f106101dc57610100808354040283529160200191610207565b820191906000526020600020905b8154815290600101906020018083116101ea57829003601f168201915b5050505050905090565b60606002805461018e906104dc565b60606003805461018e906104dc565b60606004805461018e906104dc565b835161025190600190602087019061029e565b50825161026590600290602086019061029e565b50815161027990600390602085019061029e565b50805161028d90600490602084019061029e565b50506005805460ff19169055505050565b8280546102aa906104dc565b90600052602060002090601f0160209004810192826102cc5760008555610312565b82601f106102e557805160ff1916838001178555610312565b82800160010185558215610312579182015b828111156103125782518255916020019190600101906102f7565b5061031e929150610322565b5090565b5b8082111561031e5760008155600101610323565b600060208083528351808285015260005b8181101561036457858101830151858201604001528201610348565b81811115610376576000604083870101525b50601f01601f1916929092016040019392505050565b63b95aa35560e01b600052604160045260246000fd5b600082601f8301126103b357600080fd5b813567ffffffffffffffff808211156103ce576103ce61038c565b604051601f8301601f19908116603f011681019082821181831017156103f6576103f661038c565b8160405283815286602085880101111561040f57600080fd5b836020870160208301376000602085830101528094505050505092915050565b6000806000806080858703121561044557600080fd5b843567ffffffffffffffff8082111561045d57600080fd5b610469888389016103a2565b9550602087013591508082111561047f57600080fd5b61048b888389016103a2565b945060408701359150808211156104a157600080fd5b6104ad888389016103a2565b935060608701359150808211156104c357600080fd5b506104d0878288016103a2565b91505092959194509250565b600181811c908216806104f057607f821691505b602082108114156105115763b95aa35560e01b600052602260045260246000fd5b50919050565b8054600090600181811c908083168061053157607f831692505b60208084108214156105535763b95aa35560e01b600052602260045260246000fd5b8388526020880182801561056e576001811461057f576105aa565b60ff198716825282820197506105aa565b60008981526020902060005b878110156105a45781548482015290860190840161058b565b83019850505b5050505050505092915050565b6080815260006105ca6080830187610517565b82810360208401526105dc8187610517565b905082810360408401526105f08186610517565b905082810360608401526106048185610517565b979650505050505050565b60006020828403121561062157600080fd5b8151801515811461063157600080fd5b939250505056fea264697066735822122088ebfd441a52925a275df381f3288cf6a6d5786b95d78baa94283b6378e1ad0c64736f6c634300080b0033"
    };

    public static final String SM_BINARY =
            org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {
        "[{\"inputs\":[{\"internalType\":\"string\",\"name\":\"_sig\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"_message\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"_gpk_info\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"_pbc_param_info\",\"type\":\"string\"}],\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"conflictFields\":[{\"kind\":4,\"value\":[3]}],\"inputs\":[],\"name\":\"get_group_gpk_info\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"selector\":[2085204571,3610057399],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[2]}],\"inputs\":[],\"name\":\"get_group_message\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"selector\":[2663640803,2359010226],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[4]}],\"inputs\":[],\"name\":\"get_group_pbc_param\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"selector\":[230538433,3826503743],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[1]}],\"inputs\":[],\"name\":\"get_group_sig\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"selector\":[2518041615,2034180998],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[5]}],\"inputs\":[],\"name\":\"get_group_verify_result\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[3946215065,3481198543],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":4,\"value\":[1]},{\"kind\":4,\"value\":[2]},{\"kind\":4,\"value\":[3]},{\"kind\":4,\"value\":[4]},{\"kind\":4,\"value\":[5]}],\"inputs\":[{\"internalType\":\"string\",\"name\":\"new_sig\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"new_message\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"new_gpk_info\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"new_pbc_param_info\",\"type\":\"string\"}],\"name\":\"update_group_sig_data\",\"outputs\":[],\"selector\":[3846007808,3931689988],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":0}],\"inputs\":[],\"name\":\"verify_group_sig\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[1660245969,1408762190],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"
    };

    public static final String ABI = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_GET_GROUP_GPK_INFO = "get_group_gpk_info";

    public static final String FUNC_GET_GROUP_MESSAGE = "get_group_message";

    public static final String FUNC_GET_GROUP_PBC_PARAM = "get_group_pbc_param";

    public static final String FUNC_GET_GROUP_SIG = "get_group_sig";

    public static final String FUNC_GET_GROUP_VERIFY_RESULT = "get_group_verify_result";

    public static final String FUNC_UPDATE_GROUP_SIG_DATA = "update_group_sig_data";

    public static final String FUNC_VERIFY_GROUP_SIG = "verify_group_sig";

    protected TestGroupSig(String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public static String getABI() {
        return ABI;
    }

    public String get_group_gpk_info() throws ContractException {
        final Function function =
                new Function(
                        FUNC_GET_GROUP_GPK_INFO,
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

    public String get_group_pbc_param() throws ContractException {
        final Function function =
                new Function(
                        FUNC_GET_GROUP_PBC_PARAM,
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

    public Boolean get_group_verify_result() throws ContractException {
        final Function function =
                new Function(
                        FUNC_GET_GROUP_VERIFY_RESULT,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
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
