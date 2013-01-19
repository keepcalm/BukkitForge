#!/bin/sh

JAVA=""

JAVA=`which java`

if test  -z $JAVA ; then
	read -p "No java found, please type your own: " JAVA
fi

if test -z $1; then
read -p "Enter the name of the plugin to translate: " PLUGIN
else
	PLUGIN=$1
fi


read -p "Enter the MC version: " MC

$JAVA -jar srgtool-2.0.jar apply --srg vcb2obf_$MC.srg --in $PLUGIN --out PORTED_$PLUGIN
