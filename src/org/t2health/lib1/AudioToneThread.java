/*
 * 
 */
package org.t2health.lib1;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class AudioToneThread extends Thread {
	private static final String TAG = "BioSound";

	
	private boolean isRunning = false;
	private boolean cancelled = false;
	private AndroidAudioDevice device;	
	private float frequency = 0;
	private float increment = (float)(2*Math.PI) * frequency / 44100; // angular increment for each sample	
	private float mVolume = 0;
	
	
	public AudioToneThread() {
	      device = new AndroidAudioDevice( );		
	}
	
	public float getFrequency() {
		return frequency;
	}

	public void setFrequency(float aFrequency) {
//		Log.d(TAG, "aFrequency = " + aFrequency + ", frequency = " + frequency);
		if (frequency != aFrequency) {
			frequency = aFrequency;
			increment = (float)(2*Math.PI) * frequency / 44100; // angular increment for each sample
		}
	}

	public void setVolume(float volume) {
		mVolume = volume;		
		device.setVolume(volume);
	}
	
	
	@Override
	public void run() {
		isRunning = true;
		
      float angle = 0;

      float samples[] = new float[1024];
		
		
		while(true) {
			// Break out if this was canceled.
			if(cancelled || interrupted()) {
				break;
			}
			
	        if (frequency > 30) {
	          for( int i = 0; i < samples.length; i++ )
	          {
	             samples[i] = (float)Math.sin( angle );
	             angle += increment;
	             if (angle > 2 * Math.PI) 
	            	 angle = 0;
	          }
	        	  device.writeSamples( samples );
	      }
			
		}
		

		isRunning = false;
	}
	
	
	public void cancel() {
		this.cancelled = true;
		device.stop();
		
	}
	
	public boolean isRunning() {
		return this.isRunning;
	}
	
	
	
	public void FadeVolumeTo(final int delay, final float newVolume) {
		final int direction;	
		float tmpVolume;
		if (newVolume == mVolume)
			return;
		
		if (newVolume > mVolume) {
			direction = +1;
		}
		else {
			direction = -1;
		}
		
		try {
			Runnable runner = new Runnable() {

				@Override
				public void run() {
					float volume = 0;

					float increment;
					
					if (delay > 0)
						increment = 1F / (float) delay;
					else
						increment = 0.1F;

					for (int i = 0; i < delay; i++) {
						if (direction > 0) {
							mVolume += increment;
							if (mVolume >= newVolume) {
								mVolume = newVolume; // IN case of overshoot
								break;
							}
						}
						else {
							mVolume -= increment;
							if (mVolume <= newVolume) {
								if (volume <= 0) {
									mVolume = newVolume; // IN case of overshoot
									break;
								}
							}						
						
							try {
								Thread.sleep(200);
								setVolume(mVolume);								
							} catch (Exception q) {
								Log.e(TAG, "EXCEPTION", q);
								break;
							}
						}
					}
				}
			};
			new Thread(runner).start();
		} catch (Exception ex) {
			Log.d(TAG, "EXCEPTION", ex);
		}	
	
	}
	
	
	
	
	
	
	
	
	
	
	
	public class AndroidAudioDevice
	{
	   AudioTrack track;
	   short[] buffer = new short[1024];
	 
	   public AndroidAudioDevice( )
	   {
	      int minSize =AudioTrack.getMinBufferSize( 44100, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT );        
	      track = new AudioTrack( AudioManager.STREAM_MUSIC, 44100, 
	                                        AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, 
	                                        minSize, AudioTrack.MODE_STREAM);
	      track.play();        
	   }	   
	 
	   public void writeSamples(float[] samples) 
	   {	
	      fillBuffer( samples );
	      track.write( buffer, 0, samples.length );
	   }
	 
	   private void fillBuffer( float[] samples )
	   {
	      if( buffer.length < samples.length )
	         buffer = new short[samples.length];
	 
	      for( int i = 0; i < samples.length; i++ )
	         buffer[i] = (short)(samples[i] * Short.MAX_VALUE);;
	   }	
	   
	   public void stop() {
		   track.flush();
		   track.stop();
		   track.release();
		   
	   }

	   public void setVolume(float volume) {
//		   Log.d("BioSound", "volume = " + volume);
		   track.setStereoVolume(volume, volume);
		}
	   
	}	
	
	
	
}   
