#define LOG_TAG "pdfReader-jni"
#include "host.h"
#include "com_pvi_ap_reader_external_pdf_pdfReader.h"
#include <stdlib.h>
#include <string>

/* Saved for talking back to the Java parent */
static JNIEnv *g_jniEnv = NULL;
static jobject g_this = NULL;

string   jstringTostring(JNIEnv* env, jstring jstr)  
{  
    char* rtn = NULL;  
    jclass clsstring   =   env->FindClass("java/lang/String");   
    //jstring strencode   =   env->NewStringUTF("GB2312");  
    jstring strencode = (env)->NewStringUTF("UTF8"); 
    jmethodID mid   =   env->GetMethodID(clsstring,   "getBytes",   "(Ljava/lang/String;)[B");   
    jbyteArray barr=   (jbyteArray)env->CallObjectMethod(jstr,mid,strencode);  
    jsize alen   =   env->GetArrayLength(barr);  
    jbyte* ba   =   env->GetByteArrayElements(barr,JNI_FALSE);  
    if(alen>0)  
    {  
        rtn = (char*)malloc(alen+1);         //new   char[alen+1];  
        memcpy(rtn,ba,alen);  
        rtn[alen]=0;  
        DEBUGINFO("in %s: translate to %s\n", __func__, rtn);
    }  
    env->ReleaseByteArrayElements(barr,ba,0);  
    string stemp(rtn);
    free(rtn);
    return   stemp;  
} 

jstring stoJstring( JNIEnv* env, const char* pat )
{
    //定义java String类 strClass
    jclass strClass = (env)->FindClass("java/lang/String");
    //获取java String类方法String(byte[],String)的构造器,用于将本地byte[]数组转换为一个新String
    jmethodID ctorID = (env)->GetMethodID(strClass, "<init>", "([BLjava/lang/String;)V");

    int len = strlen(pat);
#if 0    
    if (len==0)
        return (env)->NewStringUTF(pat);
#endif
    jbyteArray bytes = (env)->NewByteArray(len);//建立byte数组
    (env)->SetByteArrayRegion(bytes, 0, len, (jbyte*)pat);//将char* 转换为byte数组

    // 设置String, 保存语言类型,用于byte数组转换至String时的参数
    //jstring encoding = (env)->NewStringUTF("GB2312");
    jstring encoding = (env)->NewStringUTF("UTF8"); 
    //将byte数组转换为java String,并输出
    return (jstring)(env)->NewObject(strClass, ctorID, bytes, encoding);
}

// for loading fonts
int getAssetBytes(const char *assetName, unsigned char **buffer)
{
    jclass cls = g_jniEnv->GetObjectClass(g_this);
//    jclass cls = (g_jniEnv)->FindClass("com/pvi/ap/reader/external/pdf/pdfReader"); 
    jmethodID meth = g_jniEnv->GetMethodID(cls, "getAssetBytes", "(Ljava/lang/String;)[B");

    jstring jAssetName = g_jniEnv->NewStringUTF(assetName);
    if(jAssetName == NULL) return -1;

    jbyteArray jBytes = (jbyteArray)g_jniEnv->CallObjectMethod(g_this, meth, jAssetName);
    if(jBytes == NULL) return -1;

    int len = g_jniEnv->GetArrayLength(jBytes);
    if(len > 0) {
        *buffer = (unsigned char *)malloc(len);
        g_jniEnv->GetByteArrayRegion(jBytes, 0, len, (signed char *)*buffer);
        return len;
    }

    return -1;
}

/* Header for class com_pvi_ap_reader_external_pdf_pdfReader */

/*
 * Class:     com_pvi_ap_reader_external_pdf_pdfReader
 * Method:    init
 * Signature: ()Z
 */
adobe::LinuxHost *reader=NULL;
    JNIEXPORT jboolean JNICALL Java_com_pvi_ap_reader_external_pdf_pdfReader_init
