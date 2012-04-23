package org.racenet.racesow.models;

import java.util.ArrayList;
import java.util.List;

public class MapUpdateItem {

	public boolean changed = false;
	public String name;
	public int oldPosition;
	public int newPosition;
	public List<BeatenByItem> beatenBy = new ArrayList<BeatenByItem>();
}
