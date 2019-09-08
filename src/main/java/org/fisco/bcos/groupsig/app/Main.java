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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;


public class Main {
    private static Logger logger = LogManager.getLogger(RequestSigService.class);

    public static void main(String[] args) {
        try {
            ConfigParser configObj = new ConfigParser(args[0]);
            String url = "http://" + configObj.getConnIp() + ":" + configObj.getConnPort();
            System.out.println("url = " + url);
            RequestSigService sigServiceRequestor = new RequestSigService(url);
            SigServiceApp sigApp = new SigServiceApp(sigServiceRequestor);
            boolean configure = sigApp.loadConfig();
            if (!configure) {
                System.out.println("init configuration failed");
            }
            rpcTest(args, sigServiceRequestor, configObj.getThreadNum());
            fiscoCallBack(args, sigApp, configObj.getThreadNum());
            System.exit(0);
        } catch (Exception e) {
            logger.error("input args is unvalid, error msg:" + e.getMessage());
        }
    }

    // deploy contract
    public static StringBuffer fiscoCallBack(String[] args, SigServiceApp sigApp, int threadNum) throws Exception {

        String method = args[1];
        StringBuffer contractAddr = new StringBuffer("");
        // deploy group sig
        if (method.equals("deploy_group_sig")) {
            if (args.length != 5) return null;
            // group_name, member_name, message
            int retCode = sigApp.deployGroupSigContract(args[2], args[3], args[4], contractAddr);
            RetCode.Msg(retCode, "");
            if (retCode == RetCode.SUCCESS)
                System.out.println("##RESULT OF deploy_group_sig(Contract Address):" + contractAddr);
            return contractAddr;

        }
        // deploy ring sig
        if (method.equals("deploy_ring_sig")) {
            int pos, size;
            if (args.length == 6) {
                try {
                    pos = Integer.valueOf(args[4]);
                    size = Integer.valueOf(args[5]);
                } catch (Exception e) {
                    logger.error("invalid member_pos or ring_size" + args[4] + args[5] + " , error msg:" + e.getMessage());
                    return null;
                }
                if (pos < 0 || pos > size || size > 32) {
                    System.out.println("Member's pos or ring size is invalid!");
                    return null;
                }
            } else
                return null;
            int retCode = sigApp.deployRingSigContract(args[2], args[3], pos, size, contractAddr);
            RetCode.Msg(retCode, "");
            if (retCode == RetCode.SUCCESS) {
                System.out.println("###RESULT OF deploy_ring_sig(Contract Address):" + contractAddr);
                return contractAddr;
            }
            return null;
        }

        // group sig verify
        if (method.equals("group_sig_verify")) {
            File file = new File("stat.log");
            PrintStream ps = new PrintStream(new FileOutputStream(file));
            try {
                int stressTest_ = 0;
                if (args.length > 6) {
                    try {
                        stressTest_ = Integer.parseInt(args[6]);
                    } catch (Exception e) {
                        logger.error("parse string " + args[6] + " to int failed, error msg:" + e.getMessage());
                    }
                }
                boolean stressTest = (stressTest_ == 0) ? false : true;
                int i = 0;
                ArrayList<Thread> threadArray = new ArrayList<Thread>();
                do {
                    System.out.println("###thread " + i);
                    threadArray.add(new Thread("" + i) {
                        public void run() {
                            do {
                                try {
                                    long startTime = System.currentTimeMillis();
                                    StringBuffer verifyResult = new StringBuffer("");
                                    int retCode = sigApp.groupSigVerify(args[2], verifyResult);
                                    long endTime = System.currentTimeMillis();
                                    RetCode.Msg(retCode, "");
                                    if (retCode == RetCode.SUCCESS) {
                                        ps.println((endTime - startTime) + "ms");
                                        System.out.println("time_eclipsed:" + (endTime - startTime) + "ms");
                                        System.out.println("verify result = " + verifyResult);
                                    }
                                } catch (Exception e) {
                                    logger.error(e.getMessage());
                                }
                            } while (stressTest);
                        }
                    });
                    threadArray.get(i).start();
                    i++;
                } while (stressTest && i < threadNum);
                for (int index = 0; index < threadArray.size(); index++) {
                    threadArray.get(index).join();
                }
            } catch (Exception e) {
                logger.error("callback group_sig_verify failed, error msg:" + e.getMessage());
            } finally {
                ps.close();
            }
            return null;
        }

        if (method.equals("update_group_sig_data")) {
            if (args.length != 6) return null;
            StringBuffer updatedSig = new StringBuffer("");
            int retCode = sigApp.updateGroupSigData(args[2], args[3], args[4], args[5], updatedSig);
            RetCode.Msg(retCode, "");
            if (retCode == RetCode.SUCCESS)
                System.out.println("updated group sig result:" + updatedSig);
            return null;
        }

        if (method.equals("ring_sig_verify")) {
            File file = new File("stat.log");
            PrintStream ps = new PrintStream(new FileOutputStream(file));
            try {
                int stressTest_ = 0;
                if (args.length > 6) {
                    stressTest_ = Integer.parseInt(args[6]);
                }
                boolean stressTest = (stressTest_ == 0) ? false : true;
                int i = 0;
                ArrayList<Thread> threadArray = new ArrayList<Thread>();
                do {
                    threadArray.add(new Thread("" + i) {
                        public void run() {
                            do {
                                try {
                                    long startTime = System.currentTimeMillis();
                                    StringBuffer verifyResult = new StringBuffer("");
                                    int retCode = sigApp.ringSigVerify(args[2], verifyResult);
                                    RetCode.Msg(retCode, "");
                                    long endTime = System.currentTimeMillis();
                                    if (retCode == RetCode.SUCCESS) {
                                        System.out.println("Verify result of ring sig = " + verifyResult);
                                        ps.println((endTime - startTime) + "ms");
                                        System.out.println("time_eclipsed:" + (endTime - startTime) + "ms");
                                    }
                                } catch (Exception e) {
                                    logger.error(e.getMessage());
                                }
                            } while (stressTest);
                        }
                    });
                    threadArray.get(i).start();
                    i++;
                } while (stressTest && i < threadNum);
                for (int index = 0; index < threadArray.size(); index++) {
                    threadArray.get(index).join();
                }
            } catch (Exception e) {
                logger.error("callback ring_sig_verify failed, error msg:" + e.getMessage());
            } finally {
                ps.close();
            }
            return null;
        }

        if (method.equals("update_ring_sig_data")) {
            StringBuffer updatedRingSig = new StringBuffer("");
            int pos, size;
            if (args.length == 7) {
                try {
                    pos = Integer.valueOf(args[5]);
                    size = Integer.valueOf(args[6]);
                } catch (Exception e) {
                    logger.error("invalid member_pos or ring_size" + args[5] + args[6] + " , error msg:" + e.getMessage());
                    return null;
                }
                if (pos < 0 || pos > size || size > 32) {
                    System.out.println("Member's pos or ring size is invalid!");
                    return null;
                }
            } else
                return null;
            int retCode = sigApp.updateRingSigData(args[2], args[3], args[4], pos, size, updatedRingSig);
            RetCode.Msg(retCode, "");
            if (retCode == RetCode.SUCCESS)
                System.out.println("update ring sig data result:" + updatedRingSig);
            return null;
        }

        return contractAddr;
    }

