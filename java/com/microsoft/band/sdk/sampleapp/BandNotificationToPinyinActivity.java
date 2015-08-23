//Copyright (c) Microsoft Corporation All rights reserved.  
// 
//MIT License: 
// 
//Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
//documentation files (the  "Software"), to deal in the Software without restriction, including without limitation
//the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
//to permit persons to whom the Software is furnished to do so, subject to the following conditions: 
// 
//The above copyright notice and this permission notice shall be included in all copies or substantial portions of
//the Software. 
// 
//THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
//TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
//THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
//CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
//IN THE SOFTWARE.
package com.microsoft.band.sdk.sampleapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandIOException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.notifications.MessageFlags;
import com.microsoft.band.tiles.BandTile;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class BandNotificationToPinyinActivity extends Activity {

	private BandClient client = null;
	private Button btnStart;
	private TextView txtStatus;
    private BandNotificationToPinyin mApp;
	
	private UUID tileId = UUID.fromString("aa0D508F-70A3-47D4-BBA3-812BADB1F8Aa");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtStatus = (TextView) findViewById(R.id.txtStatus);
		
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                txtStatus.setText("");
                new appTask("hello", "it's a test message").execute();
			}
		});
		mApp = (BandNotificationToPinyin)this.getApplicationContext();
    }

    public void sendToBand(String title, String message) {
        new appTask(title, message).execute();
    }

    protected void onResume() {
        super.onResume();
		mApp.setCurrentActivity(this);
    }
    protected void onPause() {
        super.onPause();
    }
    protected void onDestroy() {
        clearReferences();
        super.onDestroy();
    }

    private void clearReferences(){
        Activity currActivity = mApp.getCurrentActivity();

        if (currActivity != null && currActivity.equals(this)) {
			mApp.setCurrentActivity(null);
		}
    }

	private class appTask extends AsyncTask<Void, Void, Void> {
        private String message;
		private String title;

        public appTask(String title, String message) {
            this.title = title;
			this.message = message;
        }

        @Override
		protected Void doInBackground(Void... params) {
			try {
				if (getConnectedBandClient()) {
					if (doesTileExist(client.getTileManager().getTiles().await(), tileId)) {
						sendMessage(title, message);
					} else {
						if(addTile()) {
							sendMessage("hint", "Send message to new message tile");
						}
					}
				} else {
					appendToUI("Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
				}
			} catch (BandException e) {
				String exceptionMessage="";
				switch (e.getErrorType()) {
				case DEVICE_ERROR:
					exceptionMessage = "Please make sure bluetooth is on and the band is in range.";
					break;
				case UNSUPPORTED_SDK_VERSION_ERROR:
					exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.";
					break;
				case SERVICE_ERROR:
					exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.";
					break;
				case BAND_FULL_ERROR:
					exceptionMessage = "Band is full. Please use Microsoft Health to remove a tile.";
					break;
				default:
					exceptionMessage = "Unknown error occured: " + e.getMessage();
					break;
				}
				appendToUI(exceptionMessage);

			} catch (Exception e) {
				appendToUI(e.getMessage());
			}
			return null;
		}
	}
	
	private void appendToUI(final String string) {
		this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
            	txtStatus.append(string);
            }
        });
	}
	
	private boolean doesTileExist(List<BandTile> tiles, UUID tileId) {
		for (BandTile tile:tiles) {
			if (tile.getTileId().equals(tileId)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean addTile() throws Exception {
        /* Set the options */
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap tileIcon = BitmapFactory.decodeResource(getBaseContext().getResources(), R.raw.tile_icon_large, options);
        Bitmap badgeIcon = BitmapFactory.decodeResource(getBaseContext().getResources(), R.raw.tile_icon_small, options);

		BandTile tile = new BandTile.Builder(tileId, "MessageTile", tileIcon)
			.setTileSmallIcon(badgeIcon).build();
		appendToUI("Message Tile is adding ...\n");
		if (client.getTileManager().addTile(this, tile).await()) {
			appendToUI("Message Tile is added.\n");
			return true;
		} else {
			appendToUI("Unable to add message tile to the band.\n");
			return false;
		}
	}
	
	private void sendMessage(String title, String message) throws BandIOException {
		client.getNotificationManager().sendMessage(tileId, title, message, new Date(), MessageFlags.SHOW_DIALOG);
		appendToUI(message + "\n");
	}
	
	private boolean getConnectedBandClient() throws InterruptedException, BandException {
		if (client == null) {
			BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
			if (devices.length == 0) {
				appendToUI("Band isn't paired with your phone.\n");
				return false;
			}
			client = BandClientManager.getInstance().create(getBaseContext(), devices[0]);
		} else if (ConnectionState.CONNECTED == client.getConnectionState()) {
			return true;
		}
		
		appendToUI("Band is connecting...\n");
		return ConnectionState.CONNECTED == client.connect().await();
	}
}