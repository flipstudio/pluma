#!/bin/bash

mkdir -p libs
cd jni
gcc -c -I/System/Library/Frameworks/JavaVM.framework/Headers database.c statement.c sqlite3.c
gcc -dynamiclib -o ../libs/libpluma.so database.o statement.o sqlite3.o -framework JavaVM
rm -rf *.o