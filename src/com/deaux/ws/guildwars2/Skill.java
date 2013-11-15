package com.deaux.ws.guildwars2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Skill {

	private String name;
	private String profession;
	private String race;
	private String weapon;
	private String attunement;
	private String slot;
	private ArrayList<String> modifiers;
	private String description;
	
	private final static String ADD_TO_DB = "INSERT INTO ?(?) VALUES(?)";
	private final static String FIND_IN_DB = "SELECT ? FROM ? WHERE ? = ?";
	
	public Skill() {
		name = "";
		profession = "";
		weapon = "";
		attunement = "";
		race = "";
		slot = "skill";
		modifiers = new ArrayList<String>();
	}
	
	public void addModifier(String modifier) {
		modifiers.add(modifier);
	}
	
	public ArrayList<String> getModifiers() {
		return modifiers;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getWeapon() {
		return weapon;
	}
	public void setWeapon(String weapon) {
		this.weapon = weapon;
		if(this.slot.equals("skill")) {
			this.slot = "weapon";
		}
	}
	public String getAttunement() {
		return attunement;
	}
	public void setAttunement(String attunement) {
		this.attunement = attunement;
	}
	public String getProffession() {
		return profession;
	}
	public void setProffession(String proffession) {
		this.profession = proffession;
	}
	public String getSlot() {
		return slot;
	}
	public void setSlot(String slot) {
		this.slot = slot;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
	//"INSERT INTO ?(?) VALUES(?)"
	//"SELECT * FROM ? WHERE ? = ?"
	/*
		name = "";
		profession = "";
		weapon = "";
		attunement = "";
		slot = "skill";
		modifiers = new ArrayList<String>();
	 */
	public void addToSqlDb(Connection connection) {
		int skill_id, prof_id, weapon_id, slot_id, race_id;
		int[] mod_ids;
		String bigQuery;
		
		skill_id = prof_id = weapon_id = slot_id = race_id = -1;
		
		if(!this.name.equals("") &&
			Skill.findInDb(connection, "s_id", "Skill", "s_name", this.name) == -1) {
			
			Skill.addToDb(connection, "Skill", "s_name", this.name);
			skill_id = Skill.findInDb(connection, "s_id", "Skill", "s_name", this.name);
			
			if(!this.profession.equals("")){
				prof_id = Skill.findInDb(connection, "p_id", "Profession", "p_name", this.profession);
				if(prof_id == -1) {
					Skill.addToDb(connection, "Profession", "p_name", this.profession);
					prof_id = Skill.findInDb(connection, "p_id", "Profession", "p_name", this.profession);
				}
			}
			
			if(!this.race.equals("")) {
				race_id = Skill.findInDb(connection, "r_id", "Race", "r_name", this.race);
				if(race_id == -1) {
					Skill.addToDb(connection, "Race", "r_name", this.race);
					race_id = Skill.findInDb(connection, "r_id", "Race", "r_name", this.race);
				}
			}
			
			if(!this.weapon.equals("")) {
				weapon_id = Skill.findInDb(connection, "w_id", "Weapon", "w_name", this.weapon);
				if(weapon_id == -1) {
					Skill.addToDb(connection, "Weapon", "w_name", this.weapon);
					weapon_id = Skill.findInDb(connection, "w_id", "Weapon", "w_name", this.weapon);
				}
			}
			
			if(!this.slot.equals("")) {
				slot_id = Skill.findInDb(connection, "ss_id", "Skill_Slot", "ss_name", this.slot);
				if(slot_id == -1) {
					Skill.addToDb(connection, "Skill_Slot", "ss_name", this.slot);
					slot_id = Skill.findInDb(connection, "ss_id", "Skill_Slot", "ss_name", this.slot);
				}
			}
			
			bigQuery = "INSERT INTO FACTS(" +
					(skill_id != -1?"s_id, ": "") +
					(prof_id != -1?"p_id, ": "") +
					(weapon_id != -1?"w_id, ": "") +
					(slot_id != -1?"ss_id, ": "") +
					(race_id != -1?"r_id, ": "");
			
			
			mod_ids = new int[modifiers.size()];
			String tempQuery;
			for(int i = 0; i < modifiers.size(); i++) {
				mod_ids[i] = Skill.findInDb(connection, "sm_id", "Skill_Modifier", "sm_attr", modifiers.get(i));
				if(mod_ids[i] == -1) {
					Skill.addToDb(connection, "Skill_Modifier", "sm_attr",  modifiers.get(i));
					mod_ids[i] = Skill.findInDb(connection, "sm_id", "Skill_Modifier", "sm_attr",  modifiers.get(i));
				}
				tempQuery = bigQuery;
				tempQuery += "sm_id) VALUES(" +
						(skill_id != -1? skill_id + ", ": "") +
						(prof_id != -1? prof_id + ", ": "") +
						(weapon_id != -1? weapon_id + ", ": "") +
						(slot_id != -1? slot_id + ", ": "") +
						(race_id != -1? race_id + ", ": "") +
						mod_ids[i] + ");";
				System.out.println(tempQuery);
				try {
					connection.prepareStatement(tempQuery).executeUpdate();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	
	private static void addToDb(Connection connection, String table, String column, String value) {
		PreparedStatement statement;
		String query = "INSERT INTO " + table + "(" + column + ") VALUES(\'"  + value + "\');";
		try {
			statement = connection.prepareStatement(query);
			statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String toString() {
		String retString = "";
		retString += this.name + "\t" + this.race + "\t" + this.weapon + "\t" + this.slot + "\t";
		
		for(String mod : modifiers) {
			retString += mod + "\t";
		}
		
		return retString;
	}
	
	public static Skill parseSkill(String[] vals) {
		Skill skill = new Skill();
		skill.setName(vals[0]);
		skill.setRace(vals[1]);
		skill.setWeapon(vals[2]);
		skill.setSlot(vals[3]);
		for(int val = 4; val < vals.length; val++) {
			skill.addModifier(vals[val]);
		}
		return skill;
	}
	
	private static int findInDb(Connection connection, String retCol, String table, String column, String value) {
		PreparedStatement statement;
		String query = "SELECT " + retCol + " FROM " + table + " WHERE " + column + "=\'" + value + "\';";
		ResultSet set;
		int retVal = -1;
		try {
			statement = connection.prepareStatement(query);
			set = statement.executeQuery();
			if(set.next()) {
				retVal = set.getInt(retCol);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retVal;
	}

	public String getRace() {
		return race;
	}

	public void setRace(String race) {
		this.race = race;
	}
}
