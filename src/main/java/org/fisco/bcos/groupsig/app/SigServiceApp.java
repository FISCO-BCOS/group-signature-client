package org.fisco.bcos.groupsig.app;

import org.fisco.bcos.groupsig.contract.TestGroupSig;
import org.fisco.bcos.groupsig.contract.TestRingSig;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.config.Config;
import org.fisco.bcos.sdk.v3.config.ConfigOption;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.ConstantConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SigServiceApp {
    private Logger logger = LoggerFactory.getLogger(SigServiceApp.class);
    // java-sdk client
    private Client client;
    // call back group signature algorithm implemented in precompiled contract
    private TestGroupSig groupSig;
    // call back ring signature algorithm implemented in precompiled contract
    private TestRingSig ringSig;
    private RequestSigService sigService;
    private static final String GROUP = "group0";
    private CryptoKeyPair credential;
    // load sdk configuration path
    private static final String configFile =
            SigServiceApp.class
                    .getClassLoader()
                    .getResource(ConstantConfig.CONFIG_FILE_NAME)
                    .getPath();
    // construct function
    public SigServiceApp(RequestSigService paramSigService) {
        sigService = paramSigService;
    }

    public boolean loadConfig() throws Exception {
        System.out.println("please wait ...");
        ConfigOption configOption = Config.load(configFile);
        // load java-sdk configuration
        client = Client.build(GROUP, configOption);
        credential = client.getCryptoSuite().getCryptoKeyPair();
        return true;
    }

    // deploy contract: return contract address
    public void deployGroupSigContract(
            String groupName, String memberName, String message, StringBuffer address)
            throws Exception {
        String errorMsg = "deploy group sig contract failed, error_msg: ";
        SigStruct sigObj = new SigStruct();
        boolean ret = sigService.groupSig(sigObj, groupName, memberName, message);
        System.out.println("### SIG: " + sigObj.getSig());
        System.out.println("### GPK: " + sigObj.getGPK());
        System.out.println("### PBC_PARAM: " + sigObj.getParam());
        if (!ret) throw new Exception(errorMsg + "call rpc failed");
        try {
            groupSig =
                    TestGroupSig.deploy(
                            client,
                            credential,
                            sigObj.getSig(),
                            message,
                            sigObj.getGPK(),
                            sigObj.getParam());
        } catch (Exception e) {
            logger.error(errorMsg + e.getMessage());
            throw new Exception(errorMsg + e.getMessage());
        }
        address.append(groupSig.getContractAddress());
    }

    // deploy ring_sig contract
    public void deployRingSigContract(
            String message, String ringName, int memberPos, int ringSize, StringBuffer address)
            throws Exception {
        SigStruct ringSigObj = new SigStruct();
        boolean ret =
                sigService.linkableRingSig(ringSigObj, message, ringName, memberPos, ringSize);
        if (!ret) throw new Exception("call rpc failed");
        try {
            ringSig =
                    TestRingSig.deploy(
                            client,
                            credential,
                            ringSigObj.getSig(),
                            message,
                            ringSigObj.getParam());
        } catch (Exception e) {
            logger.error("deploy ring sig contract failed, error_msg: " + e.getMessage());
            throw new Exception("deploy ring sig contract failed, error_msg:" + e.getMessage());
        }
        address.append(ringSig.getContractAddress());
    }

    // update group sig data
    public void updateGroupSigData(
            String contractAddr,
            String groupName,
            String memberName,
            String message,
            StringBuffer updatedSig)
            throws Exception {
        SigStruct sigObj = new SigStruct();
        boolean ret = sigService.groupSig(sigObj, groupName, memberName, message);
        if (!ret) {
            throw new Exception("call rpc failed");
        }
        groupSig = TestGroupSig.load(contractAddr, client, credential);
        try {
            groupSig.update_group_sig_data(
                    sigObj.getSig(), message, sigObj.getGPK(), sigObj.getParam());
            String sig = groupSig.get_group_sig();
            updatedSig.append(sig);
        } catch (Exception e) {
            logger.error("update group sig data failed, error_msg: " + e.getMessage());
            throw new Exception("update group sig data failed, error_msg: " + e.getMessage());
        }
    }

    // send signature to blockchain
    public void groupSigVerify(String contractAddress, StringBuffer verifyResult) throws Exception {
        groupSig = TestGroupSig.load(contractAddress, client, credential);
        try {
            groupSig.verify_group_sig();
            boolean ret = groupSig.get_group_verify_result();
            if (ret) verifyResult.append("true");
            else verifyResult.append("false");
        } catch (Exception e) {
            logger.error("get cns code failed, error msg: " + e.getMessage());
            throw new Exception("get cns code failed, error msg: " + e.getMessage());
        }
    }

    // update ring sig data
    public void updateRingSigData(
            String contractAddr,
            String message,
            String ringName,
            int memberPos,
            int ringSize,
            StringBuffer verifyResult)
            throws Exception {
        SigStruct ringSigObj = new SigStruct();
        boolean ret =
                sigService.linkableRingSig(ringSigObj, message, ringName, memberPos, ringSize);
        if (!ret) {
            throw new Exception("call rpc failed");
        }
        ringSig = TestRingSig.load(contractAddr, client, credential);
        try {
            ringSig.update_ring_sig_data(ringSigObj.getSig(), message, ringSigObj.getParam());
            String result = ringSig.get_ring_sig();
            verifyResult.append(result);
        } catch (Exception e) {
            logger.error("update ring sig data failed, error msg: " + e.getMessage());
            throw new Exception("update ring sig data failed, error msg: " + e.getMessage());
        }
    }

    // ring sig verify
    public void ringSigVerify(String contractAddress, StringBuffer verifyResult) throws Exception {
        // load contract
        ringSig = TestRingSig.load(contractAddress, client, credential);
        try {
            ringSig.verify_ring_sig();
            boolean ret = ringSig.get_ring_verify_result();
            if (ret) verifyResult.append("true");
            else verifyResult.append("false");
        } catch (Exception e) {
            logger.error("get cns code failed, error_msg: " + e.getMessage());
            throw new Exception("get cns code failed, error_msg: " + e.getMessage());
        }
    }
}
