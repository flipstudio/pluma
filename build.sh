#!/bin/sh

rm -rf obj/ libs/
ndk-build clean
ndk-build
mkdir -p obj/local/osx
mkdir -p libs/osx
echo "Building Cmake"
cd libs/osx
cmake ../../
cd ../..
cmake --build libs/osx/ --target all
echo "Cmake ok"