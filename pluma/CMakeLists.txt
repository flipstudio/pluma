cmake_minimum_required(VERSION 3.2)
project(pluma)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

set(CMAKE_BUILD_TYPE Release)

set(SQLITE_PATH ${CMAKE_CURRENT_SOURCE_DIR}/../../SQLite)

set(JNI_PATH ${CMAKE_CURRENT_SOURCE_DIR}/src/main/jni)

if(APPLE)
    if(TARGET pluma_static)
    	link_libraries(${SQLITE_PATH}/libs/osx/libfsqlite.a)
    else()
    	link_libraries(${SQLITE_PATH}/libs/osx/libfsqlite.dylib)
    endif()
elseif(UNIX)
    if(TARGET pluma_static)
    	link_libraries(${SQLITE_PATH}/libs/unix/libfsqlite.a)
    else()
    	link_libraries(${SQLITE_PATH}/libs/unix/libfsqlite.so)
		endif()
else()
    link_libraries(${SQLITE_PATH}/libs/win32/libfsqlite.dll)
endif()

find_package(JNI REQUIRED)
include_directories(${JNI_INCLUDE_DIRS})
include_directories(${SQLITE_PATH}/jni)

set(SOURCE_FILES
    ${JNI_PATH}/database.cpp
    ${JNI_PATH}/database.h
    ${JNI_PATH}/statement.cpp
    ${JNI_PATH}/statement.h
    ${JNI_PATH}/JSQLiteFunction.cpp
    ${JNI_PATH}/JSQLiteFunction.h
    ${JNI_PATH}/SQLiteFunction.cpp
    ${JNI_PATH}/SQLiteFunction.h
    ${JNI_PATH}/Utils.cpp
    ${JNI_PATH}/Utils.h
    ${JNI_PATH}/character_tokenizer.cpp
    ${JNI_PATH}/character_tokenizer.h
    ${JNI_PATH}/fts3_tokenizer.h)

add_library(pluma_shared SHARED ${SOURCE_FILES})
add_library(pluma_static STATIC ${SOURCE_FILES})

set_target_properties(pluma_shared PROPERTIES OUTPUT_NAME pluma)
set_target_properties(pluma_static PROPERTIES OUTPUT_NAME pluma)