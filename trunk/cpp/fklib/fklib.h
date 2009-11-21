/**
 * @file    fklib.h
 *  Some useful utilities.
 * 
 * @author  Fan Kai(fk), Peking University
 *
 */

#ifndef  FK_FILE_HEADER_INC
#define  FK_FILE_HEADER_INC

#include <sys/time.h>
#include <cassert>
#include <iostream>
#include <string>

namespace fk {

// This class supply similar function with boost::timer,  however time is 
//  measured in real time instead of cpu time.
class Timer {
public:
    Timer() { restart(); }

    void restart() {
        int r = gettimeofday(&_t, NULL);
        assert(r == 0);
    }

    double elapsed() {
        struct timeval t;
        int r = gettimeofday(&t, NULL);
        assert(r == 0);
        return double(t.tv_sec-_t.tv_sec) + double(t.tv_usec-_t.tv_usec)/1000000;
    }

private:
    struct timeval _t;
};  // class timer

/// This function remove redundant slash of a path.
std::string normalize_path(const std::string &path) {
    std::string np;
    for (int i = 0; i < path.size(); ++i) {
        if (i > 0 && path[i-1] == '/' && path[i] == '/') continue;
        np.push_back(path[i]);
    }
    if (np.size() > 1 && np[np.size()-1] == '/')
        np.erase(np.size()-1);
    return np;
}

/// This function find the parent directory of a path.
/// @param path A normoalized path.
std::string parent_dir(const std::string &path) {
    int i = path.size() - 2;
    while (i > 0 && path[i] != '/') --i;
    return path.substr(0, i);
}

}   // namespace fk

#endif   /* ----- #ifndef FK_FILE_HEADER_INC  ----- */

