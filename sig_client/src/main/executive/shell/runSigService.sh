#!/bin/bash
dirpath="$(cd "$(dirname "$0")" && pwd)"

SigService="$dirpath/sig_client"
chmod +x ${SigService}

# @function: output log with red color (error log)
# @param: content: error message
function LOG_ERROR()
{
    local content=${1}
    echo -e "\033[31m"${content}"\033[0m"
}

# @function: output information log
# @param: content: information message
function LOG_INFO()
{
    local content=${1}
    echo -e "\033[34m"${content}"\033[0m"
}

# @function: execute command
# @params: 1. command: command to be executed
function execute_cmd()
{
    local command="${1}"
    eval "${command}"
    local ret=$?
    if [ $ret -ne 0 ];then
        LOG_ERROR "execute command ${command} FAILED"
        exit 1
    else
        LOG_INFO "execute command ${command} SUCCESS"
    fi
}

# @function: deploy contract, and write group signature to blockchain
# @params: 1. config_file: config file path, config (ip, port)
#          2. group_name: group_name used to generate group sig 
#          3. member_name: member that want to genrate group sig, 
#             and deploy the sig to blockchain
#          4. message: plain text of the signature

function deploy_group_sig()
{
    local config_file="${1}"
    local group_name="${2}"
    local member_name="${3}"
    local message="${4}"

    local command=${SigService}" ${config_file} deploy_group_sig user.jks 123456 123456 ${group_name} ${member_name} ${message}"
    execute_cmd "${command}"
}


