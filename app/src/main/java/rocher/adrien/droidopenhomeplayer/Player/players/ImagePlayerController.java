package rocher.adrien.droidopenhomeplayer.Player.players;

import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

import rocher.adrien.droidopenhomeplayer.Player.IPlayerController;
import rocher.adrien.droidopenhomeplayer.Player.events.EventBase;
import rocher.adrien.droidopenhomeplayer.Channel.ChannelBase;
import rocher.adrien.droidopenhomeplayer.Player.players.VideoViewPlayer;

import android.net.Uri;

public class ImagePlayerController extends Observable implements IPlayerController, Observer {

    private static Logger log = Logger.getLogger(ImagePlayerController.class);

    private ImageViewPlayer ImagePlayer;

    public ImagePlayerController() {
    }

    @Override
    public void preLoadTrack(ChannelBase  track) {
        //ImagePlayer.preLoadTrack(track);
    }

    @Override
    public void loaded() {
        // TODO Auto-generated method stub
    }

    @Override
    public void openFile(ChannelBase track) {
        // TODO Auto-generated method stub
    }

    @Override
    public void playThis(ChannelBase t, long v, boolean bMute) {
        Uri sourceUri = Uri.parse(t.getUri());

        ImagePlayer.getInstance().addObserver(this);
        ImagePlayer.getInstance().playTrack(t,v,bMute);
    }

    @Override
    public void pause(boolean bPause) {
        ImagePlayer.getInstance().pause(bPause);
    }

    @Override
    public void resume() {
        ImagePlayer.getInstance().resume();
    }

    @Override
    public void stop() {
        ImagePlayer.getInstance().stop();
    }

    @Override
    public void destroy() {
        //ImagePlayer.destroy();
        stop();
    }

    @Override
    public void setMute(boolean mute) {
        ImagePlayer.getInstance().setMute(mute);
    }

    @Override
    public void setVolume(long volume) {
        ImagePlayer.getInstance().setVolume(volume);
    }

    @Override
    public void seekAbsolute(long seconds) {
        ImagePlayer.getInstance().seekAbsolute(seconds);
    }

    @Override
    public boolean isPlaying() {
        return ImagePlayer.getInstance().isPlaying();
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public String getUniqueId() {
        //return ImagePlayer.getId();
        return null;
    }

    @Override
    public void update(Observable arg0, Object obj) {
        EventBase e = (EventBase)obj;
        setChanged();
        notifyObservers(obj);
    }

    public void show() {
        ImagePlayer.getInstance().show();
    }

    public void hide() {
        ImagePlayer.getInstance().hide();
    }
}
