# 群签名&&环签名客户端操作手册

## 目录
<!-- TOC -->

- [1 基本介绍](#1-基本介绍)
    - [1.1 基本功能](#11-基本功能)
    - [1.2 场景](#12-场景)
- [2 部署](#2-部署)
    - [2.1 依赖部署](#21-依赖部署)
    - [2.2 群签名&&环签名客户端部署](#22-群签名环签名客户端部署)
- [3 群签名&&环签名客户端使用](#3-群签名环签名客户端使用)
    - [3.1 客户端源代码结构](#31-客户端源代码结构)
    - [3.2 群签名&&环签名客户端接口](#32-群签名环签名客户端接口)
        - [3.2.1 群签名RPC访问相关的接口](#321-群签名rpc访问相关的接口)
        - [3.2.2 环签名RPC访问相关的接口](#322-环签名rpc访问相关的接口)
        - [3.2.3 群签名&&环签名上链相关的接口](#323-群签名环签名上链相关的接口)
    - [3.3 群签名&&环签名客户端使用示例](#33-群签名环签名客户端使用示例)
        - [3.3.1 群签名使用示例](#331-群签名使用示例)
        - [3.3.2 环签名使用示例](#332-环签名使用示例)
- [4 注意事项](#4-注意事项)
	- [4.1 群签名相关](#41-群签名相关)
	- [4.2 部署环签名注意事项](#42-部署环签名注意事项)
        
<!-- /TOC -->
 
=============


## 1 基本介绍
### 1.1 基本功能
**（1）群签名**

 **向机构内成员提供生成群、密钥托管以及群签名服务**

- 机构内成员通过客户端sig_client访问群签名服务，可生成群、加入指定群、请求获取群签名；
- 部署在可信第三方的群签名服务提供密钥托管功能；
- 机构成员可通过调整线性对参数，折中安全性和签名速度

**向监管机构提供签名者追踪服务**

- 监管可通过群主获取某个签名对应的签名者身份

<br>

**（2）环签名：向机构内成员提供生成环签名服务**

- 成员可根据安全性需求动态调整环大小，在安全性和签名速度两方面做折中
- 支持密钥托管 && 密钥自管理

<br>

**（3）签名链上验证 && 存证**

- 通过sig_client客户端，可链上验证群签名 && 环签名，保证签名不可篡改；

- sig_client客户端以联盟链机构身份（如webank等）将签名数据上链，其他成员可重复验证签名的有效性（存证的简单demo）；

- 区块链上其他成员通过签名无法获取机构成员的身份信息，仅可获取成员所属的机构和群组；


[返回目录](#目录)
 
<br> 

### 1.2 场景

**（1）群签名场景**

- **场景1**： 机构内成员（C端用户）通过客户端sig_client访问机构内群签名服务，并在链上验证签名，保证成员的匿名性和签名的不可篡改，监管也可通过群主（可信机构，如webank）追踪签名者信息（如拍卖、匿名存证等场景）

- **场景2**：机构内下属机构各部署一套群签名服务，通过上级联盟链机构成员，将签名信息写到区块链上，链上验证群签名，保证签名的匿名性和不可篡改；（如征信等场景）

- **场景3**：B端用户部署群签名服务，生成群签名，通过AMOP将签名信息发送给上链结构(如webank)，上链机构将收集到的签名信息统一上链（如竞标、对账等场景）

<br>

**（2）环签名场景**

- **场景1（匿名投票）**：机构内成员（C端用户）通过客户端sig-service-client访问机构内环签名服务，对投票信息进行签名，并通过可信机构（如webank）将签名信息和投票结果写到链上，其他人可验证签名和投票，仅可知道发布投票到链上的机构，却无法获取投票者身份信息

- **其他场景（如匿名存证、征信）**：场景与群签名匿名存证、征信场景类似，唯一的区别是任何人都无法追踪签名者身份

- **匿名交易**：在UTXO模型下，可将环签名算法应用于匿名交易，任何人都无法追踪转账交易双方；
 
<br>

[返回目录](#目录)

<br>

## 2 部署
部署群签名&&环签名客户端，首先需要部署依赖软件，保证代码和脚本能正确编译和运行；此外，由于该客户端依赖于签名rpc提供的群签名和环签名服务，因此还需要参考[群签名&&环签名RPC服务](https://github.com/FISCO-BCOS/sig-service)部署签名rpc服务；最后，需要编译该客户端，生成相关的jar包。

### 2.1 依赖部署
**（1）安装依赖软件**

部署群签名&&环签名客户端之前需要安装git, dos2unix, lsof依赖软件

-  git：用于拉取最新代码
-  dos2unix && lsof: 用于处理windows文件上传到linux服务器时，文件格式无法被linux正确解析的问题；

可用如下命令安装这些软件：

```shell
[centos]
sudo yum -y install git
sudo yum -y install dos2unix
sudo yum -y intall lsof

[ubuntu]
sudo apt install git
sudo apt install lsof
sudo apt install tofrodos
ln -s /usr/bin/todos /usr/bin/unxi2dos
ln -s /usr/bin/fromdos /usr/bin/dos2unix
```
<br>

**（2）部署签名RPC服务**

签名RPC服务sig-service为群签名和环签名客户端sig-service-client提供群签名和环签名服务，因此启动客户端之前，需要先部署签名RPC服务sig-service，**sig-service详细部署步骤可参考**：https://github.com/FISCO-BCOS/sig-service

<br>

**（3）部署fisco-bcos，并开启【群签名&&环签名】验证开关**

sig-service-client可将群签名和环签名信息上链，并在链上验证签名，若要使用群签名和环签名的链上验证功能，需要部署fisco-bcos，并开启【群签名&&环签名】验证开关（cmake时，加上-DGROUPSIG=ON选项），fisco-bcos详细搭建步骤，以及【群签名&&环签名】验证开关开启方法可参考：[启用/关闭群签名&&环签名ethcall](https://github.com/FISCO-BCOS/FISCO-BCOS/blob/master/doc/启用_关闭群签名环签名ethcall.md)

<br>

[返回目录](#目录)

<br>

### 2.2 群签名&&环签名客户端部署

依赖部署完毕后，就可以拉取群签名&&环签名客户端sig-service-client代码，配置链上节点信息，并部署客户端，详细步骤如下：

**（1）拉取sig-service-client代码**

- 从git上拉取代码
- 若是linux/unix环境，安装依赖软件之后，执行format.sh脚本格式化shell脚本和json配置文件，使其可被linux/unix正确解析

```bash
#拉取git代码
git clone https://github.com/FISCO-BCOS/sig-service-client
#格式化shell脚本和json配置文件
[app@VM_105_58_centos sig-service-client]$ bash format.sh
```

<br>

**（2）配置链上节点信息和证书**

sig-service-client/sig_client/src/main/resources目录下包含链上链上节点相关的配置文件以及证书，为了使客户端连上区块链节点，主要有以下设置（详细配置方法可参考[web3sdk使用指南]( https://github.com/FISCO-BCOS/web3sdk) 第(三)部分）：

<br>  


| <div align = left>配置文件</div>                   | <div align = left>主要设置</div>                                     |
| ---------------------- | ---------------------------------------- |
| applicationContext.xml | 客户端配置文件，需要配置区块链节点IP和channelPort信息，一个配置示例如下 |
| ca.crt                 | CA证书，必须保证和链上节点CA证书一致                     |
| client.keystore        | 节点连接区块链节点的证书，加载口令是123456                 |
| user.jks               | 用户客户端证书，加载口令是123456                      |


**链上节点信息配置示例：**

```xml

<bean id="channelService" class="org.bcos.channel.client.Service">
    <property name="orgID" value="WB" />
    <property name="allChannelConnections">
        <map>
            <entry key="WB">   <!--这里的key必须和上面的value一致（保证是相同机构的区块链节点）-->
                <bean class="org.bcos.channel.handler.ChannelConnections">
                    <property name="connectionsStr"> 
                        <list> <!--这里的IP是区块链节点IP，端口是节点的channelPort-->
                            <value>aaa@127.0.0.1:7701</value> 
                        </list>
                    </property>
                </bean>
            </entry>
        </map>
    </property>
</bean>
```

<br>

**（3）编译群签名&&环签名客户端**


web3sdk编译依赖软件如下：

| <div align = left>软件</div>     | <div align = left>版本</div>           |
| ------ | ------------ |
| jdk    | jdk 1.8以上    |
| gradle | gradle 4.6以上 |

为了方便用户，sig-service-client在sig_client文件夹下配备了自动化编译脚本compile.sh（针对**centos/ubuntu**运行环境，windows环境下也可用gradle编译），参考[compile.sh脚本](#sig_client/compile.sh)

用户通过运行该脚本编译客户端：

```bash
#进入sig_client目录
[app@VM_105_81_centos sig-service-client]$ cd sig_client/    
[app@VM_105_81_centos sig_client]$ ls
build.gradle  compile.sh  lib  src
#调用compile脚本编译群签名&&环签名客户端
#compile.sh脚本主要包括如下功能:
#(1) 判断系统java版本（jdk版本必须大于1.8）
#(2) 若系统java版本小于1.8或者系统没安装java，则脚本从官网下载并安装jdk1.8
#(3) 根据操作系统类型安装依赖包（目前支持Ubuntu和centos/federa）
#(4) 下载并安装gradle4.6(首次编译时安装，之后再编译不会再安装)
#(5) 使用grandle build命令编译java工程，产生客户端jar包sig_client.jar: 
#    会在sig_client目录下生成sig_client_toolkit目录，所有jar包都位于sig_client_toolkit/lib目录下
# 注：首次编译时，由于可能会安装java和gradle，因此要用root权限执行compile脚本（执行时加sudo）
#     之后如果再重新编译，则以
[app@VM_105_81_centos sig_client]$ sudo bash compile.sh 
```

<br>

[返回目录](#目录)

<br>


## 3 群签名&&环签名客户端使用

### 3.1 客户端源代码结构

群签名&&环签名客户端源代码主在src目录下，具体如下：


| <div align = left>源码目录</div>                          |<div align = left>说明</div>                                       |
| --------------------------- | ---------------------------------------- |
| main/java/org/bcos/groupsig | **客户端java源码目录**:<br>1.app子目录：群签名和环签名客户端实现代码 <br> 2.group_sig_sol子目录: 从合约转换的java代码 <br> (转换方法可参考[web3sdk使用指南]( https://github.com/FISCO-BCOS/web3sdk) 第(五)部分) |
| main/executive/shell        | **客户端工具脚本和示例配置文件目录**：<br> 1. compile.sh: 客户端一键编译脚本;<br> 2. conn.json:  客户端连接的rpc服务器配置信息<br> ① "ip"：字符串，用于配置rpc服务所在主机IP; <br> ② "port"：字符串，用于配置rpc服务连接端口; <br> ③ "thread_num"：数字，线程数目，主要在压测场景使用<br>（群签名&&环签名客户端为签名、验证、签名上链和签名链上验证提供了多线程压测接口）<br>3. sig_client && runSigService.sh: <br> ① sig_client： 设置jar包路径，并使用java调用jar包内封装的方法; <br> ② runSigService.sh：调用sig_client, 进一步封装群签名和环签名客户端接口，详细介绍见3.2.1节 |
| main/executive/sol          | **群签名、环签名链上部署和验证合约**：<br> 1. libGroupSig.sol: 群签名库合约，提供群签名链上验证接口；<br> 2. libRingSig.sol: 环签名库合约，提供环签名链上验证接口；<br> 3. testGroupSig.sol: 群签名应用合约示例，将群签名数据写到链上，并返回验证结果，其他用户可调用该合约验证群签名有效性；<br> 4. testRingSig.sol： 环签名应用合约示例，将环签名数据写到链上，并返回验证接口，其他用户可调用该合约验证环签名的有效性； |
| main/resources              | **配置文件目录**：<br> 配置文件说明可参考2.2节的【配置链上节点信息和证书】 |

<br>

[返回目录](#目录)

<br>

### 3.2 群签名&&环签名客户端接口

#### 3.2.1 群签名RPC访问相关的接口


**create_group ${rpc_config_path} ${group_name} ${gm_pass} ${pbc_param}**


| 功能   |<div align = left>生成群，群主可调用该接口生成群</div>                          |
| ---- | ---------------------------------------- |
| 参数   | ① rpc_config_path：required, 设置客户端连接的rpc服务器配置信息(具体配置参考conn.json), ip默认值是127.0.0.1， port默认值是8003，thread_num默认值是50;<br>② group_name： required, 生成的群名字;<br> ③ gm_pass: required, 群主访问rpc服务的密码（目前实现版本中没有对gm_pass做校验，使用者可修改rpc服务sig-service添加相关校验<br> ④ pbc_param: optional, 用指定的线性对创建群，默认使用A类型线性对（不同类型线性对安全性不同，目前A类型线性对已经不太安全），针对不同类型线性对，下面给出pbc_param具体示例:<br> A类型线性对: '{\"linear_type\":\"a\", \"q_bits_len\": 256, \"r_bits_len\":256}'<br> A1类型线性对: '{\"linear_type\":\"a_one\", \"order\":512}'<br> E类型线性对: '{\"linear_type\":\"e\", \"q_bits_len\": 512, \"r_bits_len\":512}'<br> F类型线性对：‘{\"linear_type\":\"f\", \"bit_len\": 256}’ |
| 返回值  | 创建群成功：返回http response, 其中ret_code字段值为0;<br> 创建群失败：返回http response, ret_code对应具体的错误码，message字段对应错误信息. |

<br>

[返回目录](#目录)

<br>

**join_group ${rpc_config_path} ${group_name} ${member_name}**


| 功能   |<div align = left>加入群，群成员调用该接口加入指定群，rpc服务提供秘钥托管功能</div>          |
| ---- | ---------------------------------------- |
| 参数   | ① rpc_config_path: required, 客户端连接的rpc服务器配置信息(具体配置参考conn.json)   <br>② group_name: required, 要加入的群名称<br>③ member_name: required, 群成员名字 |
| 返回值  | 加入群成功：返回http response, 其中ret_code字段值为0, 返回群成员私钥; <br>加入群失败：返回http response, ret_code对应具体错误码，message字段对应错误信息. |

<br>

[返回目录](#目录)

<br>

**group_sig ${rpc_config_path} ${group_name} ${member_name} ${message} ${stress_test}**


| 功能   | <div align = left>为指定群成员生成群签名</div>                              |
| ---- | ---------------------------------------- |
| 参数   | ① rpc_config_path: required, 客户端连接的rpc服务器配置信息(具体配置参考conn.json) <br>② group_name: required， 签名者所在的群名称<br>③ member_name: required, 签名者名称<br>④ message: required, 签名的明文信息<br>⑤ stress_test: optional，压测选项，”0“表示只生成一次群签名; "1"表示用\${rpc_config_path}中配置的thread_num个线程并发向rpc请求签名服务，即进行压测，默认选项为”0“ |
| 返回值  | 生成群签名成功：返回http response，包含签名信息，ret_code值为0；<br>生成群签名失败：返回http response, ret_code对应具体错误码，message记录错误信息. |

<br>

[返回目录](#目录)

<br>

**group_verify ${rpc_config_path} ${group_name} ${sig} ${message} ${stress_test}**


| 功能   | <div align = left>验证指定群签名信息是否有效</div>                            |
| ---- | ---------------------------------------- |
| 参数   | ① rpc_config_path:required, 客户端连接的rpc服务器配置信息(具体配置参考conn.json) <br>② group_name:  required, 签名所在的群名称<br>③ sig: required， 群签名信息<br>④ message: required, 签名对应的明文信息<br>⑤ stress_test: optional, 压测选项，“0”表示只验证一次群签名；“1”表示用\${rpc_config_path}中配置的thread_num个线程并发向rpc请求签名服务，即进行压测，默认选项为“0” |
| 返回值  | 调用群签名验证接口成功：<br>(1) 群签名验证有效： 返回http response, 包含签名验证通过信息，ret_code为0；<br>(2) 群签名验证无效：返回http response, 包含签名验证失败信息，ret_code为0; <br>调用群签名验证接口失败：返回http response，ret_code为具体错误码，message记录错误信息. |

<br>

[返回目录](#目录)

<br>

**open_cert ${rpc_config_path} ${group_name} ${sig} ${message} ${gm_pass}**


| 功能   | <div align = left>获取签名对应的签名者证书信息</div>                           |
| ---- | ---------------------------------------- |
| 参数   | ① rpc_config_path: required, 客户端连接的rpc服务器配置信息(具体配置参考conn.json)  <br>② group_name: required, 签名所在的群名称 <br>③ sig: required, 群签名信息<br>④ message: required, 群签名对应的明文 <br>⑤ gm_pass: required, 群主口令（当前版本没对该字段做校验，使用者可基于该版本实现口令验证功能） |
| 返回值  | 获取签名对应的签名者证书成功：返回http response, ret_code为0，并返回签名对应的证书获取签名对应的签名者证书失败: 返回http response, ret_code为错误码，返回具体的出错信息. |

<br>

[返回目录](#目录)

<br>


**get_public_info ${rpc_config_path} ${group_name}**


| 功能   | <div align = left>获取群公钥信息</div>                                  |
| ---- | ---------------------------------------- |
| 参数   | ① rpc_config_path: required, 客户端连接的rpc服务器配置信息(具体配置参考conn.json) <br>② group_name: required, 群公钥对应的群名称 |
| 返回值  | 获取群公钥信息成功: 返回http response, ret_code为0，并返回具体的群公钥; <br>获取群公钥信息失败： 返回http response, ret_code为具体错误码，并返回具体出错信息(可能是群不存在等). |

<br>

[返回目录](#目录)

<br>


**get_gm_info ${rpc_config_path} ${group_name} ${gm_pass}**


| 功能   | <div align = left>获取群主私钥信息</div>                                 |
| ---- | ---------------------------------------- |
| 参数   | ① rpc_config_path: required, 客户端连接的rpc服务器配置信息(具体配置参考conn.json)<br>② group_name: required，群主所在的群名称<br>③ gm_pass: required, 群主访问签名rpc服务的口令(当前版本没对该字段做校验，使用者可基于该版本实现口令验证功能); |
| 返回值  | 获取群主私钥信息成功: 返回http response, ret_code为0，并返回群主私钥信息;<br>获取群主私钥信息失败: 返回http response, ret_code为错误码，返回具体出错信息. |

<br>

[返回目录](#目录)

<br>

**get_member_info ${rpc_config_path} ${group_name} ${member_name} ${member_pass}**


| 功能   | <div align = left>获取群成员私钥信息</div>                                |
| ---- | ---------------------------------------- |
| 参数   | ① rpc_config_path：required, 客户端连接的rpc服务器配置信息(具体配置参考conn.json)<br>② group_name: required, 群成员所在的群名称<br>③ member_name: required, 群成员名称<br>④ member_pass: required，群成员访问签名rpc服务的口令(当前版本没对该字段做校验，使用者可基于该版本实现口令验证功能) |
| 返回值  | 获取群成员私钥信息成功: 返回http response, ret_code为0，并返回群成员私钥信息;<br>获取群成员私钥信息失败： 返回http response，ret_code为错误码，返回具体出错信息. |

<br>

[返回目录](#目录)

<br>


#### 3.2.2 环签名RPC访问相关的接口


**setup_ring ${rpc_config_path} ${ring_name}**


| 功能   | <div align = left>初始化环参数</div>                                   |
| ---- | ---------------------------------------- |
| 参数   | ① rpc_config_path: required, 客户端连接的rpc服务器配置信息(具体配置参考conn.json)<br>② ring_name: required,  初始化的环名称 |
| 返回值  | 初始化环参数成功: 返回http response, ret_code为0，返回环参数信息;<br>初始化环参数失败： 返回http response, ret_code为错误码，返回具体出错信息. |

<br>

[返回目录](#目录)

<br>


**join_ring ${rpc_config_path} ${ring_name}**


| 功能   | <div align = left>加入环，为成员生成公私钥对，rpc提供了秘钥托管功能</div>               |
| ---- | ---------------------------------------- |
| 参数   | ① rpc_config_path: required, 客户端连接的rpc服务器配置信息(具体配置参考conn.json)<br>② ring_name: required, 为加入的环成员生成公私钥对 |
| 返回值  | 加入环成功: 返回http response, ret_code为0，返回新加入的环成员公私钥信息;<br>加入环失败： 返回http response, ret_code为错误码，返回具体出错信息. |

<br>

[返回目录](#目录)

<br>


**ring_sig ${rpc_config_path} ${ring_name} ${member_pos} ${message} ${ring_size} ${stress_test}**


| 功能   | <div align = left>为指定位置的环成员生成环签名</div>                           |
| ---- | ---------------------------------------- |
| 参数   | ① rpc_config_path: required, 客户端连接的rpc服务器配置信息(具体配置参考conn.json)   <br>② ring_name: required, 环名称<br>③ member_pos: required, 请求生成签名的换成员在环中的位置<br>④ message: required, 环签名对应的明文信息<br>⑤ ring_size: optional, 用于生成环签名的环大小，默认是，ring_size越大，环签名匿名安全性越高<br>⑥ stress_test: optional, 压测选项，“0”表示只生成一次环签名；"1"表示用\${rpc_config_path}中thread_num指定的线程数并发向签名rpc发送签名请求；默认值为“0” |
| 返回值  | 生成环签名成功：返回http response, ret_code为0，返回环签名信息; <br>生成环签名失败： 返回http response, ret_code为错误码，返回出错信息. |

<br>

[返回目录](#目录)

<br>


**ring_verify ${rpc_config_path} ${ring_name} ${sig} ${message} ${stress_test}**


| 功能   | <div align = left>环签名有效性验证</div>                                 |
| ---- | ---------------------------------------- |
| 参数   | ① rpc_config_path: required, 客户端连接的rpc服务器配置信息(具体配置参考conn.json)   <br>② ring_name: required, 环名称<br>③ sig: required, 要验证的环签名<br>④ message: required, 环签名对应的明文消息<br>⑤ stress_test: optional, 压测选项, "0"表示只验证一次; "1"表示用${rpc_config_path}中thread_num指定的线程数并发向签名rpc发送签名验证请求，默认值为“0” |
| 返回值  | 环签名接口调用成功: <br>(1) 验证有效：返回http response, ret_code为0，并返回签名验证通过信息; <br>(2) 验证无效：返回http response, ret_code为0，并返回签名验证不通过信息; <br>环签名接口调用失败： 返回http response, ret_code为错误码，并返回具体出错信息. |

<br>

[返回目录](#目录)

<br>


**get_ring_param ${rpc_config_path} ${ring_name}**


| 功能   | <div align = left>获取环参数</div>                                    |
| ---- | ---------------------------------------- |
| 参数   | ① rpc_config_path: required, 客户端连接的rpc服务器配置信息(具体配置参考conn.json)   <br>② ring_name: required, 环名称 |
| 返回值  | 获取环参数成功: 返回http response, ret_code为0，并返回具体的环参数;<br>获取环签名参数失败: 返回http response, ret_code为错误码，并返回具体出错信息. |

<br>

[返回目录](#目录)

<br>


**get_ring_public_key ${rpc_config_path} ${ring_name} ${member_pos}**


| 功能   | <div align = left>获取环中指定位置公钥</div>                               |
| ---- | ---------------------------------------- |
| 参数   | ① rpc_config_path: required, 客户端连接的rpc服务器配置信息(具体配置参考conn.json)   <br>② ring_name: required, 环名称<br>③ member_pos: required,  要获取的公钥在环中的位置 |
| 返回值  | 获取环中指定位置公钥成功：返回http response, ret_code为0，并返回具体公钥信息;<br>获取环中指定位置公钥失败: 返回http response, ret_code为错误码，并返回具体出错信息. |

<br>

[返回目录](#目录)

<br>


**get_ring_private_key ${rpc_config_path} ${ring_name} ${member_pos}**


| 功能   | <div align = left>取环中指定位置环成员私钥</div>                            |
| ---- | ---------------------------------------- |
| 参数   | ① rpc_config_path: required, 客户端连接的rpc服务器配置信息(具体配置参考conn.json)   <br>② ring_name: required, 环名称 <br>③ member_pos: required, 要获取的换成员私钥在环中的位置<br>（说明: 获取换成员私钥推荐加上口令验证，本版本没有开发该功能，使用者可基于本版本开发） |
| 返回值  | 获取环中指定成员私钥成功：返回http response, ret_code为0，并返回具体私钥信息; <br>获取环中指定成员私钥失败： 返回http response, ret_code为错误码，返回具体出错信息. |

<br>

[返回目录](#目录)

<br>


#### 3.2.3 群签名&&环签名上链相关的接口


**(1) 合约部署接口**


**deploy_group_sig ${rpc_config_path} ${group_name} ${member_name} ${message}** 


| 功能   | <div align = left>部署群签名合约：<br>① 请求签名rpc服务，生成群签名;<br>② 将群签名信息写入区块链节点</div> |
| ---- | ---------------------------------------- |
| 参数   | ① rpc_config_path: required, 客户端连接的rpc服务器配置信息(具体配置参考conn.json)   <br>② group_name: required, 群名称<br>③ member_name: required, 群成员名称 <br>④ message: required, 群签名对应的公钥信息 |
| 返回值  | 部署群签名合约成功：返回合约地址，rpc端http response的ret_code为0; <br>部署群签名合约失败: 返回出错信息，若是rpc服务出错，返回的http response的ret_code字段为错误码. |

<br>

[返回目录](#目录)

<br>


**deploy_ring_sig ${rpc_config_path} ${message} ${ring_name} ${member_pos} ${ring_size}**


| 功能   | <div align = left>部署环签名合约：<br>① 请求环签名rpc服务，为指定位置环成员生成环签名;<br>② 将环签名信息写入区块链节点</div> |
| ---- | ---------------------------------------- |
| 参数   | ① rpc_config_path: required, 客户端连接的rpc服务器配置信息(具体配置参考conn.json)   <br>② message: required, 环签名对应的明文 <br>③ ring_name: required, 环名称<br>④ member_pos: required, 环成员位置<br>⑤ ring_size: required, 用于生成环签名的环大小， ring_size越大，环签名匿名安全性越高 |
| 返回值  | 部署环签名合约成功：返回合约地址，rpc端http repsonse的ret_code字段值为0;<br>部署环签名合约失败: 返回出错信息; |

<br>

[返回目录](#目录)

<br>


**(2) 群签名&&环签名链上验证接口**


**eth_group_verify ${rpc_config_path} ${contract_address} ${stress_test}**


| 功能   | <div align = left>验证链上指定群签名信息的有效性</div>                          |
| ---- | ---------------------------------------- |
| 参数   | ① rpc_config_path: required, 客户端连接的rpc服务器配置信息(具体配置参考conn.json)   <br>② contract_address: required, 保存群签名信息的合约地址，该地址用于加载合约<br>③ stress_test: optional，压测选项，“0”表示只验证一次链上群签名信息；“1”表示用${rpc_config_path}中thread_num指定数目的线程并发向链上节点发送签名验证请求 |
| 返回值  | 验证通过: 返回"true"<br>验证失败: 返回验证失败信息"false"<br>ethcall调用不存在(群签名&&环签名ethcall关闭情况下): 返回"ethcall GroupSig not implemented" |

<br>

[返回目录](#目录)

<br>


**eth_ring_verify ${rpc_config_path} ${contract_address} ${stress_test}**


| 功能   | <div align = left>验证链上指定环签名信息的有效性</div>                          |
| ---- | ---------------------------------------- |
| 参数   | ① rpc_config_path: required, 客户端连接的rpc服务器配置信息(具体配置参考conn.json)   <br>② contract_address: required,  保存环签名信息的合约地址，该地址用于加载合约<br>③ stress_test: optional, 压测选项, "0"表示只验证一次链上环签名信息; "1"表示用${rpc_config_path}中thread_num指定数目的线程并发向链上节点发送签名验证请求 |
| 返回值  | 验证通过: 返回"1"，表示验证通过;<br>验证失败: 返回"0"，表示验证失败; <br>ethcall调用不存在(群签名&&环签名ethcall关闭情况下): 返回"ethcall RingSig not implemented" |

<br>

[返回目录](#目录)

<br>


**(3) 更新存储在合约中的签名信息接口**


**update_group_sig_data ${rpc_config_path} ${contract_address} ${message} ${group_name} ${member_name}**


| 功能   | <div align = left>更新链上群签名信息：<br>① 请求签名rpc生成新的群签名信息;<br>② 更新存储在合约中的群签名信息</div>|
| ---- | ---------------------------------------- |
| 参数   | ① rpc_config_path: required, 客户端连接的rpc服务器配置信息(具体配置参考conn.json)<br>② contract_address: required, 存储群签名信息的合约地址，用于加载合约; <br>③ message: required, 新签名信息对应的明文<br>④ group_name: required, 新签名信息对应的群名称<br>⑤ member_name: required, 群成员名称 |
| 返回值  | 更新群签名信息成功：返回新产生的签名信息; <br>更新群签名信息失败: 返回旧的群签名信息. |

<br>

[返回目录](#目录)

<br>


**update_ring_sig_data ${rpc_config_path} ${contract_addess} ${message} ${ring_name} ${member_pos} ${ring_size}**


| 功能   | <div align = left>更新链上的环签名信息: <br>① 请求签名rpc生成新的环签名信息; <br>② 更新存储在合约中的环签名信息</div> |
| ---- | ---------------------------------------- |
| 参数   | ① rpc_config_path:required, 客户端连接的rpc服务器配置信息(具体配置参考conn.json)    <br>② contract_address: required, 存储环签名信息的合约地址，用于加载合约<br>③ message:  required, 新签名信息对应的明文<br>④ ring_name: required, 新签名信息对应的环名称<br>⑤ member_pos: required, 新环签名信息的签名者在环中的位置<br>⑥ ring_size:required, 产生新环签名信息的公钥数目，ring_size越大，环签名匿名安全性越高，算法性能越低 |
| 返回值  | 更新环签名信息成功: 返回新产生的环签名信息; <br>更新环签名信息失败: 返回旧的环签名信息. |

<br>

[返回目录](#目录)

<br>

<br>


### 3.3 群签名&&环签名客户端使用示例


参考[2.2 群签名&&环签名客户端部署](#22-群签名环签名客户端部署)，编译完sig-service-client后，sig-service-client/sig_client/sig_client_toolkit/bin目录下生成可执行文件runSigService.sh，该文件封装了群签名&&环签名客户端所有接口，可在其中添加相应接口调用，执行该脚本，进而使用客户端。

#### 3.3.1 群签名使用示例
**(1)创建群**

示例：调用127.0.0.1 8003端口的群签名服务(群签名rpc服务部署方法参考[群签名&&环签名RPC](#https://github.com/FISCO-BCOS/sig-service) )，创建群组group1，创建群时使用了线性对F

```bash
#1. cat conn.json
{
        "ip": "127.0.0.1",
        "port": "8003"
}
#2. 编辑runSigService.sh，在最下面一行增加以下接口调用，以线性对F创建群group1：
 create_group "conn.json" "group1" "123" '"{\"linear_type\":\"f\", \"bit_len\":256}"'
#3. 执行runSigService.sh脚本
 bash runSigService.sh
```

<br>

[返回目录](#目录)

<br>



**(2)群成员加入**

示例：调用127.0.0.1 8003端口的群签名服务(群签名rpc服务部署方法参考[群签名&&环签名RPC](#https://github.com/FISCO-BCOS/sig-service) )，在group1中加入群成员member1

```bash
#1. cat conn.json
{
        "ip": "127.0.0.1",
        "port": "8003"
}
#2. 编辑runSigService.sh，在最下面一行增加以下接口调用，在group1中加入群成员member1：
join_group "conn.json" "group1" "member1"
#3. 执行runSigService.sh脚本
 bash runSigService.sh
```

<br>

[返回目录](#目录)

<br>


**(3) 生成群签名**

示例：调用127.0.0.1 8003端口的群签名服务(群签名rpc服务部署方法参考[群签名&&环签名RPC](#https://github.com/FISCO-BCOS/sig-service) )，生成群group1中成员member1对信息hello的群签名：

```bash
#1. cat conn.json
{
        "ip": "127.0.0.1",
        "port": "8003"
}
#2. 编辑runSigService.sh，在最下面一行增加以下接口调用，生成群签名：
group_sig "conn.json" "group1" "member1" "hello"
#3. 执行runSigService.sh脚本
 bash runSigService.sh
```

<br>

[返回目录](#目录)

<br>

**(4) 产生群签名，将签名信息上链**

示例：调用127.0.0.1 8003端口的群签名服务(群签名rpc服务部署方法参考[群签名&&环签名RPC](#https://github.com/FISCO-BCOS/sig-service) )，首先生成群group1成员member1对信息hello的群签名，然后使用[AMOP](https://github.com/FISCO-BCOS/Wiki/tree/master/AMOP%E4%BD%BF%E7%94%A8%E6%8C%87%E5%8D%97)将群签名信息写到链上，AMOP配置方法参考[web3sdk使用指南]( https://github.com/FISCO-BCOS/web3sdk)，返回合约地址

```bash
#1. cat conn.json
{
        "ip": "127.0.0.1",
        "port": "8003"
}
#2. 编辑runSigService.sh，在最下面一行增加以下接口调用，将产生的群签名信息写入区块链()：
deploy_group_sig "conn.json" "group1" "member1" "hello"
#3. 执行runSigService.sh脚本
 bash runSigService.sh
```
<br>

[返回目录](#目录)

<br>


**(5) 链上验证群签名**

示例：验证部署于地址为0x1af99bf7d62bf6629459d47399358b23c19fcbd2合约处的群签名有效性（返回群签名验证结果true/false）

```bash
#1. cat conn.json
{
        "ip": "127.0.0.1",
        "port": "8003"
}
#2. 编辑runSigService.sh，在最下面一行增加以下接口调用：
eth_group_verify "conn.json" "0x1af99bf7d62bf6629459d47399358b23c19fcbd2"
#3. 执行runSigService.sh脚本
 bash runSigService.sh
```

<br>

[返回目录](#目录)

<br>


#### 3.3.2 环签名使用示例

**(1) 初始化环**

示例：调用127.0.0.1 8003端口的环签名服务(环签名rpc服务部署方法参考[群签名&&环签名RPC](#https://github.com/FISCO-BCOS/sig-service) )，初始化环ring1(环成员公/私钥长度默认为1024bits)

```bash
#1. cat conn.json
{
        "ip": "127.0.0.1",
        "port": "8003"
}
#2. 编辑runSigService.sh，在最下面一行增加以下接口调用，初始化环ring1：
setup_ring "conn.json" "ring1"
#3. 执行runSigService.sh脚本
 bash runSigService.sh
```

<br>

[返回目录](#目录)

<br>



**(2) 加入环成员**

示例：调用127.0.0.1 8003端口的环签名服务(环签名rpc服务部署方法参考[群签名&&环签名RPC](#https://github.com/FISCO-BCOS/sig-service) )，在环ring1中加入一个环成员：

```bash
#1. cat conn.json
{
        "ip": "127.0.0.1",
        "port": "8003"
}
#2. 编辑runSigService.sh，在最下面一行增加以下接口调用，在环ring1中增加一个环成员：
join_ring "conn.json" "ring1"
#3. 执行runSigService.sh脚本
 bash runSigService.sh
```

<br>

[返回目录](#目录)

<br>


**(3) 生成环签名**

示例：调用127.0.0.1 8003端口的环签名服务(环签名rpc服务部署方法参考[群签名&&环签名RPC](#https://github.com/FISCO-BCOS/sig-service) )，为环ring1的位置0处的环成员生成对消息hello环签名（默认环成员大小为4）

```bash
#1. cat conn.json
{
        "ip": "127.0.0.1",
        "port": "8003"
}
#2. 编辑runSigService.sh，在最下面一行增加以下接口调用，为环ring1的位置0处的环成员生成对消息hello的环签名：
ring_sig "conn.json" "ring1" "0" "hello"
#3. 执行runSigService.sh脚本
 bash runSigService.sh
```

<br>

[返回目录](#目录)

<br>


**(4) 生成环签名，并将签名信息上链**

示例：调用127.0.0.1 8003端口的环签名服务(环签名rpc服务部署方法参考[群签名&&环签名RPC](#https://github.com/FISCO-BCOS/sig-service) )，首先生成环ring1位置0成员对消息hello的环签名(默认环成员数为4)，然后使用[AMOP](https://github.com/FISCO-BCOS/Wiki/tree/master/AMOP%E4%BD%BF%E7%94%A8%E6%8C%87%E5%8D%97)将环签名信息写到链上，AMOP配置方法参考[web3sdk使用指南]( https://github.com/FISCO-BCOS/web3sdk)，返回合约地址

```bash
#1. cat conn.json
{
        "ip": "127.0.0.1",
        "port": "8003"
}
#2. 编辑runSigService.sh，在最下面一行增加以下接口调用，部署环签名到链上，并返回合约地址：
 deploy_ring_sig "conn.json" "hello" "ring1" "0" "4"
#3. 执行runSigService.sh脚本
 bash runSigService.sh
```

<br>

[返回目录](#目录)

<br>


**(5) 环签名信息链上验证**

示例：验证部署于地址为0x418d9da6382993e6955135db364c7dba88e483a4处合约中的环签名有效性，返回验证结果

```bash
#1. cat conn.json
{
        "ip": "127.0.0.1",
        "port": "8003"
}
#2. 编辑runSigService.sh，在最下面一行增加以下接口调用，在区块链上验证环签名：
eth_ring_verify "conn.json" "0x418d9da6382993e6955135db364c7dba88e483a4"
#3. 执行runSigService.sh脚本
 bash runSigService.sh
```

<br>

[返回目录](#目录)

<br>


## 4 注意事项

### 4.1 群签名相关

**(1)群签名算法签名和验证时间时间仅与线性对参数有关,群签名和验证时间与群成员数目无关，具有良好的可扩展性**<br>
（线性对参数设置方法可参考[3.2.1 群签名RPC访问相关的接口](#321-群签名rpc访问相关的接口)的create_group使用说明）<br> 

<br>

**(2) 群签名算法签名长度与线性对参数设置有关，设置不同线性对参数时，公私钥长度和签名长度如下：**<br>


| 线性对参数 | 公私钥长度信息 |
| ---- | ---------------------------------------- |
|**A类型线性对：**<br> {<br>q_bit_len: 512 bits <br> r_bit_len: 512 bits <br>} | **1. 私钥信息 (912字节):**<br>{<br>证书A长度: 1044 bits（261字节）<br>pr_A_g2线性对映射: 1044bits （261字节）<br>私钥x长度: 516 bits （129字节）<br>}<br> **2. 公钥长度信息（2700字节）：**<br> {<br>g1 && g2 && u && v && w && h&& pr_g1_g2 && pr_g1_g2_inv && pr_h_g2 && pr_h_w bit_len: 均为1080bits (270字节---16进制表示)<br>}<br> **3. 签名消息长度信息（1602字节）：**<br> {<br>T1 && T2 && T3 : 1080 bits (270字节 ---16进制表示) <br>c && ralpha && rbeta && rdelta1 && rdelta2 && rx: 552 bits (132字节---16进制表示)}|
|**A1类型线性对：**<br> {"linear_type":"a_one", "order":512}<br> | **1. 私钥信息（1293字节）:**<br>{<br> 证书A && pr_A_g2: 均为2072 bits（518字节 --16进制表示） 用户私钥：1028 bits (257字节---16进制表示)<br>}<br> **2. 公钥长度信息（5180字节）**<br>{<br>g1 && g2 && h && u && v && w && pr_g1_g2 && pr_g1_g2_inv && pr_h_g2 && pr_h_w 长度:  均为2072 bits (518字节 ---16进制表示)<br>}<br> **3. 签名消息长度信息（3102字节）**<br>{<br>T1 && T2 && T3:  2072 bits (518字节) <br>c && ralpha && rbeta &rdelta1 && rdelta2 && rx: 1032 bits(258字节)<br>}|
|**E类型线性对：**<br>{<br>"linear_type": "e" <br>"q_bits_len": 1024<br>, "r_bits_len": 160}<br> | **1. 私钥信息（905字节）**<br>{<br> 证书A: 2068 bits (517字节) <br> pr_A_g2：1036 bits (259字节)<br>私钥x： 516 bits (129字节)<br>}<br>**2. 公钥长度信息（4148字节）**<br>{<br>g1 && g2 && u && v && w && h: 2072bits (518字节)<br>pr_g1_g2 && pr_g1_g2_inv && pr_h_g2 && pr_h_w: 1040 bits(260字节)<br>}<br>**3. 签名消息长度信息（2328字节）**<br>{<br>T1 && T2 && T3： 2072bits (518字节)<br>c && ralpha && rbeta && rdelta1 && rdelta1 && rx:  516 bits (129字节)<br>}|
|**F类型线性对：**<br>{"linear_type":"f", "bit_len": 256} | **1. 私钥信息(653字节)**<br>{<br>  证书A && pr_A_g2: 1048 bits(262字节) <br>  私钥x: 516bits (129字节)<br>}<br>**2. 公钥长度信息(2358字节)**<br>{<br>  g1 && g2 && pr_g1_g2 && pr_g1_g2_inv && pr_h_g2 &&   pr_h_w && u && v && w: 1048bits(262字节)<br>}<br>}<br>**3. 签名消息长度信息(1584字节)**<br>{<br>T1 && T2 && T3: 1080bits(270字节)<br>c && ralpha && rbeta && rdelta1 && rdelta2 && rx: 516 bits(129字节)<br>}|


<br>

[返回目录](#目录)

<br>



### 4.2 部署环签名注意事项

**(1) 环签名的签名长度、签名和验证时间与环成员数目呈线性关系：**<br>
**① 用于签名的环成员数目ring_size越大，签名和验证时间越长;**<br>
**② [FISCO BCOS](https://github.com/FISCO-BCOS/FISCO-BCOS)默认gas是30000000:**<br>
当环成员数增加到**62**时，链上节点会抛出outOf gas的异常，可通过调整maxTransactionGas和maxBlockHeadGas增加系统gas最大限制，从而解决gas超过系统gas限制问题，<br> 调整方法可参考[系统参数说明文档](#https://github.com/FISCO-BCOS/Wiki/tree/master/%E7%B3%BB%E7%BB%9F%E5%8F%82%E6%95%B0%E8%AF%B4%E6%98%8E%E6%96%87%E6%A1%A3)，具体设置方法如下：<br>

```bash
##maxTransactionGas: 单笔交易的最大gas限制，默认值是30000000
##maxBlockHeadGas: 单个块的最大gas限制, 默认值是2000000000
##进入systemcontractv2目录 && 将maxTransactionGas和maxBlockHeadGas调整成更大值
cd systemcontractv2
#调整maxTransactionGas:
babel-node tool.js ConfigAction set maxTransactionGas 40000000 #这里假设调整成40000000，若设置成40000000仍然gas不足，可继续调大
babel-node tool.js ConfigAction get maxTransactionGas  #获取设置后的maxTransactionGas，验证参数是否更新成功

#调整maxBlockHeadGas（在单个块达到gas限制情况下，调整该配置）
babel-node tool.js ConfigAction set maxBlockHeadGas 3000000000 #这里假设调整成3000000000，若设置成3000000000仍gas不足，可继续调大
```
<br>

**(2) 群签名&&环签名客户端sig-service-client采用[AMOP](https://github.com/FISCO-BCOS/Wiki/tree/master/AMOP%E4%BD%BF%E7%94%A8%E6%8C%87%E5%8D%97)将签名数据上链，AMOP限制上链数据小于2MB，因此若环签名长度大于2MB时，数据上链会失败**<br>
(注：在环签名长度大于2MB时，验证时间会特别长，因此不建议上链的环签名数据特别长)

<br>

<br>

[返回目录](#目录)

<br>

