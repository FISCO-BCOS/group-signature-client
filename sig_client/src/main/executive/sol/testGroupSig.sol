/*file: testGroupSig.sol
 *author: fisco-dev
 *time: 2017.2.27
 *function: 群签名算法测试合约
 */
 
pragma solidity ^0.4.2;
import "libGroupSig.sol";

contract testGroupSig{
    using libGroupSig for *;
    string sig;
    string message;
    string gpk_info;
    string pbc_param;
    string verify_result;

    function get_verify_result() constant public returns(string)
    {  return verify_result; }

    function get_sig()constant public returns(string)
    {  return sig;          }

    function get_message()constant public returns(string)
    {  return message;      }

    function get_gpk_info()constant public returns(string)
    {  return gpk_info;     }

    function get_pbc_param() constant public returns(string)
    {  return pbc_param;    }
    
    function update_sig_data(string new_sig, string new_message,
                            string new_gpk_info, 
                            string new_pbc_param_info) public
    {
        sig = new_sig;
        message = new_message;
        gpk_info = new_gpk_info;
        pbc_param = new_pbc_param_info;
        verify_result = "null";
    }

    //construct function
    function testGroupSig(string _sig, string _message,
        string _gpk_info, string _pbc_param_info) public
    {
        sig = _sig;
        message = _message;
        gpk_info = _gpk_info;
        pbc_param = _pbc_param_info;
    }
    
    //call verify
    function verify() public returns(string)
    {
        string memory valid;
        uint ret_code;
        (valid, ret_code)= libGroupSig.BBS04_group_verify(sig,
               message, gpk_info, pbc_param);
        if( ret_code == libGroupSig._get_succ_code())
        {
            verify_result = valid;
            return valid;
        }
        if( ret_code == libGroupSig._get_ethcall_nofound_code())
        {
            verify_result = new string(50);
            verify_result = "ethcall GroupSig not implemented";
            return valid;
        }
        verify_result = "false";
        return "false";
    }
}
