#!/usr/bin/env bash
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Apr 23 06:25:39 PM CST 2009

process() {
    web_pages_dir=$1
    echo $web_pages_dir
    for person in `ls $web_pages_dir`
    do
        pdir=$web_pages_dir/$person
        echo $pdir
        rm $pdir/all.text -f
        for index in `ls $pdir/raw`
        do
            file=$pdir/raw/$index/index.html
            text_file=$pdir/$index.text
            stemmed_file=$pdir/$index.stemmed
            link_file=$pdir/$index.link
            #echo $file
            #extract links
            java -cp .:jericho-html-3.0.jar HtmlProcessor -links $file 2>/dev/null > $link_file
            #extract text
            java -cp .:jericho-html-3.0.jar HtmlProcessor -text $file 2>/dev/null > $text_file
            cat $text_file >>$pdir/all.text
        done
        #named entity recognition
        java -mx1100m -cp stanford-ner/stanford-ner.jar edu.stanford.nlp.ie.crf.CRFClassifier -loadClassifier stanford-ner/classifiers/ner-eng-ie.crf-3-all2008-distsim.ser.gz -textFile $pdir/all.text >$pdir/all.ner 2>>prep.log

    done
}

process training/web_pages
process test/web_pages

