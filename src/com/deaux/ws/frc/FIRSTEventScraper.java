package com.deaux.ws.frc;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

public class FIRSTEventScraper {

	private final String FRCURL = "https://my.usfirst.org/myarea/index.lasso";
	
	public static void main(String[] args) {
		FIRSTEventScraper fes = new FIRSTEventScraper();
		List<Regional> regionalUrls;

		try {
			regionalUrls = fes.getRegionals(new URL("https://my.usfirst.org/myarea/index.lasso?event_type=FRC&year=2013"));
			for(Regional regional : regionalUrls) {
				regional.setTeams(fes.getTeams(new URL(regional.getUrl())));
			}
			
			Regional wisconsin = null;
			Regional crossroad = null;
			List<Regional> weekOne = new ArrayList<Regional>();
			
			for(Regional regional : regionalUrls) {
				/*System.out.println("Name: " + regional.getName());
				System.out.println("Type: " + regional.getType());
				System.out.println("Venue: " + regional.getVenue());
				System.out.println("Location: " + regional.getLocation());
				System.out.println("Dates: " + regional.getDate());
				
				List<Integer> list = regional.getTeams();
				System.out.println("Number of Teams: " + list.size());
				for(int counter = 0; counter < list.size(); counter++) {
					System.out.println(list.get(counter).intValue());
				}*/
				
				if(regional.getName().contains("Wisconsin")) {
					wisconsin = regional;
				}
				
				if(regional.getName().contains("Crossroad")) {
					crossroad = regional;
				}
				
				if(regional.getDate().contains("07-Mar")) {
					weekOne.add(regional);
				}
			}
			List<Integer> temp;
			for(Regional r : weekOne) {
				temp = wisconsin.getEqualTeams(r);
				System.out.println(r.getName());
				System.out.println(temp.size());
				for(int counter = 0; counter < temp.size(); counter++) {
					System.out.println(temp.get(counter));
				}
			}
			
			for(Regional r : weekOne) {
				temp = crossroad.getEqualTeams(r);
				System.out.println(r.getName());
				System.out.println(temp.size());
				for(int counter = 0; counter < temp.size(); counter++) {
					System.out.println(temp.get(counter));
				}
			}
			
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public List<Regional> getRegionals(URL url) {
		Document doc;
		Tidy tidy = new Tidy();
		List<Regional> retVal = new ArrayList<Regional>();
		Regional temp; 
		String link = "";
		String name = "";
		int teamCounter = 0;
		
		try {
			
			doc = tidy.parseDOM(url.openStream(), null);
			XPath xpath = XPathFactory.newInstance().newXPath();
		    XPathExpression expr = xpath.compile("//tr[@bgcolor = '#FFFFFF']/td/a/@href|//tr[@bgcolor = '#FFFFFF']/td/text()");
			NodeList nodes = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
			
			for(int i = 0; i < nodes.getLength();) {
				temp = new Regional();
				temp.setType(nodes.item(i++).getNodeValue());
				link = nodes.item(i++).getNodeValue();
				link = link.replace("event_details", "event_teamlist");
				temp.setUrl(FRCURL + link);
				temp.setVenue(nodes.item(i++).getNodeValue());
				temp.setLocation(nodes.item(i++).getNodeValue());
				temp.setDate(nodes.item(i++).getNodeValue());
				
				retVal.add(temp);
				
				link = "";
				
			}
			
			expr = xpath.compile("//tr[@bgcolor = '#FFFFFF']/td/a/text()|//tr[@bgcolor = '#FFFFFF']/td/a/em/text()");
			nodes = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
			
			for(int i = 0; i < nodes.getLength();) {
				
				name = nodes.item(i++).getNodeValue();
				while(i < nodes.getLength() && (nodes.item(i).getNodeValue().equals("FIRST") || nodes.item(i).getNodeValue().equals(" Robotics District Competition") || nodes.item(i).getNodeValue().equals(" Championship"))) {
					name += nodes.item(i++).getNodeValue();
				}
				retVal.get(teamCounter++).setName(name);
				
			}
		    
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		
		return retVal;
	}
	
	public List<Integer> getTeams(URL url) {
		Document doc;
		Tidy tidy = new Tidy();
		List<Integer> retVal = new ArrayList<Integer>();
		
		try {
			doc = tidy.parseDOM(url.openStream(), null);
			XPath xpath = XPathFactory.newInstance().newXPath();
		    XPathExpression expr = xpath.compile("//tr[@bgcolor = '#FFFFFF']/td/a/text()");
			NodeList nodes = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
			for(int i = 0; i < nodes.getLength(); i++) {
				retVal.add(Integer.parseInt(nodes.item(i).getNodeValue()));
			}
		    
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		
		return retVal;
	}
}
