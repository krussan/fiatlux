package se.qxx.android.fiatlux;

import se.qxx.fiatlux.domain.FiatluxComm;

public interface DeviceUpdatedListener {
    void dataChanged(FiatluxComm.Device device);
}
