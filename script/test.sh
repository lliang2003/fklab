#!/usr/bin/env bash
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Jan 03 04:50:15 PM CST 2009

f1() {
    echo hello || echo joke
    echo hello && echo joke
    echo hello & echo joke
    echo hello ; echo joke
    echo hello ;
    #sleep 1; echo hello1; sleep 1
    #sleep 1 & echo hello2; sleep 1 &
}

tvar() {
    echo $?
    echo $#
    echo $*
    echo $@
    shift 
    echo $*
    shift 2
    echo $*
    echo "user=$USERNAME"
    echo "home=$HOME"
}
#tvar a b c d e f g h i j k l m n

techo() {
    echo -e 'fuck\nthe world'
    echo -n 'fuck\nthe world'
    echo 'fuck\nthe world'
}
#techo

texpr() {
    expr 3 + 4
    expr 3 \* 4
    expr 3 - 4
    expr 3 / 4
    #expr 3 / 4.0
    expr 4 \< 10
    expr 4 \>= 10
    expr 30 % 4
    echo `expr 30 % 4`
    expr 'joke' \: 'j.*o'
    expr match 'joke' '\(j.*o\)'
    expr substr 'fuck the world' 3 8
    expr index 'fuck the world' r
    expr length 'fuck the world' 
    echo $?
    expr 0
    echo $?
}
#texpr

tread() {
    echo 'input a number'
    read num
    echo num=$num
}
#tread

tnum() {
    test $1 -eq $2; echo -n $?, 
    [ $1 -eq $2 ]; echo -n $?, 
    [ $1 -ne $2 ]; echo -n $?,
    [ $1 -lt $2 ]; echo -n $?,
    [ $1 -le $2 ]; echo -n $?,
    [ $1 -gt $2 ]; echo -n $?,
    [ $1 -ge $2 ]; echo -n $?,
    echo
}
tnum 1 2
tnum 2 2
tnum -4 -8

tstr() {
    test $1 = $2; echo -n $?,
    test $1 != $2; echo -n $?,
    test $3 ; echo -n $?,
    test -n $3; echo -n $?,
    test -z $3; echo -n $?
    echo
}
tstr abc def
tstr abc abc ''
tstr abc abc ''

tfile() {
    test -s $1; echo -n $?, 
    test -f $1; echo -n $?, 
    test -d $1; echo -n $?, 
    test -w $1; echo -n $?, 
    test -r $1; echo -n $?, 
    test -x $1; echo -n $?, 
    test ! -x $1; echo -n $?, 
    test -x $1 -a ! -x $1; echo -n $?, 
    test -x $1 -o ! -x $1; echo -n $?, 
    echo
}
#tfile .vimrc;tfile /home

tcond() {
    if test -f $1; then
        if test -x $1; then
            echo $1 is a executable file
        else
            echo $1 is not a executable file
        fi
    elif test -d $1; then
        echo $1 is a dir
    else
        echo unknown $1
    fi

}
#tcond 3;tcond /;tcond /bin/bash;tcond ~/.vimrc

tloop() {
    for i in 1 2 3;do
        for j in 1 2 3;do
            echo $i \* $j = `expr $i \* $j`
        done
    done

    for (( i=0; i<3; i++ )); do
        echo count $i
    done

    while [ $i -ge 0 ]; do
        echo count $i
        i=`expr $i - 1`
    done

    until [ $i -ge 3 ]; do
        echo count $i
        i=`expr $i + 1`
    done

}
#tloop

tcase() {
    case $1 in
        "car" ) echo car;;
        "auto" ) echo auto;;
        *)  echo not car;;
    esac
}
#tcase car; tcase auto; tcase jet 

tgetops() {
    while getopts a:b:c:d: opt
    do
        echo $opt, $OPTARG
    done
    echo $*
}
tgetops -a -b bb -c cc -d dd

