package rocher.adrien.droidopenhomeplayer.Providers;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.openhome.net.device.DvDevice;
import org.openhome.net.device.IDvInvocation;
import org.openhome.net.device.providers.DvProviderAvOpenhomeOrgReceiver1;
import rocher.adrien.droidopenhomeplayer.Channel.ChannelPlayList;
import rocher.adrien.droidopenhomeplayer.Channel.ChannelSongcast;
import rocher.adrien.droidopenhomeplayer.Utils.Config;
import rocher.adrien.droidopenhomeplayer.Player.PlayManager;
import rocher.adrien.droidopenhomeplayer.Player.events.EventBase;
import rocher.adrien.droidopenhomeplayer.Player.events.EventReceiverStatusChanged;
//import org.rpi.songcast.ohz.OHZConnector;
import rocher.adrien.droidopenhomeplayer.Utils.Utils;

import org.openhome.net.device.DvDevice;
import org.openhome.net.device.providers.DvProviderAvOpenhomeOrgReceiver1;
import java.util.Observer;

public class PrvReceiver extends DvProviderAvOpenhomeOrgReceiver1 implements IDisposableDevice, Observer {
    private Logger log = Logger.getLogger(PrvReceiver.class);
    private boolean bPlay = false;
    private ChannelPlayList track = null;
    private String zoneID = "";

    public PrvReceiver(DvDevice dvDevice) {
        super(dvDevice);
    }

    @Override
    protected void stop(IDvInvocation paramIDvInvocation) {
        log.debug("Stop" + Utils.getLogText(paramIDvInvocation));
        stop();
    }

    private void stop() {
//        manager.stop(zoneID);
//        manager = null;
        PlayManager.getInstance().setStatus("Stopped","SONGCAST");
    }

    @Override
    public void update(Observable arg0, Object ev) {
        EventBase e = (EventBase) ev;
        switch (e.getType()) {
            case EVENTRECEIVERSTATUSCHANGED:
                EventReceiverStatusChanged ers = (EventReceiverStatusChanged) e;
                setStatus(ers.getStatus());
                break;
            case EVENTSTOPSONGCAST:
                stop();
                break;
        }

    }
    public void setStatus(String status) {
        setPropertyTransportState(status);
    }

    @Override
    public String getName() {
        return "Receiver";
    }
}
