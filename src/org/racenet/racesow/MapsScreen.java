package org.racenet.racesow;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.racenet.framework.FileIO;
import org.racenet.framework.Camera2;
import org.racenet.framework.GLGame;
import org.racenet.framework.GLTexture;
import org.racenet.framework.Screen;
import org.racenet.framework.TexturedBlock;
import org.racenet.framework.Vector2;
import org.racenet.framework.XMLParser;
import org.racenet.helpers.MapComperator;
import org.racenet.racesow.models.MapItem;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.content.Intent;
import android.opengl.GLES10;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * Display a list of available maps to play
 * 
 * @author soh#zolex
 *
 */
public class MapsScreen extends Screen implements OnTouchListener {
	
	public TexturedBlock header;
	Camera2 camera;
	GestureDetector gestures;
	float menuVelocity = 0;
	Menu menu;
	
	/**
	 * Constructor. Initialize camera and header graphics
	 * 
	 * @param Game game
	 */
	public MapsScreen(final GLGame game) {
		
		super(game);

		this.camera = new Camera2((float)game.getScreenWidth(), (float)game.getScreenHeight());
		
		FileIO.getInstance().createDirectory("racesow" + File.separator + "maps");
		
		this.refreshMapList();
		
		GLTexture.APP_FOLDER = "racesow";
		String texture = "racesow.jpg";
		if ((float)game.getScreenWidth() < 600) {
			
			texture = "racesow_small.jpg";
		}
		
		header = new TexturedBlock(texture, TexturedBlock.FUNC_NONE, -1, -1, 0, 0, new Vector2(0, 0), new Vector2(camera.frustumWidth, 0));
		header.vertices[0].x = 0;
		header.vertices[0].y = camera.frustumHeight - header.height;
		header.texture.setFilters(GLES10.GL_LINEAR, GLES10.GL_LINEAR);
	}
	
	/**
	 * Show a slideable list of maps
	 */
	public void refreshMapList() {
		
		// get rid of the textures before loading them again
		if (this.menu != null)  {
		
			this.menu.dispose();
		}
		
		this.game.glView.setOnTouchListener(this);
		
		this.menu = new Menu(this.game, this.camera.frustumWidth, this.camera.frustumHeight);
		this.gestures = new GestureDetector(this.menu);
		
		List<MapItem> mapList = new ArrayList<MapItem>();
		
		// load the available maps from the assets
		String[] maps = FileIO.getInstance().listAssets("maps");
		for (int i = 0; i < maps.length; i++) {
			
			final String mapName = maps[i];
			if (!mapName.endsWith(".xml")) continue;
			
			
			XMLParser parser = new XMLParser();
			try {
				
				parser.read(FileIO.getInstance().readAsset("maps" + File.separator + mapName));
				
			} catch (IOException e) {
				
				continue;
			}
			
			String name = "";
			String levelshot = "nolevelshot.png";
			NodeList mapn = parser.doc.getElementsByTagName("map");
			if (mapn.getLength() == 1) {
				
				Element map = (Element)mapn.item(0);
				name = parser.getValue(map, "name");
				levelshot = parser.getValue(map, "levelshot");
			}
			
			MapItem item = new MapItem();
			item.name = name;
			item.filename = mapName;
			item.levelshot = levelshot;
			mapList.add(item);
		}
		
		// lod the available maps from the sd-card
		String[] externalMaps = FileIO.getInstance().listFiles("racesow" + File.separator + "maps", FileIO.ORDER_NAME);
		if (externalMaps != null) {
			for (int i = 0; i < externalMaps.length; i++) {
				
				final String mapName = externalMaps[i];
				if (!mapName.endsWith(".xml")) continue;
				
				
				XMLParser parser = new XMLParser();
				try {
					
					parser.read(FileIO.getInstance().readFile("racesow" + File.separator + "maps" + File.separator + mapName));
					
				} catch (IOException e) {
					
					continue;
				}
				
				String name = "";
				String levelshot = "nolevelshot.png";
				NodeList mapn = parser.doc.getElementsByTagName("map");
				if (mapn.getLength() == 1) {
					
					Element map = (Element)mapn.item(0);
					name = parser.getValue(map, "name");
					levelshot = parser.getValue(map, "levelshot");
				}
				
				MapItem item = new MapItem();
				item.name = name;
				item.filename = mapName;
				item.levelshot = levelshot;
				mapList.add(item);
			}
		}
		
		// order maps by name
		Collections.sort(mapList, new MapComperator());
		
		// add all maps to the menu
		int length = mapList.size();
		for (int i = 0; i < length; i++) {
			
			final MapItem item = mapList.get(i);
			menu.addItem(item.levelshot, menu.new Callback() {
				
				public void handle() {
					
					game.glView.queueEvent(new Runnable() {

	                    public void run() {
					
	                    	game.setScreen(new LoadingScreen(game, item.filename, null));
	                    }
					});
				}
			});
		}
		
		// also add the "download more maps" menu item
		menu.addItem("menu/download.png", menu.new Callback() {
			
			public void handle() {
				
				Intent i = new Intent((Activity)game, DownloadMaps.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			    ((Activity)game).startActivity(i);
			}
		});
	}

	/**
	 * Handle view touch events
	 */
	public boolean onTouch(View v, MotionEvent event) {
		
		this.gestures.onTouchEvent(event);
		return true;
	}
	
	@Override
	/**
	 * Clear framework touchEvent buffer and update the menu
	 */
	public void update(float deltaTime) {

		this.menu.update(deltaTime);
	}

	@Override
	/**
	 * Draw menu and header
	 */
	public void present(float deltaTime) {
		
		GLES10.glClear(GLES10.GL_COLOR_BUFFER_BIT);
		
		GLES10.glFrontFace(GLES10.GL_CCW);
		GLES10.glEnable(GLES10.GL_CULL_FACE);
		GLES10.glCullFace(GLES10.GL_BACK);
		
		this.camera.setViewportAndMatrices(this.game.glView.getWidth(), this.game.glView.getHeight());
		
		GLES10.glEnable(GLES10.GL_TEXTURE_2D);
		GLES10.glEnable(GLES10.GL_BLEND);
		GLES10.glBlendFunc(GLES10.GL_SRC_ALPHA, GLES10.GL_ONE_MINUS_SRC_ALPHA);
		
		this.header.draw();
		this.menu.draw();
	}

	@Override
	/**
	 * Nothing to do on GLGame pause
	 */
	public void pause() {
		
	}

	@Override
	/**
	 * reload all textures when the 
	 * openGL context was lost
	 */
	public void resume() {
		
		this.header.reloadTexture();
		this.menu.reloadTextures();
		this.refreshMapList();
	}

	@Override
	/**
	 * Get rid of all textures when leaving the screen
	 */
	public void dispose() {
		
		this.header.dispose();
		this.menu.dispose();
	}
}
