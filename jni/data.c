
#include <string.h>
#include <jni.h>

jstring
Java_com_luoye_wodemo_JniBridge_getPostUrl( JNIEnv* env,
                                                  jobject thiz )
{
    return (*env)->NewStringUTF(env,
	"http://s.wodemo.com/");
}
jstring
Java_com_luoye_wodemo_JniBridge_getTail( JNIEnv* env,
                                                  jobject thiz )
{
    return (*env)->NewStringUTF(env,
	"(来自我的磨助手)");
}
jstring
Java_com_luoye_wodemo_JniBridge_getBmobKey( JNIEnv* env,
                                                  jobject thiz )
{
    return (*env)->NewStringUTF(env,
	"825b00eba7aaa18b7278e01eee6513a7");
}
jstring
Java_com_luoye_wodemo_JniBridge_getAuthor( JNIEnv* env,
                                                  jobject thiz )
{
    return (*env)->NewStringUTF(env,
	"Socradi(落叶似秋)");
}
jstring
Java_com_luoye_wodemo_JniBridge_getWodemo( JNIEnv* env,
                                                  jobject thiz )
{
    return (*env)->NewStringUTF(env,
	"http://zyw8.wodemo.com");
}


jstring
Java_com_luoye_wodemo_JniBridge_getHelp( JNIEnv* env,
                                                  jobject thiz )
{
    return (*env)->NewStringUTF(env,
	"**软件作者**：Socradi(落叶似秋)"
	"</br>**工作室**：[卧槽工作室](http://jq.qq.com/?_wv=1027&k=YjGMx8)(点击链接加群讨论反馈)"
	"</br>**声明**：使用本软件所产生的纠纷与作者无关。"
	"</br>**特别感谢**：pansong291，【飛龍在天】，csngmap，耀眼阳光，优客等网友。"
	"</br>**多文件上传**：长按文件名可修改文件名，文件描述等信息。"
	"</br>**代码获取**：可以获取磨友的皮肤代码，其中获取js和css时必须是我的磨首页地址。如果已登录，会以已登录模式获取代码。"
	"</br>**撰文**：推荐使用Markdown来书写文章。"
	"</br>**浏览器**：方便浏览我的磨各个站点，如果已登录会以已登录模式访问网页。"
	"</br>**关于安全**：本软件不会变相的收集你的密码和cookie，密码和cookie已加密存储在手机上。如果不放心，可以忽略本软件。"
	"</br>**开发不容易，如果喜欢本软件就捐赠我们吧！一块也可以呀。**"
	"</br>**捐赠**：15659176841（支付宝账号）");
}



