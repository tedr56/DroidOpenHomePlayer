package rocher.adrien.droidopenhomeplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Properties;

//import org.openhome.net.device.tests.TestDvDevice;
import org.openhome.net.core.InitParams;
import org.openhome.net.core.Library;
import org.openhome.net.core.DeviceStack;
import org.openhome.net.device.DvDeviceStandard;
import org.openhome.net.device.IDvDeviceListener;


/*
import org.rpi.config.Config;
import org.rpi.utils.NetworkUtils;
*/

import rocher.adrien.droidopenhomeplayer.Utils.Config;
import rocher.adrien.droidopenhomeplayer.Utils.NetworkUtils;
import rocher.adrien.droidopenhomeplayer.Providers.PrvPlayList;

public class MainActivity extends AppCompatActivity {

    private Library lib;
    private boolean LibraryRunning;

    private DvDeviceStandard iDevice;
    private static Properties custom_product = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createUpnpDevice();

        setContentView(R.layout.activity_main);
    }

    protected boolean createUpnpDevice() {
        if (!LibraryRunning) {
            InitParams initParams = new InitParams();
            //initParams.setMsearchTimeSecs(1);
            // MsearchTimeSecs = 1,
            //initParams.setUseLoopbackNetworkAdapter();
            //UseLoopbackNetworkAdapter = true,
            //initParams.setDvServerPort(0);//DvUpnpWebServerPort = 0

            //initParams.getMsearchTimeSecs();
            //initParams.getDvServerPort();

            lib = new Library();
            lib.initialise(initParams);


            LibraryRunning = true;

            String iDeviceName = "DroidPlayer";
            String iDeviceUdn  = Config.getInstance().getUdn();
            String friendly_name = Config.getInstance().getProductRoom() + ":" + Config.getInstance().getProductName();

            DeviceStack ds = lib.startDv();

            iDevice = new DvDeviceStandard(iDeviceUdn);

            iDevice.setAttribute("Upnp.Domain", "upnp.org");
            iDevice.setAttribute("Upnp.Type", "MediaRenderer");
            iDevice.setAttribute("Upnp.Version", "1");
            iDevice.setAttribute("Upnp.FriendlyName", friendly_name);
            iDevice.setAttribute("Upnp.Manufacturer", Config.getInstance().getManufacturerName());
            iDevice.setAttribute("Upnp.ManufacturerUrl", Config.getInstance().getManufacturerUrl());
            iDevice.setAttribute("Upnp.ModelName", Config.getInstance().getProductName());
            iDevice.setAttribute("Upnp.ModelNumber", Config.getInstance().getVersion());
            iDevice.setAttribute("Upnp.ModelDescription", Config.getInstance().getProductInfo());
            iDevice.setAttribute("Upnp.ModelUrl", Config.getInstance().getProductUrl());
            iDevice.setAttribute("Upnp.SerialNumber", Config.getInstance().getSerialNumber());
            iDevice.setAttribute("Upnp.PresentationUrl", "http://" + NetworkUtils.getIPAddress(true) + ":" + Config.getInstance().getWebServerPort());

            iDevice.setEnabled();

// TODO : Create PlaylistProvider
            //DvProviderAvOpenhomeOrgPlaylist1 iPlayListProvider = new DvProviderAvOpenhomeOrgPlaylist1(iDevice);
            //PlayListProvider iPlayListProvider = new PlayListProvider(*iDevice)
            PrvPlayList iPlayListProvider = new PrvPlayList(iDevice);

        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
        if (LibraryRunning) {
            iDevice.destroy();
            lib.close();
        }
    }


}
