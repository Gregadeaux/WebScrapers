package com.deaux.ws.guildwars2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

public class GW2WebScraper {
	
	public static void getDataFromWeb(Connection connection) {
		Tidy tidy = new Tidy();
	    tidy.setXHTML(true);
	    tidy.setShowWarnings(false);
	    tidy.setShowErrors(0);
	    //connect();
    	getSkills(tidy, connection);
	}
	
	public static void getDataFromFile(File file, Connection connection) {
		try {
			Scanner scanner = new Scanner(file);
			while(scanner.hasNext()) {
				Skill.parseSkill(scanner.nextLine().split("\t")).addToSqlDb(connection);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void getSkills(Tidy tidy, Connection connection) {
		// Parse an HTML page into a DOM document
	    Thread[] thread = new Thread[13];
	    final String[] urls = {
	    		"http://wiki.guildwars2.com/wiki/List_of_elementalist_skills",
	    		"http://wiki.guildwars2.com/wiki/List_of_warrior_skills",
	    		"http://wiki.guildwars2.com/wiki/List_of_guardian_skills",
	    		"http://wiki.guildwars2.com/wiki/List_of_engineer_skills",
	    		"http://wiki.guildwars2.com/wiki/List_of_ranger_skills",
	    		"http://wiki.guildwars2.com/wiki/List_of_mesmer_skills",
	    		"http://wiki.guildwars2.com/wiki/List_of_thief_skills",
	    		"http://wiki.guildwars2.com/wiki/List_of_necromancer_skills",
	    		"http://wiki.guildwars2.com/wiki/List_of_asura_racial_skills",
	    		"http://wiki.guildwars2.com/wiki/List_of_charr_racial_skills",
	    		"http://wiki.guildwars2.com/wiki/List_of_human_racial_skills",
	    		"http://wiki.guildwars2.com/wiki/List_of_norn_racial_skills",
	    		"http://wiki.guildwars2.com/wiki/List_of_sylvari_racial_skills"
	    };
	    String[] cats = {
	    	"elementalist",
	    	"warrior",
	    	"guardian",
	    	"engineer",
	    	"ranger",
	    	"mesmer",
	    	"thief",
	    	"necromancer",
	    	"asura",
	    	"charr",
	    	"human",
	    	"norn",
	    	"sylvari"
	    };
	    ArrayList<Skill>[] skills = new ArrayList[13];
		try {
			for(int i = 0; i < 13; i++) {
				skills[i] = new ArrayList<Skill>();
				thread[i] = new Thread(new SkillRunnable(new URL(urls[i]), tidy, skills[i],
						(i < 8?true: false), cats[i]));
				thread[i].start();
			}
			for(int i = 0; i < 13; i++) {
				thread[i].join();
			}
			for(int i = 0; i < 13; i++) {
				for(int j = 0; j < skills[i].size(); j++) {
					skills[i].get(j).addToSqlDb(connection);
				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static class SkillRunnable implements Runnable {
		private URL url;
		private Tidy tidy;
		private ArrayList<Skill> skills;
		private boolean isProf;
		private String profRace;
		
		public SkillRunnable(URL url, Tidy tidy, ArrayList<Skill> skill, boolean isProf, String profRace) {
			this.url = url;
			this.tidy = tidy;
			this.skills = skill;
			this.isProf = isProf;
			this.profRace = profRace;
		}
		
		@Override
		public void run() {		    
			Document doc;
			Skill skill;
			try {
				doc = tidy.parseDOM(url.openStream(), null);
				XPath xpath = XPathFactory.newInstance().newXPath();
			    XPathExpression expr = xpath.compile("//div[@id = 'bodyContent']/table/tr/td/div/div/a/@href");
			    NodeList nodes = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
			    for(int i = 0; i < nodes.getLength()-1; i++) {
				    	skill = getSkills(tidy, new URL("http://wiki.guildwars2.com" + nodes.item(i).getNodeValue().replaceAll("File:", "").replaceAll(".png", "")));
				    	if(skill != null) {
				    		if(isProf) {
				    			skill.setProffession(profRace);
				    		}else {
				    			skill.setRace(profRace);
				    		}
				    		skills.add(skill);
				    	}
			    }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XPathExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		   
		}
		
	}
	
	public static Skill getSkills(Tidy tidy, URL url) {
		Skill skill;
		try {
			 Document doc = tidy.parseDOM(url.openStream(), null);
			 skill = new Skill();
			 
			 XPath xpath = XPathFactory.newInstance().newXPath();
			 /**** NAME ****/
			 XPathExpression expr = xpath.compile( "//h1/text()");// |" +
					 													  //"//blockquote/dl/dd/text() |" +
					 													  //"//blockquote/dl/dd/a/text() |" +
					 													  //"//div[@id = 'bodyContent']/div/p/text() ");//|" +
					 													  //"//div[@id = 'bodyContent']/div/div/dl/dd/a/text()"); 
			 NodeList nodes = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
			 for(int i = 0; i < nodes.getLength(); i++) {
				skill.setName(nodes.item(i).getNodeValue());
			 }
			 System.out.println(skill.getName());
			 
			 /**** MODIFIERS ****/
			expr = xpath.compile( "//blockquote/dl/dd |" +
								  "//blockquote/dl/dd/text() |" +
								  "//blockquote/dl/dd/a/text()");
			nodes = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
			
			String modifier = "";
			for(int i = 2; i < nodes.getLength(); i++) {
				if(nodes.item(i).getLocalName().equals("#text")) {
					modifier += nodes.item(i).getNodeValue();
				}else {
					skill.addModifier(modifier.trim());
					modifier = "";
					i++;
				}
			 }
			
			/**** DESCRIPTION ****/
			expr = xpath.compile( "//blockquote/p[@style = 'margin-bottom:0;']/text() |" +
								  "//blockquote/p[@style = 'margin-bottom:0;']/a/text()");
			nodes = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
			String description = "";
			for(int i = 0; i < nodes.getLength(); i++) {
				description += nodes.item(i).getNodeValue();
			 }
			 skill.setDescription(description);
			 System.out.println(skill.getDescription());
			
			/**** ATTRIBUTES ****/
			 expr = xpath.compile( "//div[@id = 'bodyContent']/div/div/dl/dd/a/text() |"
					 + "//div[@id = 'bodyContent']/div/div/dl/dt/text() |"
					 + "//div[@id = 'bodyContent']/div/div/dl/dt/a/text()");// |" +
			  		//"//blockquote/dl/dd/text() |" +
			  		//"//blockquote/dl/dd/a/text() |" +
			  		//"//div[@id = 'bodyContent']/div/p/text() ");//|" +
			  		//""); 
			 nodes = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
			 
			 String nodeVal = "";
			 String setString = "";
			 for(int i = 0; i < nodes.getLength();) {
				 nodeVal = nodes.item(i).getNodeValue();
				 if(nodeVal.equalsIgnoreCase("Type")) {
					 i = nodes.getLength();
				 }else if(nodeVal.equalsIgnoreCase("Profession")) {
					 nodeVal = nodes.item(++i).getNodeValue();
					 while(!nodeVal.equalsIgnoreCase("Weapon") && !nodeVal.equalsIgnoreCase("Attunement")
							 && !nodeVal.equalsIgnoreCase("Slot") && !nodeVal.equalsIgnoreCase("Parent Skill")
							 && !nodeVal.equalsIgnoreCase("Env. Weapon") && !nodeVal.equalsIgnoreCase("Activ. type")) {
						 setString += nodeVal + " ";
						 if(i < nodes.getLength()-1) {
							 nodeVal = nodes.item(++i).getNodeValue();
						 }else {
							 nodeVal = "Attunement";
						 }
					 }
					 skill.setProffession(setString);
					 setString = "";
				 }else if(nodeVal.equalsIgnoreCase("Weapon")) {
					 if(i < nodes.getLength() - 1) {
						 nodeVal = nodes.item(++i).getNodeValue();
					 }else {
						 nodeVal = "Attunement";
						 i++;
					 }
					 while(!nodeVal.equalsIgnoreCase("Attunement") && !nodeVal.equalsIgnoreCase("Slot")
							 && !nodeVal.equalsIgnoreCase("Parent Skill") && !nodeVal.equalsIgnoreCase("Env. Weapon")
							 && !nodeVal.equalsIgnoreCase("Activ. type") && !nodeVal.equalsIgnoreCase("Sequence")
							 && !nodeVal.equalsIgnoreCase("Type")) {
						 setString += nodeVal + " ";
						 if(i < nodes.getLength()-1) {
							 nodeVal = nodes.item(++i).getNodeValue();
						 }else {
							 nodeVal = "Attunement";
						 }
					 }
					 skill.setWeapon(setString);
					 setString = "";
				 }else if(nodeVal.equalsIgnoreCase("Attunement")) {
					 nodeVal = nodes.item(++i).getNodeValue();
					 while(!nodeVal.equalsIgnoreCase("Slot") && !nodeVal.equalsIgnoreCase("Activ. type")
							 && !nodeVal.equalsIgnoreCase("Type") && !nodeVal.equalsIgnoreCase("Sequence")
							 && !nodeVal.equalsIgnoreCase("Parent Skill") && !nodeVal.equalsIgnoreCase("Env. Weapon")) {
						 setString += nodeVal + " ";
						 if(i < nodes.getLength()-1) {
							 nodeVal = nodes.item(++i).getNodeValue();
						 }else {
							 nodeVal = "Slot";
						 }
					 }
					 skill.setAttunement(setString);
					 setString = "";
				 }else if(nodeVal.equalsIgnoreCase("Slot")) {
					 nodeVal = nodes.item(++i).getNodeValue();
					 while(!nodeVal.equalsIgnoreCase("Type") && !nodeVal.equalsIgnoreCase("Activ. type")
							 && !nodeVal.equalsIgnoreCase("Sequence") && !nodeVal.equalsIgnoreCase("Parent skill")) {
						 setString += nodeVal + " ";
						 if(i < nodes.getLength()-1) {
							 nodeVal = nodes.item(++i).getNodeValue();
						 }else {
							 nodeVal = "Type";
						 }
					 }
					 skill.setSlot(setString);
					 setString = "";
				 }else {
					 i++;
				 }
			 }
			 System.out.println(skill.getProffession());
			 System.out.println(skill.getWeapon());
			 System.out.println(skill.getAttunement());
			 System.out.println(skill.getSlot());
			 return skill;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		return null;
		
	}
	
	public static Trait[] getTraits(Tidy tidy, URL url) {
		try {
			 Document doc = tidy.parseDOM(url.openStream(), null);
			 
			 Trait[] traits = new Trait[5];
			 traits[0] = new Trait();
			 traits[1] = new Trait();
			 traits[2] = new Trait();
			 traits[3] = new Trait();
			 traits[4] = new Trait();
			 
			 getTraitNames(tidy, doc, traits);
			 getTraitDescription(tidy, doc, traits);
			 getTraitStats(tidy, doc, traits);
			 getTraitBoons(tidy, doc, traits);
			
			 return traits;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void getTraitNames(Tidy tidy, Document doc, Trait[] traits) {
		try {
			 XPath xpath = XPathFactory.newInstance().newXPath();
			 XPathExpression expr = xpath.compile("//div[@id = 'bodyContent']/h3/span[@class = 'mw-headline']/text()"); 
			 NodeList nodes = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
			 
			 for(int i = 0; i < nodes.getLength(); i++) {
				 traits[i].setName(nodes.item(i).getNodeValue());
			 }
		}catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
	
	public static void getTraitDescription(Tidy tidy, Document doc, Trait[] traits) {
		try {
			 XPath xpath = XPathFactory.newInstance().newXPath();
			 XPathExpression expr = xpath.compile("//div[@id = 'bodyContent']/p/text() |" +
					  													 "//div[@id = 'bodyContent']/p/a/text()"); 
			 NodeList nodes = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
			 String totalString = "";
			 for(int i = 5; i < nodes.getLength(); i++) {
				    	totalString += nodes.item(i).getNodeValue();
			 }
			 
			 String[] traitDescriptions = totalString.split("Per point:");
			 for(int i = 0; i < 5; i++) {
				 traits[i].setDescription(traitDescriptions[i]);
			 }
		}catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
	
	public static void getTraitStats(Tidy tidy, Document doc, Trait[] traits) {
		try {
			 XPath xpath = XPathFactory.newInstance().newXPath();
			 XPathExpression expr = xpath.compile("//div[@id = 'bodyContent']/dl/dd/text() |" +
					 													 "//div[@id = 'bodyContent']/dl/dd/a/text()"); 
			 NodeList nodes = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
			 String modifiers = "";
			 for(int i = 1; i < nodes.getLength(); i++) {
				 modifiers += nodes.item(i).getNodeValue() + nodes.item(i+1).getNodeValue() + "\n";
				 i += 2;
			 }
			 String[] listModifiers = modifiers.split("\n");
			 for(int i = 0; i < listModifiers.length; i++) {
				 traits[i/2].setModifier1(listModifiers[i]);
				 traits[i/2].setModifier2(listModifiers[++i]);
			 }
		}catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
	
	public static void getTraitBoons(Tidy tidy, Document doc, Trait[] traits) {
		try {
			 XPath xpath = XPathFactory.newInstance().newXPath();
			 XPathExpression expr = xpath.compile("//table[@id = 'traits']/tr/td/text() |" +
					 													 "//table[@id = 'traits']/tr/td/a/text()"); 
			 NodeList nodes = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
			 ArrayList<String> skills = new ArrayList<String>();
			 String temp;
			 String nodeVal;
			 for(int i = 0; i < nodes.getLength(); ) {
				 nodeVal = nodes.item(i++).getNodeValue() + ", ";
				 ++i;
				 temp = nodeVal;
				 nodeVal = nodes.item(i++).getNodeValue() + ", ";
				 while(!nodeVal.equals("Adept") && !nodeVal.equals("Master") && !nodeVal.equals("Grandmaster") && i < nodes.getLength())
				 {
					 if(nodeVal.trim() != "") {
						 temp += nodeVal;
					 }

					 nodeVal = nodes.item(i++).getNodeValue();
				 }
				 skills.add(temp);
				 //temp = nodes.item(i++).getNodeValue() + ",";
				 System.out.println(temp);
				 --i;
			 }
		}catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
	
}
/*Trait [] traits;
try {
	traits = getTraits(tidy, new URL("http://wiki.guildwars2.com/wiki/List_of_elementalist_traits"));
    for(Trait trait : traits) {                                        
    		System.out.println(trait);
    }
    traits = getTraits(tidy, new URL("http://wiki.guildwars2.com/wiki/List_of_engineer_traits"));
    for(Trait trait : traits) {
    		System.out.println(trait);
    }
    traits = getTraits(tidy, new URL("http://wiki.guildwars2.com/wiki/List_of_mesmer_traits"));
    for(Trait trait : traits) {
    		System.out.println(trait);
    }
    traits = getTraits(tidy, new URL("http://wiki.guildwars2.com/wiki/List_of_warrior_traits"));
    for(Trait trait : traits) {
    		System.out.println(trait);
    }
    traits = getTraits(tidy, new URL("http://wiki.guildwars2.com/wiki/List_of_guardian_traits"));
    for(Trait trait : traits) {
    		System.out.println(trait);
    }
    traits = getTraits(tidy, new URL("http://wiki.guildwars2.com/wiki/List_of_ranger_traits"));
    for(Trait trait : traits) {
    		System.out.println(trait);
    }
    traits = getTraits(tidy, new URL("http://wiki.guildwars2.com/wiki/List_of_necromancer_traits"));
    for(Trait trait : traits) {
    		System.out.println(trait);
    }
    
} catch (MalformedURLException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}*/