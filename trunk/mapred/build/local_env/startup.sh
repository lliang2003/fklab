#!/bin/bash
# @(#)startup.sh
# AUTHOR: Zhifeng YANG
# CREATED DATE: 2007-12-20
# DESCRIPTION: 
#
./shutdown.sh

find -name "*.log" |xargs rm

echo starting master
../mapred_master mapred_master.ini &
sleep 1
for (( i=1 ; $i <= 3 ; i++ ))
do
echo starting worker$i
cd worker$i
../../mapred_worker ../mapred_worker${i}.ini &
cd ..
done 

