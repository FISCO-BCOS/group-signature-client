#!/bin/bash

set -e

ring_flag=0
group_flag=0

parse_command()
{
while getopts "r:g:h" option;do
    # shellcheck disable=SC2220
    case ${option} in
    r)
        ring_flag=1
    ;;
    g)
        group_flag=1
    ;;
    h)  help;;
    esac
done
}

# shellcheck disable=SC2120
help()
{
    echo "$1"
    cat << EOF
Usage:
    -r  [Optional]   run commands for ring sig
    -g  [Optional]   run commands for group sig
    -h  call for help
EOF
exit 0
}

main()
{
    if [ ${ring_flag} -eq 1 ]; then
        java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.groupsig.app.Main './conf/conn.json' setup_ring 'ring1'

        java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.groupsig.app.Main './conf/conn.json' join_ring 'ring1'
        java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.groupsig.app.Main './conf/conn.json' join_ring 'ring1'
        java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.groupsig.app.Main './conf/conn.json' join_ring 'ring1'
        java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.groupsig.app.Main './conf/conn.json' join_ring 'ring1'

        java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.groupsig.app.Main './conf/conn.json' deploy_ring_sig 'hello' 'ring1' '0' '4'
    elif [ ${group_flag} -eq 1 ]; then
        java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.groupsig.app.Main './conf/conn.json' create_group 'group1' '123' '{\"linear_type\":\"a\", \"q_bits_len\": 256, \"r_bits_len\":256}'

        java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.groupsig.app.Main './conf/conn.json' join_group 'group1' 'member1'

        java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.groupsig.app.Main './conf/conn.json' deploy_group_sig 'group1' 'member1' 'hello'
    else
        help
    fi
}

parse_command
main
