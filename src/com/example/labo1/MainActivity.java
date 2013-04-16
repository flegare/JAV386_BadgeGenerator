package com.example.labo1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * Activité principale de notre générateur de badge
 * 
 * Exercices possibles: 
 * - Ajouter un nouveau bouton qui lance une nouvelle activité 
 * - Modifier l'appel à "About" afin de lui spécifier l'URL désiré	 
 * TODO: Traduire certain commentaires...
 * 
 * @author francois.legare1
 */
public class MainActivity extends Activity {

	/*
	 * GRANDE SECTIONS
	 * 
	 * - CONSTANTES ET VARIABLES
	 * - GESTION CYCLE DE VIE DE L'ACTIVITE 
	 * - INITIALISATION DE LA VUE 
	 * - ACTIONS
	 */

	// /////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// CONSTANTES ET VARIABLES
	//
	// /////////////////////////////////////////////////////////////////////////////////////////////////

	// Cette constante est utile pour logger des informations
	private static final String TAG = MainActivity.class.getName();

	// References aux composantes de la vue pour utilisation dans
	// l'activité
	private Button btSubmit;
	private EditText edName;

	// /////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// GESTION CYCLE DE VIE DE L'ACTIVITE
	//
	// Afin de voir les différentes étapes du cycle de vie d'une application
	// nous allons implémenter toutes les méthodes du cycle de vie de
	// l'application
	//
	// onCreate(Bundle savedInstanceState);
	// onStart();
	// onRestart();
	// onResume();
	// onPause();
	// onStop();
	// onDestroy();
	//
	// Pour les visuels:
	// http://developer.android.com/images/activity_lifecycle.png
	//
	// /////////////////////////////////////////////////////////////////////////////////////////////////

	/*
	 * Appelé lorsque l'activité est créé pour la première fois. C'est là que vous devez
	 * initialisé la vue et mettre en place les composantes qui seront utilisé par votre
	 * activité. La méthode reçoit en paramètre un "bundle" qui contient
	 * les données d'état sauvegardés avant la destruction de l'activité.
	 * Cette méthode est toujour suivit de: onStart ()
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {			
		super.onCreate(savedInstanceState);
		
		/*
		 * On indique à notre activité qu'elle doit charger ce fichier
		 * XML de layout pour s'afficher. On aurait également pus
		 * créer nous même la vue en code au besoin.
		 */
		setContentView(R.layout.activity_main);

		/*
		 * Afin de voir le cycle de vie d'une activité Nous allons laisser des
		 * traces dans les log sur tout les événements principaux du cycle de
		 * vie de l'application
		 */
		Log.d(TAG, "onCreate");

