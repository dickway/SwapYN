package com.zzkj.structure.util.device;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.zzkj.structure.util.StringUtils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class AdvertisingIdHelper {

    @WorkerThread
    public static String getAdvertisingId(Context context) {
        String id = getAdvertisingIdFromLocal(context);
        if (StringUtils.isEmpty(id)) {
            id = getAdvertisingIdFromService(context);
        }
        return id;
    }

    @WorkerThread
    public static String getAdvertisingIdFromService(Context context) {
        try {
            String aifa = queryAdvertisingIdFromService(context);
            return aifa;
        } catch (Exception var2) {
            return null;
        }
    }

    @Nullable
    public static String getAdvertisingIdFromLocal(Context context) {
        try {
//            Object AdvertisingInfoObject = getAdvertisingInfoObject(context);
//            String playAdid = (String) invokeInstanceMethod(AdvertisingInfoObject, "getId", (Class[]) null);
//            return playAdid;
//            gaid = AdvertisingIdClient.getAdvertisingIdInfo(App.INSTANCE).id
            Class<?> AdvertisingIdClientObject = Class.forName("com.google.android.gms.ads.identifier.AdvertisingIdClient");
            Object getAdvertisingIdInfoObject = AdvertisingIdClientObject
                    .getMethod("getAdvertisingIdInfo", new Class[]{Context.class})
                    .invoke(null, context);
            assert getAdvertisingIdInfoObject != null;
            return (String) getAdvertisingIdInfoObject.getClass()
                    .getMethod("getId", (Class<?>[]) null)
                    .invoke(getAdvertisingIdInfoObject);
        } catch (Throwable var3) {
            return null;
        }
    }

//    private static Object getAdvertisingInfoObject(Context context) throws Exception {
//        return invokeStaticMethod("com.google.android.gms.ads.identifier.AdvertisingIdClient", "getAdvertisingIdInfo", new Class[]{Context.class}, context);
//    }
//
//    public static Object invokeStaticMethod(String className, String methodName, Class[] cArgs, Object... args) throws Exception {
//        Class classObject = Class.forName(className);
//        return invokeMethod(classObject, methodName, (Object) null, cArgs, args);
//    }
//
//    static Object invokeInstanceMethod(Object instance, String methodName, Class[] cArgs, Object... args) throws Exception {
//        Class classObject = instance.getClass();
//        return invokeMethod(classObject, methodName, instance, cArgs, args);
//    }
//
//
//    static Object invokeMethod(Class classObject, String methodName, Object instance, Class[] cArgs, Object... args) throws Exception {
//        Method methodObject = classObject.getMethod(methodName, cArgs);
//        if (methodObject == null) {
//            return null;
//        } else {
//            Object resultObject = methodObject.invoke(instance, args);
//            return resultObject;
//        }
//    }


    @SuppressLint("WrongConstant")
    static String queryAdvertisingIdFromService(Context context) {
        GoogleAdvertisingServiceConnection connection = new GoogleAdvertisingServiceConnection();
        Intent in = new Intent("com.google.android.gms.ads.identifier.service.START");
        in.setPackage("com.google.android.gms");
        if (context.bindService(in, connection, 1)) {
            String var4;
            try {
                GoogleAdvertisingInfo advertisingInfo = GoogleAdvertisingInfo.GoogleAdvertisingInfoBinder.Create(connection.getBinder());
                var4 = advertisingInfo.getId();
            } catch (Exception var8) {
                return "";
            } finally {
                context.unbindService(connection);
            }
            return var4;
        } else {
            return "";
        }
    }

    private interface GoogleAdvertisingInfo extends IInterface {
        String getId() throws RemoteException;

        boolean getEnabled(boolean var1) throws RemoteException;

        abstract class GoogleAdvertisingInfoBinder extends Binder implements GoogleAdvertisingInfo {
            public GoogleAdvertisingInfoBinder() {
            }

            public static GoogleAdvertisingInfo Create(IBinder binder) {
                if (binder == null) {
                    return null;
                } else {
                    IInterface localIInterface = binder.queryLocalInterface("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                    return (GoogleAdvertisingInfo) (localIInterface != null && localIInterface instanceof GoogleAdvertisingInfo ? (GoogleAdvertisingInfo) localIInterface : new GoogleAdvertisingInfoImplementation(binder));
                }
            }

            public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
                switch (code) {
                    case 1:
                        data.enforceInterface("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                        String str1 = this.getId();
                        reply.writeNoException();
                        reply.writeString(str1);
                        return true;
                    case 2:
                        data.enforceInterface("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                        boolean bool1 = 0 != data.readInt();
                        boolean bool2 = this.getEnabled(bool1);
                        reply.writeNoException();
                        reply.writeInt(bool2 ? 1 : 0);
                        return true;
                    default:
                        return super.onTransact(code, data, reply, flags);
                }
            }

            private static class GoogleAdvertisingInfoImplementation implements GoogleAdvertisingInfo {
                private IBinder _binder;

                GoogleAdvertisingInfoImplementation(IBinder binder) {
                    this._binder = binder;
                }

                public IBinder asBinder() {
                    return this._binder;
                }

                public String getId() throws RemoteException {
                    Parcel localParcel1 = Parcel.obtain();
                    Parcel localParcel2 = Parcel.obtain();

                    String str;
                    try {
                        localParcel1.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                        this._binder.transact(1, localParcel1, localParcel2, 0);
                        localParcel2.readException();
                        str = localParcel2.readString();
                    } finally {
                        localParcel2.recycle();
                        localParcel1.recycle();
                    }

                    return str;
                }

                public boolean getEnabled(boolean paramBoolean) throws RemoteException {
                    Parcel localParcel1 = Parcel.obtain();
                    Parcel localParcel2 = Parcel.obtain();

                    boolean bool;
                    try {
                        localParcel1.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                        localParcel1.writeInt(paramBoolean ? 1 : 0);
                        this._binder.transact(2, localParcel1, localParcel2, 0);
                        localParcel2.readException();
                        bool = 0 != localParcel2.readInt();
                    } finally {
                        localParcel2.recycle();
                        localParcel1.recycle();
                    }

                    return bool;
                }
            }
        }
    }

    private static class GoogleAdvertisingServiceConnection implements ServiceConnection {
        boolean _consumed;
        private final BlockingQueue<IBinder> _binderQueue;

        private GoogleAdvertisingServiceConnection() {
            this._consumed = false;
            this._binderQueue = new LinkedBlockingQueue();
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                this._binderQueue.put(service);
            } catch (InterruptedException var4) {
            }
        }

        public void onServiceDisconnected(ComponentName name) {
        }

        public IBinder getBinder() throws InterruptedException {
            if (this._consumed) {
                throw new IllegalStateException();
            } else {
                this._consumed = true;
                return (IBinder) this._binderQueue.take();
            }
        }
    }
}

