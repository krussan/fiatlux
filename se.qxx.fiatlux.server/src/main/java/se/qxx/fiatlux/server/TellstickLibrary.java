package se.qxx.fiatlux.server;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Pointer;

public interface TellstickLibrary extends Library {

    // typedef void (WINAPI *TDDeviceEvent)(int deviceId, int method, const char *data, int callbackId, void *context);
    interface TDDeviceEvent extends Callback{
    	void invoke(int deviceId, int method, Pointer data, int callbackId, Pointer context);        
    }
    // TELLSTICK_API int WINAPI tdRegisterDeviceEvent( TDDeviceEvent eventFunction, void *context );
    public int tdRegisterDeviceEvent( TDDeviceEvent eventFunction, Pointer context );

    // typedef void (WINAPI *TDDeviceChangeEvent)(int deviceId, int changeEvent, int changeType, int callbackId, void *context); 
    interface TDDeviceChangeEvent extends Callback{
    	void invoke(int deviceId, int changeEvent, int changeType, int callbackId, Pointer context);        
    }
    // TELLSTICK_API int WINAPI tdRegisterDeviceChangeEvent( TDDeviceChangeEvent eventFunction, void *context);
    public int tdRegisterDeviceChangeEvent( TDDeviceChangeEvent eventFunction, Pointer context);

    // typedef void (WINAPI *TDRawDeviceEvent)(const char *data, int controllerId, int callbackId, void *context);
    interface TDRawDeviceEvent extends Callback{
    	void invoke(Pointer data, int controllerId, int callbackId, Pointer context);        
    }
    // TELLSTICK_API int WINAPI tdRegisterRawDeviceEvent( TDRawDeviceEvent eventFunction, void *context );
    public int tdRegisterRawDeviceEvent( TDRawDeviceEvent eventFunction, Pointer context );

    
    // TELLSTICK_API void WINAPI tdInit(void);
    public void tdInit();
    
    // TELLSTICK_API int WINAPI tdUnregisterCallback( int callbackId );
    public int tdUnregisterCallback( int callbackId );

    // TELLSTICK_API void WINAPI tdClose(void);
    public void tdClose();

    // TELLSTICK_API void WINAPI tdReleaseString(char *string);
    public void tdReleaseString(String string);
    
    // TELLSTICK_API int WINAPI tdTurnOn(int intDeviceId);
    public int tdTurnOn(int intDeviceId);

    // TELLSTICK_API int WINAPI tdTurnOff(int intDeviceId);
    public int tdTurnOff(int intDeviceId);

    // TELLSTICK_API int WINAPI tdBell(int intDeviceId);
    public int tdBell(int intDeviceId);

    // TELLSTICK_API int WINAPI tdDim(int intDeviceId, unsigned char level);
    public int tdDim(int intDeviceId, int level);

    // TELLSTICK_API int WINAPI tdExecute(int intDeviceId);
    public int tdExecute(int inDeviceId);
    // TELLSTICK_API int WINAPI tdUp(int intDeviceId);
    public int tdUp(int intDeviceId);
    // TELLSTICK_API int WINAPI tdDown(int intDeviceId);
    public int tdDown(int intDeviceId);
    // TELLSTICK_API int WINAPI tdStop(int intDeviceId);
    public int tdStop(int intDeviceId);
    
    // TELLSTICK_API int WINAPI tdLearn(int intDeviceId);
    public int tdLearn(int intDeviceId);

    // TELLSTICK_API int WINAPI tdMethods(int id, int methodsSupported);
    public int tdMethods(int id, int methodsSupported);

    // TELLSTICK_API int WINAPI tdLastSentCommand( int intDeviceId, int methodsSupported );
    public int tdLastSentCommand( int intDeviceId, int methodsSupported );

    // TELLSTICK_API int WINAPI tdGetNumberOfDevices();
    public int tdGetNumberOfDevices();

