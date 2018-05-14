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

package org.bcos.groupsig.app;

//common classes
import java.math.BigInteger;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import java.util.concurrent.ConcurrentHashMap;
//web3j related
import org.bcos.web3j.protocol.Web3j;
import org.bcos.channel.client.Service;
import org.bcos.web3j.protocol.channel.ChannelEthereumService;
import org.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.bcos.web3j.protocol.core.DefaultBlockParameterName;
import org.bcos.web3j.protocol.core.methods.response.EthGetCode;
import org.bcos.web3j.utils.Numeric;

//common classes of authentication
import java.security.KeyStore;
import java.security.Key;
import java.security.SignatureException;
import java.security.interfaces.ECPrivateKey;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

//eth authentication related
import org.bcos.web3j.crypto.Credentials;
import org.bcos.web3j.crypto.ECKeyPair;
import org.bcos.web3j.crypto.Keys;
import org.bcos.web3j.crypto.Sign;

//solidity java class generated automatically by web3sdk tool
import org.bcos.groupsig.group_sig_sol.TestGroupSig;
import org.bcos.groupsig.group_sig_sol.TestRingSig;

//abi related classes
import org.bcos.web3j.abi.datatypes.Address;
import org.bcos.web3j.abi.datatypes.Utf8String;
import org.bcos.web3j.abi.datatypes.generated.Uint8;

//self-defined classes
import org.bcos.groupsig.app.SigStruct;

public class SigServiceApp {
	private static Logger logger = LogManager.getLogger(RequestSigService.class);
	// web3j
	private Web3j web3j;
	ApplicationContext context;
	// solidity object
	// call back group signature algorithm implemented in ethcall
	private TestGroupSig groupSig;
	// call back ring signature algorithm implemented in ethcall
	private TestRingSig ringSig;
	private RequestSigService sigService;

	// gas related
	public final static BigInteger gasPrice = new BigInteger("99999999999");
	public final static BigInteger gasLimit = new BigInteger("9999999999999");
	public final static BigInteger initGas = new BigInteger("0");

	// construct function
	public SigServiceApp(RequestSigService paramSigService) {
		web3j = null;
		context = null;
		groupSig = null;
		ringSig = null;
		sigService = paramSigService;
	}

	// load web3j configuration(define node&&channel port information)
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