(JNIEnv *env, jobject obj)
{
    bool ret;
    LOGI("===============================================");
    LOGI("adobe RMSDK 9.2, pvi, arthur, %s %s", __DATE__, __TIME__);
    LOGI("===============================================");
    LOG_READER_DEBUG("in : %s", __func__);
    LOG_READER_DEBUG("pdf reader about to  initialize!");

    g_jniEnv = env; g_this = obj;

    if (adobe::LinuxHost::initDevice())
    {
        reader = new adobe::LinuxHost("",600.0, 690.0 ,0.0 ,0.0 );
        ret = true;
    }
    else 
    {
        reader = NULL;
        ret = false;
    }
    LOG_READER_DEBUG("out: %s", __func__);
    return ret;
}

/*
 * Class:     com_pvi_ap_reader_external_pdf_pdfReader
 * Method:    setViewPortSize
 * Signature: (II)Z
 */
    JNIEXPORT jboolean JNICALL Java_com_pvi_ap_reader_external_pdf_pdfReader_setViewPortSize
(JNIEnv *env, jobject obj, jint w, jint h)
{
    LOG_READER_DEBUG("in : %s", __func__);
    LOGD("setViewPortSize > w:%d, h:%d", w, h);
    bool ret = false;
    if (reader==NULL)
    {
        LOG_ERROR("no pdf reader, init() firstly!\n");
        ret = false;
    }
    ret = reader->changeViewPort(w,h);
    LOG_READER_DEBUG("out: %s", __func__);
    return ret;
}

/*
 * Class:     com_pvi_ap_reader_external_pdf_pdfReader
 * Method:    setCache
 * Signature: (Z)Z
 */
    JNIEXPORT jboolean JNICALL Java_com_pvi_ap_reader_external_pdf_pdfReader_setCache
(JNIEnv *env, jobject obj, jboolean)
{
    LOG_READER_DEBUG("in : %s", __func__);
    g_jniEnv = env; g_this = obj;
    if (reader==NULL)
    {
        LOG_ERROR("no pdf reader, init() firstly!\n");
        return false;
    }
    LOG_READER_DEBUG("out: %s", __func__);
    return false;/*TODO*/
}

/*
 * Class:     com_pvi_ap_reader_external_pdf_pdfReader
 * Method:    open
 * Signature: String
 */
    JNIEXPORT jboolean JNICALL Java_com_pvi_ap_reader_external_pdf_pdfReader_open
(JNIEnv *env, jobject obj, jstring instr)
{
    LOG_READER_DEBUG("in : %s", __func__);
    g_jniEnv = env; g_this = obj;
    if (reader==NULL)
    {
        LOG_ERROR("no pdf reader, init() firstly!\n");
        return false;
    }
    char* filepath;
    filepath = (char*)(*env).GetStringUTFChars(instr, NULL);

    LOG_READER_DEBUG("the file about to open is:%s", filepath);

    jboolean ret = reader->openBook(filepath, 0, 1.0);
    //    reader->jumpPage(1);
    (*env).ReleaseStringUTFChars(instr, filepath); 

    LOG_READER_DEBUG("out: %s %s", __func__, ret?"ok":"error");
    return ret;

}
/*
 * Class:     com_pvi_ap_reader_external_pdf_pdfReader
 * Method:    close
 * Signature: ()Z
 */
    JNIEXPORT jboolean JNICALL Java_com_pvi_ap_reader_external_pdf_pdfReader_close
(JNIEnv *env, jobject obj)
{
    LOG_READER_DEBUG("in : %s", __func__);
    g_jniEnv = env; g_this = obj;
    if (reader==NULL)
    {
        LOG_ERROR("no pdf reader, init() firstly!\n");
        return false;
    }
    bool ret = reader->closeBook();
    LOG_READER_DEBUG("out: %s", __func__);
    return ret;
}
/*
 * Class:     com_pvi_ap_reader_external_pdf_pdfReader
 * Method:    getCurrentPage
 * Signature: (I)I
 * cache: -1, previous page #
 *         0, current page #
 *         1, next page #
 */

    JNIEXPORT jint JNICALL Java_com_pvi_ap_reader_external_pdf_pdfReader_getCurrentPage
