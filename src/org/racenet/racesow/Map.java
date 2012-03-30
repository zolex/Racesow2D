package org.racenet.racesow;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.racenet.framework.Camera2;
import org.racenet.framework.GLGame;
import org.racenet.framework.GameObject;
import org.racenet.framework.TexturedBlock;
import org.racenet.framework.SpatialHashGrid;
import org.racenet.framework.TexturedShape;
import org.racenet.framework.TexturedTriangle;
import org.racenet.framework.Vector2;
import org.racenet.framework.XMLParser;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.util.Log;

public class Map {
	
	private List<TexturedShape> ground = new ArrayList<TexturedShape>();
	private List<TexturedShape> walls = new ArrayList<TexturedShape>();
	private TexturedBlock sky;
	private float skyPosition;
	private TexturedBlock background;
	private float backgroundSpeed = 2;
	private SpatialHashGrid groundGrid;
	private SpatialHashGrid wallGrid;
	private SpatialHashGrid funcGrid;
	public float playerX = 0;
	public float playerY = 0;
	private boolean raceStarted = false;
	private boolean raceFinished = false;
	private float startTime = 0;
	private float stopTime = 0;
	private float camWidth, camHeight;
	
	public Map(float camWidth, float camHeight) {
		
		this.camWidth = camWidth;
		this.camHeight = camHeight;
	}
	
