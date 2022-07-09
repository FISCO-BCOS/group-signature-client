/*file: TestRingSig.sol
 *author: fisco-bcos-dev
 *time: 2019.09.05
 *function: test for verifying ring-signature
 */
 
pragma solidity ^0.4.25;
import "./RingSigPrecompiled.sol";

contract TestRingSig{
    RingSigPrecompiled rp;
    string ring_sig;
    string ring_message;
    string ring_param_info;
    bool ring_verify_result = false;

    function TestRingSig(string _sig, string _message,
                    string _param_info) public
    {
        rp = RingSigPrecompiled(0x5005);
        ring_sig = _sig;
        ring_message = _message;
        ring_param_info = _param_info;
    }

    function get_ring_verify_result() public view returns(bool)
    {   return ring_verify_result;   }

    function get_ring_sig() public view returns(string)
    {   return ring_sig;             }

    function get_ring_param_info() public view returns(string)
    {   return ring_param_info;      }

    function get_ring_message() public view returns(string)
    {   return ring_message;         }

    function update_ring_sig_data(string new_sig, string new_message,
             string new_param_info) public
    {
        ring_sig = new_sig;
        ring_message = new_message;
        ring_param_info = new_param_info;
        ring_verify_result = false;
    }

    //verify validation of ring-signature
    function verify_ring_sig() public returns(bool)
    {
        int code = 0;
        (code, ring_verify_result) = rp.ringSigVerify(ring_sig, ring_message, ring_param_info);
        return ring_verify_result;
    }
}
