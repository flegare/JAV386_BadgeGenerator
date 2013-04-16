package com.example.labo1;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Une simple vue qui affiche du contenu web
 * @author francois.legare1
 *
 */
public class AboutActivity extends Activity {
	
	private static final String TAG = AboutActivity.class.getName();		   
	private WebView wv;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.activity_about);       
        //On appel notre méthode au démarrage de l'activité
        init();        
    }

    /**
     * Nous allons initialiser la vue dans cette méthode
     */
    private void init() {
    	    	    	
    	//Il ne faut pas oublié de mettre la permission INTERNET
    	//dans le manifest pour que ceci soit fonctionnel!    	
    	wv = (WebView)findViewById(R.id.about_webview);
    	wv.loadUrl("http://twitter.github.io/bootstrap/examples/starter-template.html");
    	wv.setWebViewClient(new WebViewClient(){
    		    	    
    		private long ts;

			@Override
    		public void onPageFinished(WebView view, String url) {
    			super.onPageFinished(view, url);
    			Log.d(TAG, "Loaded in: " + (System.currentTimeMillis()-ts));
    		}
    		
    		@Override
    		public void onPageStarted(WebView view, String url, Bitmap favicon) {
    			super.onPageStarted(view, url, favicon);
    			ts = System.currentTimeMillis();    					
    		}    		    	
    	});
    	
    	//Permet l'exécution de javascript
    	wv.getSettings().setJavaScriptEnabled(true);    	
    	    
    }
    
    
    @Override
    public void onBackPressed() {    
    	
    	//On quitte cette activité seulement si la webview peut
    	//revenir en arriere.    	
    	if(wv.canGoBack()){
    		wv.goBack();
    	}else{
        	super.onBackPressed();
    	}
    	
    }
    

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
}
