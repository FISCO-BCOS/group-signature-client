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
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.*;

//import net.sf.json.JSONObject;

public class RequestSigService {
    private String url;
    private static Logger logger = LogManager.getLogger(RequestSigService.class);

    public RequestSigService(String _url) {
        url = _url;
    }

    // deal with key-value format http post
    private String httpPostKeyValue(String url, Map<String, Object> params) {
        BufferedReader in = null;
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost();
            request.setURI(new URI(url));

            List<NameValuePair> param_map = new ArrayList<NameValuePair>();
            for (Iterator iter = params.keySet().iterator(); iter.hasNext(); ) {
                String key = (String) iter.next();
                String value = String.valueOf(params.get(key));
                param_map.add(new BasicNameValuePair(key, value));
                // System.out.println(key+"_"+value);
            }
            request.setEntity(new UrlEncodedFormEntity(param_map, HTTP.UTF_8));

            HttpResponse response = client.execute(request);
            int code = response.getStatusLine().getStatusCode();
            // System.out.println("ret_code:" + code);
            if (code == 200) {
                in = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                StringBuffer sb = new StringBuffer("");
                String line = "";
                String NL = System.getProperty("line.separator");
                while ((line = in.readLine()) != null)
                    sb.append(line + NL);
                return sb.toString();
            } else {
                System.out.println("get response failed");
                return null;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    logger.error("close buffer reader faild, error msg:" + e.getMessage());
                }
            }
        }
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
                System.out.println("JSON STR:" + jsonStr);
                return jsonStr;
            } else {
                System.out.println("ret_code:" + retCode);
            }
        } catch (Exception e) { // System.out.println("catch");
            logger.error(e.getMessage());
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
            try {
                httpClient.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        return null;
    }

    private String getParam(Map paramMap) {
        return JSON.toJSONString(paramMap);

    }

    private Map<String, Object> genParamMap(String method) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("method", method);
        paramMap.put("id", 1);
        paramMap.put("jsonrpc", "2.0");
        return paramMap;
    }

    /////// group sig interface related&&&&
    public String createGroup(String groupName, String gmPass, String pbcParam) {
        Map<String, Object> paramMap = genParamMap("create_group");
        Map<String, String> subParamMap = new HashMap<String, String>();

        subParamMap.put("group_name", groupName);
        subParamMap.put("gm_pass", gmPass);
        subParamMap.put("pbc_param", pbcParam);

        paramMap.put("params", subParamMap);
        String param = getParam(paramMap);
        try {
            return httpPostJson(url, param);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public String joinGroup(String groupName, String memberName) {
        Map<String, Object> paramMap = genParamMap("join_group");
        Map<String, String> subParamMap = new HashMap<String, String>();

        subParamMap.put("group_name", groupName);
        subParamMap.put("member_name", memberName);

        paramMap.put("params", subParamMap);
        String param = getParam(paramMap);
        try {
            return httpPostJson(url, param);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    // group sig
    public boolean groupSig(SigStruct sigObj, String groupName, String memberName, String message) {
        Map<String, Object> paramMap = genParamMap("group_sig");
        Map<String, String> subParamMap = new HashMap<String, String>();

        subParamMap.put("group_name", groupName);
        subParamMap.put("member_name", memberName);
        subParamMap.put("message", message);

        paramMap.put("params", subParamMap);
        String group_sig_param = getParam(paramMap);
        try {
            String jsonRet = httpPostJson(url, group_sig_param);
            JSONObject jsonObj = JSONObject.parseObject(jsonRet).getJSONObject("result");
            if (jsonObj.getInteger("ret_code") == RetCode.SUCCESS) {
                sigObj.setSig(jsonObj.getString("sig"));
                sigObj.setGPK(jsonObj.getString("gpk"));
                sigObj.setParam(jsonObj.getString("pbc_param"));
                message = jsonObj.getString("message");
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("##CALL BACK GROUPSIG FAILED " + e.getMessage());
            return false;
        }
    }

    // group sig verify
    public String groupVerify(String groupName, String sig, String message) {
        Map<String, Object> paramMap = genParamMap("group_verify");
        Map<String, String> subParamMap = new HashMap<String, String>();

        subParamMap.put("group_name", groupName);
        subParamMap.put("group_sig", sig);
        subParamMap.put("message", message);

        paramMap.put("params", subParamMap);
        String group_verify_param = getParam(paramMap);
        try {
            return httpPostJson(url, group_verify_param);
        } catch (Exception e) {
            logger.error("GROUP VERIFY FAILED " + e.getMessage());
            return null;
        }
    }

    // open cert
    public String openCert(String groupName, String groupSig, String message, String gmPass) {
        Map<String, Object> paramMap = genParamMap("open_cert");
        Map<String, String> subParamMap = new HashMap<String, String>();

        subParamMap.put("group_name", groupName);
        subParamMap.put("group_sig", groupSig);
        subParamMap.put("message", message);
        subParamMap.put("gm_pass", gmPass);

        paramMap.put("params", subParamMap);
        String openCertParam = getParam(paramMap);
        // System.out.println("##PARAM OF OPEN_CERT:"+ open_cert_param);
        try {
            return httpPostJson(url, openCertParam);
        } catch (Exception e) {
            logger.error("OPEN GROUP SIG " + groupSig + " Failed, error msg:" + e.getMessage());
            return null;
        }
    }

    // get interfaces
    public String getPublicInfo(String groupName) {
        Map<String, Object> paramMap = genParamMap("get_public_info");
        Map<String, String> subParamMap = new HashMap<String, String>();

        subParamMap.put("group_name", groupName);
        paramMap.put("params", subParamMap);
        String param = getParam(paramMap);
        // System.out.println("##PARAM OF GetPublicInfo "+ param);
        try {
            return httpPostJson(url, param);
        } catch (Exception e) {
            logger.error("GET GROUP " + groupName + " public key failed, error msg:" + e.getMessage());
            return null;
        }
    }

    // get gm passwd
    public String getGMInfo(String groupName, String gmPass) {
        Map<String, Object> paramMap = genParamMap("get_gmsk_info");
        Map<String, String> subParamMap = new HashMap<String, String>();

        subParamMap.put("group_name", groupName);
        subParamMap.put("gm_pass", gmPass);

        paramMap.put("params", subParamMap);
        String param = getParam(paramMap);
        try {
            return httpPostJson(url, param);
        } catch (Exception e) {
            logger.error("get gmsk info failed, error msg:" + e.getMessage());
            return null;
        }
    }

    // get member private key info
    public String getMemberInfo(String groupName, String memberName, String pass) {
        Map<String, Object> paramMap = genParamMap("get_gsk_info");
        Map<String, String> subParamMap = new HashMap<String, String>();

        subParamMap.put("group_name", groupName);
        subParamMap.put("member_name", memberName);
        subParamMap.put("pass", pass);

        paramMap.put("params", subParamMap);
        String param = getParam(paramMap);
        try {
            return httpPostJson(url, param);
        } catch (Exception e) {
            logger.error("GetMemberInfo failed, group_name:" + groupName + ", member_name:" + memberName + ", error msg:" + e.getMessage());
            return null;
        }
    }

    ////// Ring sig interface related
    public String setupRing(String ringName, int bitLen) {
        Map<String, Object> paramMap = genParamMap("setup_ring");
        Map<String, Object> subParamMap = new HashMap<String, Object>();

        subParamMap.put("ring_name", ringName);
        subParamMap.put("bit_len", bitLen);
        paramMap.put("params", subParamMap);
        String ringParam = getParam(paramMap);
        try {
            return httpPostJson(url, ringParam);
        } catch (Exception e) {
            logger.error("call back SetupRing Failed, error msg:" + e.getMessage());
            return null;
        }
    }

    // join ring
    public String joinRing(String ringName) {
        Map<String, Object> paramMap = genParamMap("join_ring");
        Map<String, String> subParamMap = new HashMap<String, String>();

        subParamMap.put("ring_name", ringName);
        paramMap.put("params", subParamMap);
        String ringParam = getParam(paramMap);
        try {
            return httpPostJson(url, ringParam);
        } catch (Exception e) {
            logger.error("call back joinRing failed, error msg:" + e.getMessage());
            return null;
        }
    }

    public boolean linkableRingSig(SigStruct ringSigObj, String message, String ringName, int memberPos,
                                   int ringSize) {
        Map<String, Object> paramMap = genParamMap("linkable_ring_sig");
        Map<String, Object> subParamMap = new HashMap<String, Object>();

        subParamMap.put("ring_name", ringName);
        subParamMap.put("message", message);
        subParamMap.put("id", Integer.toString(memberPos));
        subParamMap.put("ring_size", Integer.toString(ringSize));
        paramMap.put("params", subParamMap);

        String ringParam = getParam(paramMap);
        try {
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
        } catch (Exception e) {
            logger.error("callback LinkableRingSig FAILED, error msg:" + e.getMessage());
            return false;
        }
    }

    public String linkableRingVerify(String ringName, String sig, String message) {
        Map<String, Object> paramMap = genParamMap("linkable_ring_verify");
        Map<String, String> subParamMap = new HashMap<String, String>();
        subParamMap.put("ring_name", ringName);
        subParamMap.put("sig", sig);
        subParamMap.put("message", message);

        paramMap.put("params", subParamMap);
        String ringParam = getParam(paramMap);
        // System.out.println("Param of LinkableRingVerify: "+ ring_param);
        try {
            return httpPostJson(url, ringParam);
        } catch (Exception e) {
            logger.error("CALLBACK LinkableRingVerify FAILED, error msg:" + e.getMessage());
            return null;
        }
    }

    ///// get interfaces
    public String getRingParam(String ringName) {
        Map<String, Object> paramMap = genParamMap("get_ring_param");
        Map<String, String> subParamMap = new HashMap<String, String>();
        subParamMap.put("ring_name", ringName);
        paramMap.put("params", subParamMap);
        String ringParam = getParam(paramMap);
        try {
            return httpPostJson(url, ringParam);
        } catch (Exception e) {
            logger.error("Callback GetRingParam FAILED, error msg:" + e.getMessage());
            return null;
        }
    }

    public String getRingPublicKey(String ringName, String memberPos) {
        Map<String, Object> paramMap = genParamMap("get_public_key");
        Map<String, String> subParamMap = new HashMap<String, String>();

        subParamMap.put("ring_name", ringName);
        subParamMap.put("id", memberPos);

        paramMap.put("params", subParamMap);

        String ringParam = getParam(paramMap);
        try {
            return httpPostJson(url, ringParam);
        } catch (Exception e) {
            logger.error("CALLBACK GetRingPublicKey FAILED, error msg:" + e.getMessage());
            return null;
        }
    }

    public String getRingPrivateKey(String ringName, String memberPos) {
        Map<String, Object> paramMap = genParamMap("get_private_key");
        Map<String, String> subParamMap = new HashMap<String, String>();

        subParamMap.put("ring_name", ringName);
        subParamMap.put("id", memberPos);

        paramMap.put("params", subParamMap);
        String param = getParam(paramMap);

        // System.out.println("PARAM OF GetRingPrivateKey:"+ param);
        try {
            return httpPostJson(url, param);
        } catch (Exception e) {
            logger.error("CALL BACK GetRingPrivateKey FAILED, error msg:" + e.getMessage());
            return null;
        }
    }
}
