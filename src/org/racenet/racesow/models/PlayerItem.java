package org.racenet.racesow.models;

import java.util.List;

/**
 * Class to represent a single player
 * 
 * @author soh#zolex
 *
 */
public class PlayerItem {

	public boolean cancel = false;
	public int position;
	public String name;
	public String session;
	public int points;
	public List<RaceItem> races;
}
