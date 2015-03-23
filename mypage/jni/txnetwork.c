#include <android_log.h>
#include "crypt/encrypt.h"
#include "crypt/add.h"
#include "crypt/aes.h"
/*
 ******************************************************************************
 ** Main Program.
 ******************************************************************************
 */

char my_private_key[512] = { 0 };

int DoesMyPackageExist()
{
	if(-1 == access("/data/data/com.txnetwork.mypage", F_OK))
	{
		return 0;
	}
	return 1;
}
/**
 * 加密
 */
jstring Java_com_txnetwork_mypage_utils_TxNetworkPool_ECBEncryptTx(
		JNIEnv* env, jobject thiz, jstring str) {
	LOGI("Java_com_example_crypttest_MainActivity_ECBEncrypt entered!");
	const char* src_buff = (*env)->GetStringUTFChars(env, str, NULL);
	int plain_text_len = strlen(src_buff);
	int cipher_text_size = plain_text_len * 3 + 1;
	if(cipher_text_size <=32)
	{
		cipher_text_size = 256;
	}
	unsigned char* cipherText = (unsigned char*)malloc(cipher_text_size);
	if(!cipherText)
	{
		(*env)->ReleaseStringUTFChars(env, str, src_buff);
		return (*env)->NewStringUTF(env, "");
	}

	memset(cipherText, 0, cipher_text_size);
	encrypt_e(cipherText, src_buff, my_private_key);
	jstring cipherstr = (*env)->NewStringUTF(env, cipherText);

	if(cipherText)
	{
		free(cipherText);
		cipherText = NULL;
	}
	(*env)->ReleaseStringUTFChars(env, str, src_buff);
	return cipherstr;
}

/**
 * 解密
 */
jstring Java_com_txnetwork_mypage_utils_TxNetworkPool_ECBDecryptTx(
		JNIEnv* env, jobject thiz, jstring str) {
	LOGI("Java_com_example_crypttest_MainActivity_ECBDecrypt entered!");

	const char* cipher_text = (*env)->GetStringUTFChars(env, str, NULL);
	int cipher_text_len = strlen(cipher_text);
	if (!cipher_text_len) {
		(*env)->ReleaseStringUTFChars(env, str, cipher_text);
		return (*env)->NewStringUTF(env, "");
	}

	int plain_text_size = cipher_text_len + 1;
	if(32>=plain_text_size)
	{
		plain_text_size = 256;
	}
	unsigned char* plain_text = (unsigned char*)malloc(plain_text_size);
	memset(plain_text, 0, plain_text_size);
	decrypt_e(plain_text, cipher_text, my_private_key);
	jstring plainstr = (*env)->NewStringUTF(env, plain_text);

	LOGI("================0 my_private_key is: %s, plain_text_size is: %d, plain_text is %s, cipher_text is: %s", my_private_key, plain_text_size, plain_text, cipher_text);
	if(plain_text)
	{
		free(plain_text);
		plain_text = NULL;
	}

	(*env)->ReleaseStringUTFChars(env, str, cipher_text);
	LOGI("Java_com_example_crypttest_MainActivity_ECBDecrypt left!");
	return plainstr;
}
/**
 * key解密
 */
//jstring
jboolean Java_com_txnetwork_mypage_utils_TxNetworkPool_CBCDecryptTx(
		JNIEnv* env, jobject thiz, jstring str) {
	LOGI("Java_com_example_crypttest_MainActivity_CBCDecrypt entered!");
	if(!DoesMyPackageExist()) return 0;
	const char* wankey = (*env)->GetStringUTFChars(env, str, NULL);
	int srclen = strlen(wankey);
	if (44 != srclen) {
		(*env)->ReleaseStringUTFChars(env, str, wankey);
		return 0;
	}

	unsigned char localkey[256] = {0};
	//密钥转换 base64->base64
	convert(localkey, wankey);
	memset(my_private_key, 0, 512);
	strcpy(my_private_key, localkey);
	LOGI("================localkey is: %s, my_private_key is: %s", localkey, my_private_key);
	(*env)->ReleaseStringUTFChars(env, str, wankey);
	LOGI("Java_com_example_crypttest_MainActivity_CBCDecrypt left!");
	return 1;
}


/**
 * JAVA层获取C层解密后又重新加密的初始化密钥
 */
jstring Java_com_txnetwork_mypage_utils_TxNetworkPool_GetLocalKey(
		JNIEnv* env, jobject thiz) {
	return (*env)->NewStringUTF(env, my_private_key);
}

/**
 * 从JAVA层得到解密后又重新加密的初始化密钥
 */
jboolean Java_com_txnetwork_mypage_utils_TxNetworkPool_SetLocalKey(JNIEnv* env, jobject thiz, jstring localkeystr)
{
	const char* localkey = (*env)->GetStringUTFChars(env, localkeystr, NULL);
	if(0 == strlen(localkey))
	{
		(*env)->ReleaseStringUTFChars(env, localkeystr, localkey);
		return 0;
	}
	memset(my_private_key, 0, 512);
	strcpy(my_private_key, localkey);
	(*env)->ReleaseStringUTFChars(env, localkeystr, localkey);
	return 1;
}
