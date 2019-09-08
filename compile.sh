#!/bin/bash
function LOG_ERROR() {
  local content=${1}
  echo -e "\033[31m"${content}"\033[0m"
}

function LOG_INFO() {
  local content=${1}
  echo -e "\033[34m"${content}"\033[0m"
}

function execute_cmd() {
  local command="${1}"
  eval ${command}
  local ret=$?
  if [ $ret -ne 0 ]; then
    LOG_ERROR "execute command ${command} FAILED"
    exit 1
  else
    LOG_INFO "execute command ${command} SUCCESS"
  fi
}

CUR_DIR=$(pwd)

function check_java_env() {
  type java >/dev/null 2>&1
  if [ $? -eq 0 ]; then
    JAVA_VER=$(java -version 2>&1 | sed -n ';s/.* version "\(.*\)\.\(.*\)\..*"/\1\2/p;')
    #1.8 or higher version
    if [ $JAVA_VER -ge 18 ]; then
      return 0
    else
      LOG_ERROR "java version must be above 1.8, now version info is "
      LOG_ERROR $(java -version)
      return 1
    fi
  else
    LOG_ERROR "java is not installed."
    return 1
  fi
}

function install_java() {
  local ret=$(check_java_env)
  if [ $? -eq 0 ]; then
    LOG_INFO "java has been installed"
    return 0
  fi
  execute_cmd 'wget --no-check-certificate --no-cookie --header "Cookie: oraclelicense=accept-securebackup-cookie" http://download.oracle.com/otn-pub/java/jdk/8u162-b12/0da788060d494f5095bf8624735fa2f1/jdk-8u162-linux-x64.tar.gz -O jdk-8u162-linux-x64.tar.gz'
  execute_cmd "tar -zxvf jdk-8u162-linux-x64.tar.gz"
  execute_cmd "mkdir -p ~/opt &&  mv jdk1.8.0_162 ~/opt/jdk-1.8 && rm -rf jdk-8u162-linux-x64.tar.gz"
  if ! grep -Eq "JAVA_HOME" ~/.bashrc; then
    execute_cmd 'echo "export JAVA_HOME=\$HOME/opt/jdk-1.8" >> ~/.bashrc'
    execute_cmd 'echo "export JRE_HOME=\$HOME/opt/jdk-1.8/jre" >> ~/.bashrc'
    execute_cmd 'echo "export CLASSPATH=.:\$JAVA_HOME/lib:\$JRE_HOME/lib:\$CLASSPATH" >> ~/.bashrc'
    execute_cmd 'echo "export PATH=\$JAVA_HOME/bin:\$JRE_HOME/bin:\$PATH" >> ~/.bashrc'
  fi
  source ~/.bashrc
  return 0
}

function install_gradle() {
  if [ ! -d "${HOME}/opt/gradle-4.6" ]; then
    execute_cmd "wget https://services.gradle.org/distributions/gradle-4.6-bin.zip -O gradle-4.6-bin.zip"
    execute_cmd "mkdir -p ~/opt && unzip gradle-4.6-bin.zip && mv gradle-4.6 ~/opt/"
    execute_cmd "rm -rf gradle-4.6-bin.zip"
  fi

  if ! grep -Eq "gradle" ~/.bashrc; then
    LOG_INFO "export grandle"
    execute_cmd 'echo "export PATH=\$PATH:\$HOME/opt/gradle-4.6/bin" >> ~/.bashrc'
  fi
  source ~/.bashrc
}

function compile_sdk_client() {
  execute_cmd "gradle build"
}

install_java
install_gradle
compile_sdk_client
