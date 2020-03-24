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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestSigService {
    private String url;
    private Logger logger = LoggerFactory.getLogger(RequestSigService.class);

    public RequestSigService(String _url) {
        url = _url;
    }

    // deal with json format http post
    private String httpPostJson(String url, String param) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        // System.out.println("SET HEADER");
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-Type", "application/json");
        String char_set = "UTF-8";
        StringEntity entity = new StringEntity(param, char_set);
        httpPost.setEntity(entity);
        CloseableHttpResponse response = null;
        try {
            // System.out.println("execute http post");
            response = httpClient.execute(httpPost);
            // System.out.println("response"+ response.toString());
            StatusLine status = response.getStatusLine();
            int retCode = status.getStatusCode();
            if (retCode == HttpStatus.SC_OK) {

                HttpEntity responseEntity = response.getEntity();
                String jsonStr = EntityUtils.toString(responseEntity);
                System.out.println("JSON STR:\n" + jsonFormart(jsonStr));
                return jsonStr;
            } else {
                System.out.println("ret_code:" + retCode);
            }
        } catch (Exception e) { // System.out.println("catch");
            logger.error(e.getMessage());
            System.out.println(e.getMessage());
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                    System.out.println(e.getMessage());
                }
            }
            try {
                httpClient.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
                System.out.println(e.getMessage());
            }
        }
        return null;
    }

    private static String getLevelStr(int level) {
        StringBuilder levelStr = new StringBuilder();
        for (int levelI = 0; levelI < level; levelI++) {
            levelStr.append("\t");
        }
        return levelStr.toString();
    }

    private static String jsonFormart(String s) {
        int level = 0;
        StringBuilder jsonForMatStr = new StringBuilder();
        for (int index = 0; index < s.length(); index++) {

            char c = s.charAt(index);

            if (level > 0 && '\n' == jsonForMatStr.charAt(jsonForMatStr.length() - 1)) {
                jsonForMatStr.append(getLevelStr(level));
            }
            switch (c) {
                case '{':
                case '[':
                    jsonForMatStr.append(c).append("\n");
                    level++;
                    break;
                case ',':
                    jsonForMatStr.append(c).append("\n");
                    break;
                case '}':
                case ']':
                    jsonForMatStr.append("\n");
                    level--;
                    jsonForMatStr.append(getLevelStr(level));
                    jsonForMatStr.append(c);
                    break;
                default:
                    jsonForMatStr.append(c);
                    break;
            }
        }
        return jsonForMatStr.toString();
    }

    private String getParam(Map<String, Object> paramMap) {
        return JSON.toJSONString(paramMap);
    }

    private Map<String, Object> genParamMap(String method) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("method", method);
        paramMap.put("id", 1);
        paramMap.put("jsonrpc", "2.0");
        return paramMap;
    }

    /////// group sig interface related&&&&
    void createGroup(String groupName, String gmPass, String pbcParam) {
        Map<String, Object> paramMap = genParamMap("create_group");
        Map<String, String> subParamMap = new HashMap<>();

        subParamMap.put("group_name", groupName);
        subParamMap.put("gm_pass", gmPass);
        subParamMap.put("pbc_param", pbcParam);

        paramMap.put("params", subParamMap);
        String param = getParam(paramMap);
        httpPostJson(url, param);
    }

    void joinGroup(String groupName, String memberName) {
        Map<String, Object> paramMap = genParamMap("join_group");
        Map<String, String> subParamMap = new HashMap<>();

        subParamMap.put("group_name", groupName);
        subParamMap.put("member_name", memberName);

        paramMap.put("params", subParamMap);
        String param = getParam(paramMap);
        httpPostJson(url, param);
    }

    // group sig
    boolean groupSig(SigStruct sigObj, String groupName, String memberName, String message) {
        Map<String, Object> paramMap = genParamMap("group_sig");
        Map<String, String> subParamMap = new HashMap<>();

        subParamMap.put("group_name", groupName);
        subParamMap.put("member_name", memberName);
        subParamMap.put("message", message);

        paramMap.put("params", subParamMap);
        String group_sig_param = getParam(paramMap);
        String jsonRet = httpPostJson(url, group_sig_param);
        JSONObject jsonObj = JSONObject.parseObject(jsonRet).getJSONObject("result");
        if (jsonObj.getInteger("ret_code") == RetCode.SUCCESS) {
            sigObj.setSig(jsonObj.getString("sig"));
            sigObj.setGPK(jsonObj.getString("gpk"));
            sigObj.setParam(jsonObj.getString("pbc_param"));
            jsonObj.getString("message");
            return true;
        }
        return false;
    }

    // group sig verify
    String groupVerify(String groupName, String sig, String message) {
        Map<String, Object> paramMap = genParamMap("group_verify");
        Map<String, String> subParamMap = new HashMap<>();

        subParamMap.put("group_name", groupName);
        subParamMap.put("group_sig", sig);
        subParamMap.put("message", message);

        paramMap.put("params", subParamMap);
        String group_verify_param = getParam(paramMap);
        return httpPostJson(url, group_verify_param);
    }

    // open cert
    void openCert(String groupName, String groupSig, String message, String gmPass) {
        Map<String, Object> paramMap = genParamMap("open_cert");
        Map<String, String> subParamMap = new HashMap<>();

        subParamMap.put("group_name", groupName);
        subParamMap.put("group_sig", groupSig);
        subParamMap.put("message", message);
        subParamMap.put("gm_pass", gmPass);

        paramMap.put("params", subParamMap);
        String openCertParam = getParam(paramMap);
        // System.out.println("##PARAM OF OPEN_CERT:"+ open_cert_param);
        httpPostJson(url, openCertParam);
    }

    // get interfaces
    void getPublicInfo(String groupName) {
        Map<String, Object> paramMap = genParamMap("get_public_info");
        Map<String, String> subParamMap = new HashMap<>();

        subParamMap.put("group_name", groupName);
        paramMap.put("params", subParamMap);
        String param = getParam(paramMap);
        // System.out.println("##PARAM OF GetPublicInfo "+ param);
        httpPostJson(url, param);
    }

    // get gm passwd
    void getGMInfo(String groupName, String gmPass) {
        Map<String, Object> paramMap = genParamMap("get_gmsk_info");
        Map<String, String> subParamMap = new HashMap<>();

        subParamMap.put("group_name", groupName);
        subParamMap.put("gm_pass", gmPass);

        paramMap.put("params", subParamMap);
        String param = getParam(paramMap);
        httpPostJson(url, param);
    }

    // get member private key info
    void getMemberInfo(String groupName, String memberName, String pass) {
        Map<String, Object> paramMap = genParamMap("get_gsk_info");
        Map<String, String> subParamMap = new HashMap<>();

        subParamMap.put("group_name", groupName);
        subParamMap.put("member_name", memberName);
        subParamMap.put("pass", pass);

        paramMap.put("params", subParamMap);
        String param = getParam(paramMap);
        httpPostJson(url, param);
    }

    ////// Ring sig interface related
    void setupRing(String ringName, int bitLen) {
        Map<String, Object> paramMap = genParamMap("setup_ring");
        Map<String, Object> subParamMap = new HashMap<>();

        subParamMap.put("ring_name", ringName);
        subParamMap.put("bit_len", bitLen);
        paramMap.put("params", subParamMap);
        String ringParam = getParam(paramMap);
        httpPostJson(url, ringParam);
    }

    // join ring
    void joinRing(String ringName) {
        Map<String, Object> paramMap = genParamMap("join_ring");
        Map<String, String> subParamMap = new HashMap<>();

        subParamMap.put("ring_name", ringName);
        paramMap.put("params", subParamMap);
        String ringParam = getParam(paramMap);
        httpPostJson(url, ringParam);
    }

    boolean linkableRingSig(
            SigStruct ringSigObj, String message, String ringName, int memberPos, int ringSize) {
        Map<String, Object> paramMap = genParamMap("linkable_ring_sig");
        Map<String, Object> subParamMap = new HashMap<>();

        subParamMap.put("ring_name", ringName);
        subParamMap.put("message", message);
        subParamMap.put("id", Integer.toString(memberPos));
        subParamMap.put("ring_size", Integer.toString(ringSize));
        paramMap.put("params", subParamMap);

        String ringParam = getParam(paramMap);
        String jsonRet = httpPostJson(url, ringParam);
        JSONObject jsonObj = JSONObject.parseObject(jsonRet).getJSONObject("result");
        if (jsonObj != null) {
            if (jsonObj.getInteger("ret_code") == RetCode.SUCCESS) {
                ringSigObj.setSig(jsonObj.getString("sig"));
                ringSigObj.setParam(jsonObj.getString("param_info"));
                return true;
            }
        }
        return false;
    }

    void linkableRingVerify(String ringName, String sig, String message) {
        Map<String, Object> paramMap = genParamMap("linkable_ring_verify");
        Map<String, String> subParamMap = new HashMap<>();
        subParamMap.put("ring_name", ringName);
        subParamMap.put("sig", sig);
        subParamMap.put("message", message);

        paramMap.put("params", subParamMap);
        String ringParam = getParam(paramMap);
        httpPostJson(url, ringParam);
    }

    ///// get interfaces
    void getRingParam(String ringName) {
        Map<String, Object> paramMap = genParamMap("get_ring_param");
        Map<String, String> subParamMap = new HashMap<>();
        subParamMap.put("ring_name", ringName);
        paramMap.put("params", subParamMap);
        String ringParam = getParam(paramMap);
        httpPostJson(url, ringParam);
    }

    void getRingPublicKey(String ringName, String memberPos) {
        Map<String, Object> paramMap = genParamMap("get_public_key");
        Map<String, String> subParamMap = new HashMap<>();

        subParamMap.put("ring_name", ringName);
        subParamMap.put("id", memberPos);

        paramMap.put("params", subParamMap);

        String ringParam = getParam(paramMap);
        httpPostJson(url, ringParam);
    }

    void getRingPrivateKey(String ringName, String memberPos) {
        Map<String, Object> paramMap = genParamMap("get_private_key");
        Map<String, String> subParamMap = new HashMap<>();

        subParamMap.put("ring_name", ringName);
        subParamMap.put("id", memberPos);

        paramMap.put("params", subParamMap);
        String param = getParam(paramMap);

        httpPostJson(url, param);
    }
}
