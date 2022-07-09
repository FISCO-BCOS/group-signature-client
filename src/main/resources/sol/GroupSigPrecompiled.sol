pragma solidity ^0.4.25;
contract GroupSigPrecompiled{
    function groupSigVerify(string signature, string message, string gpkInfo, string paramInfo) public view returns(int, bool);
}
