package se.qxx.fiatlux.server;

import com.sun.jna.Pointer;

public class TellstickLibraryMock implements TellstickLibrary {
    @Override
    public int tdRegisterDeviceEvent(TDDeviceEvent eventFunction, Pointer context) {
        return 0;
    }

    @Override
    public int tdRegisterDeviceChangeEvent(TDDeviceChangeEvent eventFunction, Pointer context) {
        return 0;
    }

    @Override
    public int tdRegisterRawDeviceEvent(TDRawDeviceEvent eventFunction, Pointer context) {
        return 0;
    }

    @Override
    public void tdInit() {

    }

    @Override
    public int tdUnregisterCallback(int callbackId) {
        return 0;
    }

    @Override
    public void tdClose() {

    }

    @Override
    public void tdReleaseString(String string) {

    }

    @Override
    public int tdTurnOn(int intDeviceId) {
        return 0;
    }

    @Override
    public int tdTurnOff(int intDeviceId) {
        return 0;
    }

    @Override
    public int tdBell(int intDeviceId) {
        return 0;
    }

    @Override
    public int tdDim(int intDeviceId, int level) {
        return 0;
    }

    @Override
    public int tdExecute(int inDeviceId) {
        return 0;
    }

    @Override
    public int tdUp(int intDeviceId) {
        return 0;
    }

    @Override
    public int tdDown(int intDeviceId) {
        return 0;
    }

    @Override
    public int tdStop(int intDeviceId) {
        return 0;
    }

    @Override
    public int tdLearn(int intDeviceId) {
        return 0;
    }

    @Override
    public int tdMethods(int id, int methodsSupported) {
        return 0;
    }

    @Override
    public int tdLastSentCommand(int intDeviceId, int methodsSupported) {
        return 0;
    }

    @Override
    public int tdGetNumberOfDevices() {
        return 0;
    }

    @Override
    public int tdGetDeviceId(int intDeviceIndex) {
        return 0;
    }

    @Override
    public int tdGetDeviceType(int intDeviceId) {
        return 0;
    }

    @Override
    public String tdGetErrorString(int intErrorNo) {
        return null;
    }

    @Override
    public String tdGetName(int intDeviceId) {
        return null;
    }

    @Override
    public boolean tdSetName(int intDeviceId, String chNewName) {
        return false;
    }

    @Override
    public String tdGetProtocol(int intDeviceId) {
        return null;
    }

    @Override
    public boolean tdSetProtocol(int intDeviceId, String strProtocol) {
        return false;
    }

    @Override
    public String tdGetModel(int intDeviceId) {
        return null;
    }

    @Override
    public boolean tdSetModel(int intDeviceId, String intModel) {
        return false;
    }

    @Override
    public String tdGetDeviceParameter(int intDeviceId, String strName, String defaultValue) {
        return null;
    }

    @Override
    public boolean tdSetDeviceParameter(int intDeviceId, String strName, String strValue) {
        return false;
    }

    @Override
    public int tdAddDevice() {
        return 0;
    }

    @Override
    public boolean tdRemoveDevice(int intDeviceId) {
        return false;
    }

    @Override
    public int tdSendRawCommand(String command, int reserved) {
        return 0;
    }

    @Override
    public void tdConnectTellStickController(int vid, int pid, String serial) {

    }

    @Override
    public void tdDisconnectTellStickController(int vid, int pid, String serial) {

    }
}
