/*
*
*   public static final UUID OAD_SERVICE_UUID = UUID.fromString("f000ffc0-0451-4000-b000-000000000000");
    public static final UUID CC_SERVICE_UUID = UUID.fromString("f000ccc0-0451-4000-b000-000000000000");
*
* */

package com.twinly.eyebb.bluetooth;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.IntentFilter;
import android.os.Environment;
import android.util.Log;

import com.twinly.eyebb.utils.Conversion;

public class FwUpdate {
  public final static String EXTRA_MESSAGE = "ti.android.ble.FwUpdate.MESSAGE";
  // Log
  private static String TAG = "FwUpdate";

  // Programming parameters
  private static final short OAD_CONN_INTERVAL = 10; // 12.5 msec
  private static final short OAD_SUPERVISION_TIMEOUT = 100; // 1 second
  private static final int PKT_INTERVAL = 20; // Milliseconds
  private static final int GATT_WRITE_TIMEOUT = 100; // Milliseconds

  private static final int FILE_BUFFER_SIZE = 0x40000;
  private static final String FW_CUSTOM_DIRECTORY = Environment.DIRECTORY_DOWNLOADS;
  private static final String FW_FILE_A = "ImgA.bin";
  private static final String FW_FILE_B = "ImgB.bin";

  // essential OAD Image size parameters
  private static final int OAD_BLOCK_SIZE = 16;
  private static final int HAL_FLASH_WORD_SIZE = 4;
  private static final int OAD_BUFFER_SIZE = 2 + OAD_BLOCK_SIZE;
  private static final int OAD_IMG_HDR_SIZE = 8;

  // BLE
  private BluetoothGattService mOadService;
  //private BluetoothGattService mConnControlService;
  private List<BluetoothGattCharacteristic> mCharListOad;
  //private List<BluetoothGattCharacteristic> mCharListCc;
  private BluetoothGattCharacteristic mCharIdentify = null;
  private BluetoothGattCharacteristic mCharBlock = null;
  private BluetoothGattCharacteristic mCharConnReq = null;
  private BluetoothLeService mLeService;

  // Programming
  private final byte[] mFileBuffer = new byte[FILE_BUFFER_SIZE];
  private final byte[] mOadBuffer = new byte[OAD_BUFFER_SIZE];
  private ImgHdr mFileImgHdr = new ImgHdr();
  private ImgHdr mTargImgHdr = new ImgHdr();
  private Timer mTimer = null;
  private ProgInfo mProgInfo = new ProgInfo();
  private TimerTask mTimerTask = null;

  // Housekeeping
  private boolean mServiceOk = false;
  private boolean mProgramming = false;
  private int mEstDuration = 0;
  private IntentFilter mIntentFilter;
  private List<BluetoothGattService> mServiceList = null;
  private boolean fwProgramReady = false;


  public FwUpdate(BluetoothGattService OadService, List<BluetoothGattService> ServiceList, BluetoothLeService BluetoothLeService) {
    Log.d(TAG, "construct OAD");
    // BLE Gatt Service
    this.mLeService = BluetoothLeService;
    // Service information
    //mConnControlService = mDeviceActivity.getConnControlService();
    this.mServiceList = ServiceList;
    this.mOadService = OadService;
    // Characteristics list
    mCharListOad = mOadService.getCharacteristics();
    //mCharListCc = mConnControlService.getCharacteristics();

//    mServiceOk = mCharListOad.size() == 2 && mCharListCc.size() >= 3;
//    if (mServiceOk) {
//      mCharIdentify = mCharListOad.get(0);
//      mCharBlock = mCharListOad.get(1);
//      mCharConnReq = mCharListCc.get(1);
//    }

      mServiceOk = mCharListOad.size() == 2;
      if (mServiceOk) {
          mCharIdentify = mCharListOad.get(0);
          mCharBlock = mCharListOad.get(1);
      }

  }

    public void checkOad() {
        // Check if OAD is supported (needs OAD and Connection Control service)
        mOadService = null;
        //mConnControlService = null;

        //for (int i = 0; i < mServiceList.size() && (mOadService == null || mConnControlService == null); i++) {
        for (int i = 0; i < mServiceList.size() && (mOadService == null); i++) {
            BluetoothGattService srv = mServiceList.get(i);
            //System.out.println(i);
            //System.out.println(mServiceList.size());
            if (srv.getUuid().equals(BLEUtils.OAD_SERVICE_UUID)) {
                mOadService = srv;
            }
//            if (srv.getUuid().equals(GattInfo.CC_SERVICE_UUID)) {
//                mConnControlService = srv;
//                System.out.println("CCS");
//            }
        }
    }

