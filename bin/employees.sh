#!/bin/bash

HOME_DIR="/home/user/LocalSync"

LIB_PATH=$HOME_DIR"/lib"
CLASS_PATH="-cp ./":
CLASS_PATH=$CLASS_PATH$HOME_DIR"/classes":
CLASS_PATH=$CLASS_PATH$LIB_PATH"/commons-dbcp-1.4.jar":
CLASS_PATH=$CLASS_PATH$LIB_PATH"/commons-pool-1.5.5.jar":
CLASS_PATH=$CLASS_PATH$LIB_PATH"/log4j-1.2.16.jar":
CLASS_PATH=$CLASS_PATH$LIB_PATH"/mysql-connector-java-5.0.5-bin.jar":
CLASS_PATH=$CLASS_PATH$LIB_PATH"/sqljdbc4.jar":

RUN_PRG="/usr/bin/java"

APP_FILE="com.local.sync.employees.Main"
$RUN_PRG $CLASS_PATH $APP_FILE $1
