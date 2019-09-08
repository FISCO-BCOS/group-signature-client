/*file: testGroupSig.sol
 *author: fisco-bcos-dev
 *time: 2019.09.05
 *function: test for verifying group-signature
 */
 
pragma solidity ^0.4.24;
import "./GroupSigPrecompiled.sol";

contract testGroupSig{
    GroupSigPrecompiled gp;
    string sig;
    string message;
    string gpk_info;
    string pbc_param;
    bool verify_result;

    function get_verify_result() constant public returns(bool)
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
        verify_result = false;
    }

    //construct function
    function testGroupSig(string _sig, string _message,
        string _gpk_info, string _pbc_param_info) public
    {
        gp = GroupSigPrecompiled(0x5004);
        sig = _sig;
        message = _message;
        gpk_info = _gpk_info;
        pbc_param = _pbc_param_info;
    }
    
    //call verify
    function verify() public returns(bool)
    {
        bool valid;
        valid = gp.groupSigVerify(sig, message, gpk_info, pbc_param);

        if(valid)
        {
               verify_result = true;
               return true;
        }
        verify_result = false;
        return false;
    }
}
