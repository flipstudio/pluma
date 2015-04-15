#!/bin/sh

rm -rf obj/ libs/
ndk-build clean
ndk-build
mkdir -p obj/local/mac
mkdir -p libs/mac
g++ -dynamiclib ../sqlite/libs/mac/libsqlite.jnilib -o libs/mac/libpluma.jnilib obj/local/mac/database.o obj/local/mac/statement.o