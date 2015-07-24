#!/bin/sh

rm -rf build/ src/main/libs
mkdir build/
cd build

ndk-build NDK_PROJECT_PATH=../src/main clean
ndk-build NDK_PROJECT_PATH=../src/main

PLATFORM='unknown'
if [[ "$(uname)" == 'Darwin' ]]; then
   PLATFORM='osx'
else
   PLATFORM='unix'
fi

mkdir -p local/$PLATFORM

cd local/$PLATFORM
cmake ../../../
cd ../../../
cmake --build build/local/$PLATFORM
cp build/local/$PLATFORM/libpluma.* src/main/libs/