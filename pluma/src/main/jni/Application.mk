MY_APP_PATH_FOR_OUTPUT := $(call my-dir)
NDK_APP_OUT := $(MY_APP_PATH_FOR_OUTPUT)/../../../build/
APP_ABI := all
APP_STL := c++_shared
APP_CFLAGS += -std=c++11
APP_OPTM := release
NDK_TOOLCHAIN_VERSION := 4.8
APP_PLATFORM := android-14