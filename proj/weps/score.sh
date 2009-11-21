#java -cp scorer_1.1/wepsEvaluation.jar es.nlp.uned.weps.evaluation.SystemScorer test/truth_files/official_annotation/ result/ . -ALLMEASURES -AllInOne -OneInOne -average
java -Xmx1024M -cp scorer_1.1/wepsEvaluation.jar es.nlp.uned.weps.evaluation.SystemScorer test/truth_files/official_annotation/ tmp/result/ . -P -IP -FMeasure_0.5_P-IP -AllInOne -OneInOne -Combined

for f in `ls *.eval`
do
    line=`tail $f -n 1`
    echo `expr substr "$line" 9 100` $f
done
