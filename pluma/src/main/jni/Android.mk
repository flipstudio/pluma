LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE    := sqlite3-prebuilt
LOCAL_SRC_FILES := ../../../../../c-sqlite/libs/$(TARGET_ARCH_ABI)/libfsqlite.so
LOCAL_EXPORT_C_INCLUDES := ../../c-sqlite/jni
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := pluma
LOCAL_SRC_FILES := database.cpp statement.cpp Utils.cpp SQLiteFunction.cpp JSQLiteFunction.cpp character_tokenizer.cpp
LOCAL_SHARED_LIBRARIES := sqlite3-prebuilt
include $(BUILD_SHARED_LIBRARY)