(JNIEnv *env, jobject obj, jint cache)
{
    LOG_READER_DEBUG("in : %s", __func__);
    g_jniEnv = env; g_this = obj;
    if (reader==NULL)
    {
        LOG_ERROR("no pdf reader, init() firstly!\n");
        return -1;
    }
#if 0
    return reader->getCurrentPage(); 
#else
    int ret = -1;
    if (reader->getCacheEnable())
        ret = reader->pageNumber(cache);
    else
        ret = reader->getCurrentPageNum();
    LOG_READER_DEBUG("out: %s", __func__);
    return ret;
#endif
}

/*
 * Class:     com_pvi_ap_reader_external_pdf_pdfReader
 * Method:    getPageCount
 * Signature: ()I
 */
    JNIEXPORT jint JNICALL Java_com_pvi_ap_reader_external_pdf_pdfReader_getPageCount
(JNIEnv *env, jobject obj)
{
    int ret = -1;
    g_jniEnv = env; g_this = obj;
    LOG_READER_DEBUG("in : %s", __func__);
    if (reader==NULL)
    {
        LOG_ERROR("no pdf reader, init() firstly!\n");
        return -1;
    }
    ret = reader->pageCount();
    LOG_READER_DEBUG("out: %s %d", __func__, ret);
    return ret;
}

/*
 * Class:     com_pvi_ap_reader_external_pdf_pdfReader
 * Method:    getNaturalSize
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_pvi_ap_reader_external_pdf_pdfReader_getNaturalSize
  (JNIEnv *env, jobject obj, jint)
{
    g_jniEnv = env; g_this = obj;
    LOG_READER_DEBUG("in : %s", __func__);
    LOG_READER_DEBUG("out: %s", __func__);
	return 0;
}

/*
 * Class:     com_pvi_ap_reader_external_pdf_pdfReader
 * Method:    gotoPage
 * Signature: (I)Z
 */
    JNIEXPORT jboolean JNICALL Java_com_pvi_ap_reader_external_pdf_pdfReader_gotoPage
(JNIEnv *env, jobject obj, jint pg, jboolean after)
{
    bool ret = false;
    LOG_READER_DEBUG("in : %s", __func__);
    g_jniEnv = env; g_this = obj;
    if (reader==NULL)
    {
        LOG_ERROR("no pdf reader, init() firstly!\n");
        return false;
    }
    ret = reader->jumpPage(pg,after);
    LOG_READER_DEBUG("out: %s", __func__);
    return ret;
}

/*
 * Class:     com_pvi_ap_reader_external_pdf_pdfReader
 * Method:    gotoPrevScreenPage
 * Signature: ()Z
 */
    JNIEXPORT jboolean JNICALL Java_com_pvi_ap_reader_external_pdf_pdfReader_gotoPrevScreenPage
(JNIEnv *env, jobject obj)
{
    bool ret = false;
    LOG_READER_DEBUG("in : %s", __func__);
    g_jniEnv = env; g_this = obj;
    if (reader==NULL)
    {
        LOG_ERROR("no pdf reader, init() firstly!\n");
        return false;
    }
    LOG_READER_DEBUG("out: %s", __func__);
    ret = reader->previousPage();
    return ret;
}

/*
 * Class:     com_pvi_ap_reader_external_pdf_pdfReader
 * Method:    gotoNextScreenPage
 * Signature: ()Z
 */
    JNIEXPORT jboolean JNICALL Java_com_pvi_ap_reader_external_pdf_pdfReader_gotoNextScreenPage
(JNIEnv *env, jobject obj)
{
    bool ret = false;
    LOG_READER_DEBUG("in : %s", __func__);
    g_jniEnv = env; g_this = obj;
    if (reader==NULL)
    {
        LOG_ERROR("no pdf reader, init() firstly!\n");
        return false;
    }
    ret = reader->nextPage();
    return ret;
}

