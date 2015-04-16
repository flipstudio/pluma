#!/bin/sh

rm -rf obj/ libs/
ndk-build clean
ndk-build
mkdir -p obj/local/mac
mkdir -p libs/mac
g++ "-I/Applications/Xcode.app/Contents/Developer/Platforms/MacOSX.platform/Developer/SDKs/MacOSX10.10.sdk/System/Library/Frameworks/JavaVM.framework/Headers" -c jni/PlumaRuntime.cpp -o obj/local/mac/PlumaRuntime.o
g++ "-I/Applications/Xcode.app/Contents/Developer/Platforms/MacOSX.platform/Developer/SDKs/MacOSX10.10.sdk/System/Library/Frameworks/JavaVM.framework/Headers" -c jni/database.cpp -o obj/local/mac/database.o
g++ "-I/Applications/Xcode.app/Contents/Developer/Platforms/MacOSX.platform/Developer/SDKs/MacOSX10.10.sdk/System/Library/Frameworks/JavaVM.framework/Headers" -c jni/statement.cpp -o obj/local/mac/statement.o
g++ -dynamiclib ../sqlite/libs/mac/libsqlite.jnilib -o libs/mac/libpluma.jnilib obj/local/mac/PlumaRuntime.o obj/local/mac/database.o obj/local/mac/statement.o