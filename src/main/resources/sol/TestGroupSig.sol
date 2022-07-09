/*file: TestGroupSig.sol
 *author: fisco-bcos-dev
 *time: 2019.09.05
 *function: test for verifying group-signature
 */
 
pragma solidity ^0.4.24;
import "./GroupSigPrecompiled.sol";

contract TestGroupSig{
    GroupSigPrecompiled gp;
    string group_sig;
    string group_message;
    string group_gpk_info;
    string group_pbc_param;
    bool group_verify_result = false;

    function TestGroupSig(string _sig, string _message,
        string _gpk_info, string _pbc_param_info) public
    {
        gp = GroupSigPrecompiled(0x5004);
        group_sig = _sig;
        group_message = _message;
        group_gpk_info = _gpk_info;
        group_pbc_param = _pbc_param_info;
    }

    function get_group_verify_result() public view returns(bool)
    {  return group_verify_result; }

    function get_group_sig() public view returns(string)
    {  return group_sig;          }

    function get_group_message() public view returns(string)
    {  return group_message;      }

    function get_group_gpk_info() public view returns(string)
    {  return group_gpk_info;     }

    function get_group_pbc_param() public view returns(string)
    {  return group_pbc_param;    }

    function update_group_sig_data(string new_sig, string new_message,
                            string new_gpk_info,
                            string new_pbc_param_info) public
    {
        group_sig = new_sig;
        group_message = new_message;
        group_gpk_info = new_gpk_info;
        group_pbc_param = new_pbc_param_info;
        group_verify_result = false;
    }
    
    //call verify
    function verify_group_sig() public returns(bool)
    {
        int code = 0;
        (code, group_verify_result) = gp.groupSigVerify(group_sig, group_message, group_gpk_info, group_pbc_param);
        return group_verify_result;
    }
}
