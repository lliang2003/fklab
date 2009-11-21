#!/bin/bash

echo clearing test/ 
cd test
cclean
cd ..

echo clearing build/ 
cd build
make clean
cclean
cd ..

echo removing bin/ and lib/
rm -rf bin lib
