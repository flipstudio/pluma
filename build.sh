#!/bin/sh

rm -rf obj/ libs/
ndk-build clean
ndk-build
mkdir -p obj/local/osx
mkdir -p libs/osx
echo "Building OSX"
g++ -std=c++11 "-I/Applications/Xcode.app/Contents/Developer/Platforms/MacOSX.platform/Developer/SDKs/MacOSX10.10.sdk/System/Library/Frameworks/JavaVM.framework/Headers" -c jni/Utils.cpp -o obj/local/osx/Utils.o
g++ -std=c++11 "-I/Applications/Xcode.app/Contents/Developer/Platforms/MacOSX.platform/Developer/SDKs/MacOSX10.10.sdk/System/Library/Frameworks/JavaVM.framework/Headers" -c jni/SQLiteFunction.cpp -o obj/local/osx/SQLiteFunction.o
g++ -std=c++11 "-I/Applications/Xcode.app/Contents/Developer/Platforms/MacOSX.platform/Developer/SDKs/MacOSX10.10.sdk/System/Library/Frameworks/JavaVM.framework/Headers" -c jni/JSQLiteFunction.cpp -o obj/local/osx/JSQLiteFunction.o
g++ -std=c++11 "-I/Applications/Xcode.app/Contents/Developer/Platforms/MacOSX.platform/Developer/SDKs/MacOSX10.10.sdk/System/Library/Frameworks/JavaVM.framework/Headers" -c jni/database.cpp -o obj/local/osx/database.o
g++ -std=c++11 "-I/Applications/Xcode.app/Contents/Developer/Platforms/MacOSX.platform/Developer/SDKs/MacOSX10.10.sdk/System/Library/Frameworks/JavaVM.framework/Headers" -c jni/statement.cpp -o obj/local/osx/statement.o
g++ -std=c++11 -dynamiclib -o libs/osx/libpluma.jnilib obj/local/osx/Utils.o obj/local/osx/SQLiteFunction.o obj/local/osx/JSQLiteFunction.o obj/local/osx/database.o obj/local/osx/statement.o ../sqlite/obj/local/osx/fsqlite3.o
echo "OSX ok"