ADD_DEFINITIONS(-DHAVE_CXX_STDHEADERS)
ADD_EXECUTABLE(test_bdb  test_bdb.cpp) 
TARGET_LINK_LIBRARIES(test_bdb db_cxx)
ADD_EXECUTABLE(store store.cpp) 
TARGET_LINK_LIBRARIES(store db_cxx)
ADD_EXECUTABLE(store_rep store_rep.cpp) 
TARGET_LINK_LIBRARIES(store_rep db_cxx)
ADD_EXECUTABLE(test_fkbdb  test_fkbdb.cpp) 
TARGET_LINK_LIBRARIES(test_fkbdb db_cxx)

ADD_CUSTOM_COMMAND(TARGET test_bdb
    POST_BUILD
    COMMAND mkdir -p ../data/bdb)
