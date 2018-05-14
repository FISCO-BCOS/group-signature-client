/* file: libGroupSig.sol
 * author: fisco-dev
 * time: 2017.02.25
 * function: 群签名算法合约接口(调用了C++ ethcall)
 */
 
pragma solidity ^0.4.2;
import "LibString.sol";
 
library libGroupSig{
    using LibString for *;

    enum CALLBACKTYPE{CREATE, GENKEY, SIG,
         VERIFY, OPEN, REVOKE, UPDATEGSK}
    enum ALGOTYPE{BBS04, BS04_VLR, ACHM05}
    enum KEYTYPE{GPK, GMSK, GSK, PARAM, SIG, CERT}

    //ethcall id of group sig algorithm
    uint32 constant ethcall_id = 0x66670;

    function _get_succ_code() internal constant returns(uint)
    {
        uint succ = 0;
        return succ;
    }
    
    //ethcall not found
    function _get_ethcall_nofound_code() internal constant returns(uint)
    {
        uint not_found = 9999;
        return not_found;
    }

    
    /*@function: 获取指定算法指定类型key的固定长度
     *@param: 1.algo_type: 群签名算法类型(BBS04, BS04_VLR, ACHM05)
     *        2.key_type: key类型(gpk-群公钥, GMSK-群主私钥, GSK-群成员私钥)
     */
     function _get_fixed_len(ALGOTYPE algo_type, KEYTYPE key_type, uint32 key_len) internal constant returns(uint32)
     {
         if(algo_type == ALGOTYPE.BBS04)
         {
            if(key_type == KEYTYPE.GPK)
                return (100 + 10*key_len);
            if(key_type == KEYTYPE.GMSK)
                return (20 + 2*key_len);
            if(key_type == KEYTYPE.GSK)
                return (40 + 3*key_len);
            if(key_type == KEYTYPE.SIG)
                return (9*key_len);
            if(key_type == KEYTYPE.PARAM)
                return 2000;
            if(key_type == KEYTYPE.CERT)
                return 1000;
         }
         //default length: 100
         return 0;     
     }
     
    /* @function: 调用的群签名算法类型
     * @param: 群签名算法类型，包括:
     *   CREATE：生成群(群公钥&&群主私钥)
     *   GENKEY：为群成员产生私钥
     *   SIG: 签名
     *   VERIFY: 验证签名
     *   OPEN: 打开签名
     *   REVOKE: 撤销群成员
     *   UPDATEGSK: 群成员撤销后更新群成员私钥(BBS04算法需要)
     */
     function _get_callback_id(CALLBACKTYPE callback) internal constant returns(uint8)
     {
        if(callback == CALLBACKTYPE.CREATE) 
            return 0x00;
        if(callback == CALLBACKTYPE.GENKEY)
            return 0x01;
        if(callback == CALLBACKTYPE.SIG)
            return 0x02;
        if(callback == CALLBACKTYPE.VERIFY)
            return 0x03;
        if(callback == CALLBACKTYPE.OPEN)
            return 0x04;
        if(callback == CALLBACKTYPE.REVOKE)
            return 0x05;
        if(callback == CALLBACKTYPE.UPDATEGSK)
            return 0x06;
     }

   /* @function: 获取群签名算法类型ID
    * @param:群签名算法类型，包括:
    * @BBS04 (IMPLEMENTED):Dan Boneh, Xavier Boyen, Hovav Shacham(2004)
    *   Short Group Signatures
    * @BS04_VLR (UNIMPLEMENTED):Dan Boneh, Hovav Shacham(2004)
    *   Group Signatures with Verifier-Local Revocation
    * @ACHM05 (UNIMPLEMENTED):Giuseppe Ateniese, Jan Camenisch, Susan Hohenberger, Breno de Medeiros(2005)
    *   Practical Group Signatures without Random Oracles
    */
    function _get_algorithm_type(ALGOTYPE algo_type) internal constant returns(uint8)
    {
        if(algo_type == ALGOTYPE.BBS04)
            return 0x00;
        if(algo_type == ALGOTYPE.BS04_VLR)
            return 0x01;
        if(algo_type == ALGOTYPE.ACHM05)
            return 0x02;
        //default is BBS04 algorithm
        return 0x00;
    }

    /*@function: 根据群签名算法类型&&调用方法类型调用ethcall实现的群签名算法
     *@param:1. algo_type: 算法类型(BBS04, BS04_VLR, ACHM05)
     *       2. callback_type:调用类型(create, sig, verify, open, update_gsk)
     *       3. result: 存储群签名算法调用结果
     *       4. param: 调用群签名算法的参数
     */
    function callback_ethcall(ALGOTYPE algo_type, 
                              CALLBACKTYPE callback_type, 
                              string result,
                              string param) private returns(string, uint)
    {
        uint8 algo_type_id = _get_algorithm_type(algo_type);
        uint8 callback_type_id = _get_callback_id(callback_type);
        uint r;
        uint32 _id = ethcall_id;
        assembly{
            r := ethcall(_id, result, callback_type_id, algo_type_id, param, 0, 0, 0, 0, 0)
        }
        return (result, r);
    }

    //返回值: gpk_info, gmsk_info, pbc_param_str
    function create_group(string param) internal constant returns(string, uint)
    {
        uint32 key_len = 512; //512bits, 128 Bytes
        uint32 gpk_len = _get_fixed_len(ALGOTYPE.BBS04, KEYTYPE.GPK, key_len);
        uint32 gmsk_len = _get_fixed_len(ALGOTYPE.BBS04, KEYTYPE.GMSK, key_len);
        uint32 gamma_len = key_len;
        uint32 param_len = _get_fixed_len(ALGOTYPE.BBS04, KEYTYPE.GMSK, key_len);
        
        uint32 total_len = gpk_len + gmsk_len + gamma_len + param_len + 3;

        string memory result = new string(total_len);
        uint ret_code;
        (result, ret_code) = callback_ethcall(ALGOTYPE.BBS04, CALLBACKTYPE.CREATE, result, param); 
        //TODO: 参数解析
        return (result, ret_code);
    }
    
    function BBS04_create_group(string pbc_param_setting)
             internal constant returns(string, uint)
    {
        string memory param = pbc_param_setting;
        return create_group(param);
    }
   
    function BBS04_create_group()
             internal constant returns(string, uint)
    {
        string memory param = "";
        return create_group(param);
    }

    function _get_split_symbol() private returns(string)
    {
        string memory split_symbol = "*";
        return split_symbol;
    }
    
    //群签名
    function BBS04_group_sig(string gpk_info, string gsk_info,
                             string pbc_param_str, string message)
                             internal constant returns(string, uint)
    {
        string memory param = param.concat(gpk_info).concat(_get_split_symbol());
        param = param.concat(gsk_info).concat(_get_split_symbol());
        param = param.concat(pbc_param_str).concat(_get_split_symbol());
        param = param.concat(message);
        
        uint32 key_len = 512;
        string memory sig = new string(_get_fixed_len(ALGOTYPE.BBS04,
                            KEYTYPE.SIG, key_len));
        uint ret_code;
        (sig, ret_code) = callback_ethcall(ALGOTYPE.BBS04, CALLBACKTYPE.SIG, sig, param);
        return (sig, ret_code);
    }
    
    //群签名验证
    function BBS04_group_verify(string sig, string message, 
                                string gpk_info, string pbc_param)
                                internal constant returns(string, uint)
                                
    {
        string memory param = param.concat(sig).concat(_get_split_symbol());
        param = param.concat(message).concat(_get_split_symbol());
        param = param.concat(gpk_info).concat(_get_split_symbol());
        param = param.concat(pbc_param);
        
        string memory valid = new string(10);
        uint ret_code;
        (valid, ret_code) = callback_ethcall(ALGOTYPE.BBS04, CALLBACKTYPE.VERIFY, valid, param);
        return (valid, ret_code);
    }
    
    //群成员加入
    function BBS04_group_member_join(string pbc_param, string gmsk_info, 
                                     string gpk_info, string gamma_info)
                                     internal constant returns(string, uint)
    {
        string memory param = param.concat(pbc_param).concat(_get_split_symbol());
        param = param.concat(gmsk_info).concat(_get_split_symbol());
        param = param.concat(gpk_info).concat(_get_split_symbol());
        param = param.concat(gamma_info);
        
        uint32 key_len = 512;
        string memory gsk = new string(_get_fixed_len(ALGOTYPE.BBS04, KEYTYPE.GSK, key_len));
        uint ret_code;
        (gsk, ret_code) = callback_ethcall(ALGOTYPE.BBS04, CALLBACKTYPE.GENKEY, gsk, param);
        return (gsk, ret_code);
    }
    
    //群成员撤出
    function BBS04_gm_revoke(string org_gpk, string pbc_param, 
             string revoke_info, 
             string gamma_info) internal constant returns(string, uint)
    {
        string memory param = param.concat(pbc_param).concat(_get_split_symbol());
        param = param.concat(revoke_info).concat(_get_split_symbol());
        param = param.concat(gamma_info);
        
        //BBS04算法:群成员撤出时，需要更新gpk
        string memory updated_gpk = org_gpk;
        uint ret_code;
        (updated_gpk, ret_code) = callback_ethcall(ALGOTYPE.BBS04, CALLBACKTYPE.REVOKE, updated_gpk, param);
        return (updated_gpk, ret_code);
    }
    
    //群成员撤出后，更新私钥
    function BBS04_update_gsk(string org_gsk, string pbc_param, 
                             string revoke_list, string gone_list, 
                             string gtwo)
                             internal constant returns(string, uint)
    {
        string memory param = param.concat(pbc_param).concat(_get_split_symbol());
        param = param.concat(revoke_list).concat(_get_split_symbol());
        param = param.concat(gone_list).concat(_get_split_symbol());
        param = param.concat(gtwo);

        string memory updated_gsk = org_gsk;
        uint ret_code;
        (updated_gsk, ret_code) = callback_ethcall(ALGOTYPE.BBS04, CALLBACKTYPE.UPDATEGSK, updated_gsk, param);
        return (updated_gsk, ret_code);
    }
    
    //打开群签名证书
    function BBS04_open_cert(string sig, string message,
                   string gpk_info, string gmsk_info,
                   string pbc_param_info)
                   internal constant returns(string, uint)
    {
        string memory param = param.concat(sig).concat(_get_split_symbol());
        param = param.concat(message).concat(_get_split_symbol());
        param = param.concat(gpk_info).concat(_get_split_symbol());
        param = param.concat(gmsk_info).concat(_get_split_symbol());
        param = param.concat(pbc_param_info).concat(_get_split_symbol());
        uint32 key_len = 512;
        string memory opened_cert = new string(_get_fixed_len(ALGOTYPE.BBS04, KEYTYPE.CERT, key_len)); 
        uint ret_code;
        (opened_cert, ret_code) = callback_ethcall(ALGOTYPE.BBS04, CALLBACKTYPE.OPEN,
                                    opened_cert, param);
        return (opened_cert, ret_code);
    }

 } 
