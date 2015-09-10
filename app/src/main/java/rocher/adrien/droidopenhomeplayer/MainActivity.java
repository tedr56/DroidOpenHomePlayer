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
import org.openhome.net.device.DvDevice;
import org.openhome.net.device.DvDeviceFactory;

/*
import org.rpi.config.Config;
import org.rpi.utils.NetworkUtils;
*/

import rocher.adrien.droidopenhomeplayer.Main.SimpleDevice;
import rocher.adrien.droidopenhomeplayer.Utils.Config;
import rocher.adrien.droidopenhomeplayer.Utils.NetworkUtils;
import rocher.adrien.droidopenhomeplayer.Providers.PrvPlayList;
import rocher.adrien.droidopenhomeplayer.Providers.PrvAvTransport;
import rocher.adrien.droidopenhomeplayer.Providers.PrvReceiver;
import rocher.adrien.droidopenhomeplayer.Providers.PrvProduct;

public class MainActivity extends AppCompatActivity {

    private boolean LibraryRunning = false;
    /*
        private Library lib = null;

        //private DvDeviceStandard iDevice;
        private DvDevice iDevice = null;
        private static Properties custom_product = null;
    */
    private SimpleDevice simpleDevice = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createUpnpDevice();

        setContentView(R.layout.activity_main);
    }

    protected boolean createUpnpDevice() {
        if (!LibraryRunning) {
            Config.getInstance().setActivity(this);
            simpleDevice = new SimpleDevice();
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
            simpleDevice.dispose();
/*
            iDevice.destroy();
            lib.close();
*/
        }
    }


}
