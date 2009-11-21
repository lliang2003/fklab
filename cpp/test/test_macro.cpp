/**
 * @file	test_macro.cpp
 * 	
 * 
 * @author	Fan Kai(fk), Peking University	
 * @date	2008年07月01日 20时55分18秒 CST
 *
 */

#define _WRAP(X, Y) {X;Y;}

#define _THROW(EX) {\
    LOG_ERROR("throw " <<#EX); \
    throw EX; \
}

#define _CATCH(EX, FUNC) catch (EX &ex) {\
    LOG_ERROR("catch " <<#EX); \
    FUNC; \
}

#define _CATCH_THROW(EX, FUNC) catch (EX &ex) {\
    LOG_ERROR("catch " <<#EX); \
    FUNC; \
    LOG_ERROR("rethrow"); \
    throw ex; \
}


void func() {
    try {
        dosth();
    }
    _CATCH(std::exception,  _WRAP(func2(), cout <<"joke" <<endl))
    _CATCH_THROW(std::exception, func3())
    _CATCH(std::exception, _WRAP(func(), _THROW(fk::exception())))
    _CATCH(ex, break)
}

__DATE__

__FILE__

__LINE__

__STDC__

__STDC_VERSION__

__TIME__

#define LOG(format, ...) printf(format, __VA_ARGS__)

LOG("%s %s", str, count);

_WRAP(a(), b())

#define CONCAT(X, Y) X##Y
    CONCAT(x,1)

#define M1(x) mmmm(x)
#define M2(x) xxxx;M1(x);yyy
    M2(j)

#undef M1
#define M1(x) nnn(x)
    M2(k)

#if 3 > 2
    normal
#else 
    abnormal
#endif


void fk() {
    printf("hello world");
    printf(__func__);
}

#line 888
__LINE__
__LINE__
__LINE__
#line 88
__LINE__

#error WRONG

