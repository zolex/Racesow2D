package org.racenet.racesow;

import java.io.InputStream;

import android.app.ListActivity;

public abstract class XMLListActivity extends ListActivity {

	public abstract void xmlCallback(InputStream xmlStream);
}
