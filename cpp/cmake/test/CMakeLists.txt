CMAKE_MINIMUM_REQUIRED(VERSION 2.4)
PROJECT (test_cmake)
MESSAGE (STATUS "This project is just to test cmake.")
MESSAGE (STATUS "PROJECT_NAME: " ${PROJECT_NAME})

# if you are building in-source, this is the same as CMAKE_SOURCE_DIR, otherwise 
# this is the top level directory of your build tree 
MESSAGE (STATUS "CMAKE_BINARY_DIR:         " ${CMAKE_BINARY_DIR})

# if you are building in-source, this is the same as CMAKE_CURRENT_SOURCE_DIR, otherwise this 
# is the directory where the compiled or generated files from the current CMakeLists.txt will go to 
MESSAGE (STATUS "CMAKE_CURRENT_BINARY_DIR: " ${CMAKE_CURRENT_BINARY_DIR})

# this is the directory, from which cmake was started, i.e. the top level source directory 
MESSAGE (STATUS "CMAKE_SOURCE_DIR:         " ${CMAKE_SOURCE_DIR})

# this is the directory where the currently processed CMakeLists.txt is located in 
MESSAGE (STATUS "CMAKE_CURRENT_SOURCE_DIR: " ${CMAKE_CURRENT_SOURCE_DIR})

# contains the full path to the top level directory of your build tree 
MESSAGE (STATUS "PROJECT_BINARY_DIR: " ${PROJECT_BINARY_DIR})

# contains the full path to the root of your project source directory,
# i.e. to the nearest directory where CMakeLists.txt contains the PROJECT() command 
MESSAGE (STATUS "PROJECT_SOURCE_DIR: " ${PROJECT_SOURCE_DIR})

# set this variable to specify a common place where CMake should put all executable files
# (instead of CMAKE_CURRENT_BINARY_DIR)
MESSAGE (STATUS "EXECUTABLE_OUTPUT_PATH: " ${EXECUTABLE_OUTPUT_PATH})

# set this variable to specify a common place where CMake should put all libraries 
# (instead of CMAKE_CURRENT_BINARY_DIR)
MESSAGE (STATUS "LIBRARY_OUTPUT_PATH:     " ${LIBRARY_OUTPUT_PATH})

# tell CMake to search first in directories listed in CMAKE_MODULE_PATH
# when you use FIND_PACKAGE() or INCLUDE()
MESSAGE (STATUS "CMAKE_MODULE_PATH: " ${CMAKE_MODULE_PATH})

# this is the complete path of the cmake which runs currently (e.g. /usr/local/bin/cmake) 
MESSAGE (STATUS "CMAKE_COMMAND: " ${CMAKE_COMMAND})

# this is the CMake installation directory 
MESSAGE (STATUS "CMAKE_ROOT: " ${CMAKE_ROOT})

# this is the filename including the complete path of the file where this variable is used. 
MESSAGE (STATUS "CMAKE_CURRENT_LIST_FILE: " ${CMAKE_CURRENT_LIST_FILE})

# this is linenumber where the variable is used
MESSAGE (STATUS "CMAKE_CURRENT_LIST_LINE: " ${CMAKE_CURRENT_LIST_LINE})

# this is used when searching for include files e.g. using the FIND_PATH() command.
MESSAGE (STATUS "CMAKE_INCLUDE_PATH: " ${CMAKE_INCLUDE_PATH})

# this is used when searching for libraries e.g. using the FIND_LIBRARY() command.
MESSAGE (STATUS "CMAKE_LIBRARY_PATH: " ${CMAKE_LIBRARY_PATH})

# the complete system name, e.g. "Linux-2.4.22", "FreeBSD-5.4-RELEASE" or "Windows 5.1" 
MESSAGE (STATUS "CMAKE_SYSTEM: " ${CMAKE_SYSTEM})

# the short system name, e.g. "Linux", "FreeBSD" or "Windows"
MESSAGE (STATUS "CMAKE_SYSTEM_NAME: " ${CMAKE_SYSTEM_NAME})

# only the version part of CMAKE_SYSTEM 
MESSAGE (STATUS "CMAKE_SYSTEM_VERSION: " ${CMAKE_SYSTEM_VERSION})

# the processor name (e.g. "Intel(R) Pentium(R) M processor 2.00GHz") 
MESSAGE (STATUS "CMAKE_SYSTEM_PROCESSOR: " ${CMAKE_SYSTEM_PROCESSOR})

# is TRUE on all UNIX-like OS's, including Apple OS X and CygWin
MESSAGE (STATUS "UNIX: " ${UNIX})

# is TRUE on Windows, including CygWin 
MESSAGE (STATUS "WIN32: " ${WIN32})

# is TRUE on Apple OS X
MESSAGE (STATUS "APPLE: " ${APPLE})

# Microsoft compiler 
MESSAGE (STATUS "MSVC: " ${MSVC})

