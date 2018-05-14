/* file: libGroupSig.sol
 * author: fisco-dev
 * time: 2017.02.25
 * function: 群签名算法合约接口(调用了C++ ethcall)
 */
 
pragma solidity ^0.4.2;
import "LibString.sol";
 
library libRingSig{
    using LibString for *;

    //ethcall ID of ring sig
    uint32 constant ethcall_id = 0x66671;
     
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

    //ring sig verify
    function linkable_ring_sig_verify(string sig, string message,
                 string param_info)internal constant returns(string, uint)
    {
        string memory result = new string(2);
        uint32 callback_id = ethcall_id;
        uint r;
        assembly{
            r := ethcall(callback_id, result, sig, message, param_info,0,0,0,0,0 )
        }
        return (result, r);
    }
 } 