	public boolean load(GLGame game, String fileName) {
		
		XMLParser parser = new XMLParser();
		try {
			
			parser.read(game.getFileIO().readAsset("maps" + File.separator + fileName));
			
		} catch (IOException e) {
			
			try {
				
				parser.read(game.getFileIO().readFile("racesow" + File.separator + "maps" + File.separator + fileName));
				
			} catch (IOException e2) {
				
				return false;
			}
		}
		
		NodeList playern = parser.doc.getElementsByTagName("player");
		if (playern.getLength() == 1) {
			
			Element player = (Element)playern.item(0);
			try {
				
				playerX = Float.valueOf(parser.getValue(player, "x")).floatValue();
				
				playerY = Float.valueOf(parser.getValue(player, "y")).floatValue();
			} catch (NumberFormatException e) {
				
				playerX = 100;
				playerY = 10;
			}
		}
		
		NodeList blockes = parser.doc.getElementsByTagName("block");
		
		float worldWidth = 0;
		float worldHeight = 0;
		
		int numblockes = blockes.getLength();
		for (int i = 0; i < numblockes; i++) {
			
			Element block = (Element)blockes.item(i);
			float blockMaxX = Float.valueOf(parser.getValue(block, "x")).floatValue() + Float.valueOf(parser.getValue(block, "width")).floatValue();
			if (worldWidth < blockMaxX) {
				
				worldWidth = blockMaxX;
			}
			
			float blockMaxY = Float.valueOf(parser.getValue(block, "y")).floatValue() + Float.valueOf(parser.getValue(block, "height")).floatValue();
			if (worldHeight < blockMaxY) {
				
				worldHeight = blockMaxY;
			}
		}
		
		this.groundGrid = new SpatialHashGrid(worldWidth, worldHeight, 30);
		this.wallGrid = new SpatialHashGrid(worldWidth, worldHeight, 30);
		this.funcGrid = new SpatialHashGrid(worldWidth, worldHeight, 30);
		
		NodeList startTimerN = parser.doc.getElementsByTagName("starttimer");
		if (startTimerN.getLength() == 1) {
			
			Element xmlStartTimer = (Element)startTimerN.item(0);
			float startTimerX = Float.valueOf(parser.getValue(xmlStartTimer, "x")).floatValue();
			//GameObject startTimer = new Func(Func.START_TIMER, startTimerX, 0, 1, worldHeight);
			GameObject startTimer = new GameObject(new Vector2(startTimerX, 0), new Vector2(startTimerX + 1, 0), new Vector2(startTimerX + 1, worldHeight), new Vector2(startTimerX, worldHeight));
			startTimer.func = GameObject.FUNC_START_TIMER;
			this.funcGrid.insertStaticObject(startTimer);
		}
		
		NodeList stopTimerN = parser.doc.getElementsByTagName("stoptimer");
		if (stopTimerN.getLength() == 1) {
			
			Element xmlStopTimer = (Element)stopTimerN.item(0);
			float stopTimerX = Float.valueOf(parser.getValue(xmlStopTimer, "x")).floatValue();
			GameObject stopTimer = new GameObject(new Vector2(stopTimerX, 0), new Vector2(stopTimerX + 1, 0), new Vector2(stopTimerX + 1, worldHeight), new Vector2(stopTimerX, worldHeight));
			stopTimer.func = GameObject.FUNC_STOP_TIMER;
			this.funcGrid.insertStaticObject(stopTimer);
		}
		
		NodeList skyN = parser.doc.getElementsByTagName("sky");
		if (skyN.getLength() == 1) {
			
			try {
				
				this.skyPosition = Float.valueOf(parser.getValue((Element)skyN.item(0), "position")).floatValue();
				
			} catch (NumberFormatException e) {
				
				this.skyPosition = 0;
			}
			
			this.sky = new TexturedBlock(game,
					parser.getValue((Element)skyN.item(0), "texture"),
					GameObject.FUNC_NONE,
					-1,
					-1,
					new Vector2(0,skyPosition),
					new Vector2(this.camWidth, skyPosition)
					);
		}
		
		NodeList backgroundN = parser.doc.getElementsByTagName("background");
		if (backgroundN.getLength() == 1) {
			
			try {
				
				this.backgroundSpeed = Float.valueOf(parser.getValue((Element)backgroundN.item(0), "speed")).floatValue();
				
			} catch (NumberFormatException e) {
				
				this.backgroundSpeed = 2;
			}
			
			float backgroundPosition;
			try {
				
				backgroundPosition = Float.valueOf(parser.getValue((Element)backgroundN.item(0), "position")).floatValue();
			
			} catch (NumberFormatException e) {
				
				backgroundPosition = 0;
			}
			
			this.background = new TexturedBlock(game,
					parser.getValue((Element)backgroundN.item(0), "texture"),
					GameObject.FUNC_NONE,
					0.25f,
					0.25f,
					new Vector2(backgroundPosition, 0),
					new Vector2(worldWidth, 0),
					new Vector2(worldWidth, worldHeight + backgroundPosition),
					new Vector2(backgroundPosition, worldHeight + backgroundPosition)
				);
		}
		
		for (int i = 0; i < numblockes; i++) {
			
			Element xmlblock = (Element)blockes.item(i);
			
			short func;
			try {
				
				func = Short.valueOf(parser.getValue(xmlblock, "func"));
				
			} catch (NumberFormatException e) {
				
				func = 0;
			}
			
			float texSX;
			try {
				
				texSX = Float.valueOf(parser.getValue(xmlblock, "texsx")).floatValue();
						
			} catch (NumberFormatException e) {
				
				texSX = 0;
			}
			
			float texSY;
			try {
				
				texSY = Float.valueOf(parser.getValue(xmlblock, "texsy")).floatValue();
						
			} catch (NumberFormatException e) {
				
				texSY = 0;
			}
			
			float x = Float.valueOf(parser.getValue(xmlblock, "x")).floatValue();
			float y = Float.valueOf(parser.getValue(xmlblock, "y")).floatValue();
			float width = Float.valueOf(parser.getValue(xmlblock, "width")).floatValue();
			float height = Float.valueOf(parser.getValue(xmlblock, "height")).floatValue();
			
			TexturedBlock block = new TexturedBlock(game,
				parser.getValue(xmlblock, "texture"),
				func,
				texSX,
				texSY,
				new Vector2(x,y),
				new Vector2(x + width, y),
				new Vector2(x + width, y + height),
				new Vector2(x, y + height)
			);

			String level = parser.getValue(xmlblock, "level");
			if (level.equals("ground")) {
				
				this.addGround(block);
				
			} else if (level.equals("wall")) {
				
				this.addWall(block);
			}
		}

		NodeList triangles = parser.doc.getElementsByTagName("tri");
		int numTriangles = triangles.getLength();
		for (int i = 0; i < numTriangles; i++) {
			
			Element xmlblock = (Element)triangles.item(i);
			
			short func;
			try {
				
				func = Short.valueOf(parser.getValue(xmlblock, "func"));
				
			} catch (NumberFormatException e) {
				
				func = 0;
			}
			
			float texSX;
			try {
				
				texSX = Float.valueOf(parser.getValue(xmlblock, "texsx")).floatValue();
						
			} catch (NumberFormatException e) {
				
				texSX = 0;
			}
			
			float texSY;
			try {
				
				texSY = Float.valueOf(parser.getValue(xmlblock, "texsy")).floatValue();
						
			} catch (NumberFormatException e) {
				
				texSY = 0;
			}
			
			float v1x = 0;
			float v1y = 0;
			float v2x = 0;
			float v2y = 0;
			float v3x = 0;
			float v3y = 0;
			
			NodeList v1n = xmlblock.getElementsByTagName("v1");
			if (v1n.getLength() == 1) {
				
				v1x = Float.valueOf(parser.getValue((Element)v1n.item(0), "x")).floatValue();
				v1y = Float.valueOf(parser.getValue((Element)v1n.item(0), "y")).floatValue();
			}
			
			NodeList v2n = xmlblock.getElementsByTagName("v2");
			if (v2n.getLength() == 1) {
				
				v2x = Float.valueOf(parser.getValue((Element)v2n.item(0), "x")).floatValue();
				v2y = Float.valueOf(parser.getValue((Element)v2n.item(0), "y")).floatValue();
			}
			
			NodeList v3n = xmlblock.getElementsByTagName("v3");
			if (v3n.getLength() == 1) {
				
				v3x = Float.valueOf(parser.getValue((Element)v3n.item(0), "x")).floatValue();
				v3y = Float.valueOf(parser.getValue((Element)v3n.item(0), "y")).floatValue();
			}
			
			Log.d("TRIANGLE", "v1x " + String.valueOf(new Float(v1x)) + 
					" v1y " + String.valueOf(new Float(v1y)) +
					" v2x " + String.valueOf(new Float(v2x)) +
					" v2y " + String.valueOf(new Float(v2y)) +
					" v3x " + String.valueOf(new Float(v3x)) + 
					" v3y " + String.valueOf(new Float(v3y)));
			
			TexturedTriangle block = new TexturedTriangle(game,
				parser.getValue(xmlblock, "texture"),
				func,
				texSX,
				texSY,
				new Vector2(v1x, v1y),
				new Vector2(v2x, v2y),
				new Vector2(v3x, v3y)
			);
		
			String level = parser.getValue(xmlblock, "level");
			if (level.equals("ground")) {
				
				this.addGround(block);
				
			} else if (level.equals("wall")) {
				
				this.addWall(block);
			}
		}
		
		return true;
	}
	
