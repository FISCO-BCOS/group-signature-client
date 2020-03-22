#  客户端接口介绍

## 群签名接口

**create_group ${group_name} ${gm_pass} ${pbc_param}**

| 功能   | <div align = left>生成群，群主可调用该接口生成群</div>       |
| ------ | ------------------------------------------------------------ |
| 参数   | ① group_name： required, 生成的群名字;<br>②gm_pass: required, 群主访问rpc服务的密码（目前实现版本中没有对gm_pass做校验，使用者可修改rpc服务group-signature-server添加相关校验<br>③ pbc_param: optional, 用指定的线性对创建群，默认使用A类型线性对（不同类型线性对安全性不同，目前A类型线性对已经不太安全），针对不同类型线性对，下面给出pbc_param具体示例:<br> A类型线性对: '{\"linear_type\":\"a\", \"q_bits_len\": 256, \"r_bits_len\":256}'<br> A1类型线性对: '{\"linear_type\":\"a_one\", \"order\":512}'<br> E类型线性对: '{\"linear_type\":\"e\", \"q_bits_len\": 512, \"r_bits_len\":512}'<br> F类型线性对：‘{\"linear_type\":\"f\", \"bit_len\": 256}’ |
| 返回值 | 创建群成功：返回http response, 其中ret_code字段值为0;<br> 创建群失败：返回http response, ret_code对应具体的错误码，message字段对应错误信息. |

**join_group ${group_name} ${member_name}**

| 功能   | <div align = left>加入群，群成员调用该接口加入指定群，rpc服务提供秘钥托管功能</div> |
| ------ | ------------------------------------------------------------ |
| 参数   | ①group_name: required, 要加入的群名称<br>②member_name: required, 群成员名字 |
| 返回值 | 加入群成功：返回http response, 其中ret_code字段值为0, 返回群成员私钥; <br>加入群失败：返回http response, ret_code对应具体错误码，message字段对应错误信息. |

**group_sig ${group_name} ${member_name} ${message} ${stress_test}**

| 功能   | <div align = left>为指定群成员生成群签名</div>               |
| ------ | ------------------------------------------------------------ |
| 参数   | ① group_name: required， 签名者所在的群名称<br>② member_name: required, 签名者名称<br>③message: required, 签名的明文信息<br>④stress_test: optional，压测选项，”0“表示只生成一次群签名; "1"表示用\${rpc_config_path}中配置的thread_num个线程并发向rpc请求签名服务，即进行压测，默认选项为”0“ |
| 返回值 | 生成群签名成功：返回http response，包含签名信息，ret_code值为0；<br>生成群签名失败：返回http response, ret_code对应具体错误码，message记录错误信息. |

**group_verify ${group_name} ${sig} ${message} ${stress_test}**

| 功能   | <div align = left>验证指定群签名信息是否有效</div>           |
| ------ | ------------------------------------------------------------ |
| 参数   | ①group_name:  required, 签名所在的群名称<br>②sig: required， 群签名信息<br>③message: required, 签名对应的明文信息<br>④stress_test: optional, 压测选项，“0”表示只验证一次群签名；“1”表示用\${rpc_config_path}中配置的thread_num个线程并发向rpc请求签名服务，即进行压测，默认选项为“0” |
| 返回值 | 调用群签名验证接口成功：<br>(1) 群签名验证有效： 返回http response, 包含签名验证通过信息，ret_code为0；<br>(2) 群签名验证无效：返回http response, 包含签名验证失败信息，ret_code为0; <br>调用群签名验证接口失败：返回http response，ret_code为具体错误码，message记录错误信息. |

**open_cert ${group_name} ${sig} ${message} ${gm_pass}**

| 功能   | <div align = left>获取签名对应的签名者证书信息</div>         |
| ------ | ------------------------------------------------------------ |
| 参数   | ①group_name: required, 签名所在的群名称 <br>②sig: required, 群签名信息<br>③message: required, 群签名对应的明文 <br>④gm_pass: required, 群主口令（当前版本没对该字段做校验，使用者可基于该版本实现口令验证功能） |
| 返回值 | 获取签名对应的签名者证书成功：返回http response, ret_code为0，并返回签名对应的证书获取签名对应的签名者证书失败: 返回http response, ret_code为错误码，返回具体的出错信息. |


**get_public_info ${group_name}**

| 功能   | <div align = left>获取群公钥信息</div>                       |
| ------ | ------------------------------------------------------------ |
| 参数   | ①group_name: required, 群公钥对应的群名称                    |
| 返回值 | 获取群公钥信息成功: 返回http response, ret_code为0，并返回具体的群公钥; <br>获取群公钥信息失败： 返回http response, ret_code为具体错误码，并返回具体出错信息(可能是群不存在等). |

**get_gm_info ${group_name} ${gm_pass}**

| 功能   | <div align = left>获取群主私钥信息</div>                     |
| ------ | ------------------------------------------------------------ |
| 参数   | ①group_name: required，群主所在的群名称<br>②gm_pass: required, 群主访问签名rpc服务的口令(当前版本没对该字段做校验，使用者可基于该版本实现口令验证功能); |
| 返回值 | 获取群主私钥信息成功: 返回http response, ret_code为0，并返回群主私钥信息;<br>获取群主私钥信息失败: 返回http response, ret_code为错误码，返回具体出错信息. |

**get_member_info ${group_name} ${member_name} ${member_pass}**

| 功能   | <div align = left>获取群成员私钥信息</div>                   |
| ------ | ------------------------------------------------------------ |
| 参数   | ①group_name: required, 群成员所在的群名称<br>②member_name: required, 群成员名称<br>③member_pass: required，群成员访问签名rpc服务的口令(当前版本没对该字段做校验，使用者可基于该版本实现口令验证功能) |
| 返回值 | 获取群成员私钥信息成功: 返回http response, ret_code为0，并返回群成员私钥信息;<br>获取群成员私钥信息失败： 返回http response，ret_code为错误码，返回具体出错信息. |

## 环签名接口

**setup_ring ${ring_name}**

| 功能   | <div align = left>初始化环参数</div>                         |
| ------ | ------------------------------------------------------------ |
| 参数   | ①ring_name: required,  初始化的环名称                        |
| 返回值 | 初始化环参数成功: 返回http response, ret_code为0，返回环参数信息;<br>初始化环参数失败： 返回http response, ret_code为错误码，返回具体出错信息. |

**join_ring ${ring_name}**

| 功能   | <div align = left>加入环，为成员生成公私钥对，rpc提供了秘钥托管功能</div> |
| ------ | ------------------------------------------------------------ |
| 参数   | ①ring_name: required, 为加入的环成员生成公私钥对             |
| 返回值 | 加入环成功: 返回http response, ret_code为0，返回新加入的环成员公私钥信息;<br>加入环失败： 返回http response, ret_code为错误码，返回具体出错信息. |

**ring_sig ${ring_name} ${member_pos} ${message} ${ring_size} ${stress_test}**

| 功能   | <div align = left>为指定位置的环成员生成环签名</div>         |
| ------ | ------------------------------------------------------------ |
| 参数   | ①ring_name: required, 环名称<br>②member_pos: required, 请求生成签名的换成员在环中的位置<br>③message: required, 环签名对应的明文信息<br>④ring_size: optional, 用于生成环签名的环大小，默认是，ring_size越大，环签名匿名安全性越高<br>⑤stress_test: optional, 压测选项，“0”表示只生成一次环签名；"1"表示用\${rpc_config_path}中thread_num指定的线程数并发向签名rpc发送签名请求；默认值为“0” |
| 返回值 | 生成环签名成功：返回http response, ret_code为0，返回环签名信息; <br>生成环签名失败： 返回http response, ret_code为错误码，返回出错信息. |

**ring_verify ${ring_name} ${sig} ${message} ${stress_test}**

| 功能   | <div align = left>环签名有效性验证</div>                     |
| ------ | ------------------------------------------------------------ |
| 参数   | ①ring_name: required, 环名称<br>②sig: required, 要验证的环签名<br>③message: required, 环签名对应的明文消息<br>④stress_test: optional, 压测选项, "0"表示只验证一次; "1"表示用${rpc_config_path}中thread_num指定的线程数并发向签名rpc发送签名验证请求，默认值为“0” |
| 返回值 | 环签名接口调用成功: <br>(1) 验证有效：返回http response, ret_code为0，并返回签名验证通过信息; <br>(2) 验证无效：返回http response, ret_code为0，并返回签名验证不通过信息; <br>环签名接口调用失败： 返回http response, ret_code为错误码，并返回具体出错信息. |

**get_ring_param ${ring_name}**

| 功能   | <div align = left>获取环参数</div>                           |
| ------ | ------------------------------------------------------------ |
| 参数   | ①ring_name: required, 环名称                                 |
| 返回值 | 获取环参数成功: 返回http response, ret_code为0，并返回具体的环参数;<br>获取环签名参数失败: 返回http response, ret_code为错误码，并返回具体出错信息. |

**get_ring_public_key ${ring_name} ${member_pos}**

| 功能   | <div align = left>获取环中指定位置公钥</div>                 |
| ------ | ------------------------------------------------------------ |
| 参数   | ①ring_name: required, 环名称<br>②member_pos: required,  要获取的公钥在环中的位置 |
| 返回值 | 获取环中指定位置公钥成功：返回http response, ret_code为0，并返回具体公钥信息;<br>获取环中指定位置公钥失败: 返回http response, ret_code为错误码，并返回具体出错信息. |

**get_ring_private_key ${ring_name} ${member_pos}**

| 功能   | <div align = left>取环中指定位置环成员私钥</div>             |
| ------ | ------------------------------------------------------------ |
| 参数   | ①ring_name: required, 环名称 <br>②member_pos: required, 要获取的换成员私钥在环中的位置<br>（说明: 获取换成员私钥推荐加上口令验证，本版本没有开发该功能，使用者可基于本版本开发） |
| 返回值 | 获取环中指定成员私钥成功：返回http response, ret_code为0，并返回具体私钥信息; <br>获取环中指定成员私钥失败： 返回http response, ret_code为错误码，返回具体出错信息. |

## 上链相关接口

**合约部署接口**

**deploy_group_sig ${group_name} ${member_name} ${message}** 

| 功能   | <div align = left>部署群签名合约：<br>① 请求签名rpc服务，生成群签名;<br>② 将群签名信息写入区块链节点</div> |
| ------ | ------------------------------------------------------------ |
| 参数   | ①group_name: required, 群名称<br>②member_name: required, 群成员名称 <br>③message: required, 群签名对应的公钥信息 |
| 返回值 | 部署群签名合约成功：返回合约地址，rpc端http response的ret_code为0; <br>部署群签名合约失败: 返回出错信息，若是rpc服务出错，返回的http response的ret_code字段为错误码. |

**deploy_ring_sig ${message} ${ring_name} ${member_pos} ${ring_size}**

| 功能   | <div align = left>部署环签名合约：<br>① 请求环签名rpc服务，为指定位置环成员生成环签名;<br>② 将环签名信息写入区块链节点</div> |
| ------ | ------------------------------------------------------------ |
| 参数   | ①message: required, 环签名对应的明文 <br>②ring_name: required, 环名称<br>③member_pos: required, 环成员位置<br>④ring_size: required, 用于生成环签名的环大小， ring_size越大，环签名匿名安全性越高 |
| 返回值 | 部署环签名合约成功：返回合约地址，rpc端http repsonse的ret_code字段值为0;<br>部署环签名合约失败: 返回出错信息; |

**链上验证接口**

**group_sig_verify ${contract_address} ${stress_test}**

| 功能   | <div align = left>验证链上指定群签名信息的有效性</div>       |
| ------ | ------------------------------------------------------------ |
| 参数   | ①contract_address: required, 保存群签名信息的合约地址，该地址用于加载合约<br>②stress_test: optional，压测选项，“0”表示只验证一次链上群签名信息；“1”表示用${rpc_config_path}中thread_num指定数目的线程并发向链上节点发送签名验证请求 |
| 返回值 | 验证通过: 返回"true"<br>验证失败: 返回验证失败信息"false"<br>ethcall调用不存在(群签名&&环签名ethcall关闭情况下): 返回"ethcall GroupSig not implemented" |

**ring_sig_verify ${contract_address} ${stress_test}**

| 功能   | <div align = left>验证链上指定环签名信息的有效性</div>       |
| ------ | ------------------------------------------------------------ |
| 参数   | ①contract_address: required,  保存环签名信息的合约地址，该地址用于加载合约<br>②stress_test: optional, 压测选项, "0"表示只验证一次链上环签名信息; "1"表示用${rpc_config_path}中thread_num指定数目的线程并发向链上节点发送签名验证请求 |
| 返回值 | 验证通过: 返回"1"，表示验证通过;<br>验证失败: 返回"0"，表示验证失败; <br>ethcall调用不存在(群签名&&环签名ethcall关闭情况下): 返回"ethcall RingSig not implemented" |


**更新链上签名信息接口**

**update_group_sig_data ${contract_address} ${message} ${group_name} ${member_name}**

| 功能   | <div align = left>更新链上群签名信息：<br>① 请求签名rpc生成新的群签名信息;<br>② 更新存储在合约中的群签名信息</div> |
| ------ | ------------------------------------------------------------ |
| 参数   | ①contract_address: required, 存储群签名信息的合约地址，用于加载合约; <br>②message: required, 新签名信息对应的明文<br>③group_name: required, 新签名信息对应的群名称<br>④member_name: required, 群成员名称 |
| 返回值 | 更新群签名信息成功：返回新产生的签名信息; <br>更新群签名信息失败: 返回旧的群签名信息. |

**update_ring_sig_data ${contract_addess} ${message} ${ring_name} ${member_pos} ${ring_size}**

| 功能   | <div align = left>更新链上的环签名信息: <br>① 请求签名rpc生成新的环签名信息; <br>② 更新存储在合约中的环签名信息</div> |
| ------ | ------------------------------------------------------------ |
| 参数   | ①contract_address: required, 存储环签名信息的合约地址，用于加载合约<br>②message:  required, 新签名信息对应的明文<br>③ring_name: required, 新签名信息对应的环名称<br>④member_pos: required, 新环签名信息的签名者在环中的位置<br>⑤ring_size:required, 产生新环签名信息的公钥数目，ring_size越大，环签名匿名安全性越高，算法性能越低 |
| 返回值 | 更新环签名信息成功: 返回新产生的环签名信息; <br>更新环签名信息失败: 返回旧的环签名信息. |
