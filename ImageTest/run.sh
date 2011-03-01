#!/bin/bash
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
