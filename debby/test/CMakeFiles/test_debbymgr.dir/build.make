# CMAKE generated file: DO NOT EDIT!
# Generated by "Unix Makefiles" Generator, CMake Version 2.6

#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canoncical targets will work.
.SUFFIXES:

# Remove some rules from gmake that .SUFFIXES does not remove.
SUFFIXES =

.SUFFIXES: .hpux_make_needs_suffix_list

# Suppress display of executed commands.
$(VERBOSE).SILENT:

# A target that is always out of date.
cmake_force:
.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

# The shell in which to execute make rules.
SHELL = /bin/sh

# The CMake executable.
CMAKE_COMMAND = /usr/bin/cmake

# The command to remove a file.
RM = /usr/bin/cmake -E remove -f

# The program to use to edit the cache.
CMAKE_EDIT_COMMAND = /usr/bin/ccmake

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = /home/fankai/tplatform/debby

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = /home/fankai/tplatform/debby

# Include any dependencies generated for this target.
include test/CMakeFiles/test_debbymgr.dir/depend.make

# Include the progress variables for this target.
include test/CMakeFiles/test_debbymgr.dir/progress.make

# Include the compile flags for this target's objects.
include test/CMakeFiles/test_debbymgr.dir/flags.make

test/CMakeFiles/test_debbymgr.dir/TestDebbyManager.o: test/CMakeFiles/test_debbymgr.dir/flags.make
test/CMakeFiles/test_debbymgr.dir/TestDebbyManager.o: test/TestDebbyManager.cpp
	$(CMAKE_COMMAND) -E cmake_progress_report /home/fankai/tplatform/debby/CMakeFiles $(CMAKE_PROGRESS_1)
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Building CXX object test/CMakeFiles/test_debbymgr.dir/TestDebbyManager.o"
	cd /home/fankai/tplatform/debby/test && /usr/bin/c++   $(CXX_DEFINES) $(CXX_FLAGS) -o CMakeFiles/test_debbymgr.dir/TestDebbyManager.o -c /home/fankai/tplatform/debby/test/TestDebbyManager.cpp

test/CMakeFiles/test_debbymgr.dir/TestDebbyManager.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/test_debbymgr.dir/TestDebbyManager.i"
	cd /home/fankai/tplatform/debby/test && /usr/bin/c++  $(CXX_DEFINES) $(CXX_FLAGS) -E /home/fankai/tplatform/debby/test/TestDebbyManager.cpp > CMakeFiles/test_debbymgr.dir/TestDebbyManager.i

test/CMakeFiles/test_debbymgr.dir/TestDebbyManager.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/test_debbymgr.dir/TestDebbyManager.s"
	cd /home/fankai/tplatform/debby/test && /usr/bin/c++  $(CXX_DEFINES) $(CXX_FLAGS) -S /home/fankai/tplatform/debby/test/TestDebbyManager.cpp -o CMakeFiles/test_debbymgr.dir/TestDebbyManager.s

test/CMakeFiles/test_debbymgr.dir/TestDebbyManager.o.requires:
.PHONY : test/CMakeFiles/test_debbymgr.dir/TestDebbyManager.o.requires

test/CMakeFiles/test_debbymgr.dir/TestDebbyManager.o.provides: test/CMakeFiles/test_debbymgr.dir/TestDebbyManager.o.requires
	$(MAKE) -f test/CMakeFiles/test_debbymgr.dir/build.make test/CMakeFiles/test_debbymgr.dir/TestDebbyManager.o.provides.build
.PHONY : test/CMakeFiles/test_debbymgr.dir/TestDebbyManager.o.provides

test/CMakeFiles/test_debbymgr.dir/TestDebbyManager.o.provides.build: test/CMakeFiles/test_debbymgr.dir/TestDebbyManager.o
.PHONY : test/CMakeFiles/test_debbymgr.dir/TestDebbyManager.o.provides.build

