/*file: testRingSig.sol
 *author: fisco-bcos-dev
 *time: 2019.09.05
 *function: test for verifying ring-signature
 */
 
pragma solidity ^0.4.24;
import "./RingSigPrecompiled.sol";

contract testRingSig{
    RingSigPrecompiled rp;
    string sig;
    string message;
    string param_info;
    bool verify_result;

    function testRingSig(string _sig, string _message,
                    string _param_info) public
    {
        rp = RingSigPrecompiled(0x5005);
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
        verify_result = false;
    }

    function get_verify_result() constant public returns(bool)
    {   return verify_result;   }

    function get_sig() constant public returns(string)
    {   return sig;             }

    function get_param_info() constant public returns(string)
    {   return param_info;      }

    function get_message()constant public returns(string)
    {   return message;         }


    //verify validation of ring-signature
    function verify() public returns(bool)
    {
        bool valid;
        valid = rp.ringSigVerify(sig, message, param_info);
        if(valid)
        {
            verify_result = true;
            return true;
        }
        verify_result = false;
        return false;
    }
}