	public void update(Vector2 position) {
		
		this.background.setPosition(new Vector2(
			position.x / this.backgroundSpeed,
			this.background.getPosition().y
		));
		
		this.sky.setPosition(new Vector2(
			position.x - this.sky.width / 2,
			position.y - this.sky.height / 2 + this.skyPosition
		));
	}
	
	public void reloadTextures() {
		
		this.sky.reloadTexture();
		
		int length = this.ground.size();
		for (int i = 0; i < length; i++) {
			
			this.ground.get(i).reloadTexture();
		}
		
		length = this.walls.size();
		for (int i = 0; i < length; i++) {
			
			this.walls.get(i).reloadTexture();
		}
	}
	
	public void dispose() {
		
		int length = this.ground.size();
		for (int i = 0; i < length; i++) {
			
			this.ground.get(i).dispose();
		}
		
		length = this.walls.size();
		for (int i = 0; i < length; i++) {
			
			this.walls.get(i).dispose();
		}
	}
	
	public void addGround(TexturedShape block) {
		
		this.ground.add(block);
		this.groundGrid.insertStaticObject(block);
	}
	
	public TexturedShape getGround(int i) {
		
		return this.ground.get(i);
	}
	
	public int numGround() {
		
		return this.ground.size();
	}
	
	public void addWall(TexturedShape block) {
		
		this.walls.add(block);
		this.wallGrid.insertStaticObject(block);
	}
	
	public TexturedShape getWall(int i) {
		
		return this.walls.get(i);
	}
	
	public int numWalls() {
		
		return this.walls.size();
	}
	
	public TexturedShape getGround(GameObject o) {
		
		int highestPart = 0;
		float maxHeight = 0;
		
		List<GameObject> colliders = groundGrid.getPotentialColliders(o);
		int length = colliders.size();
		if (length == 0) return null;
		for (int i = 0; i < length; i++) {
			
			GameObject part = colliders.get(i);
			if (o.getPosition().x >= part.getPosition().x && o.getPosition().x <= part.getPosition().x + part.width) {
				
				float height = part.getPosition().y + part.height;
				if (height > maxHeight) {
					
					maxHeight = height;
					highestPart = i;
				}
			}
		}
		
		return (TexturedShape)colliders.get(highestPart);
	}
	
	public List<GameObject> getPotentialGroundColliders(GameObject o) {
		
		return this.groundGrid.getPotentialColliders(o);
	}

	public List<GameObject> getPotentialWallColliders(GameObject o) {
		
		return this.wallGrid.getPotentialColliders(o);
	}
	
	public List<GameObject> getPotentialFuncColliders(GameObject o) {
		
		return this.funcGrid.getPotentialColliders(o);
	}
	
	public void draw() {
		
		this.sky.draw();
		this.background.draw();
		
		int length = this.numWalls();
		for (int i = 0; i < length; i++) {
			
			this.getWall(i).draw();
		}
		
		length = this.numGround();
		for (int i = 0; i < length; i++) {
			
			this.getGround(i).draw();
		}
	}
	
	public void restartRace(Player player) {
		
		this.startTime = 0;
		this.stopTime = 0;
		this.raceStarted = false;
		this.raceFinished= false;
		player.reset(this.playerX, this.playerY);
	}
	
	public boolean inRace() {
		
		return this.raceStarted;
	}
	
	public boolean raceFinished() {
		
		return this.raceFinished;
	}
	
	public void startTimer() {
		
		this.raceStarted = true;
		this.startTime = System.nanoTime() / 1000000000.0f;
	}
	
	public void stopTimer() {
		
		this.raceFinished = true;
		this.raceStarted = false;
		this.stopTime = System.nanoTime() / 1000000000.0f;
	}
	
	public float getCurrentTime() {
		
		if (this.raceStarted) {
		
			return System.nanoTime() / 1000000000.0f - this.startTime;
			
		} else if (this.raceFinished) {
			
			return this.stopTime - this.startTime;
			
		} else {
			
			return 0;
		}
	}
}