  // for Activity
  public IntentFilter getinitIntentFilter() {
  	mIntentFilter = new IntentFilter();
  	mIntentFilter.addAction(BLEUtils.ACTION_GATT_READ_SUCCESS);
  	mIntentFilter.addAction(BLEUtils.ACTION_GATT_WRITE_SUCCESS);
  	mIntentFilter.addAction(BLEUtils.ACTION_GATT_WRITE_FAILURE);
  	return mIntentFilter;
  }


  private void startProgramming() {

    mProgramming = true;

    // Prepare image notification
    byte[] buf = new byte[OAD_IMG_HDR_SIZE + 2 + 2];
    buf[0] = Conversion.loUint16(mFileImgHdr.ver);
    buf[1] = Conversion.hiUint16(mFileImgHdr.ver);
    buf[2] = Conversion.loUint16(mFileImgHdr.len);
    buf[3] = Conversion.hiUint16(mFileImgHdr.len);
    System.arraycopy(mFileImgHdr.uid, 0, buf, 4, 4);

    // Send image notification
    mCharIdentify.setValue(buf);
    mLeService.writeCharacteristic(mCharIdentify);

    // Initialize stats
    mProgInfo.reset();

    // Start the packet timer
    mTimer = null;
    mTimer = new Timer();
    mTimerTask = new ProgTimerTask();
    mTimer.scheduleAtFixedRate(mTimerTask, 0, PKT_INTERVAL);
  }

  private void stopProgramming() {
    mTimer.cancel();
    mTimer.purge();
    mTimerTask.cancel();
    mTimerTask = null;

    mProgramming = false;

    if (mProgInfo.iBlocks == mProgInfo.nBlocks) {
      Log.d(TAG,"Programming complete!");
    } else {
        Log.d(TAG, "Programming cancelled");
    }
  }

  private boolean loadFile(String filepath) {
    boolean fSuccess = false;

    // Load binary file
    try {
      // Read the file raw into a buffer
      InputStream stream;

      File f = new File(filepath);
      stream = new FileInputStream(f);

      stream.read(mFileBuffer, 0, mFileBuffer.length);
      stream.close();
    } catch (IOException e) {
      // Handle exceptions here
      Log.d(TAG, "File open failed: ");
      return false;
    }

    // Show new image info
    mFileImgHdr.ver = Conversion.buildUint16(mFileBuffer[5], mFileBuffer[4]);
    mFileImgHdr.len = Conversion.buildUint16(mFileBuffer[7], mFileBuffer[6]);
    mFileImgHdr.imgType = ((mFileImgHdr.ver & 1) == 1) ? 'B' : 'A';
    System.arraycopy(mFileBuffer, 8, mFileImgHdr.uid, 0, 4);
    //getImageInfo(mFileImgHdr);

    // Verify image types
    boolean Imageready = mFileImgHdr.imgType != mTargImgHdr.imgType;    // Compare the current image type with target

    // *important*  Enable programming  only if image types differ
    this.fwProgramReady = true;

    // Expected duration
    mEstDuration = ((PKT_INTERVAL * mFileImgHdr.len * 4) / OAD_BLOCK_SIZE) / 1000;
    //displayStats();

    // Log
    Log.d(TAG, "Image " + mFileImgHdr.imgType + " selected.\n");
    System.out.println(Imageready ? "Ready to program device!\n" : "Incompatible image, select alternative!\n");

    return fSuccess;
  }



  private String getImageInfo(ImgHdr h) {
    int imgVer = (h.ver) >> 1;
    int imgSize = h.len * 4;
    String s = String.format("Type: %c Ver.: %d Size: %d", h.imgType, imgVer, imgSize);
    return s;
  }



  private String displayStats() {
    String txt;
    int byteRate;
    int sec = mProgInfo.iTimeElapsed / 1000;
    if (sec > 0) {
      byteRate = mProgInfo.iBytes / sec;
    } else {
      byteRate = 0;
    }

    txt = String.format("Time: %d / %d sec", sec, mEstDuration);
    txt += String.format("    Bytes: %d (%d/sec)", mProgInfo.iBytes, byteRate);
    return txt;
  }