		// channel eth service
		ChannelEthereumService channelService = new ChannelEthereumService();
		channelService.setChannelService(service);
		web3j = Web3j.build(channelService);
		if (web3j != null)
			return true;
		return false;
	}

	// deploy contract: return contract address
	public int deployGroupSigContract(String keystoreFile, String keystorePass, String keyPass, String groupName,
			String memberName, String message, StringBuffer address) {
		System.out.println("###begin deploy group sig contract");
		Credentials cert = loadKey(keystoreFile, keystorePass, keyPass);
		if (null == cert)
			return RetCode.LOAD_CERT_FAILED;
		try {
			Service service = context.getBean(Service.class);
			service.run();
		} catch (Exception e) {
			logger.error("deploy group sig contract failed, error_msg:" + e.getMessage());
			return RetCode.SERVICE_RUN_FAILED;
		}
		// callback group sig service
		// args[4]: group_name
		// agrs[5]: member_name
		// agrs[6]: message
		SigStruct sigObj = new SigStruct();
		boolean ret = sigService.groupSig(sigObj, groupName, memberName, message);
		System.out.println("##SIG:" + sigObj.getSig());
		System.out.println("###GPK:" + sigObj.getGPK());
		System.out.println("###PBC_PARAM:" + sigObj.getParam());
		if (!ret)
			return RetCode.CALL_GROUPSIG_RPC_FAILED;
		try {
			groupSig = TestGroupSig
					.deploy(web3j, cert, gasPrice, gasLimit, initGas, new Utf8String(sigObj.getSig()),
							new Utf8String(message), new Utf8String(sigObj.getGPK()), new Utf8String(sigObj.getParam()))
					.get();
		} catch (Exception e) {
			logger.error("deploy group sig contract failed, error_msg:" + e.getMessage());
			return RetCode.DEPLOY_GROUP_CONTRACT_FAILED;
		}
		if (null != groupSig) {
			address.append(groupSig.getContractAddress());
			System.out.println("RESULT OF DEPLOY GROUP SIG CONTRACT: " + address);
			return RetCode.SUCCESS;
		};
		return RetCode.DEPLOY_GROUP_CONTRACT_FAILED;
	}

	// deploy ring_sig contract
	public int deployRingSigContract(String keystoreFile, String keystorePass, String keyPass, String message,
			String ringName, String memberPos, int ringSize, StringBuffer address) {
		Credentials cert = loadKey(keystoreFile, keystorePass, keyPass);
		if (null == cert)
			return RetCode.LOAD_CERT_FAILED;
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
			ringSig = TestRingSig.deploy(web3j, cert, gasPrice, gasLimit, initGas, new Utf8String(ringSigObj.getSig()),
					new Utf8String(message), new Utf8String(ringSigObj.getParam())).get();
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

	// check ethGetCode
	private boolean checkCnsCode(String contractAddress, String currentCnsCode) {
		if (Address.DEFAULT.toString().contains(contractAddress)
				|| contractAddress.contains(Address.DEFAULT.toString())) {
			System.out.println("invalid contract address:" + contractAddress);
			return false;
		}
		EthGetCode ethGetCode;
		try {
			ethGetCode = web3j.ethGetCode(contractAddress, DefaultBlockParameterName.LATEST).send();
			String code = Numeric.cleanHexPrefix(ethGetCode.getCode());
			if (currentCnsCode.contains(code))
				return true;
			return false;
		} catch (Exception e) {
			logger.error("get ethcode failed, error msg:" + e.getMessage());
			return false;
		}
	}

	// update group sig data
	public int updateGroupSigData(String keystoreFile, String keystorePass, String keyPass, String contractAddr,
			String groupName, String memberName, String message, StringBuffer updatedSig) {
		SigStruct sigObj = new SigStruct();
		boolean ret = sigService.groupSig(sigObj, groupName, memberName, message);
		System.out.println("##SIG:" + sigObj.getSig());
		System.out.println("###GPK:" + sigObj.getGPK());
		System.out.println("###PBC_PARAM:" + sigObj.getParam());
		if (false == ret) {
			return RetCode.CALL_GROUPSIG_RPC_FAILED;
		}
		Credentials cert = loadKey(keystoreFile, keystorePass, keyPass);
		if (null == cert) {
			return RetCode.LOAD_CERT_FAILED;
		}
		groupSig = TestGroupSig.load(contractAddr, web3j, cert, gasPrice, gasLimit);
		if (null == groupSig) {
			return RetCode.LOAD_CONTRACT_FAILED;
		}

		if (checkCnsCode(contractAddr, groupSig.getContractBinary())) {
			try {
				TransactionReceipt receipt = groupSig.update_sig_data(new Utf8String(sigObj.getSig()),
						new Utf8String(message), new Utf8String(sigObj.getGPK()), new Utf8String(sigObj.getParam()))
						.get();
				String sig = groupSig.get_sig().get().toString();
				updatedSig.append(sig);
				return RetCode.SUCCESS;
			} catch (Exception e) {
				logger.error("update group sig data failed, error_msg:" + e.getMessage());
				return RetCode.UPDATE_GROUP_SIG_DATA_FAILED;
			}
		} else {
			return RetCode.INVALID_CONTRACT_ADDRESS;
		}
	}

	// send signature to blockchain
	public int groupSigVerify(String keystoreFile, String keystorePass, String keyPass, String contractAddress,
			StringBuffer verifyResult) {
		// obtain credentials
		Credentials cert = loadKey(keystoreFile, keystorePass, keyPass);
		// load contract
		if (cert == null) {
			return RetCode.LOAD_CERT_FAILED;
		}
		groupSig = TestGroupSig.load(contractAddress, web3j, cert, gasPrice, gasLimit);
		if (null == groupSig) {
			return RetCode.LOAD_CONTRACT_FAILED;
		}
		if (checkCnsCode(contractAddress, groupSig.getContractBinary())) {
			try {
				TransactionReceipt receipt = groupSig.verify().get();
				String result = groupSig.get_verify_result().get().toString();
				verifyResult.append(result);
				return RetCode.SUCCESS;
			} catch (Exception e) {
				logger.error("get cns code failed, error msg:" + e.getMessage());
				return RetCode.GET_CNS_CODE_FAILED;
			}
		} else {
			return RetCode.INVALID_CONTRACT_ADDRESS;
		}
	}

	// update ring sig data
	public int updateRingSigData(String keystoreFile, String keystorePass, String keyPass, String contractAddr,
			String message, String ringName, String memberPos, int ringSize, StringBuffer verifyResult) {
		SigStruct ringSigObj = new SigStruct();
		boolean ret = sigService.linkableRingSig(ringSigObj, message, ringName, memberPos, ringSize);
		if (false == ret) {
			return RetCode.CALL_RINGSIG_RPC_FAILED;
		}
		// obtain credential
		Credentials cert = loadKey(keystoreFile, keystorePass, keyPass);
		if (null == cert) {
			return RetCode.LOAD_CERT_FAILED;
		}
		ringSig = TestRingSig.load(contractAddr, web3j, cert, gasPrice, gasLimit);
		if (null == ringSig) {
			return RetCode.LOAD_CONTRACT_FAILED;
		}
		if (checkCnsCode(contractAddr, ringSig.getContractBinary())) {
			try {
				TransactionReceipt receipt = ringSig.update_sig_data(new Utf8String(ringSigObj.getSig()),
						new Utf8String(message), new Utf8String(ringSigObj.getParam())).get();
				String result = ringSig.get_sig().get().toString();
				verifyResult.append(result);
				return RetCode.SUCCESS;
			} catch (Exception e) {
				logger.error("update ring sig data failed, error msg:" + e.getMessage());
				return RetCode.GET_CNS_CODE_FAILED;
			}
		} else {
			return RetCode.INVALID_CONTRACT_ADDRESS;
		}

	}

	// ring sig verify
	public int ringSigVerify(String keystoreFile, String keystorePass, String keyPass, String contractAddress,
			StringBuffer verifyResult) {
		// obtain credentials
		Credentials cert = loadKey(keystoreFile, keystorePass, keyPass);
		if (null == cert) {
			return RetCode.LOAD_CERT_FAILED;
		}
		// load contract
		ringSig = TestRingSig.load(contractAddress, web3j, cert, gasPrice, gasLimit);
		if (null == ringSig) {
			return RetCode.LOAD_CONTRACT_FAILED;
		}

		if (checkCnsCode(contractAddress, ringSig.getContractBinary())) {
			try {
				TransactionReceipt receipt = ringSig.verify().get();
				String result = ringSig.get_verify_result().get().toString();
				verifyResult.append(result);
				return RetCode.SUCCESS;
			} catch (Exception e) {
				logger.error("get cns code failed, error_msg:" + e.getMessage());
				return RetCode.GET_CNS_CODE_FAILED;
			}
		} else {
			return RetCode.INVALID_CONTRACT_ADDRESS;
		}
	}

	// load Credentials
	private Credentials loadKey(String keystoreFile, String keystorePass, String keyPass) {
		if (web3j == null)
			return null;
		InputStream keystoreStream = null;
		try {
			System.out.println("keystore_file:" + keystoreFile);
			KeyStore ks = KeyStore.getInstance("JKS");
			keystoreStream = SigServiceApp.class.getClassLoader().getResourceAsStream(keystoreFile);

			ks.load(keystoreStream, keystorePass.toCharArray());
			System.out.println("load keystore succeed:" + ks.containsAlias("ec"));
			System.out.println("keystore_pass: " + keystorePass + "  key_pass:" + keyPass);
			Key key = ks.getKey("ec", keyPass.toCharArray());
			ECKeyPair keyPair = null;
			if (key != null)
				keyPair = ECKeyPair.create(((ECPrivateKey) key).getS());
			else {
				System.out.println("create key failed");
				return null;
			}

			// create cert
			Credentials cert = null;
			if (keyPair != null)
				cert = Credentials.create(keyPair);
			else {
				System.out.println("create key_pair failed");
				return null;
			}
			if (cert != null)
				return cert;
			else {
				System.out.println("invalid key input");
			}
		} catch (Exception e) {
			logger.error("##LOADKEY FAILED, error msg:" + e.getMessage());
		} finally {
			if (keystoreStream != null) {
				try {
					keystoreStream.close();
				} catch (Exception e) {
					logger.error("close keystore stream failed, error msg:" + e.getMessage());
				}
			}
		}
		return null;
	}
}
