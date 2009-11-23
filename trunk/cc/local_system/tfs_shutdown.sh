killall tfs_master
killall tfs_chunkserver
#for f in `ps a|grep tfs_chunkserver|cut -b 1-5 `
#do
#    kill $f
#done

#for f in `ps a|grep tfs_master|cut -b 1-5`
#do
#    kill $f
#done
