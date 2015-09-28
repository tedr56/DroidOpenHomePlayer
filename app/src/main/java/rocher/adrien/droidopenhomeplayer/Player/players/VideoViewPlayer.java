package rocher.adrien.droidopenhomeplayer.Player.players;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.VideoView;

import java.io.IOException;
import java.util.Observable;

import java.util.Observer;

import org.apache.log4j.Logger;
import rocher.adrien.droidopenhomeplayer.Channel.ChannelBase;
import rocher.adrien.droidopenhomeplayer.Player.IPlayer;
import rocher.adrien.droidopenhomeplayer.Player.TrackInfo;
import rocher.adrien.droidopenhomeplayer.Player.events.EventBase;
import rocher.adrien.droidopenhomeplayer.Player.events.EventDurationUpdate;
import rocher.adrien.droidopenhomeplayer.Player.events.EventFinishedCurrentTrack;
import rocher.adrien.droidopenhomeplayer.Player.events.EventStatusChanged;
import rocher.adrien.droidopenhomeplayer.Player.events.EventTimeUpdate;
import rocher.adrien.droidopenhomeplayer.Player.events.EventTrackChanged;
import rocher.adrien.droidopenhomeplayer.Player.events.EventUpdateTrackMetaText;
import rocher.adrien.droidopenhomeplayer.R;
import rocher.adrien.droidopenhomeplayer.Utils.Config;
//import rocher.adrien.droidopenhomeplayer.Radio.parsers.FileParser;


public class VideoViewPlayer extends Observable implements IPlayer, Observer{

    private Logger log = Logger.getLogger(VideoViewPlayer.class);

    private String mUriPath;

    private boolean bPaused = false;

    private boolean bPlaying = false;

    // private boolean bPreLoad = false;
    private boolean bLoading = false;
    private TrackInfo mTrackInfo = null;

    private long volume = 100;

    private String uniqueId = "";
    private ChannelBase current_track = null;

    private boolean bMute;//Used to mute when playing a track

    private boolean mute = false; //Used to keep track of mute status
    private VideoView mVideoView = null;

    private MediaPlayer mMediaPlayer = null;

    //static private Handler timeHandler = new Handler();
    static private VideoViewPlayer instance = null;
    private Uri mUri;

    private VideoViewPlayer() {
        initVideoView();
    }

    public static VideoViewPlayer getInstance() {
        if (instance == null) {
            instance = new VideoViewPlayer();
        }
        return instance;
    }

