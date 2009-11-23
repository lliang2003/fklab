/**
 * @file    TestFile.hpp
 *  Test functions in File class
 * 
 * @author  Fan Kai(fk), Peking University
 * @date    10/18/2007 07:31:30 PM CST
 *
 */

#ifndef  TESTFILE_FILE_HEADER_INC
#define  TESTFILE_FILE_HEADER_INC

#include <File.hpp>
#include <FileSystem.hpp>
#include <tfs.h>
#include <cxxtest/TestSuite.h>
#include <iostream>
#include <algorithm>

namespace {

using namespace tfs::api;
using namespace std;

const int NUM = 1024 * 1024;
const int CHUNK_SIZE = 2 * 1024 * 1024;

class TestFile : public CxxTest::TestSuite {
private:
        char data[NUM], tmp[NUM];
public:
    // This test don't test for connectness.
    void testSingleChunkAppend() {
        try {
            tfs::FileSystem fs;
            if (fs.existFile("/test/file"))
                fs.removeFile("/test/file");
            fs.createFile("/test/file", 3, CHUNK_SIZE);
            TS_ASSERT(fs.existFile("/test/file"));

            for (int i = 0; i < NUM; ++i)
                data[i] = '0'+i%10;
            //std::random_shuffle(data, data + NUM);

            tfs::File f;
            f.open("/test/file", tfs::FileOpenAppend);
            f.append(data, -1);
            f.append(data, 0);
            f.append(data, 1);
            f.append(data + 1, 999);
            f.append(data + 1000, NUM/2 - 1000);
            f.append(data + NUM/2, NUM/2);
            f.append(data, 0);
            f.close();
            system("sleep 1");

            tfs::FileInfo info = fs.getFileInfo("/test/file");
            TS_ASSERT_EQUALS(info.size, NUM);
        } catch (TFSException &ex) {
            TS_FAIL(ex.what());
        }
    }

    // This test assert the last test succeeded.
    void testSingleChunkRead() {
        try {
            tfs::File f;
            f.open("/test/file", tfs::FileOpenRead);
            int n = f.read(tmp, NUM);
            TS_ASSERT_EQUALS(n, NUM);
            TS_ASSERT(memcmp(data,tmp, n) == 0);
            n = f.read(tmp, NUM);
            TS_ASSERT_EQUALS(n, 0);
            f.close();
        } catch (TFSException &ex) {
            TS_FAIL(ex.what());
        }
    }

    void testAppendRead() {
        try {
            tfs::FileSystem fs;
            tfs::File f, g;
            int n;
            tfs::FileInfo info = fs.getFileInfo("/test/file");
            TS_ASSERT_EQUALS(info.size, NUM);
            f.open("/test/file", tfs::FileOpenAppend);
            for (int i = 0; i < 6; ++i)
                f.append(data, NUM);
            //system("sleep 1");
            info = fs.getFileInfo("/test/file");
            TS_ASSERT_EQUALS(info.size, NUM * 7);
            g.open("/test/file", tfs::FileOpenRead);
            for (int i = 0; i < 7; ++i) {
                bzero(tmp, NUM);
                n = g.read(tmp, NUM);
                TS_ASSERT_EQUALS(n, NUM);
                TS_ASSERT(memcmp(data,tmp, n) == 0);
            }
            n = g.read(data, NUM);
            TS_ASSERT_EQUALS(n, 0);

            f.close();
            g.close();
        } catch (TFSException &ex) {
            TS_FAIL(ex.what());
        }
    }

