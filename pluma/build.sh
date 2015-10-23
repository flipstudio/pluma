#!/bin/sh

rm -rf libs/
mkdir libs/
cd libs

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
cmake --build libs/local/$PLATFORM