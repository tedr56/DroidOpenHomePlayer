package rocher.adrien.droidopenhomeplayer.Providers;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CopyOnWriteArrayList;

import org.openhome.net.device.DvDevice;
import org.openhome.net.device.providers.DvProviderAvOpenhomeOrgPlaylist1;

import rocher.adrien.droidopenhomeplayer.Utils.Config;
import rocher.adrien.droidopenhomeplayer.Providers.IDisposableDevice;


public class PrvPlayList extends DvProviderAvOpenhomeOrgPlaylist1 implements IDisposableDevice {
    private Logger log = Logger.getLogger(PrvPlayList.class);

    public PrvPlayList(DvDevice iDevice) {
        super(iDevice);
        log.debug("Creating custom OpenHome Playlist service");

//        plw = new PlayListWriter();
//        plw.start();
        enablePropertyTransportState();
        enablePropertyRepeat();
        enablePropertyShuffle();
        enablePropertyId();
        enablePropertyTracksMax();
        enablePropertyProtocolInfo();
        enablePropertyIdArray();

        byte[] array = new byte[0];
        setPropertyId(0);
        setPropertyProtocolInfo(Config.getInstance().getProtocolInfo());
        setPropertyRepeat(false);
        setPropertyShuffle(false);
        setPropertyTracksMax(1000);
        setPropertyTransportState("");
        setPropertyIdArray(array);

        enableActionPlay();
        enableActionPause();
        enableActionStop();
        enableActionNext();
        enableActionPrevious();
        enableActionSetRepeat();
        enableActionRepeat();
        enableActionSetShuffle();
        enableActionShuffle();
        enableActionSeekSecondAbsolute();
        enableActionSeekSecondRelative();
        enableActionSeekId();
        enableActionSeekIndex();
        enableActionTransportState();
        enableActionId();
        enableActionRead();
        enableActionReadList();
        enableActionInsert();
        enableActionDeleteId();
        enableActionDeleteAll();
        enableActionTracksMax();
        enableActionIdArray();
        enableActionIdArrayChanged();
        enableActionProtocolInfo();
//        PlayManager.getInstance().observePlayListEvents(this);
//        loadPlayList();
    }


    @Override
    public String getName() {
        return "PlayList";
    }
}
