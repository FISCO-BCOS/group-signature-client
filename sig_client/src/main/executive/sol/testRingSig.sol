/*file: testRingSig.sol
 *author: fisco-dev
 *time: 2017.2.27
 *function: 环签名算法测试合约
 */
 
pragma solidity ^0.4.2;
import "libRingSig.sol";

contract testRingSig{
    using libRingSig for *;
    string sig;
    string message;
    string param_info;
    string verify_result;

    function testRingSig(string _sig, string _message,
                    string _param_info) public
    {
        sig = _sig;
        message = _message;
        param_info = _param_info;
    }
   
    function update_sig_data(string new_sig, string new_message,
             string new_param_info) public
    {
        sig = new_sig;
        message = new_message;
        param_info = new_param_info;
        verify_result = "null";
    }

    function get_verify_result() constant public returns(string)
    {   return verify_result;   }

    function get_sig() constant public returns(string)
    {   return sig;             }

    function get_param_info() constant public returns(string)
    {   return param_info;      }

    function get_message()constant public returns(string)
    {   return message;         }


    //verify validation of ring-signature
    function verify() public returns(string)
    {
        string memory valid;
        uint ret;
        (valid, ret) = libRingSig.linkable_ring_sig_verify(sig,
                    message, param_info);
        if( ret == libRingSig._get_succ_code())
        {
            verify_result = valid;
            return valid;
        }
        if( ret == libRingSig._get_ethcall_nofound_code())
        {
            verify_result = new string(50);
            verify_result = "ethcall RingSig not implemented";
            return valid;
        }
        verify_result = "0";
        return "0";
    }
}