  /*
  * Read the Image type of target device by notification
  *
  * */
  private boolean getTargetImageInfo() {
    // Enable notification
    boolean ok = enableNotification(mCharIdentify, true);
    // Prepare data for request (try image A and B respectively, only one of
    // them will give a notification with the image info)
    if (ok)
      ok = writeCharacteristic(mCharIdentify, (byte) 0);
    if (ok)
      ok = writeCharacteristic(mCharIdentify, (byte) 1);
    if (!ok)
      Log.v(TAG, "Failed to get target info");

    return ok;
  }

  private boolean writeCharacteristic(BluetoothGattCharacteristic c, byte v) {

    boolean ok = mLeService.writeCharacteristic(c);
    if (ok)
      ok = mLeService.waitIdle(GATT_WRITE_TIMEOUT);
    return ok;
  }

  private boolean enableNotification(BluetoothGattCharacteristic c, boolean enable) {
    boolean ok = mLeService.setCharacteristicNotification(c, enable);
    if (ok)
      ok = mLeService.waitIdle(GATT_WRITE_TIMEOUT);
    return ok;
  }

  private void setConnectionParameters() {
    // Make sure connection interval is long enough for OAD (not necessary for current application)
    byte[] value = { Conversion.loUint16(OAD_CONN_INTERVAL), Conversion.hiUint16(OAD_CONN_INTERVAL), Conversion.loUint16(OAD_CONN_INTERVAL),
        Conversion.hiUint16(OAD_CONN_INTERVAL), 0, 0, Conversion.loUint16(OAD_SUPERVISION_TIMEOUT), Conversion.hiUint16(OAD_SUPERVISION_TIMEOUT) };
    mCharConnReq.setValue(value);
    boolean ok = mLeService.writeCharacteristic(mCharConnReq);
    if (ok)
      ok = mLeService.waitIdle(GATT_WRITE_TIMEOUT);
  }

  /*
   * Called when a notification with the current image info has been received
   */

  private void onBlockTimer() {

    if (mProgInfo.iBlocks < mProgInfo.nBlocks) {
      mProgramming = true;

      // Prepare block
      mOadBuffer[0] = Conversion.loUint16(mProgInfo.iBlocks);
      mOadBuffer[1] = Conversion.hiUint16(mProgInfo.iBlocks);
      System.arraycopy(mFileBuffer, mProgInfo.iBytes, mOadBuffer, 2, OAD_BLOCK_SIZE);

      // Send block
      mCharBlock.setValue(mOadBuffer);
      boolean success = mLeService.writeCharacteristic(mCharBlock);

      if (success) {
        // Update stats
        mProgInfo.iBlocks++;
        mProgInfo.iBytes += OAD_BLOCK_SIZE;
        //mProgressBar.setProgress((mProgInfo.iBlocks * 100) / mProgInfo.nBlocks);
      } else {
        // Check if the device has been prematurely disconnected
        if (BluetoothLeService.getBtGatt() == null)
          mProgramming = false;
      }
    } else {
      mProgramming = false;
    }
    mProgInfo.iTimeElapsed += PKT_INTERVAL;

//        example of how to update real-time upgrade info
//    if (!mProgramming) {
//      runOnUiThread(new Runnable() {
//        public void run() {
//          displayStats();
//          stopProgramming();
//        }
//      });
//    }
  }

  private class ProgTimerTask extends TimerTask {
    @Override
    public void run() {
      mProgInfo.mTick++;
      if (mProgramming) {
        onBlockTimer();
//        example of how to update real-time upgrade info
//        if ((mProgInfo.mTick % PKT_INTERVAL) == 0) {
//          runOnUiThread(new Runnable() {
//            public void run() {
//              displayStats();
//            }
//          });
//        }
      }
    }
  }

  private class ImgHdr {
    short ver;
    short len;
    Character imgType;
    byte[] uid = new byte[4];
  }

  private class ProgInfo {
    int iBytes = 0; // Number of bytes programmed
    short iBlocks = 0; // Number of blocks programmed
    short nBlocks = 0; // Total number of blocks
    int iTimeElapsed = 0; // Time elapsed in milliseconds
    int mTick = 0;

    void reset() {
      iBytes = 0;
      iBlocks = 0;
      iTimeElapsed = 0;
      mTick = 0;
      nBlocks = (short) (mFileImgHdr.len / (OAD_BLOCK_SIZE / HAL_FLASH_WORD_SIZE));
    }
  }

}
