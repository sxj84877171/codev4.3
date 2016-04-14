/**
 * The interface class to use NetCacheService 
 * @author rd031
 *
 */

package com.pvi.ap.reader.service;

import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.httpclient.HttpException;

public interface INetCache extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.pvi.ap.reader.service.INetCache
{
private static final java.lang.String DESCRIPTOR = "com.pvi.ap.reader.service.INetCache";

private java.util.HashMap getCanPass(java.util.HashMap src) {
	java.util.Iterator iterator = src.keySet().iterator();
	java.util.HashMap ret = new java.util.HashMap();
	while (iterator.hasNext()) {
		String key = iterator.next().toString();
		Object o = src.get(key);
		if (o instanceof java.io.Serializable) {
			ret.put(key, o);
		}
	}
	return ret;
}

/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.pvi.ap.reader_service.service.INetCache interface,
 * generating a proxy if needed.
 */
public static com.pvi.ap.reader.service.INetCache asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.pvi.ap.reader.service.INetCache))) {
return ((com.pvi.ap.reader.service.INetCache)iin);
}
return new com.pvi.ap.reader.service.INetCache.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_GetNetImage:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
android.graphics.Bitmap _result = this.GetNetImage(_arg0);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getServerTimeAsString:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getServerTimeAsString();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getUserInfo:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getUserInfo(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_modifyUserInfo:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.modifyUserInfo(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_addFriend:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.addFriend(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getFriendList:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getFriendList(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getBlockList:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getBlockList(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getCatalogHomePage:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getCatalogHomePage(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getCatalogHomePage2:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getCatalogHomePage2(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getCatalogContent:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getCatalogContent(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getCatalogRank:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getCatalogRank(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getSpecifiedRank:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getSpecifiedRank(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getContentInfo:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getContentInfo(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getChapterList:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getChapterList(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getChapterInfo:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getChapterInfo(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getChapterImage:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getChapterImage(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getAuthorInfo:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getAuthorInfo(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getBookNewsList:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getBookNewsList(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getBookNewsInfo:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getBookNewsInfo(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getRecommendContentList:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getRecommendContentList(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getBlockContent:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getBlockContent(_arg0, _arg1);
} catch (Exception e) {
//	e.printStackTrace();
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getRankType:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getRankType(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_downloadContent:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.downloadContent(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_downloadFreeContent:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.downloadFreeContent(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getSubscriptionList:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getSubscriptionList(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getConsumeHistoryList:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getConsumeHistoryList(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getFascicleList:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getFascicleList(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getHandsetProperties:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getHandsetProperties(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_addSystemBookmark:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.addSystemBookmark(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_addUserBookmark:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.addUserBookmark(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_deleteAllSystemBookmark:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.deleteAllSystemBookmark(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_deleteAllUserBookmark:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.deleteAllUserBookmark(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_deleteBookmark:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.deleteBookmark(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_deleteContentBookmark:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.deleteContentBookmark(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_deleteMessage:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.deleteMessage(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getBeShelvesBookList:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getBeShelvesBookList(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getHandsetBroadcast:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getHandsetBroadcast(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getMessage:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getMessage(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getResources:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getResources(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getSystemParameter:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getSystemParameter(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getUserBookmark:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getUserBookmark(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_searchContent:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.searchContent(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_sendMessage:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.sendMessage(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getContentBookmark:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getContentBookmark(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getFavorite:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getFavorite(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_addFavorite:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.addFavorite(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_deleteFavorite:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.deleteFavorite(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_deleteAllFavorite:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.deleteAllFavorite(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getSystemBookmark:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getSystemBookmark(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_bookUpdate:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.bookUpdate(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getAllSerialChapters:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getAllSerialChapters(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_subscribeChapters:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.subscribeChapters(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_batchSubscribeNextChapters:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.batchSubscribeNextChapters(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getCatalogProductInfo:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getCatalogProductInfo(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_subscribeContent:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.subscribeContent(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_subscribeCatalog:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.subscribeCatalog(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getContentProductInfo:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getContentProductInfo(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_unbookUpdate:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.unbookUpdate(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getBookUpdateList:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getBookUpdateList(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getChaptersUrl:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getChaptersUrl(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_bookUpdateSet:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.bookUpdateSet(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_unsubscribeCatalog:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.unsubscribeCatalog(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getCatalogSubscriptionList:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getCatalogSubscriptionList(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getComment:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getComment(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_submitComment:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.submitComment(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_submitVote:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.submitVote(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_recommendedContent:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.recommendedContent(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_syncMessageState:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.syncMessageState(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_uploadReadRecord:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.uploadReadRecord(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_uploadClientLog:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.uploadClientLog(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_syncBookmark:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.syncBookmark(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_handsetAssociate:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.handsetAssociate(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_handsetAuthenticate:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.handsetAuthenticate(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getSMSVerifyCode:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getSMSVerifyCode(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_quit:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.quit(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_checkUpdate:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.checkUpdate(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_register:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.register(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getBindState:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getBindState(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_resend:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.resend(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getFriendInfo:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getFriendInfo(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_deleteFriend:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.deleteFriend(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getUnconfirmedFriendList:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getUnconfirmedFriendList(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getHandsetInfo:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getHandsetInfo(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_unbindHandset:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.unbindHandset(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getHandsetUserTicketInfo:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getHandsetUserTicketInfo(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_presentBook:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.presentBook(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getPresentBookInfo:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getPresentBookInfo(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_getPresentBookList:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.getPresentBookList(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_downloadContentAck:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.downloadContentAck(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_userOrderBook:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.userOrderBook(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_addUserLeaveWord:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.addUserLeaveWord(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
case TRANSACTION_submitCommentVote:
{
data.enforceInterface(DESCRIPTOR);
java.util.HashMap _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.HashMap _arg1;
cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg1 = data.readHashMap(cl);
java.util.HashMap _result;
try {
	_result = this.submitCommentVote(_arg0, _arg1);
} catch (Exception e) {
	_result = null;
}
reply.writeNoException();
reply.writeMap(getCanPass(_result));
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.pvi.ap.reader.service.INetCache
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
// type 1

public android.graphics.Bitmap GetNetImage(java.lang.String url)
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.graphics.Bitmap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(url);
mRemote.transact(Stub.TRANSACTION_GetNetImage, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.graphics.Bitmap.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String getServerTimeAsString()
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getServerTimeAsString, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public	java.util.Date getServerTimeAsDate() {
	String nowServerTime = getServerTimeAsString();
	java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	try {
		return df.parse(nowServerTime);
	} catch (java.text.ParseException e) {
		return new java.util.Date();
	}
}
public	long getServerTimeAsTimes() {
	java.util.Date nowServerTime = getServerTimeAsDate();
	return nowServerTime.getTime();
}

// type 2

public java.util.HashMap getUserInfo(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getUserInfo, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap modifyUserInfo(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_modifyUserInfo, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap addFriend(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_addFriend, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getFriendList(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getFriendList, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getBlockList(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getBlockList, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getCatalogHomePage(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getCatalogHomePage, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getCatalogHomePage2(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getCatalogHomePage2, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getCatalogContent(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getCatalogContent, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getCatalogRank(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getCatalogRank, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getSpecifiedRank(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getSpecifiedRank, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getContentInfo(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getContentInfo, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
private java.util.HashMap isHaveException(java.util.HashMap _result)
		throws HttpException, IOException {
	if ((_result != null) && _result.containsKey("Exception")) {
		Object e = _result.get("Exception");
		if (e instanceof org.apache.commons.httpclient.HttpException) {
			throw (org.apache.commons.httpclient.HttpException)e;
		} else if (e instanceof java.io.IOException) {
			throw (java.io.IOException)e;
		}else {
			throw new java.io.IOException("Unknow Excepiton");
		}
	}
	return _result;
}
public java.util.HashMap getChapterList(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getChapterList, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getChapterInfo(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getChapterInfo, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getChapterImage(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getChapterImage, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getAuthorInfo(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getAuthorInfo, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getBookNewsList(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getBookNewsList, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getBookNewsInfo(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getBookNewsInfo, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getRecommendContentList(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getRecommendContentList, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getBlockContent(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getBlockContent, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getRankType(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getRankType, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap downloadContent(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_downloadContent, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap downloadFreeContent(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_downloadFreeContent, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getSubscriptionList(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getSubscriptionList, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getConsumeHistoryList(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getConsumeHistoryList, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getFascicleList(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getFascicleList, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getHandsetProperties(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getHandsetProperties, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap addSystemBookmark(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_addSystemBookmark, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap addUserBookmark(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_addUserBookmark, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap deleteAllSystemBookmark(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_deleteAllSystemBookmark, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap deleteAllUserBookmark(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_deleteAllUserBookmark, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap deleteBookmark(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_deleteBookmark, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap deleteContentBookmark(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_deleteContentBookmark, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap deleteMessage(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_deleteMessage, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getBeShelvesBookList(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getBeShelvesBookList, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getHandsetBroadcast(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getHandsetBroadcast, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getMessage(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getMessage, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getResources(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getResources, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getSystemParameter(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getSystemParameter, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getUserBookmark(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getUserBookmark, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap searchContent(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_searchContent, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap sendMessage(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_sendMessage, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getContentBookmark(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getContentBookmark, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getFavorite(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getFavorite, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap addFavorite(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_addFavorite, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap deleteFavorite(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_deleteFavorite, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap deleteAllFavorite(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_deleteAllFavorite, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getSystemBookmark(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getSystemBookmark, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap bookUpdate(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_bookUpdate, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getAllSerialChapters(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getAllSerialChapters, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap subscribeChapters(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_subscribeChapters, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap batchSubscribeNextChapters(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_batchSubscribeNextChapters, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getCatalogProductInfo(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getCatalogProductInfo, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap subscribeContent(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_subscribeContent, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap subscribeCatalog(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_subscribeCatalog, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getContentProductInfo(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getContentProductInfo, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap unbookUpdate(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_unbookUpdate, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getBookUpdateList(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getBookUpdateList, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getChaptersUrl(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getChaptersUrl, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap bookUpdateSet(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_bookUpdateSet, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap unsubscribeCatalog(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_unsubscribeCatalog, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getCatalogSubscriptionList(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getCatalogSubscriptionList, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getComment(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getComment, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap submitComment(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_submitComment, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap submitVote(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_submitVote, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap recommendedContent(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_recommendedContent, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap syncMessageState(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_syncMessageState, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap uploadReadRecord(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_uploadReadRecord, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap uploadClientLog(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_uploadClientLog, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap syncBookmark(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_syncBookmark, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap handsetAssociate(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_handsetAssociate, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap handsetAuthenticate(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_handsetAuthenticate, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getSMSVerifyCode(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getSMSVerifyCode, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap quit(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_quit, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap checkUpdate(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_checkUpdate, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap register(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_register, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getBindState(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getBindState, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap resend(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_resend, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getFriendInfo(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getFriendInfo, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap deleteFriend(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_deleteFriend, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getUnconfirmedFriendList(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getUnconfirmedFriendList, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getHandsetInfo(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getHandsetInfo, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap unbindHandset(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_unbindHandset, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getHandsetUserTicketInfo(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getHandsetUserTicketInfo, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap presentBook(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_presentBook, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getPresentBookInfo(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getPresentBookInfo, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap getPresentBookList(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_getPresentBookList, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap downloadContentAck(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_downloadContentAck, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap userOrderBook(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_userOrderBook, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap addUserLeaveWord(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_addUserLeaveWord, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
public java.util.HashMap submitCommentVote(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.HashMap _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(ahmHeaderMap);
_data.writeMap(ahmNamePair);
mRemote.transact(Stub.TRANSACTION_submitCommentVote, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
} catch (android.os.RemoteException e) {
	_result = null;
}
finally {
_reply.recycle();
_data.recycle();
}
_result = isHaveException(_result);
return _result;
}
}
static final int TRANSACTION_GetNetImage = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_getServerTimeAsString = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_getUserInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_modifyUserInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_addFriend = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_getFriendList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_getBlockList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_getCatalogHomePage = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_getCatalogHomePage2 = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_getCatalogContent = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_getCatalogRank = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_getSpecifiedRank = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_getContentInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
static final int TRANSACTION_getChapterList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
static final int TRANSACTION_getChapterInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
static final int TRANSACTION_getChapterImage = (android.os.IBinder.FIRST_CALL_TRANSACTION + 15);
static final int TRANSACTION_getAuthorInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 16);
static final int TRANSACTION_getBookNewsList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 17);
static final int TRANSACTION_getBookNewsInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 18);
static final int TRANSACTION_getRecommendContentList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 19);
static final int TRANSACTION_getBlockContent = (android.os.IBinder.FIRST_CALL_TRANSACTION + 20);
static final int TRANSACTION_getRankType = (android.os.IBinder.FIRST_CALL_TRANSACTION + 21);
static final int TRANSACTION_downloadContent = (android.os.IBinder.FIRST_CALL_TRANSACTION + 22);
static final int TRANSACTION_downloadFreeContent = (android.os.IBinder.FIRST_CALL_TRANSACTION + 23);
static final int TRANSACTION_getSubscriptionList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 24);
static final int TRANSACTION_getConsumeHistoryList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 25);
static final int TRANSACTION_getFascicleList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 26);
static final int TRANSACTION_getHandsetProperties = (android.os.IBinder.FIRST_CALL_TRANSACTION + 27);
static final int TRANSACTION_addSystemBookmark = (android.os.IBinder.FIRST_CALL_TRANSACTION + 28);
static final int TRANSACTION_addUserBookmark = (android.os.IBinder.FIRST_CALL_TRANSACTION + 29);
static final int TRANSACTION_deleteAllSystemBookmark = (android.os.IBinder.FIRST_CALL_TRANSACTION + 30);
static final int TRANSACTION_deleteAllUserBookmark = (android.os.IBinder.FIRST_CALL_TRANSACTION + 31);
static final int TRANSACTION_deleteBookmark = (android.os.IBinder.FIRST_CALL_TRANSACTION + 32);
static final int TRANSACTION_deleteContentBookmark = (android.os.IBinder.FIRST_CALL_TRANSACTION + 33);
static final int TRANSACTION_deleteMessage = (android.os.IBinder.FIRST_CALL_TRANSACTION + 34);
static final int TRANSACTION_getBeShelvesBookList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 35);
static final int TRANSACTION_getHandsetBroadcast = (android.os.IBinder.FIRST_CALL_TRANSACTION + 36);
static final int TRANSACTION_getMessage = (android.os.IBinder.FIRST_CALL_TRANSACTION + 37);
static final int TRANSACTION_getResources = (android.os.IBinder.FIRST_CALL_TRANSACTION + 38);
static final int TRANSACTION_getSystemParameter = (android.os.IBinder.FIRST_CALL_TRANSACTION + 39);
static final int TRANSACTION_getUserBookmark = (android.os.IBinder.FIRST_CALL_TRANSACTION + 40);
static final int TRANSACTION_searchContent = (android.os.IBinder.FIRST_CALL_TRANSACTION + 41);
static final int TRANSACTION_sendMessage = (android.os.IBinder.FIRST_CALL_TRANSACTION + 42);
static final int TRANSACTION_getContentBookmark = (android.os.IBinder.FIRST_CALL_TRANSACTION + 43);
static final int TRANSACTION_getFavorite = (android.os.IBinder.FIRST_CALL_TRANSACTION + 44);
static final int TRANSACTION_addFavorite = (android.os.IBinder.FIRST_CALL_TRANSACTION + 45);
static final int TRANSACTION_deleteFavorite = (android.os.IBinder.FIRST_CALL_TRANSACTION + 46);
static final int TRANSACTION_deleteAllFavorite = (android.os.IBinder.FIRST_CALL_TRANSACTION + 47);
static final int TRANSACTION_getSystemBookmark = (android.os.IBinder.FIRST_CALL_TRANSACTION + 48);
static final int TRANSACTION_bookUpdate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 49);
static final int TRANSACTION_getAllSerialChapters = (android.os.IBinder.FIRST_CALL_TRANSACTION + 50);
static final int TRANSACTION_subscribeChapters = (android.os.IBinder.FIRST_CALL_TRANSACTION + 51);
static final int TRANSACTION_batchSubscribeNextChapters = (android.os.IBinder.FIRST_CALL_TRANSACTION + 52);
static final int TRANSACTION_getCatalogProductInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 53);
static final int TRANSACTION_subscribeContent = (android.os.IBinder.FIRST_CALL_TRANSACTION + 54);
static final int TRANSACTION_subscribeCatalog = (android.os.IBinder.FIRST_CALL_TRANSACTION + 55);
static final int TRANSACTION_getContentProductInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 56);
static final int TRANSACTION_unbookUpdate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 57);
static final int TRANSACTION_getBookUpdateList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 58);
static final int TRANSACTION_getChaptersUrl = (android.os.IBinder.FIRST_CALL_TRANSACTION + 59);
static final int TRANSACTION_bookUpdateSet = (android.os.IBinder.FIRST_CALL_TRANSACTION + 60);
static final int TRANSACTION_unsubscribeCatalog = (android.os.IBinder.FIRST_CALL_TRANSACTION + 61);
static final int TRANSACTION_getCatalogSubscriptionList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 62);
static final int TRANSACTION_getComment = (android.os.IBinder.FIRST_CALL_TRANSACTION + 63);
static final int TRANSACTION_submitComment = (android.os.IBinder.FIRST_CALL_TRANSACTION + 64);
static final int TRANSACTION_submitVote = (android.os.IBinder.FIRST_CALL_TRANSACTION + 65);
static final int TRANSACTION_recommendedContent = (android.os.IBinder.FIRST_CALL_TRANSACTION + 66);
static final int TRANSACTION_syncMessageState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 67);
static final int TRANSACTION_uploadReadRecord = (android.os.IBinder.FIRST_CALL_TRANSACTION + 68);
static final int TRANSACTION_uploadClientLog = (android.os.IBinder.FIRST_CALL_TRANSACTION + 69);
static final int TRANSACTION_syncBookmark = (android.os.IBinder.FIRST_CALL_TRANSACTION + 70);
static final int TRANSACTION_handsetAssociate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 71);
static final int TRANSACTION_handsetAuthenticate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 72);
static final int TRANSACTION_getSMSVerifyCode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 73);
static final int TRANSACTION_quit = (android.os.IBinder.FIRST_CALL_TRANSACTION + 74);
static final int TRANSACTION_checkUpdate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 75);
static final int TRANSACTION_register = (android.os.IBinder.FIRST_CALL_TRANSACTION + 76);
static final int TRANSACTION_getBindState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 77);
static final int TRANSACTION_resend = (android.os.IBinder.FIRST_CALL_TRANSACTION + 78);
static final int TRANSACTION_getFriendInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 79);
static final int TRANSACTION_deleteFriend = (android.os.IBinder.FIRST_CALL_TRANSACTION + 80);
static final int TRANSACTION_getUnconfirmedFriendList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 81);
static final int TRANSACTION_getHandsetInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 82);
static final int TRANSACTION_unbindHandset = (android.os.IBinder.FIRST_CALL_TRANSACTION + 83);
static final int TRANSACTION_getHandsetUserTicketInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 84);
static final int TRANSACTION_presentBook = (android.os.IBinder.FIRST_CALL_TRANSACTION + 85);
static final int TRANSACTION_getPresentBookInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 86);
static final int TRANSACTION_getPresentBookList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 87);
static final int TRANSACTION_downloadContentAck = (android.os.IBinder.FIRST_CALL_TRANSACTION + 88);
static final int TRANSACTION_userOrderBook = (android.os.IBinder.FIRST_CALL_TRANSACTION + 89);
static final int TRANSACTION_addUserLeaveWord = (android.os.IBinder.FIRST_CALL_TRANSACTION + 90);
static final int TRANSACTION_submitCommentVote = (android.os.IBinder.FIRST_CALL_TRANSACTION + 91);
}
// type 1

public android.graphics.Bitmap GetNetImage(java.lang.String url);
public java.lang.String getServerTimeAsString();
public	java.util.Date getServerTimeAsDate()  ;
public	long getServerTimeAsTimes()  ;
// type 2

public java.util.HashMap getUserInfo(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap modifyUserInfo(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap addFriend(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getFriendList(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getBlockList(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getCatalogHomePage(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getCatalogHomePage2(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getCatalogContent(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getCatalogRank(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getSpecifiedRank(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getContentInfo(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getChapterList(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getChapterInfo(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getChapterImage(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getAuthorInfo(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getBookNewsList(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getBookNewsInfo(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getRecommendContentList(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getBlockContent(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair)  throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getRankType(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap downloadContent(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap downloadFreeContent(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getSubscriptionList(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getConsumeHistoryList(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getFascicleList(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getHandsetProperties(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap addSystemBookmark(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap addUserBookmark(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap deleteAllSystemBookmark(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap deleteAllUserBookmark(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap deleteBookmark(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap deleteContentBookmark(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap deleteMessage(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getBeShelvesBookList(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getHandsetBroadcast(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getMessage(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getResources(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getSystemParameter(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getUserBookmark(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap searchContent(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap sendMessage(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getContentBookmark(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getFavorite(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap addFavorite(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap deleteFavorite(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap deleteAllFavorite(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getSystemBookmark(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap bookUpdate(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getAllSerialChapters(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap subscribeChapters(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap batchSubscribeNextChapters(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getCatalogProductInfo(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap subscribeContent(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap subscribeCatalog(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getContentProductInfo(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap unbookUpdate(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getBookUpdateList(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getChaptersUrl(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap bookUpdateSet(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap unsubscribeCatalog(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getCatalogSubscriptionList(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getComment(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap submitComment(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap submitVote(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap recommendedContent(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap syncMessageState(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap uploadReadRecord(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap uploadClientLog(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap syncBookmark(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap handsetAssociate(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap handsetAuthenticate(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getSMSVerifyCode(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap quit(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap checkUpdate(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap register(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getBindState(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap resend(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getFriendInfo(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap deleteFriend(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getUnconfirmedFriendList(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getHandsetInfo(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap unbindHandset(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getHandsetUserTicketInfo(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap presentBook(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getPresentBookInfo(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap getPresentBookList(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap downloadContentAck(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap userOrderBook(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap addUserLeaveWord(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
public java.util.HashMap submitCommentVote(java.util.HashMap ahmHeaderMap, java.util.HashMap ahmNamePair) throws  org.apache.commons.httpclient.HttpException, java.io.IOException;
}