# set this to true if you don't want to rebuild the object files if the rules have changed, 
# but not the actual source files or headers (e.g. if you changed the some compiler switches) 
MESSAGE (STATUS "CMAKE_SKIP_RULE_DEPENDENCY: " ${CMAKE_SKIP_RULE_DEPENDENCY})

# since CMake 2.1 the install rule depends on all, i.e. everything will be built before installing. 
# If you don't like this, set this one to true.
MESSAGE (STATUS "CMAKE_SKIP_INSTALL_ALL_DEPENDENCY: " ${CMAKE_SKIP_INSTALL_ALL_DEPENDENCY})

# If set, runtime paths are not added when using shared libraries. Default it is set to OFF
MESSAGE (STATUS "CMAKE_SKIP_RPATH: " ${CMAKE_SKIP_RPATH})

# set this to true if you are using makefiles and want to see the full compile and link 
# commands instead of only the shortened ones 
MESSAGE (STATUS "CMAKE_VERBOSE_MAKEFILE: " ${CMAKE_VERBOSE_MAKEFILE})

# this will cause CMake to not put in the rules that re-run CMake. This might be useful if 
# you want to use the generated build files on another machine. 
MESSAGE (STATUS "CMAKE_SUPPRESS_REGENERATION: " ${CMAKE_SUPPRESS_REGENERATION})


# A simple way to get switches to the compiler is to use ADD_DEFINITIONS(). 
# But there are also two variables exactly for this purpose: 

# the compiler flags for compiling C sources 
MESSAGE (STATUS "CMAKE_C_FLAGS: " ${CMAKE_C_FLAGS})

# the compiler flags for compiling C++ sources 
MESSAGE (STATUS "CMAKE_CXX_FLAGS: " ${CMAKE_CXX_FLAGS})


# Choose the type of build.  Example: SET(CMAKE_BUILD_TYPE Debug) 
MESSAGE (STATUS "CMAKE_BUILD_TYPE: " ${CMAKE_BUILD_TYPE})

# if this is set to ON, then all libraries are built as shared libraries by default.
MESSAGE (STATUS "BUILD_SHARED_LIBS: " ${BUILD_SHARED_LIBS})

# the compiler used for C files 
MESSAGE (STATUS "CMAKE_C_COMPILER: " ${CMAKE_C_COMPILER})

# the compiler used for C++ files 
MESSAGE (STATUS "CMAKE_CXX_COMPILER: " ${CMAKE_CXX_COMPILER})

# if the compiler is a variant of gcc, this should be set to 1 
MESSAGE (STATUS "CMAKE_COMPILER_IS_GNUCC: " ${CMAKE_COMPILER_IS_GNUCC})

# if the compiler is a variant of g++, this should be set to 1 
MESSAGE (STATUS "CMAKE_COMPILER_IS_GNUCXX : " ${CMAKE_COMPILER_IS_GNUCXX})

# the tools for creating libraries 
MESSAGE (STATUS "CMAKE_AR: " ${CMAKE_AR})
MESSAGE (STATUS "CMAKE_RANLIB: " ${CMAKE_RANLIB})

MESSAGE (STATUS "ENV USER: " $ENV{USER})
MESSAGE (STATUS "ENV HOME: " $ENV{HOME})
MESSAGE (STATUS "ENV SHELL: " $ENV{SHELL})
MESSAGE (STATUS "ENV PWD: " $ENV{PWD})

#SET (CMAKE_VERBOSE_MAKEFILE ON)
MESSAGE (STATUS "CMAKE_VERBOSE_MAKEFILE: " ${CMAKE_VERBOSE_MAKEFILE})

MESSAGE (STATUS)
MESSAGE (STATUS "TEST IF CLAUSE")
IF (WIN32)
    MESSAGE (STATUS "System is win32")
ELSEIF (UNIX)
    MESSAGE (STATUS "System is unix")
ELSE (WIN32)
    MESSAGE (STATUS "System is others")
ENDIF (WIN32)

MESSAGE (STATUS)
MESSAGE (STATUS "TEST FIND")
FIND_PATH(fpath ls)
MESSAGE (STATUS "path of ls: " ${fpath})
FIND_LIBRARY (flib Ice)
MESSAGE (STATUS "path of Ice lib is: " ${flib})
FIND_PACKAGE (PythonInterp)
MESSAGE (STATUS "PythonInterp found? " ${PYTHONINTERP_FOUND})

MESSAGE (STATUS)
MESSAGE (STATUS "TEST LIST")
SET (lista a b c)
LIST (LENGTH lista lena)
MESSAGE (STATUS len: ${lena})
LIST (GET lista 2 geta)
MESSAGE (STATUS get[2]: ${geta})
LIST (INSERT lista 2 e)
LIST (REVERSE lista)
MESSAGE (STATUS list: ${lista})

MESSAGE (STATUS)
MACRO(tmac)
    MESSAGE (STATUS "TEST MACRO")
ENDMACRO(tmac)
tmac()

