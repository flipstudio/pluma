#!/bin/sh

rm -rf obj/ libs/
ndk-build clean
ndk-build

PLATFORM='unknown'
if [[ "$(uname)" == 'Darwin' ]]; then
   PLATFORM='osx'
else
   PLATFORM='unix'
fi

mkdir -p libs/$PLATFORM
cd libs/$PLATFORM
cmake ../../
cd ../../
cmake --build libs/$PLATFORM --target all