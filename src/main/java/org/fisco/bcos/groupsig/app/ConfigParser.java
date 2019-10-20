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

import com.alibaba.fastjson.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigParser {
    private String configFile;
    private String connIp;
    private String connPort;
    private int threadNum;
    private static Logger logger = LoggerFactory.getLogger(RequestSigService.class);

    public String getConnIp() {
        return connIp;
    }

    public String getConnPort() {
        return connPort;
    }

    public int getThreadNum() {
        return threadNum;
    }

    public ConfigParser(String paramConfigFile) {
        configFile = paramConfigFile;
        parseJsonStr();
    }

    // read content from file_path
    public static String read(String filePath) {
        String result = "";
        InputStreamReader reader = null;
        BufferedReader bufReader = null;
        try {
            File file = new File(filePath);
            if (file.isFile() && file.exists()) {
                try {
                    reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
                } catch (Exception e) {
                    logger.error("init InputStreamReader failed" + e.getMessage());
                }
                try {
                    if (reader != null) bufReader = new BufferedReader(reader);
                } catch (Exception e) {
                    System.out.println("init BufferedReader failed, error msg:" + e.getMessage());
                }
                String line = null;
                if (bufReader != null) {
                    while ((line = bufReader.readLine()) != null) {
                        result += line;
                    }
                    System.out.println("load config " + filePath + " result = " + result);
                }
            }
            return result;
        } catch (Exception e) {
            logger.error("load config " + filePath + "failed, error msg:" + e.getMessage());
            return null;
        } finally {
            if (bufReader != null) {
                try {
                    bufReader.close();

                } catch (Exception e) {
                    logger.error("close buf_reader failed, error msg:" + e.getMessage());
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    logger.error("close reader failed, error_msg:" + e.getMessage());
                }
            }
        }
    }

    private void parseJsonStr() {
        connIp = "127.0.0.1";
        connPort = "8005";
        threadNum = 10; // default thread num
        if (configFile.equals("") == false) {
            String jsonStr = read(configFile);
            if (jsonStr != null) {
                JSONObject jsonObj = JSONObject.parseObject(jsonStr);
                if (jsonObj != null) {
                    if (jsonObj.containsKey("ip")) connIp = jsonObj.getString("ip");

                    if (jsonObj.containsKey("port")) connPort = jsonObj.getString("port");
                    if (jsonObj.containsKey("threadNum"))
                        threadNum = jsonObj.getIntValue("thread_num");
                }
            }
        }
        System.out.println("ip:" + connIp + " port:" + connPort);
    }
}
