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

ARCH_FOLDER_LS := $(TARGET_ARCH)
ifeq ($(ARCH_FOLDER_LS),arm)
    ARCH_FOLDER_LS = armeabi-v7a
endif
ifeq ($(ARCH_FOLDER_LS),arm64)
    ARCH_FOLDER_LS = arm64-v8a
endif

include $(CLEAR_VARS)

LOCAL_MODULE := saltpack
LOCAL_SRC_FILES := ${LIBSALTPACK_PATH}/android/obj/local/$(ARCH_FOLDER_LS)/libsaltpack.a
include $(PREBUILT_STATIC_LIBRARY)

LOCAL_MODULE := sodium
LOCAL_SRC_FILES := ${LIBSODIUM_PATH}/libsodium-android-$(ARCH_FOLDER)/lib/libsodium.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)

LOCAL_MODULE := saltpack-jni

LOCAL_C_INCLUDES := $(LOCAL_PATH)/../../src/main/cpp/include
LOCAL_C_INCLUDES += ${LIBSODIUM_PATH}/libsodium-android-$(ARCH_FOLDER)/include
LOCAL_C_INCLUDES += ${MSGPACK_PATH}/include
LOCAL_C_INCLUDES += ${BOOST_PATH}
LOCAL_C_INCLUDES += ${LIBSALTPACK_PATH}/include

LOCAL_SRC_FILES += ../../src/main/cpp/InputStreamWrapper.cpp
LOCAL_SRC_FILES += ../../src/main/cpp/com_gherynos_libsaltpack_MessageReader.cpp
LOCAL_SRC_FILES += ../../src/main/cpp/com_gherynos_libsaltpack_MessageWriter.cpp
LOCAL_SRC_FILES += ../../src/main/cpp/com_gherynos_libsaltpack_Utils.cpp
LOCAL_SRC_FILES += ../../src/main/cpp/OutputStreamWrapper.cpp

LOCAL_STATIC_LIBRARIES += saltpack
LOCAL_STATIC_LIBRARIES += sodium

include $(BUILD_SHARED_LIBRARY)