test/CMakeFiles/test_debbymgr.dir/__/src/server/DataManager.o: test/CMakeFiles/test_debbymgr.dir/flags.make
test/CMakeFiles/test_debbymgr.dir/__/src/server/DataManager.o: src/server/DataManager.cpp
	$(CMAKE_COMMAND) -E cmake_progress_report /home/fankai/tplatform/debby/CMakeFiles $(CMAKE_PROGRESS_2)
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Building CXX object test/CMakeFiles/test_debbymgr.dir/__/src/server/DataManager.o"
	cd /home/fankai/tplatform/debby/test && /usr/bin/c++   $(CXX_DEFINES) $(CXX_FLAGS) -o CMakeFiles/test_debbymgr.dir/__/src/server/DataManager.o -c /home/fankai/tplatform/debby/src/server/DataManager.cpp

test/CMakeFiles/test_debbymgr.dir/__/src/server/DataManager.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/test_debbymgr.dir/__/src/server/DataManager.i"
	cd /home/fankai/tplatform/debby/test && /usr/bin/c++  $(CXX_DEFINES) $(CXX_FLAGS) -E /home/fankai/tplatform/debby/src/server/DataManager.cpp > CMakeFiles/test_debbymgr.dir/__/src/server/DataManager.i

test/CMakeFiles/test_debbymgr.dir/__/src/server/DataManager.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/test_debbymgr.dir/__/src/server/DataManager.s"
	cd /home/fankai/tplatform/debby/test && /usr/bin/c++  $(CXX_DEFINES) $(CXX_FLAGS) -S /home/fankai/tplatform/debby/src/server/DataManager.cpp -o CMakeFiles/test_debbymgr.dir/__/src/server/DataManager.s

test/CMakeFiles/test_debbymgr.dir/__/src/server/DataManager.o.requires:
.PHONY : test/CMakeFiles/test_debbymgr.dir/__/src/server/DataManager.o.requires

test/CMakeFiles/test_debbymgr.dir/__/src/server/DataManager.o.provides: test/CMakeFiles/test_debbymgr.dir/__/src/server/DataManager.o.requires
	$(MAKE) -f test/CMakeFiles/test_debbymgr.dir/build.make test/CMakeFiles/test_debbymgr.dir/__/src/server/DataManager.o.provides.build
.PHONY : test/CMakeFiles/test_debbymgr.dir/__/src/server/DataManager.o.provides

test/CMakeFiles/test_debbymgr.dir/__/src/server/DataManager.o.provides.build: test/CMakeFiles/test_debbymgr.dir/__/src/server/DataManager.o
.PHONY : test/CMakeFiles/test_debbymgr.dir/__/src/server/DataManager.o.provides.build

test/CMakeFiles/test_debbymgr.dir/__/src/server/DebbyManager.o: test/CMakeFiles/test_debbymgr.dir/flags.make
test/CMakeFiles/test_debbymgr.dir/__/src/server/DebbyManager.o: src/server/DebbyManager.cpp
	$(CMAKE_COMMAND) -E cmake_progress_report /home/fankai/tplatform/debby/CMakeFiles $(CMAKE_PROGRESS_3)
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Building CXX object test/CMakeFiles/test_debbymgr.dir/__/src/server/DebbyManager.o"
	cd /home/fankai/tplatform/debby/test && /usr/bin/c++   $(CXX_DEFINES) $(CXX_FLAGS) -o CMakeFiles/test_debbymgr.dir/__/src/server/DebbyManager.o -c /home/fankai/tplatform/debby/src/server/DebbyManager.cpp

test/CMakeFiles/test_debbymgr.dir/__/src/server/DebbyManager.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/test_debbymgr.dir/__/src/server/DebbyManager.i"
	cd /home/fankai/tplatform/debby/test && /usr/bin/c++  $(CXX_DEFINES) $(CXX_FLAGS) -E /home/fankai/tplatform/debby/src/server/DebbyManager.cpp > CMakeFiles/test_debbymgr.dir/__/src/server/DebbyManager.i

test/CMakeFiles/test_debbymgr.dir/__/src/server/DebbyManager.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/test_debbymgr.dir/__/src/server/DebbyManager.s"
	cd /home/fankai/tplatform/debby/test && /usr/bin/c++  $(CXX_DEFINES) $(CXX_FLAGS) -S /home/fankai/tplatform/debby/src/server/DebbyManager.cpp -o CMakeFiles/test_debbymgr.dir/__/src/server/DebbyManager.s

