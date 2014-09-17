cmake_minimum_required(VERSION 2.8.4)
project(Pluma)

set(CMAKE_BUILD_TYPE Release)

include_directories(${CMAKE_CURRENT_SOURCE_DIR}/jni /System/Library/Frameworks/JavaVM.framework/Headers/ ${CMAKE_CURRENT_SOURCE_DIR}/../SQLite/jni)
link_directories("${CMAKE_CURRENT_SOURCE_DIR}/../SQLite/jni")
add_library(Pluma SHARED jni/database.cpp jni/statement.cpp ${CMAKE_CURRENT_SOURCE_DIR}/../SQLite/jni/sqlite3.c)