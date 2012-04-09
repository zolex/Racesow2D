package org.racenet.racesow;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import org.racenet.framework.Camera2;
import org.racenet.framework.GLGame;
import org.racenet.framework.GLGraphics;
import org.racenet.framework.GLTexture;
import org.racenet.framework.TexturedBlock;
import org.racenet.framework.Vector2;
import org.racenet.framework.XMLParser;
import org.racenet.framework.interfaces.Game;
import org.racenet.framework.interfaces.Input.TouchEvent;
import org.racenet.framework.interfaces.Screen;
import org.racenet.helpers.MapComperator;
import org.racenet.racesow.models.MapItem;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.content.Intent;
import android.view.GestureDetector;

/**
 * Display a list of available maps to play
 * 
 * @author soh#zolex
 *
 */
public class MapsScreen extends Screen {
	
	public TexturedBlock header;
	Camera2 camera;
	GLGraphics glGraphics;
	GestureDetector gestures;
	float menuVelocity = 0;
	Menu menu;
	
	/**
	 * Constructor. Initialize camera and header graphics
	 * 
	 * @param Game game
	 */
	public MapsScreen(final Game game) {
		
		super(game);
		
		glGraphics = ((GLGame)game).getGLGraphics();
		
		this.camera = new Camera2(glGraphics, (float)game.getScreenWidth(), (float)game.getScreenHeight());
		
		this.refreshMapList();
		
		
		GLTexture.APP_FOLDER = "racesow";
		String texture = "racesow.jpg";
		if ((float)game.getScreenWidth() < 600) {
			
			texture = "racesow_small.jpg";
		}
		
		this.header = new TexturedBlock((GLGame)game, texture, TexturedBlock.FUNC_NONE, -1, -1, 0, 0,
				new Vector2(0, 0), new Vector2(this.camera.frustumWidth, 0));
		this.header.setPosition(new Vector2(0, this.camera.frustumHeight - this.header.height));
		this.header.texture.setFilters(GL10.GL_LINEAR, GL10.GL_LINEAR);
	}
	
	/**
	 * Show a slideable list of maps
	 */
	public void refreshMapList() {
		
		// get rid of the textures before loading them again
		if (this.menu != null)  {
		
			this.menu.dispose();
		}
		
		this.menu = new Menu((GLGame)this.game, this.camera.frustumWidth, this.camera.frustumHeight);
		this.gestures = new GestureDetector(this.menu);
		
		List<MapItem> mapList = new ArrayList<MapItem>();
		
		// load the available maps from the assets
		String[] maps = game.getFileIO().listAssets("maps");
		for (int i = 0; i < maps.length; i++) {
			
			final String mapName = maps[i];
			if (!mapName.endsWith(".xml")) continue;
			
			
			XMLParser parser = new XMLParser();
			try {
				
				parser.read(game.getFileIO().readAsset("maps" + File.separator + mapName));
				
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
		String[] externalMaps = game.getFileIO().listFiles("racesow" + File.separator + "maps");
		if (externalMaps != null) {
			for (int i = 0; i < externalMaps.length; i++) {
				
				final String mapName = externalMaps[i];
				if (!mapName.endsWith(".xml")) continue;
				
				
				XMLParser parser = new XMLParser();
				try {
					
					parser.read(game.getFileIO().readFile("racesow" + File.separator + "maps" + File.separator + mapName));
					
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
					
					game.setScreen(new LoadingScreen(game, item.filename, null));
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

	@Override
	/**
	 * Update the menu according to the users input (gestures)
	 */
	public void update(float deltaTime) {

		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		int length = touchEvents.size();
		for (int i = 0; i < length; i++) {
			
			gestures.onTouchEvent(touchEvents.get(i).source);  
		}
		
		this.menu.update(deltaTime);
	}

	@Override
	/**
	 * Draw menu and header
	 */
	public void present(float deltaTime) {
		
		GL10 gl = glGraphics.getGL();
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		gl.glFrontFace(GL10.GL_CCW);
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glCullFace(GL10.GL_BACK);
		
		this.camera.setViewportAndMatrices();
		
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
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
