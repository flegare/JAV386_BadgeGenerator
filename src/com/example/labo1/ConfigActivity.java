package com.example.labo1;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Cette activite s'occupe de la configuration de l'application
 * @author francois.legare1
 *
 */
public class ConfigActivity extends PreferenceActivity {
	
	private static final String TAG = ConfigActivity.class.getName();
		    		
	@SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);                       
        addPreferencesFromResource(R.xml.config_preferences);
        showInfoInDebug();
    }

	/**
	 * Petite methode pour vérifier les valeurs en debug
	 */
	private void showInfoInDebug() {
		
		SharedPreferences pm = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
        String backgroundColor = pm.getString(getString(R.string.pref_key_badgeBackgroundColor),"#FF0000");
        Boolean showPicture = pm.getBoolean(getString(R.string.pref_key_badgeShowPicture),false);
        Boolean showLocation = pm.getBoolean(getString(R.string.pref_key_badgeShowLocation),false);		
        
        String msg = "Pref [color:"+backgroundColor+",pix:"+showPicture+",loc:"+showLocation;
        Log.d(TAG, msg);
        
	}  	
}
