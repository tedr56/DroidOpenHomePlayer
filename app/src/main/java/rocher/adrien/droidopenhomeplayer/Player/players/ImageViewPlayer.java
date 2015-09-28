package rocher.adrien.droidopenhomeplayer.Player.players;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Time;
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


public class ImageViewPlayer extends Observable implements IPlayer, Observer{

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
    private ImageView mImageView = null;

    private MediaPlayer mMediaPlayer = null;

    //static private Handler timeHandler = new Handler();
    static private ImageViewPlayer instance = null;
    private Uri mUri;

    private long mStartTime;
    private long mElapsedTime;

    private ImageViewPlayer() {
        initVideoView();
    }

    public static ImageViewPlayer getInstance() {
        if (instance == null) {
            instance = new ImageViewPlayer();
        }
        return instance;
    }

    private void initVideoView() {
        AppCompatActivity main = Config.getInstance().getActivity();

        View v = main.getWindow().getDecorView().findViewById(android.R.id.content);
        mImageView = (ImageView) v.findViewById(R.id.imagezone);

//        mImageView.setOnInfoListener(
//                new MediaPlayer.OnInfoListener() {
//                    @Override
//                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
//                        //if (mMediaPlayer == null) { mMediaPlayer = mp; }
//                        mMediaPlayer = mp;
//                        Log.d("MPinfo", "MediaPlayer.OnInfoListener : " + what);
//                        Log.d("MPinfo", "MediaPlayer.OnInfoListenerX: " + extra);
//                        switch (what) {
//                            case 3:
//                                startPlaying();
//                                MediaExtractor mx = new MediaExtractor();
//                                try {
//                                    mx.setDataSource(mUriPath);
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                    return false;
//                                }
//                                int numTracks = mx.getTrackCount();
//                                for (int i = 0; i < numTracks; ++i) {
//                                    MediaFormat mf = mx.getTrackFormat(i);
//                                    String mime = mf.getString(MediaFormat.KEY_MIME);
//                                    Log.d("MPinfo", "MediaPlayer.OnInfoListener : " + mime);
//                                    String mimeType = mime.split("/")[0];
//                                    if (mimeType.equals("audio")) {
//                                        int Duration = mImageView.getDuration() / 1000;
//                                        mTrackInfo.setDuration(Duration);
//
//                                        EventDurationUpdate e = new EventDurationUpdate();
//                                        e.setDuration(mTrackInfo.getDuration());
//                                        fireEvent(e);
//
//                                        mTrackInfo.setBitDepth((long) mf.getInteger(MediaFormat.KEY_SAMPLE_RATE));
//                                        mTrackInfo.setSampleRate((long) mf.getInteger(MediaFormat.KEY_SAMPLE_RATE));
//                                        mTrackInfo.setBitrate((long) mf.getInteger(MediaFormat.KEY_SAMPLE_RATE));
//
//                                        mTrackInfo.setCodec(mime);
//
//                                        EventTrackChanged ev = new EventTrackChanged();
//                                        ev.setTrack(current_track);
//                                        fireEvent(ev);
//
//                                    }
//                                }
//                                mImageView.postDelayed(getTime, 1000);
//
//                                //seekHandler.postDelayed(getTime, 1000);
//
//                                break;
//                            case 701:
//                                EventStatusChanged ev = new EventStatusChanged();
//                                ev.setStatus("Buffering");
//                                fireEvent(ev);
//
//                                break;
//                            case 702:
//
//                                int i = 0;
//                                break;
//                        }
//                        return true;
//                    }
//                }
//        );
//        mImageView.setOnCompletionListener(
//                new MediaPlayer.OnCompletionListener() {
//                    @Override
//                    public void onCompletion(MediaPlayer mp) {
//                        mMediaPlayer = mp;
//                        EventFinishedCurrentTrack ev = new EventFinishedCurrentTrack();
//                        fireEvent(ev);
//                    }
//                }
//        );
    }

    /***
     * Plays the Custom Track
     *
     * @param track
     * @return
     */
    public boolean playTrack(ChannelBase track, long volume, boolean mute) {
        if (mImageView == null) {
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

        EventStatusChanged ev = new EventStatusChanged();
        ev.setStatus("Buffering");
        fireEvent(ev);

        DownloadImage dlBitmap = new DownloadImage();

        dlBitmap.execute(mUriPath);
        return true;
    }

    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        protected Bitmap  doInBackground(String... urls) {
            Bitmap bitmap = null;
            try {
                URL url = new URL(urls[0]);
                InputStream is = (InputStream) url.getContent();
                bitmap = BitmapFactory.decodeStream(is);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }
        protected void onPostExecute(Bitmap result) {
            mImageView.setImageBitmap(result);
            mElapsedTime = 0;

            startPlaying();

            EventDurationUpdate evD = new EventDurationUpdate();
            evD.setDuration(20);
            fireEvent(evD);

            TrackInfo mTrackInfo = new TrackInfo();
            mTrackInfo.setBitDepth(12);
            mTrackInfo.setBitrate(42);
            mTrackInfo.setSampleRate(44100);
            mTrackInfo.setCodec("image");

            EventTrackChanged evT = new EventTrackChanged();
            evT.setTrack(current_track);
            fireEvent(evT);

            EventStatusChanged evP = new EventStatusChanged();
            evP.setStatus("Playing");
            fireEvent(evP);

            mImageView.postDelayed(getTime, 1000);

        }
    }

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
            if (mElapsedTime > 20000) {

                EventFinishedCurrentTrack ev = new EventFinishedCurrentTrack();
                fireEvent(ev);
            }
            else if (bPlaying) {
                mElapsedTime += 1000;
                long msecPos = mElapsedTime;
                EventTimeUpdate ev = new EventTimeUpdate();
                ev.setTime((int) msecPos / 1000);
                fireEvent(ev);
                mImageView.postDelayed(getTime, 1000);
            }
        }
    };

    @Override
    public void pause(boolean bPause) {
        bPaused = true;
        log.debug("Sending: pause");
//        mImageView.pause();
        EventStatusChanged ev = new EventStatusChanged();
        ev.setStatus("Paused");
        fireEvent(ev);
    }

    @Override
    public synchronized void stop() {
        log.debug("Sending: quit");
        setPlaying(false);
//        mImageView.stopPlayback();
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
        return bPlaying;
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
//        mImageView.stopPlayback();
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
//        if (mute) {
//            mMediaPlayer.setVolume(0, 0);
//
//        } else {
//            mMediaPlayer.setVolume(volume, volume);
//            setVolume(volume);
//        }
        setVolume(volume);
    }

    @Override
    public void setVolume(long volume) {
        this.volume = volume;
//        if(!mute)
//            mMediaPlayer.setVolume(volume, volume);
    }

    @Override
    public void seekAbsolute(long seconds) {
//        mImageView.seekTo((int) seconds*1000);
        mElapsedTime = seconds * 1000;
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

        mImageView.post(resumePlaying);
    }

    Runnable resumePlaying = new Runnable() {
        @Override
        public void run() {
            setPlaying(true);
//            mImageView.resume();
            mImageView.postDelayed(getTime, 1000);
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
        mImageView.post(new Runnable() {
            @Override
            public void run() {
                mImageView.setVisibility(View.VISIBLE);
            }
        });
    }

    public void hide() {
//        stop();
        setPlaying(false);
        mImageView.post(new Runnable() {
            @Override
            public void run() {
                mImageView.setVisibility(View.GONE);
            }
        });
    }
}