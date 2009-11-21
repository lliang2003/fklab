CMAKE_MINIMUM_REQUIRED(VERSION 2.4)
PROJECT (test_cmake)
MESSAGE (STATUS "This project is just to test cmake.")
MESSAGE (STATUS "PROJECT_NAME: " ${PROJECT_NAME})

SET (SRC_DIR ${CMAKE_SOURCE_DIR}/src)
SET (EXECUTABLE_OUTPUT_PATH ${CMAKE_SOURCE_DIR}/build)
SET (LIBRARY_OUTPUT_PATH ${CMAKE_SOURCE_DIR}/build)
SET (BIN_DIR ${CMAKE_SOURCE_DIR}/bin)
SET (LIB_DIR ${CMAKE_SOURCE_DIR}/lib)

ADD_EXECUTABLE(list src/list.cc)
GET_TARGET_PROPERTY(list_exe list LOCATION)
MESSAGE (STATUS "location: " ${list_exe})
GET_TARGET_PROPERTY(list_type list TYPE)
MESSAGE (STATUS "type: " ${list_type})
SET_TARGET_PROPERTIES(list PROPERTIES OWNER fankai LANG cpp)
GET_TARGET_PROPERTY(list_owner list OWNER)
MESSAGE (STATUS "owner: " ${list_owner})
GET_TARGET_PROPERTY(list_lang list LANG)
MESSAGE (STATUS "lang: " ${list_lang})

ADD_CUSTOM_TARGET (
    #COMMENT "Building custom target a"
    ta ALL ls >/dev/null)
ADD_CUSTOM_TARGET (
    #COMMENT "Building custom target b"
    tb ALL ls >/dev/null)
ADD_DEPENDENCIES (ta tb)

ADD_CUSTOM_TARGET (
    dolist list
    COMMENT "try out list"
)
ADD_DEPENDENCIES (dolist list)

ADD_CUSTOM_COMMAND(TARGET list 
    PRE_BUILD
    COMMENT echo "after build list, do custom command" 
    COMMAND ls >/dev/null)

ADD_CUSTOM_COMMAND(
    OUTPUT ${SRC_DIR}/Transfer.cpp ${SRC_DIR}/Transfer.h
    COMMAND slice2cpp ${SRC_DIR}/Transfer.ice --output-dir ${SRC_DIR}
    DEPENDS ${SRC_DIR}/Transfer.ice)
INCLUDE_DIRECTORIES(${SRC_DIR})
ADD_LIBRARY(Transfer ${SRC_DIR}/Transfer.cpp)

INSTALL (TARGETS list Transfer
    RUNTIME DESTINATION ${BIN_DIR}
    ARCHIVE DESTINATION ${LIB_DIR})
