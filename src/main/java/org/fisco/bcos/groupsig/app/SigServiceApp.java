package org.fisco.bcos.groupsig.app;

import java.math.BigInteger;
import org.fisco.bcos.channel.client.Service;
import org.fisco.bcos.groupsig.contract.TestGroupSig;
import org.fisco.bcos.groupsig.contract.TestRingSig;
import org.fisco.bcos.web3j.crypto.Credentials;
import org.fisco.bcos.web3j.crypto.gm.GenCredential;
import org.fisco.bcos.web3j.protocol.Web3j;
import org.fisco.bcos.web3j.protocol.channel.ChannelEthereumService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SigServiceApp {
    private Logger logger = LoggerFactory.getLogger(SigServiceApp.class);
    // web3j
    private Web3j web3j;
    private ApplicationContext context;
    // solidity object
    // call back group signature algorithm implemented in precompiled contract
    private TestGroupSig groupSig;
    // call back ring signature algorithm implemented in precompiled contract
    private TestRingSig ringSig;
    private RequestSigService sigService;

    private Credentials credentials;
    private static final BigInteger gasPrice = new BigInteger("3000000000");
    private static final BigInteger gasLimit = new BigInteger("3000000000");

    // construct function
    public SigServiceApp(RequestSigService paramSigService) {
        sigService = paramSigService;
    }

    // load web3j configuration(define node&&channel port information) and init credentials
    public boolean loadConfig() throws Exception {
        System.out.println("please wait ...");
        Service service;
        try {
            context = new ClassPathXmlApplicationContext("classpath:node/application.xml");
            service = context.getBean(Service.class);
            service.run();
        } catch (Exception e) {
            logger.error("load config failed, error msg: " + e.getMessage());
            throw new Exception("load config failed, error msg: " + e.getMessage());
        }

        credentials = GenCredential.create();

        // channel eth service
        ChannelEthereumService channelService = new ChannelEthereumService();
        channelService.setChannelService(service);
        channelService.setTimeout(5000);
        web3j = Web3j.build(channelService, service.getGroupId());
        return true;
    }

    // deploy contract: return contract address
    public void deployGroupSigContract(
            String groupName, String memberName, String message, StringBuffer address)
            throws Exception {
        String errorMsg = "deploy group sig contract failed, error_msg: ";
        try {
            Service service = context.getBean(Service.class);
            service.run();
        } catch (Exception e) {
            logger.error(errorMsg + e.getMessage());
            throw new Exception(errorMsg + e.getMessage());
        }
        // callback group sig service
        SigStruct sigObj = new SigStruct();
        boolean ret = sigService.groupSig(sigObj, groupName, memberName, message);
        System.out.println("### SIG: " + sigObj.getSig());
        System.out.println("### GPK: " + sigObj.getGPK());
        System.out.println("### PBC_PARAM: " + sigObj.getParam());
        if (!ret) throw new Exception(errorMsg + "call rpc failed");
        try {
            groupSig =
                    TestGroupSig.deploy(
                                    web3j,
                                    credentials,
                                    gasPrice,
                                    gasLimit,
                                    sigObj.getSig(),
                                    message,
                                    sigObj.getGPK(),
                                    sigObj.getParam())
                            .send();
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
        try {
            Service service = context.getBean(Service.class);
            service.run();
        } catch (Exception e) {
            logger.error("init service failed, error msg: " + e.getMessage());
            throw new Exception("init service failed, error msg: " + e.getMessage());
        }

        SigStruct ringSigObj = new SigStruct();
        boolean ret =
                sigService.linkableRingSig(ringSigObj, message, ringName, memberPos, ringSize);
        if (!ret) throw new Exception("call rpc failed");
        try {
            ringSig =
                    TestRingSig.deploy(
                                    web3j,
                                    credentials,
                                    gasPrice,
                                    gasLimit,
                                    ringSigObj.getSig(),
                                    message,
                                    ringSigObj.getParam())
                            .send();
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
        groupSig = TestGroupSig.load(contractAddr, web3j, credentials, gasPrice, gasLimit);

        try {
            groupSig.update_group_sig_data(
                            sigObj.getSig(), message, sigObj.getGPK(), sigObj.getParam())
                    .send();
            String sig = groupSig.get_group_sig().send();
            updatedSig.append(sig);
        } catch (Exception e) {
            logger.error("update group sig data failed, error_msg: " + e.getMessage());
            throw new Exception("update group sig data failed, error_msg: " + e.getMessage());
        }
    }

    // send signature to blockchain
    public void groupSigVerify(String contractAddress, StringBuffer verifyResult) throws Exception {
        groupSig = TestGroupSig.load(contractAddress, web3j, credentials, gasPrice, gasLimit);
        try {
            groupSig.verify_group_sig().send();
            boolean ret = groupSig.get_group_verify_result().send();
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
        ringSig = TestRingSig.load(contractAddr, web3j, credentials, gasPrice, gasLimit);

        try {
            ringSig.update_ring_sig_data(ringSigObj.getSig(), message, ringSigObj.getParam())
                    .send();
            String result = ringSig.get_ring_sig().send();
            verifyResult.append(result);
        } catch (Exception e) {
            logger.error("update ring sig data failed, error msg: " + e.getMessage());
            throw new Exception("update ring sig data failed, error msg: " + e.getMessage());
        }
    }

    // ring sig verify
    public void ringSigVerify(String contractAddress, StringBuffer verifyResult) throws Exception {
        // load contract
        ringSig = TestRingSig.load(contractAddress, web3j, credentials, gasPrice, gasLimit);
        try {
            ringSig.verify_ring_sig().send();
            boolean ret = ringSig.get_ring_verify_result().send();
            if (ret) verifyResult.append("true");
            else verifyResult.append("false");
        } catch (Exception e) {
            logger.error("get cns code failed, error_msg: " + e.getMessage());
            throw new Exception("get cns code failed, error_msg: " + e.getMessage());
        }
    }
}
