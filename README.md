# group-signature-client

![](https://github.com/FISCO-BCOS/FISCO-BCOS/raw/master/docs/images/FISCO_BCOS_Logo.svg?sanitize=true)

[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com)
[![GitHub issues](https://img.shields.io/github/issues/FISCO-BCOS/group-signature-client.svg)](https://github.com/FISCO-BCOS/group-signature-client/issues)
[![All releases](https://img.shields.io/github/release/FISCO-BCOS/group-signature-client.svg)](https://github.com/FISCO-BCOS/group-signature-client/releases)
![](https://img.shields.io/github/license/FISCO-BCOS/group-signature-client) 

群/环签名客户端，负责向[服务端](https://github.com/FISCO-BCOS/group-signature-server)发起RPC请求生成签名，并将签名上链，然后通过调用群/环签名[预编译合约](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/privacy.html#id17)实现签名的链上认证。

群/环签名客户端和服务端是专门提供给社区用户的FISCO BCOS[隐私模块](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/privacy.html)关于群/环签名的开发示例，架构如下：

![](image/demo.jpg)

## 代码结构

| <div align = left>源码目录</div>  | <div align = left>说明</div>                                 |
| --------------------------------- | ------------------------------------------------------------ |
| main/java/org/fisco/bcos/groupsig/app | 客户端主逻辑，包括RPC调用和合约调用 |
| main/java/org/fisco/bcos/groupsig/contract | 由智能合约转换的java代码 |
| main/resources/conf               | 客户端配置文件目录|
| main/resources/sol                | 链上部署的智能合约目录|
| main/resources/node               | 节点配置文件目录 |

## 部署客户端

### 部署依赖

**部署服务端**

群/环签名客户端要访问签名服务，因此需要先部署[服务端](https://github.com/FISCO-BCOS/sig-service)，详细步骤可参考[操作文档](https://github.com/FISCO-BCOS/sig-service)。

**部署FISCO BCOS**

客户端可将群/环签名上链，若要使用链上验证功能，需要部署[FISCO BCOS](https://github.com/FISCO-BCOS/FISCO-BCOS)，并开启隐私模块开关。

FISCO BCOS详细搭建步骤，以及隐私模块启用方法可参考[操作手册](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/privacy.html)。

### 编译与配置

依赖部署完毕后，就可以拉取代码，编译并配置客户端。详细步骤如下：

**编译客户端**

```bash
git clone https://github.com/FISCO-BCOS/group-signature-client
# 进入客户端目录
cd group-signature-client
# 编译客户端需安装jdk v1.8及以上，gradle v4.6及以上
./gradlew build
# 编译成功后会生成目标目录dist
```

**配置客户端**

1. 配置RPC服务：`dist/conf/conn.json`

- ip：服务端IP，默认是127.0.0.1
- port：服务端RPC监听端口，默认是8005
- thread_num：线程数目，压测场景使用

2. 配置日志：`dist/conf/log4j.properties`

- 使用默认配置即可

3. 配置区块链节点信息：`dist/conf/node`

- 将`dist/conf/node`目录下的`application-sample.xml`拷贝成`application.xml`
- 将节点的`nodes/127.0.0.1/sdk`目录下的`ca.crt`、`sdk.crt`和`sdk.key`文件拷贝`dist/conf/node`目录下
- `application.xml`详细配置说明可参考[web3sdk配置](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/java_sdk.html#spring)

## 使用客户端

配置完客户端后就可以开始体验它的相关功能了。

### 群签名示例

接口说明详见[群签名接口文档](doc/interface.md#群签名接口)，以下命令在dist目录下执行。

**(1)创建群**

创建群组group1，创建群时使用了A类型线性对。

```bash
# 参数说明
# 启动客户端程序：java -cp 'apps/*:lib/*:conf' org.fisco.bcos.groupsig.app.Main
# args[0] : 调用的接口名称
# args[1:]: 调用的接口参数 
java -cp 'apps/*:lib/*:conf' org.fisco.bcos.groupsig.app.Main create_group 'group1' '123' '{\"linear_type\":\"a\", \"q_bits_len\":256, \"r_bits_len\":256}'
```

**(2)群成员加入**

在group1中加入群成员member1。

```bash
java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.groupsig.app.Main join_group 'group1' 'member1'
```

**(3) 生成群签名**

生成群group1中成员member1对信息“hello”的群签名。

```bash
java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.groupsig.app.Main group_sig 'group1' 'member1' 'hello'
```

**(4) 产生群签名，将签名信息上链**

首先生成群group1成员member1对信息“hello”的群签名，然后使用[AMOP](https://github.com/FISCO-BCOS/Wiki/tree/master/AMOP%E4%BD%BF%E7%94%A8%E6%8C%87%E5%8D%97)将群签名信息写到链上，返回合约地址。

```bash
java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.groupsig.app.Main deploy_group_sig 'group1' 'member1' 'hello'
```

**(5) 链上验证群签名**

示例：验证部署于地址为0xd6c8a04b8826b0a37c6d4aa0eaa8644d8e35b79f合约处的群签名有效性（返回群签名验证结果true/false）。

```bash
java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.groupsig.app.Main group_sig_verify '0xd6c8a04b8826b0a37c6d4aa0eaa8644d8e35b79f'
```

### 环签名示例

接口说明详见[环签名接口文档](doc/interface.md#环签名接口)。

(1) 初始化环**

调用127.0.0.1 8005端口的群签名服务(群签名RPC服务部署方法参考[群/环签名RPC](https://github.com/FISCO-BCOS/group-signature-server/tree/dev-2.0) )，初始化环ring1(环成员公/私钥长度默认为1024bits)。

```bash
java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.groupsig.app.Main setup_ring 'ring1'
```

**(2) 加入环成员**

在环ring1中加入一个环成员，可重复执行该命令来生成多个环成员。

```bash
java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.groupsig.app.Main join_ring 'ring1'
```

**(3) 生成环签名**

为环ring1的位置0处的环成员生成对消息“hello”环签名，假设当前的环成员大于4。

```bash
java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.groupsig.app.Main ring_sig 'hello' 'ring1' '0' '4'
```

**(4) 生成环签名，并将签名信息上链**

首先生成环ring1位置0成员对消息“hello”的环签名，然后使用[AMOP](https://github.com/FISCO-BCOS/Wiki/tree/master/AMOP%E4%BD%BF%E7%94%A8%E6%8C%87%E5%8D%97)将环签名信息写到链上，返回合约地址。

```bash
java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.groupsig.app.Main deploy_ring_sig 'hello' 'ring1' '0' '4'
```

**(5) 环签名信息链上验证**

示例：验证部署于地址为0x2426edaa1173f65cd7d62c93c935bfde329d247c处合约中的环签名有效性，返回验证结果。

```bash
java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.groupsig.app.Main ring_sig_verify '0x2426edaa1173f65cd7d62c93c935bfde329d247c'
```

### 注意事项

- 群签名算法签名和验证时间与群成员数目无关，具有良好的可扩展性。

- 群签名算法签名长度与线性对参数设置有关，不同线性对对应的公私钥长度和签名长度如下：

| 线性对参数                                                   | 长度信息                                               |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| **A类型线性对：**<br> {<br>"linear_type":"a",<br>"q_bit_len":256,<br>"r_bit_len":256<br>} | 1. 私钥信息 (912字节) <br>2. 公钥长度信息（2700字节）<br>3. 签名消息长度信息（1602字节） |
| **A1类型线性对：**<br> {<br>"linear_type":"a_one",<br>"order":512<br>}| 1. 私钥信息 (1293字节) <br>2. 公钥长度信息（5180字节）<br>3. 签名消息长度信息（3102字节） |
| **E类型线性对：**<br>{<br>"linear_type":"e",<br>"q_bits_len":1024,<br>"r_bits_len":160}<br> | 1. 私钥信息 (905字节) <br>2. 公钥长度信息（4148字节）<br>3. 签名消息长度信息（2328字节） |
| **F类型线性对：**<br>{<br>"linear_type":"f",<br>"bit_len":256<br>}     | 1. 私钥信息 (653字节) <br>2. 公钥长度信息（2358字节）<br>3. 签名消息长度信息（1584字节）|

- 环签名限制

由于环签名的签名长度以及签名/验证时间与环成员数目呈线性关系，为防止超gas，每个环的环成员数量不能超过32个。

## 接口说明

详见[接口文档](doc/interface.md)。

## 贡献代码

- 我们欢迎并非常感谢您的贡献，请参阅[代码贡献流程](CONTRIBUTING.md)。
- 如项目对您有帮助，欢迎star支持！

## 加入社区

**FISCO BCOS开源社区**是国内活跃的开源社区，社区长期为机构和个人开发者提供各类支持与帮助。已有来自各行业的数千名技术爱好者在研究和使用FISCO BCOS。如您对FISCO BCOS开源技术及应用感兴趣，欢迎加入社区获得更多支持与帮助。

![](https://raw.githubusercontent.com/FISCO-BCOS/LargeFiles/master/images/QR_image.png)

## License

![license](https://img.shields.io/github/license/FISCO-BCOS/group-signature-client.svg)

group-signature-client的开源协议为[GNU GENERAL PUBLIC LICENSE](http://www.gnu.org/licenses/gpl-3.0.en.html). 详情参考[LICENSE](./LICENSE)。