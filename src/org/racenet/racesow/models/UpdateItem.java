package org.racenet.racesow.models;

import java.util.ArrayList;
import java.util.List;

public class UpdateItem {

	public int id;
	public boolean changed = false;
	public int oldPoints;
	public int newPoints;
	public int oldPosition;
	public int newPosition;
	public String createdAt;
	public List<MapUpdateItem> maps = new ArrayList<MapUpdateItem>();
}
