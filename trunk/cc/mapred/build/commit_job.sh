if test $# -ne 1
then
    echo "commit <job_spec>"
    exit 0
fi
src/app/mapred_job commit $1 $1 --detach
