package org.pathways.openciss.shared;

public class GenderMap {
	public static int map(int db_gender) {
		System.out.println("incoming gender: " + db_gender);
		int gender;
		switch (db_gender) {
			case 2: gender = 0; break;
			case 1: gender = 1; break;
			case 3: gender = 2; break;
			case 4: gender = 3; break;
			case 5: gender = 4; break;
			case 8: gender = 8; break;
			case 9: gender = 9; break;
			default: gender = -1;
		}
		System.out.println("outgoing gender: " + gender);
		
		return gender;
	}
}
	