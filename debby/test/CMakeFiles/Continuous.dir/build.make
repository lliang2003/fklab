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

# Utility rule file for Continuous.

test/CMakeFiles/Continuous: test/CMakeFiles/Continuous.dir/build.make
	cd /home/fankai/tplatform/debby/test && /usr/bin/ctest -D Continuous

Continuous: test/CMakeFiles/Continuous
Continuous: test/CMakeFiles/Continuous.dir/build.make
.PHONY : Continuous

# Rule to build all files generated by this target.
test/CMakeFiles/Continuous.dir/build: Continuous
.PHONY : test/CMakeFiles/Continuous.dir/build

test/CMakeFiles/Continuous.dir/clean:
	cd /home/fankai/tplatform/debby/test && $(CMAKE_COMMAND) -P CMakeFiles/Continuous.dir/cmake_clean.cmake
.PHONY : test/CMakeFiles/Continuous.dir/clean

test/CMakeFiles/Continuous.dir/depend:
	cd /home/fankai/tplatform/debby && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /home/fankai/tplatform/debby /home/fankai/tplatform/debby/test /home/fankai/tplatform/debby /home/fankai/tplatform/debby/test /home/fankai/tplatform/debby/test/CMakeFiles/Continuous.dir/DependInfo.cmake --color=$(COLOR)
.PHONY : test/CMakeFiles/Continuous.dir/depend

