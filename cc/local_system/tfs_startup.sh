./shutdown.sh >/dev/null 2>&1

./tfs_master tfs_master.ini &
sleep 3
cd c1
nice -5 ../tfs_chunkserver tfs_chunkserver.ini &
cd ..
cd c2
nice -5 ../tfs_chunkserver tfs_chunkserver.ini &
cd ..
cd c3
nice -5 ../tfs_chunkserver tfs_chunkserver.ini &
cd ..
cd c4
nice -5 ../tfs_chunkserver tfs_chunkserver.ini &
cd ..
cd c5
nice -5 ../tfs_chunkserver tfs_chunkserver.ini &
cd ..

