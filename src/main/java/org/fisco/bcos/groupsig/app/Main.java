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

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static Logger logger = LoggerFactory.getLogger(Main.class);
    private static boolean RPCFlag = true;

    public static void main(String[] args) {
        try {
            ConfigParser configObj = new ConfigParser(args[0]);
            String url = "http://" + configObj.getConnIp() + ":" + configObj.getConnPort();
            RequestSigService sigServiceRequestor = new RequestSigService(url);
            SigServiceApp sigApp = new SigServiceApp(sigServiceRequestor);
            callRpc(args, sigServiceRequestor, configObj.getThreadNum(), "RPC-URL: " + url);
            callFisco(args, sigApp, configObj.getThreadNum());
            System.exit(0);
        } catch (Exception e) {
            logger.error(e.getMessage());
            System.out.println("extra error message: " + e.getMessage());
            System.exit(-1);
        }
    }

    private static void callRpc(
            String[] args, RequestSigService sigServiceRequestor, int threadNum, String url) {
        String method = args[1];
        switch (method) {
            case "create_group":
                if (args.length != 4 && args.length != 5) {
                    System.out.println("illegal parameters");
                    return;
                }
                System.out.println(url);
                String pbcParam = "";
                if (args.length >= 5) {
                    pbcParam = args[4];
                }
                sigServiceRequestor.createGroup(args[2], args[3], pbcParam);
                break;
            case "join_group":
                if (args.length != 4) {
                    System.out.println("illegal parameters");
                    return;
                }
                System.out.println(url);
                sigServiceRequestor.joinGroup(args[2], args[3]);
                break;
            case "group_sig":
                {
                    if (args.length != 5 && args.length != 6) {
                        System.out.println("illegal parameters");
                        return;
                    }
                    System.out.println(url);
                    File file = new File("stat.log");
                    try (PrintStream ps = new PrintStream(new FileOutputStream(file))) {
                        int stressTest_ = 0;
                        if (args.length == 6) {
                            try {
                                stressTest_ = Integer.parseInt(args[5]);
                            } catch (Exception e) {
                                logger.error(e.getMessage());
                            }
                        }
                        boolean stressTest = stressTest_ != 0;

                        int i = 0;
                        ArrayList<Thread> threadArray = new ArrayList<>();
                        do {
                            // create thread
                            threadArray.add(
                                    new Thread("thread" + i) {
                                        public void run() {
                                            do {
                                                try {
                                                    long startTime = System.currentTimeMillis();
                                                    SigStruct sigObj = new SigStruct();
                                                    boolean ret =
                                                            sigServiceRequestor.groupSig(
                                                                    sigObj, args[2], args[3],
                                                                    args[4]);
                                                    long endTime =
                                                            System.currentTimeMillis(); // end time
                                                    ps.println((endTime - startTime) + "ms");
                                                    System.out.println(
                                                            "time_eclipsed:"
                                                                    + (endTime - startTime)
                                                                    + "ms");
                                                    if (!ret)
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
                        for (Thread thread : threadArray) {
                            thread.join();
                        }
                    } catch (Exception e) {
                        logger.error("callback group_sig failed,error msg:" + e.getMessage());
                    }
                    break;
                }
            case "group_verify":
                {
                    if (args.length != 5 && args.length != 6) {
                        System.out.println("illegal parameters");
                        return;
                    }
                    System.out.println(url);
                    File file = new File("stat_verify.log");
                    try (PrintStream ps = new PrintStream(new FileOutputStream(file))) {
                        int stressTest_ = 0;
                        if (args.length == 6) {
                            try {
                                stressTest_ = Integer.parseInt(args[5]);
                            } catch (Exception e) {
                                logger.error(e.getMessage());
                            }
                        }
                        boolean stressTest = stressTest_ != 0;
                        int i = 0;
                        ArrayList<Thread> threadArray = new ArrayList<>();
                        do {
                            threadArray.add(
                                    new Thread("thread" + i) {
                                        public void run() {
                                            do {
                                                try {
                                                    long startTime = System.currentTimeMillis();
                                                    String result =
                                                            sigServiceRequestor.groupVerify(
                                                                    args[2], args[3], args[4]);
                                                    System.out.println("group verify: " + result);
                                                    long endTime =
                                                            System.currentTimeMillis(); // end time
                                                    ps.println((endTime - startTime) + "ms");
                                                    System.out.println(
                                                            "time_eclipsed:"
                                                                    + (endTime - startTime)
                                                                    + "ms");
                                                } catch (Exception e) {
                                                    logger.error(e.getMessage());
                                                }
                                            } while (stressTest);
                                        }
                                    });
                            threadArray.get(i).start();
                            i++;
                        } while (stressTest && i < threadNum);
                        for (Thread thread : threadArray) {
                            thread.join();
                        }
                    } catch (Exception e) {
                        logger.error("callback gruop_verify failed, error msg:" + e.getMessage());
                    }
                    break;
                }
            case "open_cert":
                if (args.length != 6) {
                    System.out.println("illegal parameters");
                    return;
                }
                System.out.println(url);
                sigServiceRequestor.openCert(args[2], args[3], args[4], args[5]);
                break;
            case "get_public_info":
                if (args.length != 3) {
                    System.out.println("illegal parameters");
                    return;
                }
                System.out.println(url);
                sigServiceRequestor.getPublicInfo(args[2]);
                break;
            case "get_gm_info":
                if (args.length != 4) {
                    System.out.println("illegal parameters");
                    return;
                }
                System.out.println(url);
                sigServiceRequestor.getGMInfo(args[2], args[3]);
                break;
            case "get_member_info":
                if (args.length != 5) {
                    System.out.println("illegal parameters");
                    return;
                }
                sigServiceRequestor.getMemberInfo(args[2], args[3], args[4]);
                break;

                // ring sig
            case "setup_ring":
                if (args.length != 3 && args.length != 4) {
                    System.out.println("illegal parameters");
                    return;
                }
                System.out.println(url);
                int bitLen = 1024;
                if (args.length == 4) {
                    try {
                        bitLen = Integer.parseInt(args[3]);
                    } catch (Exception e) {
                        logger.error(
                                "invalid bit len of public/private key "
                                        + args[3]
                                        + "error msg:"
                                        + e.getMessage());
                        return;
                    }
                }
                sigServiceRequestor.setupRing(args[2], bitLen);
                break;
            case "join_ring":
                if (args.length != 3) {
                    System.out.println("illegal parameters");
                    return;
                }
                System.out.println(url);
                sigServiceRequestor.joinRing(args[2]);
                break;
            case "ring_sig":
                {
                    if (args.length != 6 && args.length != 7) {
                        System.out.println("illegal parameters");
                        return;
                    }
                    System.out.println(url);
                    int stressTest_ = 0;
                    if (args.length == 7) {
                        try {
                            stressTest_ = Integer.parseInt(args[6]);
                        } catch (Exception e) {
                            logger.error(e.getMessage());
                        }
                    }
                    boolean stressTest = stressTest_ != 0;
                    File file = new File("stat_ring_sig.log");
                    try (PrintStream ps = new PrintStream(new FileOutputStream(file))) {
                        int pos, size;
                        try {
                            pos = Integer.parseInt(args[4]);
                            size = Integer.parseInt(args[5]);
                        } catch (Exception e) {
                            System.out.println(
                                    "invalid member_pos or ring_size"
                                            + args[4]
                                            + args[5]
                                            + " , error msg:"
                                            + e.getMessage());
                            return;
                        }
                        if (pos < 0 || pos > size || size > 32) {
                            System.out.println("member's pos or ring-size is invalid!");
                            System.out.println("0 <= pos < ring-size <= 32");
                            return;
                        }

                        SigStruct ringSigObj = new SigStruct();
                        int i = 0;
                        ArrayList<Thread> threadArray = new ArrayList<>();
                        do {
                            threadArray.add(
                                    new Thread("thread" + i) {
                                        public void run() {
                                            do {
                                                try {
                                                    long startTime = System.currentTimeMillis();
                                                    boolean ret =
                                                            sigServiceRequestor.linkableRingSig(
                                                                    ringSigObj,
                                                                    args[2],
                                                                    args[3],
                                                                    pos,
                                                                    size);
                                                    long endTime = System.currentTimeMillis();
                                                    ps.println((endTime - startTime) + "ms");
                                                    System.out.println(
                                                            "time_eclipsed:"
                                                                    + (endTime - startTime)
                                                                    + "ms");
                                                    if (!ret)
                                                        System.out.println(
                                                                "LINKABLE RING SIG FAILED");
                                                } catch (Exception e) {
                                                    logger.error(e.getMessage());
                                                }
                                            } while (stressTest);
                                        }
                                    });
                            threadArray.get(i).start();
                            i++;
                        } while (stressTest && (i < threadNum));
                        for (Thread thread : threadArray) {
                            thread.join();
                        }
                    } catch (Exception e) {
                        logger.error("callback ring_sig failed, error msg:" + e.getMessage());
                    }
                    break;
                }
            case "ring_verify":
                {
                    if (args.length != 5 && args.length != 6) {
                        System.out.println("illegal parameters");
                        return;
                    }
                    System.out.println(url);
                    // System.out.println("args_len:" + args.length + " ring_size:"+ring_size);
                    int stressTest_ = 0;
                    if (args.length == 6) {
                        try {
                            stressTest_ = Integer.parseInt(args[5]);
                        } catch (Exception e) {
                            logger.error(e.getMessage());
                        }
                    }
                    boolean stressTest = stressTest_ != 0;
                    File file = new File("stat_ring_sig.log");
                    try (PrintStream ps = new PrintStream(new FileOutputStream(file))) {
                        int i = 0;
                        ArrayList<Thread> threadArray = new ArrayList<>();
                        do {
                            threadArray.add(
                                    new Thread("thread" + i) {
                                        public void run() {
                                            do {
                                                try {
                                                    long startTime = System.currentTimeMillis();
                                                    sigServiceRequestor.linkableRingVerify(
                                                            args[2], args[3], args[4]);
                                                    long endTime = System.currentTimeMillis();
                                                    ps.println((endTime - startTime) + "ms");
                                                    System.out.println(
                                                            "time_eclipsed:"
                                                                    + (endTime - startTime)
                                                                    + "ms");
                                                } catch (Exception e) {
                                                    logger.error(e.getMessage());
                                                }
                                            } while (stressTest);
                                        }
                                    });
                            threadArray.get(i).start();
                            i++;
                        } while (stressTest && i < threadNum);
                        for (Thread thread : threadArray) {
                            thread.join();
                        }
                    } catch (Exception e) {
                        logger.error("callback ring_verify failed, error msg:" + e.getMessage());
                    }
                    break;
                }
            case "get_ring_param":
                if (args.length != 3) {
                    System.out.println("illegal parameters");
                    return;
                }
                System.out.println(url);
                sigServiceRequestor.getRingParam(args[2]);
                break;
            case "get_ring_public_key":
                if (args.length != 4) {
                    System.out.println("illegal parameters");
                    return;
                }
                System.out.println(url);
                sigServiceRequestor.getRingPublicKey(args[2], args[3]);
                break;
            case "get_ring_private_key":
                if (args.length != 4) {
                    System.out.println("illegal parameters");
                    return;
                }
                System.out.println(url);
                sigServiceRequestor.getRingPrivateKey(args[2], args[3]);
                break;
            default:
                RPCFlag = false;
                break;
        }
    }

    // deploy contract
    public static void callFisco(String[] args, SigServiceApp sigApp, int threadNum)
            throws Exception {
        if (RPCFlag) return;
        boolean configure = sigApp.loadConfig();
        if (!configure) {
            System.out.println("init configuration failed");
        }
        String method = args[1];
        StringBuffer contractAddr = new StringBuffer();
        // deploy group sig
        switch (method) {
            case "deploy_group_sig":
                {
                    if (args.length != 5) return;
                    // group_name, member_name, message
                    sigApp.deployGroupSigContract(args[2], args[3], args[4], contractAddr);
                    System.out.println(
                            "\nRESULT OF deploy_group_sig(Contract Address): " + contractAddr);
                    break;
                }
                // deploy ring sig
            case "deploy_ring_sig":
                {
                    if (args.length != 6) {
                        System.out.println("illegal parameters");
                        return;
                    }
                    int pos, size;
                    try {
                        pos = Integer.parseInt(args[4]);
                        size = Integer.parseInt(args[5]);
                    } catch (Exception e) {
                        logger.error(
                                "invalid member_pos or ring_size"
                                        + args[4]
                                        + args[5]
                                        + " , error msg:"
                                        + e.getMessage());
                        return;
                    }
                    if (pos < 0 || pos > size || size > 32) {
                        System.out.println("member's pos or ring-size is invalid!");
                        System.out.println("0 <= pos < ring-size <= 32");
                        return;
                    }
                    sigApp.deployRingSigContract(args[2], args[3], pos, size, contractAddr);
                    System.out.println(
                            "RESULT OF deploy_ring_sig(Contract Address): " + contractAddr);

                    break;
                }

                // group sig verify
            case "group_sig_verify":
                {
                    if (args.length != 3 && args.length != 4) {
                        System.out.println("illegal parameters");
                        return;
                    }
                    if (checkContractAddress(args[2])) {
                        System.out.println("Illegal contract address: " + args[2]);
                        return;
                    }
                    File file = new File("stat.log");
                    try (PrintStream ps = new PrintStream(new FileOutputStream(file))) {
                        int stressTest_ = 0;
                        if (args.length == 4) {
                            try {
                                stressTest_ = Integer.parseInt(args[3]);
                            } catch (Exception e) {
                                logger.error(
                                        "parse string "
                                                + args[3]
                                                + " to int failed, error msg:"
                                                + e.getMessage());
                            }
                        }
                        boolean stressTest = stressTest_ != 0;
                        int i = 0;
                        ArrayList<Thread> threadArray = new ArrayList<>();
                        do {
                            if (stressTest) {
                                System.out.println("### thread " + i);
                            }
                            threadArray.add(
                                    new Thread("thread" + i) {
                                        public void run() {
                                            do {
                                                try {
                                                    long startTime = System.currentTimeMillis();
                                                    StringBuffer verifyResult = new StringBuffer();
                                                    sigApp.groupSigVerify(args[2], verifyResult);
                                                    long endTime = System.currentTimeMillis();
                                                    ps.println((endTime - startTime) + "ms");
                                                    System.out.println(
                                                            "time_eclipsed:"
                                                                    + (endTime - startTime)
                                                                    + "ms");
                                                    System.out.println(
                                                            "verify result = " + verifyResult);
                                                } catch (Exception e) {
                                                    logger.error(e.getMessage());
                                                    System.out.println(e.getMessage());
                                                }
                                            } while (stressTest);
                                        }
                                    });
                            threadArray.get(i).start();
                            i++;
                        } while (stressTest && i < threadNum);
                        for (Thread thread : threadArray) {
                            thread.join();
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                }
            case "update_group_sig_data":
                {
                    if (args.length != 6) {
                        System.out.println("illegal parameters");
                        return;
                    }
                    if (checkContractAddress(args[2])) {
                        System.out.println("Illegal contract address: " + args[2]);
                        return;
                    }
                    StringBuffer updatedSig = new StringBuffer();
                    sigApp.updateGroupSigData(args[2], args[3], args[4], args[5], updatedSig);
                    System.out.println("updated group sig result:" + updatedSig);
                    break;
                }
            case "ring_sig_verify":
                {
                    if (args.length != 3 && args.length != 4) {
                        System.out.println("illegal parameters");
                        return;
                    }
                    if (checkContractAddress(args[2])) {
                        System.out.println("Illegal contract address: " + args[2]);
                        return;
                    }
                    File file = new File("stat.log");
                    try (PrintStream ps = new PrintStream(new FileOutputStream(file))) {
                        int stressTest_ = 0;
                        if (args.length == 4) {
                            stressTest_ = Integer.parseInt(args[3]);
                        }
                        boolean stressTest = stressTest_ != 0;
                        int i = 0;
                        ArrayList<Thread> threadArray = new ArrayList<>();
                        do {
                            threadArray.add(
                                    new Thread("thread" + i) {
                                        public void run() {
                                            do {
                                                try {
                                                    long startTime = System.currentTimeMillis();
                                                    StringBuffer verifyResult = new StringBuffer();
                                                    sigApp.ringSigVerify(args[2], verifyResult);
                                                    long endTime = System.currentTimeMillis();
                                                    System.out.println(
                                                            "verify result of ring sig = "
                                                                    + verifyResult);
                                                    ps.println((endTime - startTime) + "ms");
                                                    System.out.println(
                                                            "time_eclipsed:"
                                                                    + (endTime - startTime)
                                                                    + "ms");

                                                } catch (Exception e) {
                                                    logger.error(e.getMessage());
                                                    System.out.println(e.getMessage());
                                                }
                                            } while (stressTest);
                                        }
                                    });
                            threadArray.get(i).start();
                            i++;
                        } while (stressTest && i < threadNum);
                        for (Thread thread : threadArray) {
                            thread.join();
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                }
            case "update_ring_sig_data":
                {
                    if (args.length != 7) {
                        System.out.println("illegal parameters");
                        return;
                    }
                    if (checkContractAddress(args[2])) {
                        System.out.println("Illegal contract address: " + args[2]);
                        return;
                    }
                    StringBuffer updatedRingSig = new StringBuffer();
                    int pos, size;
                    try {
                        pos = Integer.parseInt(args[5]);
                        size = Integer.parseInt(args[6]);
                    } catch (Exception e) {
                        logger.error(
                                "invalid member_pos or ring_size"
                                        + args[5]
                                        + args[6]
                                        + " , error msg:"
                                        + e.getMessage());
                        return;
                    }
                    if (pos < 0 || pos > size || size > 32) {
                        System.out.println("Member's pos or ring size is invalid!");
                        return;
                    }
                    sigApp.updateRingSigData(args[2], args[3], args[4], pos, size, updatedRingSig);
                    System.out.println("update ring sig data result:" + updatedRingSig);
                    break;
                }
            default:
                System.out.println("Method: " + method + " not found");
                break;
        }
    }

    private static boolean checkContractAddress(String address) {
        return address.length() != 42 || !address.startsWith("0x");
    }
}
