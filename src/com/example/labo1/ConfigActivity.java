package com.example.labo1;

import java.util.Map;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Cette activité s'occupe de la configuration de l'application via un fichier
 * de péférence.
 * 
 * @author francois.legare1
 * 
 */
public class ConfigActivity extends PreferenceActivity {

	private static final String TAG = ConfigActivity.class.getName();

	@SuppressWarnings("deprecation")
	// Afin de rester compatible avec Android 2.x+
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.config_preferences);
	}

	/**
	 * Petite methode pour vérifier les valeurs en debug
	 */
	public void showConfigInDebug() {

		/*
		 * Le share preference permet de sauver et charger rapidement des
		 * données pour votre application Évidement, si vous avez beaucoup de
		 * donner il serait préférable d'utiliser une base de donnée sql light
		 */
		SharedPreferences pm = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		Map<String, ?> keys = pm.getAll();
		for (Map.Entry<String, ?> e : keys.entrySet()) {
			Log.d(TAG, e.getKey() + ": " + e.getValue().toString());
		}
	}	
}