###deploy ring sig contract
# @function: deploy contract, and write ring signature to blockchain
# @params: 1. config_file: file path of configuration(used to config (ip, port))
#          2. message: plain text of the signature
#          3. ring_name: specified ring the signature belongs to
#          4. mem_pos: position of specified member in specified ring to 
#             deploy its signature to blockchain
#          5. ring_size: option,
#             number of public keys of all used to generate ring signature 
function deploy_ring_sig()
{
    local config_file="${1}"
    local message="${2}"
    local ring_name="${3}"
    local mem_pos="${4}"
    local ring_size="4"
    if [ $# -ge 5 ];then
        ring_size="${5}"
    fi
    local command=${SigService}" ${config_file} deploy_ring_sig user.jks 123456 123456 ${message} ${ring_name} ${mem_pos} ${ring_size}"
    execute_cmd "${command}"
}

# @function: verify specified group signature on blockchain is valid or not
# @params: 1. config_file: file path of (ip, port) configuration
#          2. contract_address: contract address related to some group signature
#          3. stress_test: (1) "0": callback on-chain verification only once
#                          (2) "1": callback on-chian verification continually
#                              (for stress testing) 
function eth_group_verify()
{
    local config_file="${1}"
    local contract_address="${2}"
    local stress_test="0"
    if [ $# -ge 3 ];then
        stress_test="${3}"
    fi
    local command="${SigService} ${config_file} group_sig_verify user.jks 123456 123456 ${contract_address} ${stress_test}"
    execute_cmd "${command}"
}

# @function: verify specified ring signature on blockchain is valid or not
# @params: 1. config_file: file path of (ip, port) configuration
#          2. contract_address: contract address related to some ring signature
#          3. stress_test: (1) "0": callback on-chain verification only once
#                          (2) "1": callback on-chian verification continually
#                              (for stress testing) 
function eth_ring_verify()
{
    local config_file="${1}"
    local contract_address="${2}"
	local stress_test="0"
	if [ $# -ge 3 ];then
		stress_test="${3}"
	fi
    local command="${SigService} ${config_file} ring_sig_verify user.jks 123456 123456 ${contract_address} ${stress_test}"
    echo "command = "${command}
    execute_cmd "${command}"
}

######*******group sig related RPC**********####
# @function: create group with specified group_name
#           (generate public keys, params and group manager private key)
# @params: 1. config_file: file path of (ip, port) configuration
#          2. group_name: group name
#          3. gm_pass: password of group manager(is reserved now, can be every string)
#          4. pbc_param: specified pbc linear pair used to generate group param, 
#             default is A type linear pair, examples:
#             (1) A type linear pair: 
#               '"{\"linear_type\": \"a\", \"q_bits_len\": , \"r_bits_len\": 256, \"q_bits_len\":256}"'
#             (2) A1 type linear pair: 
#               '"{\"linear_type\":\"a_one\", \"order\":512}"'
#             (3) E type linear pair:
#              '"{\"linear_type\":\"e\", \"q_bits_len\": 512, \"r_bits_len\":512}"'
#             (4) F type linear pair:
#              '"{\"linear_type\":\"f\", \"bit_len\": 256}"'
function create_group()
{
    local config_file="${1}"
    local group_name="${2}"
    local gm_pass="${3}"
    local pbc_param=""
    if [ $# -ge 4 ];then
        pbc_param="${4}"
    fi
    local command="${SigService} ${config_file} create_group ${group_name} ${gm_pass} ${pbc_param}"
    LOG_INFO "command=${command}"
    execute_cmd "${command}"
}

##function: join group
# @function: group manager generate private key && cert for group members
# @params: 1. config_file: file path of (ip, port) configuration
#          2. group_name: group name of the member wants to join in
#          3. member_name: member id of the one wants to obtain group private key and cert
function join_group()
{
    local config_file="${1}"
    local group_name="${2}"
    local member_name="${3}"
    local command="${SigService} ${config_file} join_group ${group_name} ${member_name}"
    execute_cmd "${command}"
}

# @function: generate signature with bbs04 group sig algorithm 
# @params: 1. config_file: file path of (ip, port) configuration
#          2. group_name: group name of the signature belongs to 
#          3. member_name: member name of the signature belongs to
#          4. message: plain text 
function group_sig()
{
    local config_file="${1}"
    local group_name="${2}"
    local member_name="${3}"
    local message="${4}"
    local stress_test="0"
    if [ $# -ge 5 ];then
        stress_test="${5}"
    fi
    local command="${SigService} ${config_file} group_sig ${group_name} ${member_name} ${message} ${stress_test}"
    execute_cmd "${command}"
}

# @function: verify specified signature
# @params: 1. config_file: file path of (ip, port) configuration
#          2. group name: group name the signature belongs to
#          3. sig: signature
#          4. message: plain text
#          5. stress test: optional
#             (1)"0": verify only once
#             (2) "1": verify continually, used for stress testing
function group_verify()
{
    local config_file="${1}"
    local group_name="${2}"
    local sig="${3}"
    local message="${4}"
    local stress_test="0"
    if [ $# -ge 5 ];then
        stress_test="${5}"
    fi
    local command="${SigService} ${config_file} group_verify ${group_name} ${sig} ${message} ${stress_test}"
    LOG_INFO "GROUP VERIFY COMMAND = ${command}"
    execute_cmd "${command}"
}

##open_cert
# @function: get cert according to given signature
#            (only group manager can calculate the cert)
#            (generally used in regulation cases)
# @params: 1. config_file: file path of (ip, port) configuration
#          2. group_name: group name of specified signature belongs to
#          3. sig: signature to trace its signer
#          4. message: plain text
#          5. gm_pass: password of group manager(reserved now, can be every string)
function open_cert()
{
    local config_file="${1}"
    local group_name="${2}"
    local sig="${3}"
    local message="${4}"
    local gm_pass="${5}"
    local command="${SigService} ${config_file} open_cert ${group_name} ${sig} ${message} ${gm_pass}"
    execute_cmd "${command}"
}

# @function: get public key of specified group
# @params: 1. config_file: file path of (ip, port) configuration
#          2. group_name: group name 
function get_public_info()
{
    local config_file="${1}"
    local group_name="${2}"
    local command="${SigService} ${config_file} get_public_info ${group_name}"
    execute_cmd "${command}"
}

# @function: get private key of group manager
# @params: 1. config_file: file path of (ip, port) configuration
#          2. group_name: group name of the group manager belongs to
#          3. gm_pass: password of group manager(is reserved now, can be every string)
function get_gm_info()
{
    local config_file="${1}"
    local group_name="${2}"
    local gm_pass="${3}"
    local command="${SigService} ${config_file} get_gm_info ${group_name} ${gm_pass}"
    execute_cmd "${command}"
}

# @function: get private key of specified group member
# @params: 1. config_file: file path of (ip, port) configuration
#          2. group_name: group name the member belongs to
#          3. member_name: member name
#          4. pass: password of the member(is reserved now, can be every string)
function get_member_info()
{
    local config_file="${1}"
    local group_name="${2}"
    local member_name="${3}"
    local pass="${4}"
    local command="${SigService} ${config_file} get_member_info ${group_name} ${member_name} ${pass}"
    execute_cmd "${command}"
}

############Ring Sig related RPC#######

# @function: generate params for specified ring 
# @params: 1. config_file: file path of (ip, port) configuration
#          2. ring_name: ring name
function setup_ring()
{
    local config_file="${1}"
    local ring_name="${2}"
    local bit_len="1024"
    if [ $# -ge 3 ];then
        bit_len="${3}"
    fi
    local command="${SigService} ${config_file} setup_ring ${ring_name} ${bit_len}"
    execute_cmd "${command}"
}

# @function: generate private key for member of specified ring
# @params: 1. config_file: file path of (ip, port) configuration
#          2. ring_name: ring name
function join_ring()
{
    local config_file="${1}"
    local ring_name="${2}"
    local command="${SigService} ${config_file} join_ring ${ring_name}"
    execute_cmd "${command}"
}

# @function: generate signature with linkable ring sig
#            algorithm for specified ring member
# @params: 1. config_file: file path of (ip, port) configuration
#          2. ring_name: ring name
#          3. member_pos: postion of the member to generate signature
#          4. message: plain text
#          5. ring_size: ring size of the member used to generate signature
#          6. stress_test: optional
#          (1) "0": only generate signature once
#          (2) "1": generate signature continually(for stress testing)
function ring_sig()
{
    local config_file="${1}"
    local ring_name="${2}"
    local member_pos="${3}"
    local message="${4}"
    local ring_size="4"
    local stress_test="0"
    if [ $# -ge 5 ];then
        ring_size="${5}"
    fi
    if [ $# -ge 6 ];then
        stress_test="${6}"
    fi
    local command="${SigService} ${config_file} ring_sig ${message} ${ring_name} ${member_pos} ${ring_size} ${stress_test}"
    execute_cmd "${command}"
}

# @function: verify given signature is valid or not
# @params: 1. config_file: file path of (ip, port) configuration
#          2. ring_name: ring name
#          3. signature: linkable ring signature
#          4. message: plain text of given signature
#          5. stress_test: optional
#          (1) "0": only verify signature once
#          (2) "1": verify signature continually(for stress testing)
function ring_verify()
{
    local config_file="${1}"
    local ring_name="${2}"
    local sig="${3}"
    local message="${4}"
    local stress_test="0"
    if [ $# -ge 5 ];then
        stress_test="${5}"
    fi
    local command="${SigService} ${config_file} ring_verify ${ring_name} ${sig} ${message} ${stress_test}"
    execute_cmd "${command}"
}

# @function: get ring params according to given ring_name
# @params: 1. config_file: file path of (ip, port) configuration
#          2. ring_name: ring_name
function get_ring_param()
{
    local config_file="${1}"
    local ring_name="${2}"
    local command="${SigService} ${config_file} get_ring_param ${ring_name}"
    execute_cmd "${command}"
}

# @function: get public key of specified member
# @params: 1. config_file: file path of (ip, port) configuration
#          2. ring_name: ring name
#          3. member_pos: position of the public key
function get_ring_public_key()
{
    local config_file="${1}"
    local ring_name="${2}"
    local member_pos="${3}"
    local command="${SigService} ${config_file} get_ring_public_key ${ring_name} ${member_pos}"
    execute_cmd "${command}"
}

# @function: get private key of specified ring member
# @params: 1. config_file: file path of (ip, port) configuration
#          2. ring_name: ring name
#          3. member_pos: position of the member
function get_ring_private_key()
{
    local config_file="${1}"
    local ring_name="${2}"
    local member_pos="${3}"
    local command="${SigService} ${config_file} get_ring_private_key ${ring_name} ${member_pos}"
    execute_cmd "${command}"
}

# @function: get time interval
# @params: 1. start: start time
#          2. end: end time
function get_time()
{
    local start="${1}"
    local end="${2}"
    local start_s=$(echo $start | cut -d '.' -f 1)
    local start_ns=$(echo $start | cut -d '.' -f 2) 
    local end_s=$(echo $end | cut -d '.' -f 1)
    local end_ns=$(echo $end | cut -d '.' -f 2)

    echo "start:"${start}
    echo "end:"${end}

    local time=$(( ( 10#$end_s - 10#$start_s ) * 1000 + ( 10#$end_ns / 1000000 - 10#$start_ns / 1000000 ) )) 
    echo "${time} ms"
}

# @function: update ring signature of specified contract address 
# @params: 1. config_file: file path of (ip, port) configuration
#          2. contract_addr: contract address whose signature should be updated
#          3. message: plain text of the new signature
#          4. ring_name: ring name
#          5. mem_pos: position of the member to generate new signature
#          6. ring_size: optional
#             ring size of the member used to generate signature(default is 4) 
function update_ring_sig_data()
{
    local config_file="${1}"
    local contract_addr="${2}"
    local message="${3}"
    local ring_name="${4}"
    local mem_pos="${5}"
    local ring_size="4"
    if [ $# -ge 6 ];then
        ring_size="${6}"
    fi
    local command="${SigService} ${config_file} update_ring_sig_data user.jks 123456 123456 ${contract_addr} ${message} ${ring_name} ${mem_pos} ${ring_size}"
    execute_cmd "${command}"
}

# @function: update group signature of specified contract address
# @params: 1. config_file: file path of (ip, port) configuration
#          2. contract_addr: contract address whose signature should be updated
#          3. message: plain text of the new signature
#          4. group_name: group of the new signature
#          5. mem_name: the signer of the signature
function update_group_sig_data()
{
    local config_file="${1}"
    local contract_addr="${2}"
    local group_name="${3}"
    local mem_name="${4}"
    local message="${5}"
    local command="${SigService} ${config_file} update_group_sig_data user.jks 123456 123456 ${contract_addr} ${group_name} ${mem_name} ${message}"
    execute_cmd "${command}"
}
####group sig example
#create_group "conn.json" "group4" "123" '"{\"linear_type\":\"f\", \"bit_len\":256}"'
#join_group "conn.json" "group4" "member1"
#group_sig "conn.json" "group4" "member1" "hello"
#deploy_group_sig "conn.json" "group4" "member1" "hello"
#true means that verify succ
#eth_group_verify "conn.json" "0x1af99bf7d62bf6629459d47399358b23c19fcbd2"
#update_group_sig_data "conn.json" "0x1af99bf7d62bf6629459d47399358b23c19fcbd2" "group4" "test1" "hello" 


####ring sig example
#setup_ring "conn.json" "ring1"
#join_ring "conn.json" "ring1"
#join_ring "conn.json" "ring1"
#join_ring "conn.json" "ring1"
#join_ring "conn.json" "ring1"
#ring_sig "conn.json" "ring1" "0" "hello" "4"
#1 represents that verify succ
#deploy_ring_sig "conn.json" "hello" "ring1" "0" "4"
#eth_ring_verify "conn.json" "0x418d9da6382993e6955135db364c7dba88e483a4"
#update_ring_sig_data "conn.json" "0x418d9da6382993e6955135db364c7dba88e483a4" "hello" "ring1" "1" "4"
