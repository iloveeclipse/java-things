#!/bin/bash

function printUsageAndExit()
{
    echo "Must provide arguments: <arch. type> <swt version>"
    echo "   possible values: <32|64> <3.3.2|3.4|3.4.1|3.4.2|3.5|3.5.1|3.5.2|3.6.1|3.6.2|3.7M5>"
    echo "Example:"
    echo "   ./run.sh 64 3.5.1"
    exit -1
}


if [ "X${1}" = "X" ]
then
    printUsageAndExit
fi
if [ "X${2}" = "X" ]
then
    printUsageAndExit
fi

DEST_DIR=~/swt_test
PKG=de/loskutov/swt/tests
CLASS=de.loskutov.swt.tests.ShellWithImages
GC_PROJECT=http://java-things.googlecode.com
#set -x

if [[ ! -a ${DEST_DIR}/${PKG} ]]
then
    mkdir -p ${DEST_DIR}/${PKG}
fi

cd ${DEST_DIR}

if [ ! "X${1}" = "X64" ]
then
    JAVA_CMD=/opt/java1.6/bin/java
    if [[ ! -a ./32bit.zip ]]
    then
        echo "downloading 32 bit swt jars (12MB)"
        wget -q ${GC_PROJECT}/files/32bit.zip
    fi
    unzip -nq 32bit.zip
    classpath=${DEST_DIR}/:${DEST_DIR}/32bit/${2}/swt.jar

else
    JAVA_CMD=/opt/java1.6_x86_64/bin/java
    if [[ ! -a ./64bit.zip ]]
    then
        echo "downloading 64 bit swt jars (13MB)"
        wget -q ${GC_PROJECT}/files/64bit.zip
    fi
    unzip -nq 64bit.zip
    classpath=${DEST_DIR}/:${DEST_DIR}/64bit/${2}/swt.jar
fi

if [[ ! -a ${JAVA_CMD} ]]
then
    echo "Java not found in "${JAVA_CMD}
    JAVA_CMD=${JAVA_HOME}/bin/java
    echo "Using whatever defined by \$JAVA_HOME: "${JAVA_CMD}
fi

if [[ ! -a ${DEST_DIR}/${PKG}/ShellWithImages.class ]]
then
    cd ${DEST_DIR}/${PKG}
    echo "downloading test source"
    wget -q ${GC_PROJECT}/hg/ImageTest/src/${PKG}/ShellWithImages.java
    cd ${DEST_DIR}
    echo "compiling test"
    ${JAVA_CMD}c ${PKG}/ShellWithImages.java -cp ${classpath}

fi

set -x
${JAVA_CMD} -cp ${classpath} ${CLASS} "${1}bit ${2}"
