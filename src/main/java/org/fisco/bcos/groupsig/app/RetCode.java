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

public class RetCode {
    public final static int SUCCESS = 0;
    public final static int UNKOWN_RET = 1;

    public final static int DEPLOY_GROUP_CONTRACT_FAILED = 30100;
    public final static int DEPLOY_RING_CONTRACT_FAILED = 30101;

    public final static int ETH_GROUP_SIG_VERIFY_FAILED = 30102;
    public final static int ETH_RING_SIG_VERIFY_FAILED = 30103;

    public final static int UPDATE_RING_SIG_DATA_FAILED = 30104;
    public final static int UPDATE_GROUP_SIG_DATA_FAILED = 30105;

    //    public final static int LOAD_CERT_FAILED = 30106;
    public final static int LOAD_CONTRACT_FAILED = 30107;

    public final static int CALL_GROUPSIG_RPC_FAILED = 30108;
    public final static int CALL_RINGSIG_RPC_FAILED = 30109;

    public final static int GET_CNS_CODE_FAILED = 30110;
    public final static int INVALID_CONTRACT_ADDRESS = 30110;

    public final static int CALL_RPC_FAILED = 30111;
    public final static int SERVICE_RUN_FAILED = 30112;

    public static void Msg(int ret_code, String prefix) {
        prefix = "**" + prefix;
        if (SUCCESS == ret_code)
            System.out.println(prefix + " SUCCESS");
        if (UNKOWN_RET == ret_code)
            System.out.println(prefix + " UNKOWN RET CODE");

        if (DEPLOY_GROUP_CONTRACT_FAILED == ret_code)
            System.out.println(prefix + " DEPLOY GROUP SIG CONTRACT FAILED");

        if (DEPLOY_RING_CONTRACT_FAILED == ret_code)
            System.out.println(prefix + " DEPLOY RING SIG CONTRACT FAILED");

        if (ETH_GROUP_SIG_VERIFY_FAILED == ret_code)
            System.out.println(prefix + " CALL ETH GROUP_SIG VERIFY FAILED");

        if (ETH_RING_SIG_VERIFY_FAILED == ret_code)
            System.out.println(prefix + "CALL ETH RING_SIG VERIFY FAILED");

        if (UPDATE_RING_SIG_DATA_FAILED == ret_code)
            System.out.println(prefix + "CALL ETH UPDATE_RING_SIG_DATA FAILED");

        if (UPDATE_GROUP_SIG_DATA_FAILED == ret_code)
            System.out.println(prefix + "CALL ETH UPDATE_GROUP_SIG_DATA FAILED");

//        if (LOAD_CERT_FAILED == ret_code)
//            System.out.println(prefix + "LOAD CERT FAILED");

        if (LOAD_CONTRACT_FAILED == ret_code)
            System.out.println(prefix + "LOAD CONTRACT FAILED");

        if (CALL_GROUPSIG_RPC_FAILED == ret_code)
            System.out.println(prefix + "CALL GROUPSIG RPC BEFORE DEPLOY GROUP SIG CONTRACT FAILED");

        if (CALL_RINGSIG_RPC_FAILED == ret_code)
            System.out.println(prefix + "CALL RINGSIG RPC BEFORE DEPLOY RING SIG CONTRACT FAILED");

        if (GET_CNS_CODE_FAILED == ret_code)
            System.out.println(prefix + "GET CNS CODE FAILED");

        if (INVALID_CONTRACT_ADDRESS == ret_code)
            System.out.println(prefix + " INVALID CONTRACT ADDRESS");

        if (SERVICE_RUN_FAILED == ret_code)
            System.out.println(prefix + " RUN sdk client FAILD");
    }
    // others... TODO

}
