pragma solidity ^0.4.24;
  contract GroupSigPrecompiled{
      function groupSigVerify(string signature, string message, string gpkInfo, string paramInfo) public constant returns(bool);
}