    private void initVideoView() {
        AppCompatActivity main = Config.getInstance().getActivity();

        View v = main.getWindow().getDecorView().findViewById(android.R.id.content);
        mVideoView = (VideoView) v.findViewById(R.id.videozone);
//        mVideoView.setOnPreparedListener(
//                new MediaPlayer.OnPreparedListener() {
//                    @Override
//                    public void onPrepared(MediaPlayer mp) {
//                        mMediaPlayer = mp;
//                    }
//                }
//        );
        mVideoView.setOnInfoListener(
                new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        //if (mMediaPlayer == null) { mMediaPlayer = mp; }
                        mMediaPlayer = mp;
                        Log.d("MPinfo", "MediaPlayer.OnInfoListener : " + what);
                        Log.d("MPinfo", "MediaPlayer.OnInfoListenerX: " + extra);
                        switch (what) {
                            case 3:
                                startPlaying();
                                MediaExtractor mx = new MediaExtractor();
                                try {
                                    mx.setDataSource(mUriPath);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    return false;
                                }
                                int numTracks = mx.getTrackCount();
                                for (int i = 0; i < numTracks; ++i) {
                                    MediaFormat mf = mx.getTrackFormat(i);
                                    String mime = mf.getString(MediaFormat.KEY_MIME);
                                    Log.d("MPinfo", "MediaPlayer.OnInfoListener : " + mime);
                                    String mimeType = mime.split("/")[0];
                                    if (mimeType.equals("audio")) {
                                        int Duration = mVideoView.getDuration() / 1000;
                                        mTrackInfo.setDuration(Duration);

                                        EventDurationUpdate e = new EventDurationUpdate();
                                        e.setDuration(mTrackInfo.getDuration());
                                        fireEvent(e);

                                        mTrackInfo.setBitDepth((long) mf.getInteger(MediaFormat.KEY_SAMPLE_RATE));
                                        mTrackInfo.setSampleRate((long) mf.getInteger(MediaFormat.KEY_SAMPLE_RATE));
                                        mTrackInfo.setBitrate((long) mf.getInteger(MediaFormat.KEY_SAMPLE_RATE));

                                        mTrackInfo.setCodec(mime);

                                        EventTrackChanged ev = new EventTrackChanged();
                                        ev.setTrack(current_track);
                                        fireEvent(ev);

                                    }
                                }
                                mVideoView.postDelayed(getTime, 1000);

                                //seekHandler.postDelayed(getTime, 1000);

                                break;
                            case 701:
                                EventStatusChanged ev = new EventStatusChanged();
                                ev.setStatus("Buffering");
                                fireEvent(ev);

                                break;
                            case 702:

                                int i = 0;
                                break;
                        }
                        return true;
                    }
                }
        );
        mVideoView.setOnCompletionListener(
                new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mMediaPlayer = mp;
                        EventFinishedCurrentTrack ev = new EventFinishedCurrentTrack();
                        fireEvent(ev);
                    }
                }
        );
    }

    /***
     * Plays the Custom Track
     *
     * @param track
     * @return
     */
    public boolean playTrack(ChannelBase track, long volume, boolean mute) {
        if (mVideoView == null) {
            initVideoView();
        }

        uniqueId = track.getUniqueId();
        this.volume = volume;
        this.bMute = mute;
        current_track = track;
        log.info("Playing track: " + uniqueId + " (" + track.getTitle() + ")");

        String url = track.getUri();
        mUriPath = url;
        mTrackInfo = new TrackInfo();
        mTrackInfo.addObserver(this);
        Uri uri = Uri.parse(url);
        mUri = uri;
        try {
            mVideoView.post(playVideo);

        } catch (Exception e) {
            log.error("Error playing track: " + uniqueId + " (" + track.getTitle() + ")");
        }
        return true;
    }

    Runnable playVideo = new Runnable() {
        @Override
        public void run() {
            mVideoView.setVideoURI(mUri);
            mVideoView.requestFocus();
            mVideoView.start();
        }
    };

    /***
     * PreLoad the Track ready for when the current Track ends
     */
    public void preLoadTrack(ChannelBase track) {
    }

    /***
     * Used to start a pre loaded track
     */
    public void startTrack() {
        startPlaying();
    }

    /***
     * If the Player is Playing we can change tracks.
     *
     * @param t
     */
    public void openFile(ChannelBase t) {
    }

    public synchronized void startPlaying() {
        log.debug("Starting to Play: " + uniqueId);
        setVolume(volume);
        if (bMute) {
            setMute(bMute);
        }
        log.debug("Started to Play: " + uniqueId);
        setStatus("Playing");
        setPlaying(true);
        bLoading = false;
        bPaused = false;

        //timeHandler.postDelayed(getTime, 1000);
    }

    public synchronized void loaded() {
    }

    public synchronized void playingTrack() {
        startPlaying();
    }


    Runnable getTime = new Runnable() {
        @Override
        public void run() {
            if (bPlaying) {
                int msecPos = mVideoView.getCurrentPosition();
                EventTimeUpdate ev = new EventTimeUpdate();
                ev.setTime((int) msecPos / 1000);
                fireEvent(ev);
                mVideoView.postDelayed(getTime, 1000);
            }
        }
    };

    @Override
    public void pause(boolean bPause) {
        bPaused = true;
        log.debug("Sending: pause");
        setPlaying(false);
        mVideoView.pause();
        EventStatusChanged ev = new EventStatusChanged();
        ev.setStatus("Paused");
        fireEvent(ev);
    }

    @Override
    public synchronized void stop() {
        log.debug("Sending: quit");
        setPlaying(false);
        mVideoView.stopPlayback();
        EventStatusChanged ev = new EventStatusChanged();
        ev.setStatus("Stopped");
        fireEvent(ev);
    }

    @Override
    public synchronized void destroy() {
        log.debug("Attempting to Stop MPlayer");
        stop();
    }

    /**
     * @return the bPaused
     */
    public boolean isbPaused() {
        return bPaused;
    }

    /**
     * @return the bPlaying
     */
    public boolean isPlaying() {
        if (mVideoView.isPlaying()) {
            this.bPlaying = true;
            return true;
        }
        bPlaying = false;
        return false;
    }

    /**
     * @param bPlaying
     *            the bPlaying to set
     */
    public void setPlaying(boolean bPlaying) {
        log.debug("setPlaying: " + bPlaying);
        this.bPlaying = bPlaying;
    }

    /***
     * Track has stopped Playing, get Next Track..
     */
    public synchronized void stoppedPlaying() {
        log.debug("Stopped Playing get next track: ");
        setPlaying(false);
        setStatus("Stopped");
        mVideoView.stopPlayback();
        EventFinishedCurrentTrack ev = new EventFinishedCurrentTrack();
        fireEvent(ev);
    }

    /***
     * Update OpenHome with the new Status
     *
     * @param status
     */
    public synchronized void setStatus(String status) {
        EventStatusChanged ev = new EventStatusChanged();
        ev.setStatus(status);
        ev.setTrack(current_track);
        fireEvent(ev);
    }

    /**
     * @return the trackInfo
     */
    public synchronized TrackInfo getTrackInfo() {
        return mTrackInfo;
    }

    @Override
    public void setMute(boolean mute) {
        this.mute = mute;
        if (mute) {
            mMediaPlayer.setVolume(0, 0);

        } else {
            mMediaPlayer.setVolume(volume, volume);
            setVolume(volume);
        }
    }

    @Override
    public void setVolume(long volume) {
        this.volume = volume;
        if(!mute)
            mMediaPlayer.setVolume(volume, volume);
    }

    @Override
    public void seekAbsolute(long seconds) {
        mVideoView.seekTo((int) seconds*1000);
    }

    /***
     * Used by the ICY info to update the track being played on the Radio
     *
     * @param artist
     * @param title
     */
    public synchronized void updateInfo(String artist, String title) {
        EventUpdateTrackMetaText ev = new EventUpdateTrackMetaText();
        ev.setArtist(artist);
        ev.setTitle(title);
        fireEvent(ev);
    }

    /***
     *
     */
    public void endPositionThread() {

    }

    /***
     *
     */
    @Override
    public synchronized void resume() {
        bPaused = false;

        mVideoView.post(resumePlaying);
    }

    Runnable resumePlaying = new Runnable() {
        @Override
        public void run() {
            setPlaying(true);
            mVideoView.resume();
            mVideoView.postDelayed(getTime, 1000);
            EventStatusChanged ev = new EventStatusChanged();
            ev.setStatus("Playing");
            fireEvent(ev);
        }
    };

    public synchronized void fireEvent(EventBase ev) {
        setChanged();
        notifyObservers(ev);
    }

    public boolean isLoading() {
        return bLoading;
    }

    @Override
    public String getUniqueId() {
        return uniqueId;
    }

    @Override
    public void update(Observable o, Object evt) {
        EventBase e = (EventBase)evt;
        fireEvent(e);
    }

    public void show() {
        mVideoView.post(new Runnable() {
            @Override
            public void run() {
                mVideoView.setVisibility(View.VISIBLE);
            }
        });
//        mVideoView.setVisibility(View.VISIBLE);
    }
    public void hide() {
//        stop();
        setPlaying(false);
        mVideoView.post(new Runnable() {
            @Override
            public void run() {
                mVideoView.setVisibility(View.GONE);
            }
        });
//        mVideoView.setVisibility(View.INVISIBLE);
    }
}
