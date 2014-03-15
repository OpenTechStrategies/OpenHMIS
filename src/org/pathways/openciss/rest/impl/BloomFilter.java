// Deprecated by memcache version
package org.pathways.openciss.rest.impl;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import com.google.appengine.api.rdbms.AppEngineDriver;

//Bloom Filter (k=3)
public class BloomFilter implements Serializable{
	
	static final long serialVersionUID = 1L;
	static final int M=2048;
	int b[]=new int[M];
	
	static final int f1(int x) {
	    return Math.abs(x*9973%M);
	}
	static final int f2(int x) {
	    return Math.abs(x*x*x%M);
	}
	static final int f3(int x) {
	    return Math.abs(((((x<<5)+3)<<7)+111)%M);
	}
	
	public void insert(String s) {
		int nameHash = s.hashCode();
		b[f1(nameHash)]=1; 
		b[f2(nameHash)]=1; 
		b[f3(nameHash)]=1;
	}
	
	public boolean test(String s) {
		int nameHash = s.hashCode();
		// checking for a match with db populated entries
		if (b[f1(nameHash)]+b[f1(nameHash)]+b[f1(nameHash)]==3) {
			return true;
		}
		return false;
	}
 	public void populate() {
		//System.out.println("Inside TriePopulate");
		AppEngineDriver a = new AppEngineDriver();
		
		try {
			Connection conn = null;
			DriverManager.registerDriver(a);
			conn = DriverManager.getConnection("jdbc:google:rdbms://pcni.org:openhmis:openciss/compass");
			Statement st = conn.createStatement();
			//System.out.println("SELECT " + type + " FROM `compass`.`path_client`;");
			ResultSet rs;
			
			rs = st.executeQuery("SELECT name_first, name_last FROM `compass`.`path_client`;");
			
			while (rs.next()) {
				//System.out.println("inserting rs value: " + rs.getString(1));
				this.insert(rs.getString(1) + rs.getString(2));
				//System.out.println("trying to insert:" + rs.getString(1)+rs.getString(2));
			}

		} catch (SQLException e) {e.printStackTrace();}
	}
	
	public static void main(String[] args) {

		BloomFilter bf = new BloomFilter();
		
		String[] names = {"ChrisHarris", "ChrisHarris", "ChrisBarris", "Chrisbarris", "ChrisSmith", "JaneSmith", "BobSmith", "EdSmith"};
		
		for (int i=0; i<names.length; i++) {
			System.out.println("names[i] being handled is: " + names[i]);
			int name = names[i].hashCode();
			//Integer nameInt = Integer.parseInt(names[i]);
			System.out.println("Converted number is: " + name);
			bf.b[f1(name)]=1; 
			bf.b[f2(name)]=1; 
			bf.b[f3(name)]=1;
		}
		System.out.println(Arrays.toString(bf.b));
		int n=0; //test for false positives
		for (int i=101 ; i<=200; i++)
			if (bf.b[f1(i)]+bf.b[f1(i)]+bf.b[f1(i)]==3) //   
				System.out.println(i + ": " + f1(i) + " " + f2(i) + " " + f3(i) + " :: "+ bf.b[f1(i)] + ", " + bf.b[f1(i)] + ", " + bf.b[f1(i)] +"/"+ ++n+" ");
				//output: 256-25 512-19 1024-7 2048-4
			else 
				System.out.println(i + ": " + f1(i) + " " + f2(i) + " " + f3(i) + " :: " + bf.b[f1(i)] + ", " + bf.b[f1(i)] + ", " + bf.b[f1(i)]);
	}
}