test/CMakeFiles/test_debbymgr.dir/__/src/server/DebbyManager.o.requires:
.PHONY : test/CMakeFiles/test_debbymgr.dir/__/src/server/DebbyManager.o.requires

test/CMakeFiles/test_debbymgr.dir/__/src/server/DebbyManager.o.provides: test/CMakeFiles/test_debbymgr.dir/__/src/server/DebbyManager.o.requires
	$(MAKE) -f test/CMakeFiles/test_debbymgr.dir/build.make test/CMakeFiles/test_debbymgr.dir/__/src/server/DebbyManager.o.provides.build
.PHONY : test/CMakeFiles/test_debbymgr.dir/__/src/server/DebbyManager.o.provides

test/CMakeFiles/test_debbymgr.dir/__/src/server/DebbyManager.o.provides.build: test/CMakeFiles/test_debbymgr.dir/__/src/server/DebbyManager.o
.PHONY : test/CMakeFiles/test_debbymgr.dir/__/src/server/DebbyManager.o.provides.build

test/CMakeFiles/test_debbymgr.dir/test_debbymgr_runner.o: test/CMakeFiles/test_debbymgr.dir/flags.make
test/CMakeFiles/test_debbymgr.dir/test_debbymgr_runner.o: test/test_debbymgr_runner.cpp
	$(CMAKE_COMMAND) -E cmake_progress_report /home/fankai/tplatform/debby/CMakeFiles $(CMAKE_PROGRESS_4)
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Building CXX object test/CMakeFiles/test_debbymgr.dir/test_debbymgr_runner.o"
	cd /home/fankai/tplatform/debby/test && /usr/bin/c++   $(CXX_DEFINES) $(CXX_FLAGS) -o CMakeFiles/test_debbymgr.dir/test_debbymgr_runner.o -c /home/fankai/tplatform/debby/test/test_debbymgr_runner.cpp

test/CMakeFiles/test_debbymgr.dir/test_debbymgr_runner.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/test_debbymgr.dir/test_debbymgr_runner.i"
	cd /home/fankai/tplatform/debby/test && /usr/bin/c++  $(CXX_DEFINES) $(CXX_FLAGS) -E /home/fankai/tplatform/debby/test/test_debbymgr_runner.cpp > CMakeFiles/test_debbymgr.dir/test_debbymgr_runner.i

test/CMakeFiles/test_debbymgr.dir/test_debbymgr_runner.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/test_debbymgr.dir/test_debbymgr_runner.s"
	cd /home/fankai/tplatform/debby/test && /usr/bin/c++  $(CXX_DEFINES) $(CXX_FLAGS) -S /home/fankai/tplatform/debby/test/test_debbymgr_runner.cpp -o CMakeFiles/test_debbymgr.dir/test_debbymgr_runner.s

test/CMakeFiles/test_debbymgr.dir/test_debbymgr_runner.o.requires:
.PHONY : test/CMakeFiles/test_debbymgr.dir/test_debbymgr_runner.o.requires

test/CMakeFiles/test_debbymgr.dir/test_debbymgr_runner.o.provides: test/CMakeFiles/test_debbymgr.dir/test_debbymgr_runner.o.requires
	$(MAKE) -f test/CMakeFiles/test_debbymgr.dir/build.make test/CMakeFiles/test_debbymgr.dir/test_debbymgr_runner.o.provides.build
.PHONY : test/CMakeFiles/test_debbymgr.dir/test_debbymgr_runner.o.provides

test/CMakeFiles/test_debbymgr.dir/test_debbymgr_runner.o.provides.build: test/CMakeFiles/test_debbymgr.dir/test_debbymgr_runner.o
.PHONY : test/CMakeFiles/test_debbymgr.dir/test_debbymgr_runner.o.provides.build

