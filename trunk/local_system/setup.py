#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Dec 27 19:37:02 CST 2008

import os

def init_cs():
    for i in range(1, 6):
        f = open('c%d/tfs_chunkserver.ini' % i, 'w')
        f.write('[ChunkServer]\n')
        f.write('work_dir=%s\n' % cwd)
        f.write('chunk_store_dir=%s/chunk_store\n' % cwd)
        f.write('chunk_tmp_dir=%s/chunk_tmp\n' % cwd)
        f.write('log_path=%s/tfs_chunkserver.log\n' % cwd)
        f.write('master_proxy_info=ChunkServerService:tcp -h 127.0.0.1 -p 11000\n')
        f.write('data_sock_port=900%d\n' % i)
        f.write('locale_ip=127.0.0.1\n')

cwd = os.getcwd()
init_cs()

