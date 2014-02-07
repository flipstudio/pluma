#!/bin/sh

rm -rf obj/ libs/
ndk-build clean
ndk-build
mkdir -p obj/local/mac
mkdir -p libs/mac
gcc "-I/System/Library/Frameworks/JavaVM.framework/Headers" -c jni/database.c -o obj/local/mac/database.o
gcc "-I/System/Library/Frameworks/JavaVM.framework/Headers" -c jni/statement.c -o obj/local/mac/statement.o
gcc "-I/System/Library/Frameworks/JavaVM.framework/Headers" -c jni/sqlite3.c -o obj/local/mac/sqlite3.o
gcc -dynamiclib -o libs/mac/libpluma.jnilib obj/local/mac/database.o obj/local/mac/statement.o obj/local/mac/sqlite3.o