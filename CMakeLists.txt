cmake_minimum_required(VERSION 3.1)
project(pluma)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

set(CMAKE_BUILD_TYPE Release)

include_directories(/Applications/Xcode.app/Contents/Developer/Platforms/MacOSX.platform/Developer/SDKs/MacOSX10.10.sdk/System/Library/Frameworks/JavaVM.framework/Headers/)

set(SQLITE_PATH ../sqlite)

set(SOURCE_FILES
    jni/database.cpp
    jni/database.h
    jni/statement.cpp
    jni/statement.h
    jni/PlumaRuntime.cpp
    jni/PlumaRuntime.h)

link_libraries(${SQLITE_PATH}/libs/mac/libsqlite.jnilib)

add_library(Pluma SHARED ${SOURCE_FILES} jni/PlumaRuntime.cpp jni/PlumaRuntime.h)