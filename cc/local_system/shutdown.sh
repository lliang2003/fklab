#!/bin/bash
# @(#)shutdown.sh
# AUTHOR: Zhifeng YANG
# CREATED DATE: 2007-12-20
# DESCRIPTION: kill all mapred processes
#
killall -s SIGKILL mapred_worker
killall -s SIGKILL mapred_master