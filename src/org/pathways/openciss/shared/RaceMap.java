package org.pathways.openciss.shared;

public class RaceMap {
	public static int map(int db_race) {
		System.out.println("incoming race: " + db_race);
		int race;
		switch (db_race) {
			case 7: race = 1; break;
			case 5: race = 2; break;
			case 6: race = 3; break;
			case 9: race = 4; break;
			case 8: race = 5; break;
			case 15: race = 8; break;
			case 16: race = 9; break;
			default: race = -1;
		}
		System.out.println("outgoing race: " + race);
		return race;
	}
}
