cmake_minimum_required(VERSION 3.4.1)

set(LIBS ${CMAKE_SOURCE_DIR}/src/main/jniLibs)
# set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${LIBS}/mp3-encoder/${ANDROID_ABI})
# set(CMAKE_ARCHIVE_OUTPUT_DIRECTORY ${LIBS}/mp3lame/${ANDROID_ABI})

add_library(
  lib-opencv-java3
  SHARED
  IMPORTED
 )
set_target_properties(lib-opencv-java3 PROPERTIES IMPORTED_LOCATION
   ${LIBS}/${ANDROID_ABI}/libopencv_java3.so)

AUX_SOURCE_DIRECTORY(src/main/cpp SRC_FACE_DETECTION)

# 增加其他目录的源文件到集合变量中
# list(APPEND SRC_FACE_DETECTION
#        ../common/1.c
#        ../common/2.c
#        ../common/3.c
#        ../common/4.c
#        ../common/5.c
#        ../common/WriteLog.c
# )

add_library(
  face-detection
  SHARED
  ${SRC_FACE_DETECTION})

target_include_directories(
  face-detection
  PRIVATE
  ${LIBS}/include)

target_link_libraries(
  face-detection
  lib-opencv-java3
  jnigraphics
  android
  log
)

# ADD_SUBDIRECTORY(SUB_DIR)

# copy头文件到 静态库相同文件夹下
# add_custom_command(TARGET myjni
#  PRE_BUILD
#  COMMAND echo "executing a copy command"
#  COMMAND cp ${CMAKE_CURRENT_SOURCE_DIR}/myjni.h ${PROJECT_SOURCE_DIR}/../../../build/outputs/staticLib/myjni/${ANDROID_ABI}
#  COMMENT "PRE_BUILD, so This command will be executed before building target myjni"
# )