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

public class SigStruct {
    private String sig;
    private String param_info;
    private String gpk_info;

    public void setSig(String _sig) {
        sig = _sig;
    }

    public void setParam(String _param) {
        param_info = _param;
    }

    public void setGPK(String _gpk) {
        gpk_info = _gpk;
    }

    public String getSig() {
        return sig;
    }

    public String getParam() {
        return param_info;
    }

    public String getGPK() {
        return gpk_info;
    }
}
