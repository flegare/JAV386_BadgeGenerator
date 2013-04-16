package com.example.labo1;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * Cette classe présente la badge à l'usager. 
 * @author francois.legare1
 * 
 */
public class BadgeActivity extends Activity {
	
	
	/*
	 * GRAND SECTION
	 *
	 * - CONSTANTES ET VARIABLES
	 * - INITIALISATION DE LA VUE
	 * - GESTION CYCLE DE VIE DE L'ACTIVITE
	 * - GESTION DU GPS
	 * - GESTION DE LA PHOTO
	 * - GESTION DES MENUS
	 */
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////
	// 
	// CONSTANTES ET VARIABLES
	//
	///////////////////////////////////////////////////////////////////////////////////////////////////
	

	private static final String TAG = BadgeActivity.class.getName();

	public static final String INTENT_EXTRA_NAME_OF_USER = "nameOfUser";
	
	private String ASK_FOR_GPS_ACTIVATION = "ask_user_to_activate_gps_already_asked?";
	private String CUSTOM_PICTURE_USED = "is_a_custom_picture_used?";
	private String CUSTOM_PICTURE_PATH = "define_the_path_of_the_custom_picture";	
	
	public static final int RESULT_GPS_ACTIVATION_REQUEST = 999;
	public static final int RESULT_GALLERY_IMAGE_REQUEST = 998;
	public static final int RESULT_CAMERA_IMAGE_REQUEST = 997;
	

	private String nameOfUser;

	private TextView txtUserName;

	private ImageView userImg;

	private TextView userLoc;

	private LocationListener locationListener;

	private boolean showLocation;

	private boolean showPicture;

	private boolean askedSettingGPS;

	private LocationManager locationManager;

	private ProgressDialog mProgressDialog;

	private boolean customPictureUsed;

	private String customPicturePath;
	

	///////////////////////////////////////////////////////////////////////////////////////////////////
	// 
	// INITIALISATION DE LA VUE
	//
	///////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Nous allons initialiser la vue dans cette méthode
	 */
	private void init() {

		// On obtient l'intent qui nous appel, et s'assure que nos extra sont la
		Intent i = getIntent();
		if (i.hasExtra(INTENT_EXTRA_NAME_OF_USER)) {
			nameOfUser = i.getExtras().getString(INTENT_EXTRA_NAME_OF_USER);
		} else {
			nameOfUser = getString(R.string.activity_badge_txt_namestub);
		}

		txtUserName = ((TextView) findViewById(R.id.badge_txt_displayName));
		txtUserName.setText(nameOfUser);

		// On applique une custom font
		Typeface font = Typeface.createFromAsset(getAssets(),
				"fonts/Graziano.ttf");
		txtUserName.setTypeface(font);

		userImg = (ImageView) findViewById(R.id.badge_img_userPicture);
		registerForContextMenu(userImg); // On veut permettre un menu contextuel
											// sur l'image
		userLoc = (TextView) findViewById(R.id.badge_txt_location);

	}
	
