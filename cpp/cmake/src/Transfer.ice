module fktest {
    sequence<byte> ByteSeq;
    interface Transfer {
        int send(ByteSeq bytes, int id);
        int sendsn(int sn, int id);
    };
};