		/*
		 * Pour les activités complexe, la gestion de la vue peut prendre
		 * beaucoup de code pour simplement "mapper" les composantes de la vue
		 * et le code. Comme ce code est souvent long il est recommandé de
		 * l'isoler dans une méthode utilitaire. Ceci est complétement
		 * facultatif!
		 */
		init();
	}
	
		
	/* 
	 * Called after your activity has been stopped, prior to it being started again.
	 * Always followed by onStart()
	 */
	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d(TAG, "onRestart");
	}
	
	/* 
	 * Called when the activity is becoming visible to the user.
	 * Followed by onResume() if the activity comes to the foreground, or onStop()
	 * if it becomes hidden.
	 */
	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG, "onStart");
	}
	
	/* 
	 * Called when the activity will start interacting with the user. 
	 * At this point your activity is at the top of the activity stack,
	 * with user input going to it.
	 * Always followed by onPause().
	 */
	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onRestart");
	}
	
	/* 
	 * Called when the system is about to start resuming a previous activity. 
	 * This is typically used to commit unsaved changes to persistent data, 
	 * stop animations and other things that may be consuming CPU, etc. 
	 * Implementations of this method must be very quick because the next 
	 * activity will not be resumed until this method returns.
	 * Followed by either onResume() if the activity returns back to the front, 
	 * or onStop() if it becomes invisible to the user.
	 */
	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "onPause");
	}
	
	/* 
	 * Called when the activity is no longer visible to the user, because
	 * another activity has been resumed and is covering this one. 
	 * This may happen either because a new activity is being started, an existing
	 * one is being brought in front of this one, or this one is being destroyed.
	 * Followed by either onRestart() if this activity is coming back to interact
	 * with the user, or onDestroy() if this activity is going away.
	 */
	@Override
	protected void onStop() {
		super.onPause();
		Log.d(TAG, "onStop");
	}
	
	/* 
	 * The final call you receive before your activity is destroyed.
	 * This can happen either because the activity is finishing 
	 * (someone called finish() on it, or because the system is temporarily
	 * destroying this instance of the activity to save space.
	 * You can distinguish between these two scenarios with the isFinishing() method.
	 * (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onPause();
		Log.d(TAG, "onDestroy");
	}
	

	// /////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// INITIALISATION DE LA VUE
	//
	// /////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Cette méthode s'occupe de retrouver les identifiants des composantes
	 * créees dans le xml et au besoin ajoute des "listeners"
	 */
	private void init() {
		/*
		 * La méthode findViewById fait la jonction entre la vue xml et le code.
		 * On doit caster la View selon le type de composante. On pourrait
		 * utiliser RoboGuice afin de faciliter ce genre de travail :
		 * https://github.com/roboguice/roboguice
		 */
		edName = (EditText) findViewById(R.id.main_edt_name);
		btSubmit = (Button) findViewById(R.id.main_bt_submit);

		/*
		 * On applique un click Listener à ce boutton qui sera exécuter quand
		 * l'usager appuie sur le bouton. Il existe plusieurs type de listener
		 * disponible sur une composante View.
		 * 
		 * Voir: http://developer.android.com/reference/android/view/View.html
		 * 
		 * On utilise frequement des classes anonymes sous android pour faire ce
		 * genre d'actions.
		 */
		btSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				/*
				 * On utilise frequement des classes anonymes pour gérer le code
				 * du listener le plus propre reste d'exécuter une méthode à
				 * l'extérieur de cette classe afin de maximiser la
				 * réutilisation et la lecture du code.
				 */
				launchBadgeActivity();
			}
		});

	}

	/**
	 * Lance l'activité BadgeActivity
	 */
	private void launchBadgeActivity() {
		/*
		 * On utilise un intent pour lancer une autre activité. Pour ce faire on
		 * indique la classe que l'on désire exécuter. Les intents peuvent
		 * également lancer des activité qui ne font pas parti de notre
		 * application.
		 */
		Intent i = new Intent(this, BadgeActivity.class);

		/*
		 * L'intent peut également contenir des paramètres qui seront reçues par
		 * l'activité cible. Pour ce faire on encapsule dans des "extras". Dans
		 * l'exemple ci-dessou on capture le nom de l'usager présent dans le
		 * EditText
		 */

		String nomUsager = "" + edName.getText();
		i.putExtra(BadgeActivity.INTENT_EXTRA_NAME_OF_USER, nomUsager);

		/*
		 * On lance l'intent dans le "BUS", l'OS s'occupera d'exécuter celle-ci
		 */
		startActivity(i);
	}
	
	
	// /////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// ACTIONS
	//
	// /////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * On a vu comment lancer une activité via un click listener sur le bouton.
	 * Voici maintenant une technique beaucoup plus simple. Il s'agit de définir
	 * le nom de la méthode a exécuter directement dans la vue XML via
	 * l'attribut onClick
	 * 
	 * Regarder le fichier activity_main.xml et regarder le bouton
	 * "main_bt_about" on y retrouve notre méthode "launchAboutActivity"
	 * 
	 * Pour que cette technique fonctionne, il faut les conditions suivantes:
	 * 
	 * 1) Le nom onClick correspont à cette méthode 
	 * 2) La méthode est publique
	 * 3) La méthode a View en paramètre d'entré 
	 * 4) La méthode retourne Void
	 * 
	 */
	public void launchAboutActivity(View v) {
		startActivity(new Intent(this, AboutActivity.class));
	}

	/**
	 * Méthode pour lancer l'activité de la config
	 */
	public void launchConfigActivity(View v) {
		startActivity(new Intent(this, ConfigActivity.class));
	}

}