    public static void rpcTest(String[] args, RequestSigService sigServiceRequestor, int threadNum)
            throws Exception {
        String method = args[1];
        // System.out.println("rpc_test: callback: "+ method);

        String result = "";
        if (method.equals("create_group")) {
            String pbcParam = "";
            if (args.length >= 5) {
                pbcParam = args[4];
            }
            result = sigServiceRequestor.createGroup(args[2], args[3], pbcParam);
        }
        if (method.equals("join_group")) {
            result = sigServiceRequestor.joinGroup(args[2], args[3]);
        }
        if (method.equals("group_sig")) {
            File file = new File("stat.log");
            PrintStream ps = new PrintStream(new FileOutputStream(file));
            try {
                int stressTest_ = 0;
                if (args.length > 5) {
                    try {
                        stressTest_ = Integer.parseInt(args[5]);
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                }
                boolean stressTest = (stressTest_ == 0) ? false : true;

                int i = 0;
                ArrayList<Thread> threadArray = new ArrayList<Thread>();
                do {
                    System.out.println("####create thread");
                    threadArray.add(new Thread("" + i) {
                        public void run() {
                            do {
                                try {
                                    long startTime = System.currentTimeMillis();
                                    SigStruct sigObj = new SigStruct();
                                    boolean ret = sigServiceRequestor.groupSig(sigObj, args[2], args[3], args[4]);
                                    long endTime = System.currentTimeMillis(); //end time
                                    ps.println((endTime - startTime) + "ms");
                                    System.out.println("time_eclipsed:" + (endTime - startTime) + "ms");
                                    if (ret == false)
                                        System.out.println("GROUP SIG FAILED");
                                } catch (Exception e) {
                                    logger.error(e.getMessage());
                                }
                            } while (stressTest);
                        }
                    });
                    threadArray.get(i).start();
                    i++;
                } while (stressTest && i < threadNum);
                for (int index = 0; index < threadArray.size(); index++) {
                    threadArray.get(index).join();
                }
            } catch (Exception e) {
                logger.error("callback group_sig failed,error msg:" + e.getMessage());
            } finally {
                ps.close();
            }
        }

        if (method.equals("group_verify")) {
            File file = new File("stat_verify.log");
            PrintStream ps = new PrintStream(new FileOutputStream(file));
            try {
                int stressTest_ = 0;
                if (args.length > 5) {
                    try {
                        stressTest_ = Integer.parseInt(args[5]);
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                }
                boolean stressTest = (stressTest_ == 0) ? false : true;
                int i = 0;
                ArrayList<Thread> threadArray = new ArrayList<Thread>();
                do {
                    threadArray.add(new Thread("" + i) {
                        public void run() {
                            do {
                                try {
                                    long startTime = System.currentTimeMillis();
                                    String result = sigServiceRequestor.groupVerify(args[2], args[3], args[4]);
                                    System.out.println("group verify: " + result);
                                    long endTime = System.currentTimeMillis(); //end time
                                    ps.println((endTime - startTime) + "ms");
                                    System.out.println("time_eclipsed:" + (endTime - startTime) + "ms");
                                } catch (Exception e) {
                                    logger.error(e.getMessage());
                                }
                            } while (stressTest);
                        }
                    });
                    threadArray.get(i).start();
                    i++;
                } while (stressTest && i < threadNum);
                for (int index = 0; index < threadArray.size(); index++) {
                    threadArray.get(index).join();
                }
            } catch (Exception e) {
                logger.error("callback gruop_verify failed, error msg:" + e.getMessage());
            } finally {
                ps.close();
            }
        }

        if (method.equals("open_cert"))
            result = sigServiceRequestor.openCert(args[2], args[3], args[4], args[5]);

        if (method.equals("get_public_info"))
            result = sigServiceRequestor.getPublicInfo(args[2]);

        if (method.equals("get_gm_info"))
            result = sigServiceRequestor.getGMInfo(args[2], args[3]);

        if (method.equals("get_member_info"))
            result = sigServiceRequestor.getMemberInfo(args[2], args[3], args[4]);

        // ring sig
        if (method.equals("setup_ring")) {
            int bitLen = 1024;
            if (args.length > 3) {
                try {
                    bitLen = Integer.valueOf(args[3]);
                } catch (Exception e) {
                    logger.error("invalid bit len of public/private key " + args[3] + "error msg:" + e.getMessage());
                    return;
                }
            }
            result = sigServiceRequestor.setupRing(args[2], bitLen);
        }

        if (method.equals("join_ring"))
            result = sigServiceRequestor.joinRing(args[2]);

        if (method.equals("ring_sig")) {
            int stressTest_ = 0;
            if (args.length > 6) {
                try {
                    stressTest_ = Integer.valueOf(args[6]);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
            boolean stressTest = (stressTest_ == 0) ? false : true;
            File file = new File("stat_ring_sig.log");
            PrintStream ps = new PrintStream(new FileOutputStream(file));
            try {
                int pos, size;
                if (args.length > 5) {
                    try {
                        pos = Integer.valueOf(args[4]);
                        size = Integer.valueOf(args[5]);
                    } catch (Exception e) {
                        logger.error("invalid member_pos or ring_size" + args[4] + args[5] + " , error msg:" + e.getMessage());
                        return;
                    }
                    if (pos < 0 || pos > size || size > 32) {
                        System.out.println("Member's pos or ring size is invalid!");
                        return;
                    }
                } else
                    return;

                SigStruct ringSigObj = new SigStruct();
                int i = 0;
                ArrayList<Thread> threadArray = new ArrayList<Thread>();
                do {
                    threadArray.add(new Thread("" + i) {
                        public void run() {
                            do {
                                try {
                                    long startTime = System.currentTimeMillis();
                                    boolean ret = sigServiceRequestor.linkableRingSig(ringSigObj, args[2], args[3],
                                            pos, size);
                                    long endTime = System.currentTimeMillis();
                                    ps.println((endTime - startTime) + "ms");
                                    System.out.println("time_eclipsed:" + (endTime - startTime) + "ms");
                                    if (ret == false)
                                        System.out.println("LINKABLE RING SIG FAILED");
                                } catch (Exception e) {
                                    logger.error(e.getMessage());
                                }
                            } while (stressTest);
                        }
                    });
                    threadArray.get(i).start();
                    i++;
                } while (stressTest && (i < threadNum));
                for (int index = 0; index < threadArray.size(); index++) {
                    threadArray.get(index).join();
                }
            } catch (Exception e) {
                logger.error("callback ring_sig failed, error msg:" + e.getMessage());
            } finally {
                ps.close();
            }
        }

        if (method.equals("ring_verify")) {
            // System.out.println("args_len:" + args.length + " ring_size:"+ring_size);
            int stressTest_ = 0;
            if (args.length > 5) {
                try {
                    stressTest_ = Integer.valueOf(args[5]);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
            boolean stressTest = (stressTest_ == 0) ? false : true;
            File file = new File("stat_ring_sig.log");
            PrintStream ps = new PrintStream(new FileOutputStream(file));
            try {
                int i = 0;
                ArrayList<Thread> threadArray = new ArrayList<Thread>();
                do {
                    threadArray.add(new Thread("" + i) {
                        public void run() {
                            do {
                                try {
                                    long startTime = System.currentTimeMillis();
                                    String result = sigServiceRequestor.linkableRingVerify(args[2], args[3], args[4]);
                                    long endTime = System.currentTimeMillis();
                                    ps.println((endTime - startTime) + "ms");
                                    System.out.println("time_eclipsed:" + (endTime - startTime) + "ms");
                                } catch (Exception e) {
                                    logger.error(e.getMessage());
                                }
                            } while (stressTest);

                        }
                    });
                    threadArray.get(i).start();
                    i++;
                } while (stressTest && i < threadNum);
                for (int index = 0; index < threadArray.size(); index++) {
                    threadArray.get(index).join();
                }
            } catch (Exception e) {
                logger.error("callback ring_verify failed, error msg:" + e.getMessage());
            } finally {
                ps.close();
            }

        }

        if (method.equals("get_ring_param"))
            result = sigServiceRequestor.getRingParam(args[2]);

        if (method.equals("get_ring_public_key"))
            result = sigServiceRequestor.getRingPublicKey(args[2], args[3]);

        if (method.equals("get_ring_private_key"))
            result = sigServiceRequestor.getRingPrivateKey(args[2], args[3]);
    }

}