	/**
	 * Applique la configuration
	 */
	private void applyConfig() {

		SharedPreferences pm = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
					
		// ------------------------------
		// Gestion de la couleur du badge
		String bgColor = pm.getString(getString(R.string.pref_key_badgeBackgroundColor), "#FF0000");
		int color = Color.parseColor(bgColor);
		findViewById(R.id.badge_layout_top).setBackgroundColor(color);
		findViewById(R.id.badge_layout_bottom).setBackgroundColor(color);

		// ------------------------------
		// Gestion de la photo
		showPicture = pm.getBoolean(
				getString(R.string.pref_key_badgeShowPicture), false);
		if (showPicture) {
			userImg.setVisibility(View.VISIBLE);
		} else {
			userImg.setVisibility(View.GONE);
		}
		
		// ------------------------------
		// Gestion de la localisation
		showLocation = pm.getBoolean(
				getString(R.string.pref_key_badgeShowLocation), false);
		if (showLocation) {
			userLoc.setVisibility(View.VISIBLE);
			startTrackingLocation();
		} else {
			userLoc.setVisibility(View.GONE);
		}

	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////
	// 
	// GESTION CYCLE DE VIE DE L'ACTIVITE
	//
	///////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_badge);
		// On appel notre méthode au démarrage de l'activité
		init();
	}

	
	/*
	 * On sauvegarde l'etat de la vue, par exemple le variable qui sont
	 * detruite et non persister de facon permanante mais quand meme
	 * utile a garder l'etat de la vue.
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d(TAG, "Saving Activity state");
		// Le systeme s'assure de sauver pour nous au besoin
		outState.putBoolean(ASK_FOR_GPS_ACTIVATION, askedSettingGPS);
		
		//On sauvegarde l'etat de la custom pix
		outState.putBoolean(CUSTOM_PICTURE_USED, customPictureUsed);
		outState.putString(CUSTOM_PICTURE_PATH, customPicturePath);
	}

	/*
	 * On restaure l'etat de le vue sauve par onSaveInstanceState
	 */
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {

		Log.d(TAG, "Restoring Activity state");
		// On restore des preferences pour cette vue
		if (savedInstanceState != null){
			
			if(savedInstanceState.containsKey(ASK_FOR_GPS_ACTIVATION)) {
				askedSettingGPS = savedInstanceState.getBoolean(ASK_FOR_GPS_ACTIVATION, false);
			}
			
			if(savedInstanceState.containsKey(CUSTOM_PICTURE_USED)) {
				customPictureUsed = savedInstanceState.getBoolean(CUSTOM_PICTURE_USED);				
				if(customPictureUsed){
					customPicturePath = savedInstanceState.getString(CUSTOM_PICTURE_PATH);
					updatePictureFromPath(customPicturePath);
				}										
			}
		}

		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG, "onStart");
	}
	/*
	 * Tout les appels que l'on execute avec startActivityForResult
	 * retournerons ici avec les resultats demandés. On utilise
	 * des request code maison afin d'identifier qu'est-ce qui
	 * nous appel.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Log.d(TAG, "onActivityResult");
						
		if (requestCode == RESULT_GPS_ACTIVATION_REQUEST && resultCode == RESULT_OK) {			
			/*
			 * NOTE: Notre activité a probablement pas été détruite pendant la
			 * demande, dans ces cas la le onRestoreInstanceState n'est pas
			 * executer. On restore donc ici cet valeur
			 */	
			askedSettingGPS = true;
		}
				
		// On recoit la photo de la gallerie
		if (requestCode == RESULT_GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            updatePictureFromPath(picturePath);
        }
			
		//On recoit l'image de la camera
        if (requestCode == RESULT_CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
        	//On utilise la même formule pour retrouver le path
        	updatePictureFromPath(createTempFileToSave().getAbsolutePath());
        }  
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();
		applyConfig();
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "onPause");
		// On s'assure d'enlever le location listener pour eviter de gaspiller
		// des ressources
		if (showLocation && locationListener != null) {
			Log.d(TAG, "Stop GPS tracking");
			locationManager.removeUpdates(locationListener);
		}
	}

	@Override
	protected void onStop() {
		Log.d(TAG, "onStop");
		super.onStop();
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////
	// 
	// GESTION DE LA LOCATION GPS
	//
	///////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Obtient la position de l'usager Ne pas oublier d'ajouter les permission
	 * dans le manifest!
	 */
	private void startTrackingLocation() {

		// On affiche la position network avant celle de GPS si dispo
		boolean lastKnowLocAvailable = false;

		// Acquire a reference to the system Location Manager
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			// On indique rapidement la dernière position connue
			String locationProvider = LocationManager.NETWORK_PROVIDER;
			Location lastKnownLocation = locationManager
					.getLastKnownLocation(locationProvider);
			if (lastKnownLocation != null) {
				makeUseOfNewLocation(lastKnownLocation);
				lastKnowLocAvailable = true;
			} else {
				userLoc.setText("LOC: NETWORK LOC NOT ACTIVATED");
			}
		}

		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Log.d(TAG, "Start GPS tracking");

			if (!lastKnowLocAvailable) {

				userLoc.setText("LOC: AQUIRING LOCATION...");
			}

			// Define a listener that responds to location updates
			locationListener = new LocationListener() {
				public void onLocationChanged(Location location) {
					// Called when a new location is found by the network
					// location provider.
					makeUseOfNewLocation(location);
				}

				public void onStatusChanged(String provider, int status,
						Bundle extras) {
				}

				public void onProviderEnabled(String provider) {
				}

				public void onProviderDisabled(String provider) {
				}

			};

			// Register the listener with the Location Manager to receive
			// location updates
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0, locationListener);

		} else {
			// Le GPS de l'usager n'est pas active. On lance un dialog à
			// l'usager pour l'active
			if (!askedSettingGPS)
				showGPSDialog();
		}

	}

	/**
	 * Lance un dialog pour demander a l'usager d'activer son GPS
	 */
	private void showGPSDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"Your GPS module is disabled. Would you like to enable it ?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int id) {
								// On lance un intent qui demande d afficher les
								// setting de location
								Intent callGPSSettingIntent = new Intent(
										android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								startActivityForResult(callGPSSettingIntent,
										RESULT_GPS_ACTIVATION_REQUEST);
								dialog.dismiss();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						askedSettingGPS = true; // On ne redemandera pas
					}
				});

		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * On met a jour la vue
	 * 
	 * @param location
	 */
	private void makeUseOfNewLocation(Location location) {

		Log.d(TAG, "Loc updated :" + location);

		double lat = location.getLatitude();
		double lon = location.getLongitude();		
		float acc = location.getAccuracy();
		/*
		float spd = location.getSpeed();
		long upt = location.getTime();
		double alt = location.getAltitude();
		float bea = location.getBearing();
		*/

		userLoc.setText("LOC: " + lat + "N" + lon + "W" + " ACC:" + acc);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////
	// 
	// GESTION DE LA PHOTO
	//
	///////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Demande a l'usager  un URL pour le telechargement de la photo
	 */
	private void askPictureDownloadUrlDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage("What is the url")
				.setView(
						getLayoutInflater().inflate(
								R.layout.dialog_download_url, null))
				.setCancelable(true)
				.setPositiveButton("Download",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int id) {
								EditText edit = (EditText) ((AlertDialog) dialog)
										.findViewById(R.id.dialog_download_txt_url);
								Log.d(TAG,"Will download from: " + edit.getText());								
								startDownload(""+edit.getText());															
								dialog.dismiss();
							}

						});

		AlertDialog alert = builder.create();
		alert.show();
	}
	
		
	/**
	 * Telecharge une photo d'internet
	 * @param string
	 */
	private void startDownload(String url) {
	
		// instantiate it within the onCreate method
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage("Downloading");
		mProgressDialog.setIndeterminate(false);
		mProgressDialog.setMax(100);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

		// execute this when the downloader must be fired
		DownloadFile downloadFile = new DownloadFile();
		downloadFile.execute(url);
		
	}

	/**
	 * Une async task qui télécharge un fichier sur la carte SD du téléphone
	 */
	private class DownloadFile extends AsyncTask<String, Integer, String> {
			
	    @Override
	    protected String doInBackground(String... sUrl) {
	    		    	
	        try {
	        	
	            URL url = new URL(sUrl[0]);
	            URLConnection connection = url.openConnection();
	            connection.connect();
	            // this will be useful so that you can show a typical 0-100% progress bar
	            int fileLength = connection.getContentLength();

	            // download the file
	            InputStream input = new BufferedInputStream(url.openStream());
	            
	            File pictureToBeSaved = createTempFileToSave();	            
				OutputStream output = new FileOutputStream(pictureToBeSaved);
				
	            byte data[] = new byte[1024];
	            long total = 0;
	            int count;
	            while ((count = input.read(data)) != -1) {
	                total += count;
	                // publishing the progress....
	                publishProgress((int) (total * 100 / fileLength));
	                output.write(data, 0, count);
	            }

	            output.flush();
	            output.close();
	            input.close();
	            
	            
	            if(pictureToBeSaved.exists()){
	            	return pictureToBeSaved.getAbsolutePath();
	            }else{
	            	return null; //Fail :(
	            }
	            
	        } catch (Exception e) {
	        	//TODO Handle
	        	Log.e(TAG, e.toString());
	        	return null;
	        }	        	        				     
	    }
	    
		@Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        mProgressDialog.show();
	    }
	    
	    @Override
	    protected void onProgressUpdate(Integer... progress) {
	        super.onProgressUpdate(progress);
	        mProgressDialog.setProgress(progress[0]);
	    }
	    
	    @Override
	    protected void onPostExecute(String result) {
	    	super.onPostExecute(result);
	    	mProgressDialog.dismiss();
	    	updatePictureFromPath(result);
	    }	    	    
	}
	
	
    /**
     * Retourne ou on veut sauver l image temporaire
     * @return
     */
    private File createTempFileToSave() {
    	    	    
    	/*
    	On aurait pu garder également ce fichier temporairement
    
    	try {
			File tempFile = File.createTempFile("tempPicture", ".jpg");
			return tempFile;					
		} catch (IOException e) {
			//Well too bad...
			Toast.makeText(this, "Can't save temporary file, cause:" + e, Toast.LENGTH_LONG);			
			return null;
		}
		*/
    
    	//On utilise toujours le même fichier store sur la carte SD dans ce cas.
		File SDCardRoot = Environment.getExternalStorageDirectory();
		File dir = new File(SDCardRoot + "/labo1/");
		if (dir.exists() == false) {
			dir.mkdirs();
		}
		
		return new File(dir, "imageLabo1.png");		
	}
	
	/**
	 * Update the picture of the image view
	 * @param path
	 */
	private void updatePictureFromPath(String path) {		
		customPictureUsed = true;
		customPicturePath = path;
		Bitmap myBitmap = BitmapFactory.decodeFile(path);
		userImg.setImageBitmap(myBitmap);		
	}
		
	/**
	 * Broadcast receiver maison responsable de mettre a jour la photo
	 * quand un intent est envoyez 
	 **/
	public static class BadgeActivityReceiver extends BroadcastReceiver{
		
		public static final String ACTION_UPDATE_USER_PICTURE = "com.example.labo1.pictureDownloadedSignal";
		public static final String KEY_PICTURE_PATH = "path_of_the_picture";
		
		public BadgeActivityReceiver() {}
				
		@Override
		public void onReceive(Context context, Intent intent) {
			String path = intent.getExtras().getString(KEY_PICTURE_PATH);				
			Log.d(TAG, "Sucess, file is : " + path);
		}
		
	}
	
	private void askPictureFromGallery() {
		Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, RESULT_GALLERY_IMAGE_REQUEST);		
	}
	

	private void askPictureFromCamera() {
		
		File tempFile = createTempFileToSave();				
		Uri uri = Uri.fromFile(tempFile);		
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(intent, RESULT_CAMERA_IMAGE_REQUEST);
		
		/*
		Pour une capture simple sans fichier temporaire		
		Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
		startActivityForResult(cameraIntent, RESULT_CAMERA_IMAGE_REQUEST);
		*/
	}

	
	///////////////////////////////////////////////////////////////////////////////////////////////////
	// 
	// GESTION DES MENUS
	//
	///////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_take_picture, menu);
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_take_picture, menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_picture_from_download:
			askPictureDownloadUrlDialog();
			return true;
		case R.id.menu_picture_from_gallery:
			askPictureFromGallery();
			return true;
		case R.id.menu_picture_from_camera:
			askPictureFromCamera();
			return true;				
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.menu_picture_from_download:
			askPictureDownloadUrlDialog();
			return true;
		case R.id.menu_picture_from_gallery:
			askPictureFromGallery();
			return true;
		case R.id.menu_picture_from_camera:
			askPictureFromCamera();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}
	
	/**
	 * Methode pour lancer l'activité de la config
	 * (remarqué qu'on utilise un MenuItem et non une View!)
	 */
	public void launchConfigActivity(MenuItem m) {
		startActivity(new Intent(this,ConfigActivity.class));		
	}
	
}
