LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := pluma
LOCAL_SRC_FILES := database.c statement.c sqlite3.c

include $(BUILD_SHARED_LIBRARY)