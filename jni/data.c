/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
#include <string.h>
#include <jni.h>

/* This is a trivial JNI example where we use a native method
 * to return a new VM String. See the corresponding Java source
 * file located at:
 *304628
 *   apps/samples/hello-jni/project/src/com/example/hellojni/HelloJni.java
 */
jstring
Java_com_luoye_wodemo_SuggestionActivity_getPostUrl( JNIEnv* env,
                                                  jobject thiz )
{
    return (*env)->NewStringUTF(env,
	"http://s.wodemo.com/");
}
jstring
Java_com_luoye_wodemo_SuggestionActivity_getTail( JNIEnv* env,
                                                  jobject thiz )
{
    return (*env)->NewStringUTF(env,
	"(来自我的磨助手)");
}
jstring
Java_com_luoye_wodemo_SuggestionActivity_getFid( JNIEnv* env,
                                                  jobject thiz )
{
    return (*env)->NewStringUTF(env,
	"304628");
}
jstring
Java_com_luoye_wodemo_AboutActivity_getAuthor( JNIEnv* env,
                                                  jobject thiz )
{
    return (*env)->NewStringUTF(env,
	"落叶似秋");
}
jstring
Java_com_luoye_wodemo_AboutActivity_getWodemo( JNIEnv* env,
                                                  jobject thiz )
{
    return (*env)->NewStringUTF(env,
	"http://zyw8.wodemo.net");
}

jstring
Java_com_luoye_wodemo_MainActivity_getWodemo( JNIEnv* env,
                                                  jobject thiz )
{
    return (*env)->NewStringUTF(env,
	"http://zyw8.wodemo.net");
}
jstring
Java_com_luoye_wodemo_AboutActivity_getHelp( JNIEnv* env,
                                                  jobject thiz )
{
    return (*env)->NewStringUTF(env,
	"声明：使用本软件所产生的纠纷与作者无关。\n\n特别感谢：pansong291，【飛龍在天】，csngmap，耀眼阳光，优客等网友。\n\n多文件上传：长按文件名可修改文件名，文件描述等信息。\n\n代码获取：可以获取磨友的皮肤代码，其中获取js和css时必须是我的磨首页地址。如果已登录，会以已登录模式获取代码。\n\n关于安全：本软件不会变相的收集你的密码和cookie，密码和cookie已加密存储在手机上。如果不放心，可以忽略本软件。");
}



