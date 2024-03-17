LOCAL_PATH := $(call my-dir)

ARCH_FOLDER := $(TARGET_ARCH)
ifeq ($(ARCH_FOLDER),arm)
    ARCH_FOLDER = armv7-a
endif
ifeq ($(ARCH_FOLDER),arm64)
    ARCH_FOLDER = armv8-a+crypto
endif
ifeq ($(ARCH_FOLDER),x86)
    ARCH_FOLDER = i686
endif
ifeq ($(ARCH_FOLDER),x86_64)
    ARCH_FOLDER = westmere
endif

include $(CLEAR_VARS)

LOCAL_MODULE := sodium
LOCAL_SRC_FILES := ${LIBSODIUM_PATH}/libsodium-android-$(ARCH_FOLDER)/lib/libsodium.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)

LOCAL_MODULE := saltpack

LOCAL_C_INCLUDES := $(LOCAL_PATH)/../../include
LOCAL_C_INCLUDES += ${LIBSODIUM_PATH}/libsodium-android-$(ARCH_FOLDER)/include
LOCAL_C_INCLUDES += ${MSGPACK_PATH}/include
LOCAL_C_INCLUDES += ${BOOST_PATH}

LOCAL_SRC_FILES += ../../src/ArmoredInputStream.cpp
LOCAL_SRC_FILES += ../../src/ArmoredOutputStream.cpp
LOCAL_SRC_FILES += ../../src/Base.cpp
LOCAL_SRC_FILES += ../../src/MessageReader.cpp
LOCAL_SRC_FILES += ../../src/MessageWriter.cpp
LOCAL_SRC_FILES += ../../src/Utils.cpp

LOCAL_STATIC_LIBRARIES += sodium

include $(BUILD_STATIC_LIBRARY)
