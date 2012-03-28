package org.racenet.racesow;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import org.racenet.framework.Camera2;
import org.racenet.framework.GLGame;
import org.racenet.framework.GLGraphics;
import org.racenet.framework.GLTexture;
import org.racenet.framework.TexturedBlock;
import org.racenet.framework.XMLParser;
import org.racenet.framework.interfaces.Game;
import org.racenet.framework.interfaces.Input.TouchEvent;
import org.racenet.framework.interfaces.Screen;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.util.Log;
import android.view.GestureDetector;

public class MapsScreen extends Screen {
	
	public TexturedBlock header;
	Camera2 camera;
	GLGraphics glGraphics;
	GestureDetector gestures;
	float camWidth;
	float camHeight;
	float menuVelocity = 0;
	Menu menu;
	
	public MapsScreen(final Game game) {
		
		super(game);
		
		glGraphics = ((GLGame)game).getGLGraphics();
		
		camWidth = (float)game.getScreenWidth();
		camHeight = (float)game.getScreenHeight();
		camera = new Camera2(glGraphics, camWidth, camHeight);
		
		menu = new Menu((GLGame)game, camWidth, camHeight);
		gestures = new GestureDetector(menu);
		
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
			
			String levelshot = "nolevelshot.png";
			NodeList mapn = parser.doc.getElementsByTagName("map");
			if (mapn.getLength() == 1) {
				
				Element map = (Element)mapn.item(0);
				levelshot = parser.getValue(map, "levelshot");
			}
			
			menu.addItem(levelshot, menu.new Callback() {
				
				public void handle() {
					
					game.setScreen(new GameScreen(game, mapName));
				}
			});
		}
		
		
		
		GLTexture.APP_FOLDER = "racesow";
		//header = new TexturedBlock((GLGame)game, 0, 0, camWidth, -1, "racesow.jpg", TexturedBlock.FUNC_NONE, -1, -1);
		//header.position.y = camHeight - header.bounds.height;
		//header.texture.setFilters(GL10.GL_LINEAR, GL10.GL_LINEAR);
	}

	@Override
	public void update(float deltaTime) {

		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		int length = touchEvents.size();
		for (int i = 0; i < length; i++) {
			
			gestures.onTouchEvent(touchEvents.get(i).source);  
		}
		
		this.menu.update(deltaTime);
	}

	@Override
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
		
		//this.header.draw();
		this.menu.draw();
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
		//this.header.reloadTexture();
		this.menu.reloadTextures();
	}

	@Override
	public void dispose() {
		
		//this.header.dispose();
		this.menu.dispose();
	}
}