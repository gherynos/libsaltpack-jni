LOCAL_PATH := $(call my-dir)

ARCH_FOLDER := $(TARGET_ARCH)
ifeq ($(ARCH_FOLDER),arm)
    ARCH_FOLDER = armv6
endif
ifeq ($(ARCH_FOLDER),arm64)
    ARCH_FOLDER = armv8-a
endif
ifeq ($(ARCH_FOLDER),x86)
    ARCH_FOLDER = i686
endif
ifeq ($(ARCH_FOLDER),x86_64)
    ARCH_FOLDER = westmere
endif
ifeq ($(ARCH_FOLDER),mips)
    ARCH_FOLDER = mips32
endif
ifeq ($(ARCH_FOLDER),mips64)
    ARCH_FOLDER = mips64r6
endif

ARCH_FOLDER_LS := $(TARGET_ARCH)
ifeq ($(ARCH_FOLDER_LS),arm)
    ARCH_FOLDER_LS = armeabi
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
LOCAL_MODULE := gmp
LOCAL_SRC_FILES := ${LIBGMP_PATH}/libgmp-android-$(ARCH_FOLDER)/lib/libgmp.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := gmpp
LOCAL_SRC_FILES := ${LIBGMP_PATH}/libgmp-android-$(ARCH_FOLDER)/lib/libgmpxx.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)

LOCAL_MODULE := saltpack-jni

LOCAL_C_INCLUDES := $(LOCAL_PATH)/../../src/main/cpp/include
LOCAL_C_INCLUDES += ${LIBSODIUM_PATH}/libsodium-android-$(ARCH_FOLDER)/include
LOCAL_C_INCLUDES += ${LIBGMP_PATH}/libgmp-android-$(ARCH_FOLDER)/include
LOCAL_C_INCLUDES += ${MSGPACK_PATH}/include
LOCAL_C_INCLUDES += ${LIBSALTPACK_PATH}/include

LOCAL_SRC_FILES += ../../src/main/cpp/InputStreamWrapper.cpp
LOCAL_SRC_FILES += ../../src/main/cpp/net_nharyes_libsaltpack_MessageReader.cpp
LOCAL_SRC_FILES += ../../src/main/cpp/net_nharyes_libsaltpack_MessageWriter.cpp
LOCAL_SRC_FILES += ../../src/main/cpp/net_nharyes_libsaltpack_Utils.cpp
LOCAL_SRC_FILES += ../../src/main/cpp/OutputStreamWrapper.cpp

LOCAL_LDLIBS += -latomic

LOCAL_STATIC_LIBRARIES += saltpack
LOCAL_STATIC_LIBRARIES += sodium
LOCAL_STATIC_LIBRARIES += gmp
LOCAL_STATIC_LIBRARIES += gmpp

include $(BUILD_SHARED_LIBRARY)