    void testSeek() {
        try {
            tfs::File f;
            f.open("/test/file", tfs::FileOpenRead);
            int p = f.seek(NUM, SEEK_CUR);
            TS_ASSERT_EQUALS(p, NUM);

            p = f.seek(NUM * 6, SEEK_CUR);
            TS_ASSERT_EQUALS(p, NUM * 7);

            p = f.seek(NUM * 6, SEEK_CUR);
            TS_ASSERT_EQUALS(p, NUM * 7);

            p = f.seek(0-NUM, SEEK_CUR);
            TS_ASSERT_EQUALS(p, NUM * 6);

            p = f.seek(0, SEEK_CUR);
            TS_ASSERT_EQUALS(p, NUM * 6);

            p = f.seek(0, SEEK_SET);
            TS_ASSERT_EQUALS(p, 0);

            p = f.seek(-777, SEEK_SET);
            TS_ASSERT_EQUALS(p, 0);

            p = f.seek(NUM * 4, SEEK_SET);
            TS_ASSERT_EQUALS(p, NUM * 4);

            p = f.seek(NUM * 8, SEEK_SET);
            TS_ASSERT_EQUALS(p, NUM * 7);

            p = f.seek(0, SEEK_END);
            TS_ASSERT_EQUALS(p, NUM * 7);

            p = f.seek(-NUM * 4, SEEK_END);
            TS_ASSERT_EQUALS(p, NUM * 3);

            p = f.seek(-NUM * 8, SEEK_END);
            TS_ASSERT_EQUALS(p, 0);

            p = f.seek(999, SEEK_END);
            TS_ASSERT_EQUALS(p, NUM * 7);
        } catch (TFSException &ex) {
            TS_FAIL(ex.what());
        }
    }

    void testSeekRead() {
        try {
            tfs::File f;
            f.open("/test/file", tfs::FileOpenRead);

            bzero(tmp, NUM);
            int n = f.read(tmp, NUM);
            TS_ASSERT_EQUALS(n, NUM);
            TS_ASSERT(memcmp(data,tmp, n) == 0);

            f.seek(-NUM, SEEK_END);
            bzero(tmp, NUM);
            n = f.read(tmp, NUM);
            TS_ASSERT_EQUALS(n, NUM);
            TS_ASSERT(memcmp(data,tmp, n) == 0);

            n = f.read(tmp, NUM);
            TS_ASSERT_EQUALS(n, 0);

            f.seek(-NUM, SEEK_CUR);
            bzero(tmp, NUM);
            n = f.read(tmp, NUM);
            TS_ASSERT_EQUALS(n, NUM);
            TS_ASSERT(memcmp(data,tmp, n) == 0);

            f.seek(0, SEEK_SET);
            for (int i = 0; i < 7; ++i) {
                bzero(tmp, NUM);
                n = f.read(tmp, NUM);
                TS_ASSERT_EQUALS(n, NUM);
                TS_ASSERT(memcmp(data,tmp, n) == 0);
            }
            f.seek(0, SEEK_SET);
            for (int i = 0; i < 7; ++i) {
                bzero(tmp, NUM);
                n = f.read(tmp, NUM - 512);
                TS_ASSERT_EQUALS(n, NUM - 512);
                TS_ASSERT(memcmp(data,tmp, n) == 0);
                n = f.read(tmp, 512);
                TS_ASSERT_EQUALS(n, 512);
                TS_ASSERT_SAME_DATA(data + NUM -512, tmp, n);
                TS_ASSERT(memcmp(data+NUM-512,tmp, n) == 0);
            }

            f.seek(-100, SEEK_END);
            n = f.read(tmp, NUM);
            TS_ASSERT_EQUALS(n, 100);
            TS_ASSERT(memcmp(data + NUM - n, tmp, n) == 0);
			TS_ASSERT_SAME_DATA(data + NUM - n, tmp, n);

            f.seek(0, SEEK_SET);
            for (int i = 0; i < 7; ++i) {
                bzero(tmp, NUM);
                n = f.read(tmp, NUM);
                TS_ASSERT_EQUALS(n, NUM);
                TS_ASSERT(memcmp(data,tmp, n) == 0);
            }
			n = 0; int t = 0;
			f.seek(0, SEEK_SET);
			while (f.seek(0, SEEK_CUR) < NUM){
				t = rand() % (NUM/4);
                t = min(t, NUM-n);
				f.read(tmp+n, t);
				n += t;
			}
			TS_ASSERT_SAME_DATA(data, tmp, 200);
			//TS_ASSERT(memcmp(data, tmp, NUM) == 0);
        } catch (TFSException &ex) {
            TS_FAIL(ex.what());
        }
    }

};  // class TestFile

}

#endif   /* ----- #ifndef TESTFILE_FILE_HEADER_INC  ----- */