/*
 * Class:     com_pvi_ap_reader_external_pdf_pdfReader
 * Method:    setFontSize
 * Signature: (D)Z
 */
    JNIEXPORT jboolean JNICALL Java_com_pvi_ap_reader_external_pdf_pdfReader_setFontSize
(JNIEnv *env, jobject obj, jdouble scale)
{
    LOG_READER_DEBUG("in : %s", __func__);
    g_jniEnv = env; g_this = obj;
    if (reader==NULL)
    {
        LOG_ERROR("no pdf reader, init() firstly!\n");
        return false;
    }
#if 1
    reader->setPdfFontSize(scale);
    LOG_READER_DEBUG("out: %s", __func__);
    return true;
#endif
}

/*
 * Class:     com_pvi_ap_reader_external_pdf_pdfReader
 * Method:    getBitmapStuff
 * Signature: (I)[B
 */
    JNIEXPORT jbyteArray JNICALL Java_com_pvi_ap_reader_external_pdf_pdfReader_getBitmapStuff
(JNIEnv *env, jobject obj, jint cache)

{
    LOG_READER_DEBUG("in : %s", __func__);
    g_jniEnv = env; g_this = obj;
    bool res = false;
    if (reader==NULL)
    {
        LOG_ERROR("no pdf reader, init() firstly!\n");
        return NULL;
    }


    int size = reader->getImageSize();/* get $size only*/
    unsigned char* buffer ;

#if 0
    char log_info[128];
    memset(log_info, 0, sizeof(log_info));
    sprintf(log_info,"size=%d\n",size);
    LOG_DEBUG(log_info);
#endif 

    if ((buffer =(unsigned char*)malloc(size))==NULL)
    {
        LOG_ERROR("malloc memory error\n");
        /*exit(-1);*/
        env->ThrowNew(env->FindClass("java/lang/OutOfMemoryError"),"getBitmapStuff, OutOfMemoryError");
        return NULL;
    }

    res = reader->getImage(buffer, cache);/* get the real stuff */
    jbyteArray bytearray;
    if (res)
    {
        bytearray = env->NewByteArray(size);
        (*env).SetByteArrayRegion(bytearray, 0, size, (jbyte*)buffer);
    }else 
    {
        free(buffer);
        return NULL;
    }
    free(buffer);
    LOG_READER_DEBUG("out: %s", __func__);
    return bytearray;
}

/*
 * Class:     com_pvi_ap_reader_external_pdf_pdfReader
 * Method:    getChapterList
 * Signature: (I)[Lcom/pvi/ap/reader/external/pdf/ChapterInfo;
 */

  // count is a static value, record the number of TOC items that remained after geting #times.
  // 100 items each time.
    JNIEXPORT jobjectArray JNICALL Java_com_pvi_ap_reader_external_pdf_pdfReader_getChapterList
