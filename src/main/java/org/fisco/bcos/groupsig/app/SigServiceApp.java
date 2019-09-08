/*
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License. See accompanying LICENSE file.
 */

package org.fisco.bcos.groupsig.app;

//common classes

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fisco.bcos.channel.client.Service;
import org.fisco.bcos.groupsig.contract.TestGroupSig;
import org.fisco.bcos.groupsig.contract.TestRingSig;
import org.fisco.bcos.web3j.crypto.Credentials;
import org.fisco.bcos.web3j.protocol.Web3j;
import org.fisco.bcos.web3j.protocol.channel.ChannelEthereumService;
import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.math.BigInteger;

//web3j related
//common classes of authentication
//eth authentication related
//solidity java class generated automatically by web3sdk tool
//abi related classes
//self-defined classes

public class SigServiceApp {
    private static Logger logger = LogManager.getLogger(RequestSigService.class);
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
    // gas related
    private final static BigInteger gasPrice = new BigInteger("99999999999");
    private final static BigInteger gasLimit = new BigInteger("9999999999999");

    // construct function
    public SigServiceApp(RequestSigService paramSigService) {
        web3j = null;
        context = null;
        groupSig = null;
        ringSig = null;
        sigService = paramSigService;
    }

    // load web3j configuration(define node&&channel port information) and init credentials
    public boolean loadConfig() throws Exception {
        Service service;
        try {
            context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
            service = context.getBean(Service.class);
            service.run();
        } catch (Exception e) {
            logger.error("load config failed, error msg:" + e.getMessage());
            return false;
        }

        credentials = Credentials.create("b83261efa42895c38c6c2364ca878f43e77f3cddbc922bf57d0d48070f79feb6");

        // channel eth service
        ChannelEthereumService channelService = new ChannelEthereumService();
        channelService.setChannelService(service);

        web3j = Web3j.build(channelService);
        return true;
    }

    // deploy contract: return contract address
    public int deployGroupSigContract(String groupName, String memberName, String message, StringBuffer address) {
        System.out.println("###begin deploy group sig contract");
        try {
            Service service = context.getBean(Service.class);
            service.run();
        } catch (Exception e) {
            logger.error("deploy group sig contract failed, error_msg:" + e.getMessage());
            return RetCode.SERVICE_RUN_FAILED;
        }
        // callback group sig service
        SigStruct sigObj = new SigStruct();
        boolean ret = sigService.groupSig(sigObj, groupName, memberName, message);
        System.out.println("##SIG:" + sigObj.getSig());
        System.out.println("###GPK:" + sigObj.getGPK());
        System.out.println("###PBC_PARAM:" + sigObj.getParam());
        if (!ret)
            return RetCode.CALL_GROUPSIG_RPC_FAILED;
        try {
            groupSig = TestGroupSig.deploy(web3j, credentials, gasPrice, gasLimit, sigObj.getSig(), message, sigObj.getGPK(), sigObj.getParam()).send();
        } catch (Exception e) {
            logger.error("deploy group sig contract failed, error_msg:" + e.getMessage());
            return RetCode.DEPLOY_GROUP_CONTRACT_FAILED;
        }
        if (null != groupSig) {
            address.append(groupSig.getContractAddress());
            System.out.println("RESULT OF DEPLOY GROUP SIG CONTRACT: " + address);
            return RetCode.SUCCESS;
        }
        ;
        return RetCode.DEPLOY_GROUP_CONTRACT_FAILED;
    }

