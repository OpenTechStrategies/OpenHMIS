package org.pathways.openciss.shared;

public class EthnicityMap {
public static int map(int db_ethnicity) {
		System.out.println("incoming ethnicity: " + db_ethnicity);
		int ethnicity;
		switch (db_ethnicity) {
		case 104: ethnicity = 0; break;
		case 105: ethnicity = 1; break;
		case 8: ethnicity = 8; break;
		case 9: ethnicity = 9; break;
		default: ethnicity = -1;
	}
		System.out.println("outgoing ethnicity: " + ethnicity);
		return ethnicity;
	}
}
