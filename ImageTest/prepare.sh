#!/bin/bash

DEST_DIR=~/swt_test
PKG=de/loskutov/swt/tests
CLASS=de.loskutov.swt.tests.ShellWithImages

if [[ ! -a ${DEST_DIR}/${PKG} ]]
then
    mkdir -p ${DEST_DIR}/${PKG}
fi

cd ${DEST_DIR}
if [[ ! -a ${DEST_DIR}/${PKG}/ShellWithImages.class ]]
then
    cd ShellWithImages.class ${DEST_DIR}/${PKG}
	wget http://java-things.googlecode.com/files/ShellWithImages.class
fi

if [ ! "X${1}" = "X64" ]
then
    JAVA_CMD=/opt/java1.6/bin/java    
	if [[ ! -a ./32bit.zip ]]
	then
		echo "downloading 32 bit swt jars (12MB)"
		wget http://java-things.googlecode.com/files/32bit.zip
	fi
	unzip -nq 32bit.zip
    classpath=${DEST_DIR}/:${DEST_DIR}/32bit
		
else
    JAVA_CMD=/opt/java1.6_x86_64/bin/java
	if [[ ! -a ./64bit.zip ]]
	then
	    echo "downloading 64 bit swt jars (13MB)" 
	    wget http://java-things.googlecode.com/files/64bit.zip
	fi
    unzip -nq 64bit.zip
    classpath=${DEST_DIR}/:${DEST_DIR}/64bit
fi

set -x
${JAVA_CMD} -cp ${classpath}/${2}/swt.jar ${CLASS} "${1}bit ${2}"