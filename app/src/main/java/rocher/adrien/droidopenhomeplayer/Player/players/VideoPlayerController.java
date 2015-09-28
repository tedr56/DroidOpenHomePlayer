package rocher.adrien.droidopenhomeplayer.Player.players;

import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

import rocher.adrien.droidopenhomeplayer.Player.IPlayerController;
import rocher.adrien.droidopenhomeplayer.Player.events.EventBase;
import rocher.adrien.droidopenhomeplayer.Channel.ChannelBase;
import rocher.adrien.droidopenhomeplayer.Player.players.VideoViewPlayer;

import android.net.Uri;

public class VideoPlayerController extends Observable implements IPlayerController, Observer {

    private static Logger log = Logger.getLogger(VideoPlayerController.class);

    //private VideoView VideoPlayer = null;
    //private VideoPlayerActivity VideoPlayer = null;

    private VideoViewPlayer VideoPlayer;

    public VideoPlayerController() {
    }

    @Override
    public void preLoadTrack(ChannelBase  track) {
        //VideoPlayer.preLoadTrack(track);
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

        //VideoPlayer = VideoViewPlayer.getInstance();
        VideoPlayer.getInstance().addObserver(this);
        VideoPlayer.getInstance().playTrack(t,v,bMute);
    }

    @Override
    public void pause(boolean bPause) {
        VideoPlayer.getInstance().pause(bPause);
    }

    @Override
    public void resume() {
        VideoPlayer.getInstance().resume();
    }

    @Override
    public void stop() {
        VideoPlayer.getInstance().stop();
    }

    @Override
    public void destroy() {
        //VideoPlayer.destroy();
        stop();
    }

    @Override
    public void setMute(boolean mute) {
        VideoPlayer.getInstance().setMute(mute);
    }

    @Override
    public void setVolume(long volume) {
        VideoPlayer.getInstance().setVolume(volume);
    }

    @Override
    public void seekAbsolute(long seconds) {
        VideoPlayer.getInstance().seekAbsolute(seconds);
    }

    @Override
    public boolean isPlaying() {
        return VideoPlayer.getInstance().isPlaying();
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public String getUniqueId() {
        //return VideoPlayer.getId();
        return null;
    }

    @Override
    public void update(Observable arg0, Object obj) {
        EventBase e = (EventBase)obj;
        setChanged();
        notifyObservers(obj);
    }

    public void show() {
        VideoPlayer.getInstance().show();
    }

    public void hide() {
        VideoPlayer.getInstance().hide();
    }
}