    // TELLSTICK_API int WINAPI tdGetDeviceId(int intDeviceIndex);
    public int tdGetDeviceId(int intDeviceIndex);

    // TELLSTICK_API int WINAPI tdGetDeviceType(int intDeviceId);
    public int tdGetDeviceType(int intDeviceId);

    // TELLSTICK_API char * WINAPI tdGetErrorString(int intErrorNo);
    public String tdGetErrorString(int intErrorNo);

    // TELLSTICK_API char * WINAPI tdGetName(int intDeviceId);
    public String tdGetName(int intDeviceId);

    // TELLSTICK_API bool WINAPI tdSetName(int intDeviceId, const char* chNewName);
    public boolean tdSetName(int intDeviceId, String chNewName);

    // TELLSTICK_API char * WINAPI tdGetProtocol(int intDeviceId);
    public String tdGetProtocol(int intDeviceId);

    // TELLSTICK_API bool WINAPI tdSetProtocol(int intDeviceId, const char* strProtocol);
    public boolean tdSetProtocol(int intDeviceId, String strProtocol);

    // TELLSTICK_API char * WINAPI tdGetModel(int intDeviceId);
    public String tdGetModel(int intDeviceId);

    // TELLSTICK_API bool WINAPI tdSetModel(int intDeviceId, const char *intModel);
    public boolean tdSetModel(int intDeviceId, String intModel);

    // TELLSTICK_API char * WINAPI tdGetDeviceParameter(int intDeviceId, const char *strName, const char *defaultValue);
    public String tdGetDeviceParameter(int intDeviceId, String strName, String defaultValue);

    // TELLSTICK_API bool WINAPI tdSetDeviceParameter(int intDeviceId, const char *strName, const char* strValue);
    public boolean tdSetDeviceParameter(int intDeviceId, String strName, String strValue);

    // TELLSTICK_API int WINAPI tdAddDevice();
    public int tdAddDevice();

    // TELLSTICK_API bool WINAPI tdRemoveDevice(int intDeviceId);
    public boolean tdRemoveDevice(int intDeviceId);

    // TELLSTICK_API int WINAPI tdSendRawCommand(const char *command, int reserved);
    public int tdSendRawCommand(String command, int reserved);

    // TELLSTICK_API void WINAPI tdConnectTellStickController(int vid, int pid, const char *serial);
    public void tdConnectTellStickController(int vid, int pid, String serial);

    // TELLSTICK_API void WINAPI tdDisconnectTellStickController(int vid, int pid, const char *serial);
    public void tdDisconnectTellStickController(int vid, int pid, String serial);

    public final int TELLSTICK_TURNON = 1;
    public final int TELLSTICK_TURNOFF = 2;
    public final int TELLSTICK_BELL = 4;
    public final int TELLSTICK_TOGGLE = 8;
    public final int TELLSTICK_DIM = 16;
    public final int TELLSTICK_LEARN = 32;
    public final int TELLSTICK_EXECUTE = 64;
    public final int TELLSTICK_UP = 128;
    public final int TELLSTICK_DOWN = 256;
    public final int TELLSTICK_STOP = 512;

    public final int TELLSTICK_TYPE_DEVICE = 1;
    public final int TELLSTICK_TYPE_GROUP = 2;
    public final int TELLSTICK_TYPE_SCENE = 3;
    
    public final int TELLSTICK_DEVICE_ADDED = 1;
    public final int TELLSTICK_DEVICE_CHANGED = 2;
    public final int TELLSTICK_DEVICE_REMOVED = 3;
    public final int TELLSTICK_DEVICE_STATE_CHANGED = 4;
    
    public final int TELLSTICK_CHANGE_NAME = 1;
    public final int TELLSTICK_CHANGE_PROTOCOL = 2;
    public final int TELLSTICK_CHANGE_MODEL = 3;
    public final int TELLSTICK_CHANGE_METHOD = 4;

}