(JNIEnv *env, jobject obj, jint times ) 
{
    LOG_READER_DEBUG("in : %s", __func__);
    g_jniEnv = env; g_this = obj;
    if (reader==NULL) { LOG_ERROR("no pdf reader, init() firstly!\n"); return NULL; }
    const int max_obj = 100;

    list< toc_item > toc_list = reader->get_toc_list();
    static int count = count = toc_list.size();

    jobjectArray args = 0;  //申明一个object数组  
    jsize        len = 0;  //数组大小  
    if (count>max_obj)
    {
        len = max_obj;
        count -= max_obj;
    }
    else
    {
        len = count;
    }

    if (toc_list.size()==0) {
        return args;
    }

    //获取object所属类,一般为ava/lang/Object就可以了  
    jclass objClass = (env)->FindClass("com/pvi/ap/reader/external/pdf/ChapterInfo"); 

    //新建object数组  
    args = (env)->NewObjectArray(len, objClass, 0);  
    if (args==NULL)
    {
        LOG_ERROR("can not new object array");
        return args;
    }

    /**//* 下面为获取到Java中对应的实例类中的变量*/  

    //获取Java中的实例类  
    jclass objectClass = (env)->FindClass("com/pvi/ap/reader/external/pdf/ChapterInfo");  

    jmethodID methodId= env->GetMethodID(objectClass,"<init>","()V");
    //获取类中每一个变量的定义  
    jfieldID titleId    = (env)->GetFieldID(objectClass,"title","Ljava/lang/String;");  
    jfieldID positionId = (env)->GetFieldID(objectClass,"position","I");  
    jfieldID levelId= (env)->GetFieldID(objectClass,"level","I");  

    //给每一个实例的变量付值，并且将实例作为一个object，添加到objcet数组中  
    list<toc_item>::iterator it;
    int i;
    for (it = toc_list.begin(),i=0;i<max_obj*times;it++,i++)
        ;
        
    for(i=0; (it != toc_list.end()) && i<len; it++,i++ )  
    {  
        jobject _obj= env->AllocObject(objectClass);
        //        jobject _obj= env->NewObject(objectClass, methodId);
        //给每一个实例的变量付值  
        jstring jtitle = stoJstring(env, (*it).title);  
        (env)->SetObjectField(_obj,titleId,jtitle);  
        (env)->SetIntField(_obj,positionId,(*it).position);  
        (env)->SetIntField(_obj,levelId,(*it).level);  
        //添加到objcet数组中  
        (env)->SetObjectArrayElement(args, i, _obj);  
        LOGD("%d: %s:", i, (*it).title);
    }  

    LOG_READER_DEBUG("out: %s", __func__);
    //返回object数组  
    return args;
}

/*
 * Class:     com_pvi_ap_reader_external_pdf_pdfReader
 * Method:    getChapterListLen
 * Signature: ()I
 */
    JNIEXPORT jint JNICALL Java_com_pvi_ap_reader_external_pdf_pdfReader_getChapterListLen
(JNIEnv *env, jobject obj)
{
    int ret = -1;
    LOG_READER_DEBUG("in : %s", __func__);
    g_jniEnv = env; g_this = obj;
    reader->getTOCList();    
    ret = reader->get_toc_list().size();
    LOGI("TOC list length:%d", ret);
    LOG_READER_DEBUG("out: %s", __func__);
    return ret;
}

/*
 * Class:     com_pvi_ap_reader_external_pdf_pdfReader
 * Method:    hasTableOfContent
 * Signature: ()Z
 */
    JNIEXPORT jboolean JNICALL Java_com_pvi_ap_reader_external_pdf_pdfReader_hasTableOfContent
(JNIEnv *env, jobject obj)
{
    int ret = -1;
    LOG_READER_DEBUG("in : %s", __func__);
    g_jniEnv = env; g_this = obj;
#if 1
    if (reader==NULL)
    {
        LOG_ERROR("no pdf reader, init() firstly!\n");
        return false;
    }
    ret = reader->getChaptercount();
    LOG_READER_DEBUG("out: %s", __func__);
    return ret>0;
#else
    return true;
#endif
}

/*
 * Class:     com_pvi_ap_reader_external_pdf_pdfReader
 * Method:    autoCutMargin
 * Signature: (Z)Z
 */
    JNIEXPORT jboolean JNICALL Java_com_pvi_ap_reader_external_pdf_pdfReader_autoCutMargin
(JNIEnv *env, jobject obj, jboolean cut)
{
    LOG_READER_DEBUG("in : %s", __func__);
    g_jniEnv = env; g_this = obj;
    bool ret = false;
    g_jniEnv = env; g_this = obj;
    ret = reader->autoCutMargin(cut);
    LOG_READER_DEBUG("out: %s", __func__);
    return ret;
}

/*
 * Class:     com_pvi_ap_reader_external_pdf_pdfReader
 * Method:    setViewPort
 * Signature: (IIII)Z
 */
    JNIEXPORT jboolean JNICALL Java_com_pvi_ap_reader_external_pdf_pdfReader_setViewPort
