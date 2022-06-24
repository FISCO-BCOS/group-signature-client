pragma solidity ^0.4.25;
contract RingSigPrecompiled{
  function ringSigVerify(string signature, string message, string paramInfo) public view returns(int, bool);
}