    // deploy ring_sig contract
    public int deployRingSigContract(String message, String ringName, int memberPos, int ringSize, StringBuffer address) {
        try {
            Service service = context.getBean(Service.class);
            service.run();
        } catch (Exception e) {
            logger.error("init service failed, error msg:" + e.getMessage());
            return RetCode.SERVICE_RUN_FAILED;
        }

        SigStruct ringSigObj = new SigStruct();
        boolean ret = sigService.linkableRingSig(ringSigObj, message, ringName, memberPos, ringSize);
        if (!ret)
            return RetCode.CALL_RINGSIG_RPC_FAILED;
        try {
            ringSig = TestRingSig.deploy(web3j, credentials, gasPrice, gasLimit, ringSigObj.getSig(), message, ringSigObj.getParam()).send();
        } catch (Exception e) {
            logger.error("deploy ring sig contract failed, error_msg:" + e.getMessage());
            return RetCode.DEPLOY_RING_CONTRACT_FAILED;
        }
        if (null != ringSig) {
            address.append(ringSig.getContractAddress());
            return RetCode.SUCCESS;
        }
        return RetCode.DEPLOY_RING_CONTRACT_FAILED;
    }

    // update group sig data
    public int updateGroupSigData(String contractAddr, String groupName, String memberName, String message, StringBuffer updatedSig) {
        SigStruct sigObj = new SigStruct();
        boolean ret = sigService.groupSig(sigObj, groupName, memberName, message);
        if (!ret) {
            return RetCode.CALL_GROUPSIG_RPC_FAILED;
        }
        groupSig = TestGroupSig.load(contractAddr, web3j, credentials, gasPrice, gasLimit);

        try {
            TransactionReceipt receipt = groupSig.update_sig_data(sigObj.getSig(), message, sigObj.getGPK(), sigObj.getParam()).send();
            String sig = groupSig.get_sig().send();
            updatedSig.append(sig);
            return RetCode.SUCCESS;
        } catch (Exception e) {
            logger.error("update group sig data failed, error_msg:" + e.getMessage());
            return RetCode.UPDATE_GROUP_SIG_DATA_FAILED;
        }
    }

    // send signature to blockchain
    public int groupSigVerify(String contractAddress, StringBuffer verifyResult) {
        groupSig = TestGroupSig.load(contractAddress, web3j, credentials, gasPrice, gasLimit);
        try {
            TransactionReceipt receipt = groupSig.verify().send();
            boolean ret = groupSig.get_verify_result().send();
            if (ret) verifyResult.append("true");
            else verifyResult.append("false");

            return RetCode.SUCCESS;
        } catch (Exception e) {
            logger.error("get cns code failed, error msg:" + e.getMessage());
            return RetCode.GET_CNS_CODE_FAILED;
        }
    }

    // update ring sig data
    public int updateRingSigData(String contractAddr, String message, String ringName, int memberPos, int ringSize, StringBuffer verifyResult) {
        SigStruct ringSigObj = new SigStruct();
        boolean ret = sigService.linkableRingSig(ringSigObj, message, ringName, memberPos, ringSize);
        if (!ret) {
            return RetCode.CALL_RINGSIG_RPC_FAILED;
        }
        ringSig = TestRingSig.load(contractAddr, web3j, credentials, gasPrice, gasLimit);

        try {
            TransactionReceipt receipt = ringSig.update_sig_data(ringSigObj.getSig(), message, ringSigObj.getParam()).send();
            String result = ringSig.get_sig().send();
            verifyResult.append(result);

            return RetCode.SUCCESS;
        } catch (Exception e) {
            logger.error("update ring sig data failed, error msg:" + e.getMessage());
            return RetCode.GET_CNS_CODE_FAILED;
        }

    }

    // ring sig verify
    public int ringSigVerify(String contractAddress, StringBuffer verifyResult) {
        // load contract
        ringSig = TestRingSig.load(contractAddress, web3j, credentials, gasPrice, gasLimit);
        try {
            TransactionReceipt receipt = ringSig.verify().send();
            boolean ret = ringSig.get_verify_result().send();
            if (ret) verifyResult.append("true");
            else verifyResult.append("false");

            return RetCode.SUCCESS;
        } catch (Exception e) {
            logger.error("get cns code failed, error_msg:" + e.getMessage());
            return RetCode.GET_CNS_CODE_FAILED;
        }
    }

}
