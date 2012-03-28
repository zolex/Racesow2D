package org.racenet.racesow;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.racenet.framework.Func;
import org.racenet.framework.GLGame;
import org.racenet.framework.GameObject;
import org.racenet.framework.TexturedBlock;
import org.racenet.framework.SpatialHashGrid;
import org.racenet.framework.Vector2;
import org.racenet.framework.XMLParser;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.util.Log;

public class Map {
	
	private List<TexturedBlock> ground = new ArrayList<TexturedBlock>();
	private List<TexturedBlock> walls = new ArrayList<TexturedBlock>();
	private SpatialHashGrid groundGrid;
	private SpatialHashGrid wallGrid;
	private SpatialHashGrid funcGrid;
	public float playerX = 0;
	public float playerY = 0;
	private boolean raceStarted = false;
	private boolean raceFinished = false;
	private float startTime = 0;
	private float stopTime = 0;
	
	public Map() {
		
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
			GameObject startTimer = new Func(Func.START_TIMER, new Vector2(startTimerX, 0), new Vector2(startTimerX + 1, 0), new Vector2(startTimerX + 1, worldHeight), new Vector2(startTimerX, worldHeight));
			this.funcGrid.insertStaticObject(startTimer);
		}
		
		NodeList stopTimerN = parser.doc.getElementsByTagName("stoptimer");
		if (stopTimerN.getLength() == 1) {
			
			Element xmlStopTimer = (Element)stopTimerN.item(0);
			float stopTimerX = Float.valueOf(parser.getValue(xmlStopTimer, "x")).floatValue();
			GameObject stopTimer = new Func(Func.START_TIMER, new Vector2(stopTimerX, 0), new Vector2(stopTimerX + 1, 0), new Vector2(stopTimerX + 1, worldHeight), new Vector2(stopTimerX, worldHeight));
			this.funcGrid.insertStaticObject(stopTimer);
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
		
		return true;
	}
	
	public void reloadTextures() {
		
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
	
	public void addGround(TexturedBlock block) {
		
		this.ground.add(block);
		this.groundGrid.insertStaticObject(block);
	}
	
	public TexturedBlock getGround(int i) {
		
		return this.ground.get(i);
	}
	
	public int numGround() {
		
		return this.ground.size();
	}
	
	public void addWall(TexturedBlock block) {
		
		this.walls.add(block);
		this.wallGrid.insertStaticObject(block);
	}
	
	public TexturedBlock getWall(int i) {
		
		return this.walls.get(i);
	}
	
	public int numWalls() {
		
		return this.walls.size();
	}
	
	public TexturedBlock getGround(GameObject o) {
		
		int tallestPart = 0;
		float tallestHeight = 0;
		
		List<GameObject> colliders = groundGrid.getPotentialColliders(o);
		int length = colliders.size();
		if (length == 0) return null;
		for (int i = 0; i < length; i++) {
			
			GameObject part = colliders.get(i);
			if (o.bounds.getPosition().x >= part.bounds.getPosition().x && o.bounds.getPosition().x <= part.bounds.getPosition().x + part.bounds.getWidth()) {
				
				float height = part.bounds.getPosition().y + part.bounds.getHeight();
				if (height > tallestHeight) {
					
					tallestHeight = height;
					tallestPart = i;
				}
			}
		}
		
		return (TexturedBlock)colliders.get(tallestPart);
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
