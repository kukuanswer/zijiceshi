#include <unistd.h>
#include <jni.h>
#include <android/log.h>

extern "C" {
#include "play.h"
}

#ifdef ANDROID

/* Include the SDL main definition header */

/*******************************************************************************
                 Functions called by JNI
*******************************************************************************/


// Library init
//extern "C" jint JNI_OnLoad(JavaVM* vm, void* reserved)
//{
//    return JNI_VERSION_1_4;
//}

// Start up the SDL app
extern "C" int Java_org_libsdl_app_SDLActivity_PlayerInit(JNIEnv* env,  jobject obj)
{
   return player_init();
}

extern "C" int Java_org_libsdl_app_SDLActivity_PlayerPrepare(JNIEnv* env,  jobject obj, jstring jfileName, jint bufferSize, jint maxAnalyzeDuration)
{
        int minFrames = bufferSize;
        int analyzeDuration = maxAnalyzeDuration;
        jboolean isCopy;
        char localFileName[1024];
        const char *fileString     = env->GetStringUTFChars(jfileName, &isCopy);

        strncpy(localFileName, fileString, 1024);
        env->ReleaseStringUTFChars(jfileName, fileString);
        return player_prepare(localFileName, minFrames, analyzeDuration);
}

extern "C" int Java_org_libsdl_app_SDLActivity_PlayerMain(JNIEnv* env,  jobject obj)
{
   return player_main();
}

extern "C" int Java_org_libsdl_app_SDLActivity_PlayerExit(JNIEnv* env,  jobject obj)
{
   return player_exit();
}

extern "C" int Java_org_libsdl_app_SDLActivity_PlayerSeekTo(JNIEnv* env,  jobject obj, jint msec)
{
   int pos = msec;
   return seekTo(pos);
}

extern "C" int Java_org_libsdl_app_SDLActivity_PlayerPause(JNIEnv* env,  jobject obj)
{
   return streamPause();
}

extern "C" int Java_org_libsdl_app_SDLActivity_PlayerIsPlay(JNIEnv* env,  jobject obj)
{
   return isPlay();
}

extern "C" int Java_org_libsdl_app_SDLActivity_PlayerGetDuration(JNIEnv* env,  jobject obj)
{
   return getDuration();
}

extern "C" int Java_org_libsdl_app_SDLActivity_PlayergetCurrentPosition(JNIEnv* env,  jobject obj)
{
   return getCurrentPosition();
}

extern "C" jstring Java_org_libsdl_app_SDLActivity_getyingguiData(JNIEnv* env,  jobject obj)
{ 
 // jobjectArray swArray = (jobjectArray)(env->NewObjectArray(5,env->FindClass("java/lang/String"),env->NewStringUTF("nihao")));
  //SetObjectArrayElement
//  int i = 0;
//  for(i = 0; i < 5; i++)
 //  env->SetObjectArrayElement(swArray, i, "nihao") ;
   //return swArray;
 //  char* tempStr = getyingguiData();
   char* tempStr = getyingguiData();
   return env->NewStringUTF(tempStr);
}

extern "C" int Java_org_libsdl_app_SDLActivity_PlayerChangeAudio(JNIEnv* env,  jobject obj, jint index)
{
   int stream_index = index;
   return changeAudio(stream_index);
}

extern "C" int Java_org_libsdl_app_SDLActivity_PlayerSetAspectRatio(JNIEnv* env,  jobject obj, jint index)
{
   int type = index;
   return set_aspect_ratio(type);
}

extern "C" int Java_org_libsdl_app_SDLActivity_PlayerSetScreenParams(JNIEnv* env,  jobject obj, jint width, jint height)
{
   int mwidth = width;
   int mheight = height;
   return set_screen_params(mwidth, mheight);
}

#endif /* ANDROID */

/* vi: set ts=4 sw=4 expandtab: */