(JNIEnv *env, jobject obj, jint, jint, jint, jint)
{
    LOG_READER_DEBUG("in : %s", __func__);
    LOG_READER_DEBUG("out: %s", __func__);
    return false;
}

/*
 * Class:     com_pvi_ap_reader_external_pdf_pdfReader
 * Method:    setFitMode
 * Signature: (I)Z
 */
    JNIEXPORT jboolean JNICALL Java_com_pvi_ap_reader_external_pdf_pdfReader_setFitMode
(JNIEnv *env, jobject obj, jint fit)
{
    LOG_READER_DEBUG("in : %s", __func__);
    g_jniEnv = env; g_this = obj;
    bool ret = false;
    ret = reader->setFitMode(fit);
    LOG_READER_DEBUG("out: %s", __func__);
    return ret;
}

/*
 * Class:     com_pvi_ap_reader_external_pdf_pdfReader
 * Method:    setFitMode
 * Signature: (I)Z
 */
    JNIEXPORT jboolean JNICALL Java_com_pvi_ap_reader_external_pdf_pdfReader_setPagingMode
(JNIEnv *env, jobject obj, jint pagingmode)
{
    bool ret = false;
    LOG_READER_DEBUG("in : %s", __func__);
    g_jniEnv = env; g_this = obj;
    if (reader->setPagingMode(pagingmode))
    {
        reader->setMatrix();
        ret = true;
    }

    LOG_READER_DEBUG("out: %s", __func__);
    return ret;

}



/*
 * Class:     com_pvi_ap_reader_external_pdf_pdfReader
 * Method:    setScrollOffset
 * Signature: (I)Z
 */
    JNIEXPORT jboolean JNICALL Java_com_pvi_ap_reader_external_pdf_pdfReader_setScrollOffset
(JNIEnv *env, jobject obj, jint ystart)
{
    bool ret = false;
    LOG_READER_DEBUG("in : %s", __func__);
    ret = reader->setMatrix(1, ystart);
    LOG_READER_DEBUG("out: %s", __func__);
    return ret;
}

/*
 * Class:     com_pvi_ap_reader_external_pdf_pdfReader
 * Method:    search
 * Signature: (Ljava/lang/String;ZII)I
 */
    JNIEXPORT jint JNICALL Java_com_pvi_ap_reader_external_pdf_pdfReader_search
(JNIEnv *env, jobject obj, jstring text, jboolean backward, jint start, jint range )
{
    int ret = -1;
    LOG_READER_DEBUG("in : %s", __func__);
    g_jniEnv = env; g_this = obj;
    string str = jstringTostring(env, text); 
    ret = reader->search(str.c_str(), backward, start, range );
    LOG_READER_DEBUG("out: %s", __func__);
    return ret;
}

/*
 * Class:     com_pvi_ap_reader_external_pdf_pdfReader
 * Method:    docReachHeadOrTail
 * Signature: (Z)Z
 */
JNIEXPORT jboolean JNICALL Java_com_pvi_ap_reader_external_pdf_pdfReader_docReachHeadOrTail
  (JNIEnv *env, jobject obj, jboolean head)
  {
      bool ret = false;
      LOG_READER_DEBUG("in : %s", __func__);
      g_jniEnv = env; g_this = obj;
      ret = reader->docReachHeadOrTail(head);
      LOG_READER_DEBUG("out: %s", __func__);
      return ret;
  }

