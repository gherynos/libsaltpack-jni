cmake_minimum_required(VERSION 3.0)
project(saltpack-jni)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -fPIC -Wall")
if(MSVC)
    #vc 2012 fix for vararg templates
    set(MSVC_COMPILER_DEFS "-D_VARIADIC_MAX=10")
endif()

IF(WIN32)
    SET(CMAKE_FIND_LIBRARY_SUFFIXES .lib .a ${CMAKE_FIND_LIBRARY_SUFFIXES})
ELSE(WIN32)
    SET(CMAKE_FIND_LIBRARY_SUFFIXES .a ${CMAKE_FIND_LIBRARY_SUFFIXES})
ENDIF(WIN32)

set(CMAKE_MODULE_PATH "${CMAKE_SOURCE_DIR}/cmake_modules" ${CMAKE_MODULE_PATH})

include_directories("src/main/cpp/include")

# Sources
set(SOURCE_FILES src/main/cpp/InputStreamWrapper.cpp src/main/cpp/OutputStreamWrapper.cpp src/main/cpp/com_gherynos_libsaltpack_MessageReader.cpp src/main/cpp/com_gherynos_libsaltpack_MessageWriter.cpp src/main/cpp/com_gherynos_libsaltpack_Utils.cpp)
set(HEADER_FILES src/main/cpp/include/common.h src/main/cpp/include/InputStreamWrapper.h src/main/cpp/include/OutputStreamWrapper.h src/main/cpp/include/com_gherynos_libsaltpack_MessageReader.h src/main/cpp/include/com_gherynos_libsaltpack_MessageWriter.h src/main/cpp/include/com_gherynos_libsaltpack_Utils.h)

add_library(saltpack-jni SHARED ${SOURCE_FILES} ${HEADER_FILES})

# JNI
find_package(JNI REQUIRED)
include_directories(${JNI_INCLUDE_DIRS})
target_link_libraries(saltpack-jni ${JNI_LIBRARIES})

# LibSaltpack
find_package(LibSaltpack REQUIRED)
include_directories(${LIBSALTPACK_INCLUDE_DIRS})
target_link_libraries(saltpack-jni ${LIBSALTPACK_LIBRARIES})

# LibSodium
find_package(Libsodium REQUIRED)
include_directories(${LIBSODIUM_INCLUDE_DIRS})
target_link_libraries(saltpack-jni ${LIBSODIUM_LIBRARIES})

# MessagePack
find_package(Msgpack REQUIRED)
include_directories(${MSGPACK_INCLUDE_DIRS})