test/TestDebbyManager.cpp: test/server/TestDebbyManager.hpp
test/TestDebbyManager.cpp: test/CMakeFiles/test_debbymgr.dir/build.make
	$(CMAKE_COMMAND) -E cmake_progress_report /home/fankai/tplatform/debby/CMakeFiles $(CMAKE_PROGRESS_5)
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --blue --bold "Generating TestDebbyManager.cpp"
	cd /home/fankai/tplatform/debby/test && /usr/bin/python2.5 /home/fankai/tplatform/debby/..//thirdparty/cxxtest/cxxtestgen.py --part -o TestDebbyManager.cpp /home/fankai/tplatform/debby/test/server/TestDebbyManager.hpp

test/test_debbymgr_runner.cpp: test/CMakeFiles/test_debbymgr.dir/build.make
	$(CMAKE_COMMAND) -E cmake_progress_report /home/fankai/tplatform/debby/CMakeFiles $(CMAKE_PROGRESS_6)
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --blue --bold "Generating test_debbymgr_runner.cpp"
	cd /home/fankai/tplatform/debby/test && /usr/bin/python2.5 /home/fankai/tplatform/debby/..//thirdparty/cxxtest/cxxtestgen.py --runner=ErrorPrinter --root -o test_debbymgr_runner.cpp

# Object files for target test_debbymgr
test_debbymgr_OBJECTS = \
"CMakeFiles/test_debbymgr.dir/TestDebbyManager.o" \
"CMakeFiles/test_debbymgr.dir/__/src/server/DataManager.o" \
"CMakeFiles/test_debbymgr.dir/__/src/server/DebbyManager.o" \
"CMakeFiles/test_debbymgr.dir/test_debbymgr_runner.o"

# External object files for target test_debbymgr
test_debbymgr_EXTERNAL_OBJECTS =

test/test_debbymgr: test/CMakeFiles/test_debbymgr.dir/TestDebbyManager.o
test/test_debbymgr: test/CMakeFiles/test_debbymgr.dir/__/src/server/DataManager.o
test/test_debbymgr: test/CMakeFiles/test_debbymgr.dir/__/src/server/DebbyManager.o
test/test_debbymgr: test/CMakeFiles/test_debbymgr.dir/test_debbymgr_runner.o
test/test_debbymgr: libdebby_ice.a
test/test_debbymgr: test/CMakeFiles/test_debbymgr.dir/build.make
test/test_debbymgr: test/CMakeFiles/test_debbymgr.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --red --bold "Linking CXX executable test_debbymgr"
	cd /home/fankai/tplatform/debby/test && $(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/test_debbymgr.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
test/CMakeFiles/test_debbymgr.dir/build: test/test_debbymgr
.PHONY : test/CMakeFiles/test_debbymgr.dir/build

test/CMakeFiles/test_debbymgr.dir/requires: test/CMakeFiles/test_debbymgr.dir/TestDebbyManager.o.requires
test/CMakeFiles/test_debbymgr.dir/requires: test/CMakeFiles/test_debbymgr.dir/__/src/server/DataManager.o.requires
test/CMakeFiles/test_debbymgr.dir/requires: test/CMakeFiles/test_debbymgr.dir/__/src/server/DebbyManager.o.requires
test/CMakeFiles/test_debbymgr.dir/requires: test/CMakeFiles/test_debbymgr.dir/test_debbymgr_runner.o.requires
.PHONY : test/CMakeFiles/test_debbymgr.dir/requires

test/CMakeFiles/test_debbymgr.dir/clean:
	cd /home/fankai/tplatform/debby/test && $(CMAKE_COMMAND) -P CMakeFiles/test_debbymgr.dir/cmake_clean.cmake
.PHONY : test/CMakeFiles/test_debbymgr.dir/clean

test/CMakeFiles/test_debbymgr.dir/depend: test/TestDebbyManager.cpp
test/CMakeFiles/test_debbymgr.dir/depend: test/test_debbymgr_runner.cpp
	cd /home/fankai/tplatform/debby && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /home/fankai/tplatform/debby /home/fankai/tplatform/debby/test /home/fankai/tplatform/debby /home/fankai/tplatform/debby/test /home/fankai/tplatform/debby/test/CMakeFiles/test_debbymgr.dir/DependInfo.cmake --color=$(COLOR)
.PHONY : test/CMakeFiles/test_debbymgr.dir/depend

