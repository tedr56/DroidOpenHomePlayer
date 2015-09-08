package rocher.adrien.droidopenhomeplayer.Providers;

import org.openhome.net.device.DvDevice;
import org.openhome.net.device.providers.DvProviderUpnpOrgAVTransport1;

import java.util.Observable;
import java.util.Observer;

public class PrvAvTransport extends DvProviderUpnpOrgAVTransport1 implements Observer, IDisposableDevice {

    public PrvAvTransport(DvDevice dvDevice) {
        super(dvDevice);
    }

    @Override
    public void update(Observable arg0, Object ev) {
    }

    @Override
    public String getName() {
        return "AVTransport";
    }
}