#ifdef RMSDK_9_2
#define JNIREG_CLASS "com/pvi/ap/reader/external/pdf/pdfReader"  //class name to be registered
JNIEXPORT __attribute__ ((visibility("default"))) jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved)
{
    JNIEnv* jniEnv = NULL;

    JNINativeMethod methods[] =  {
	{ "init",               "()Z",              (void*)Java_com_pvi_ap_reader_external_pdf_pdfReader_init               },
	{ "setViewPortSize",    "(II)Z",            (void*)Java_com_pvi_ap_reader_external_pdf_pdfReader_setViewPortSize    },
	{ "setCache",           "(Z)Z",             (void*)Java_com_pvi_ap_reader_external_pdf_pdfReader_setCache           },
	{ "open",               "(Ljava/lang/String;)Z", (void*)Java_com_pvi_ap_reader_external_pdf_pdfReader_open          },
	{ "close",              "()Z",              (void*)Java_com_pvi_ap_reader_external_pdf_pdfReader_close              },
	{ "getBitmapStuff",     "(I)[B",            (void*)Java_com_pvi_ap_reader_external_pdf_pdfReader_getBitmapStuff     },
	{ "getCurrentPage",     "(I)I",             (void*)Java_com_pvi_ap_reader_external_pdf_pdfReader_getCurrentPage     },
	{ "getPageCount",       "()I",              (void*)Java_com_pvi_ap_reader_external_pdf_pdfReader_getPageCount       },
	{ "getNaturalSize",     "(I)I",             (void*)Java_com_pvi_ap_reader_external_pdf_pdfReader_getNaturalSize     },
	{ "gotoPage",           "(IZ)Z",            (void*)Java_com_pvi_ap_reader_external_pdf_pdfReader_gotoPage           },
	{ "gotoPrevScreenPage", "()Z",              (void*)Java_com_pvi_ap_reader_external_pdf_pdfReader_gotoPrevScreenPage },
	{ "gotoNextScreenPage", "()Z",              (void*)Java_com_pvi_ap_reader_external_pdf_pdfReader_gotoNextScreenPage },
	{ "setFontSize",        "(D)Z",             (void*)Java_com_pvi_ap_reader_external_pdf_pdfReader_setFontSize        },
	{ "getChapterList",     "(I)[Lcom/pvi/ap/reader/external/pdf/ChapterInfo;", 
                                                (void*)Java_com_pvi_ap_reader_external_pdf_pdfReader_getChapterList     },
	{ "getChapterListLen",  "()I",              (void*)Java_com_pvi_ap_reader_external_pdf_pdfReader_getChapterListLen  },
	{ "hasTableOfContent",  "()Z",              (void*)Java_com_pvi_ap_reader_external_pdf_pdfReader_hasTableOfContent  },
	{ "autoCutMargin",      "(Z)Z",             (void*)Java_com_pvi_ap_reader_external_pdf_pdfReader_autoCutMargin      },
	{ "setFitMode",         "(I)Z",             (void*)Java_com_pvi_ap_reader_external_pdf_pdfReader_setFitMode         },
	{ "setViewPort",        "(IIII)Z",          (void*)Java_com_pvi_ap_reader_external_pdf_pdfReader_setViewPort        },
	{ "setPagingMode",      "(I)Z",             (void*)Java_com_pvi_ap_reader_external_pdf_pdfReader_setPagingMode      },
	{ "setScrollOffset",    "(I)Z",             (void*)Java_com_pvi_ap_reader_external_pdf_pdfReader_setScrollOffset    },
	{ "search",             "(Ljava/lang/String;ZII)I", (void*)Java_com_pvi_ap_reader_external_pdf_pdfReader_search     },
	{ "docReachHeadOrTail", "(Z)Z",             (void*)Java_com_pvi_ap_reader_external_pdf_pdfReader_docReachHeadOrTail }
    };

    if (vm->GetEnv(reinterpret_cast<void**>(&jniEnv), JNI_VERSION_1_2) != JNI_OK)
    {
        return JNI_ERR;
    }
    if(jniEnv == NULL)
    {
        return JNI_ERR;
    }
    jclass cls = jniEnv->FindClass(JNIREG_CLASS);
    if(cls == NULL)
    {
        return JNI_ERR;
    }

    jniEnv->RegisterNatives(cls, methods, sizeof(methods)/sizeof(methods[0]));

    return JNI_VERSION_1_2;
}

#endif
