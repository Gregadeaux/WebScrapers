package com.deaux.ws.sc2;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

public class SC2WebScraper {

	private static final String URL = "http://us.battle.net/sc2/en/game/unit/";
	
	private Tidy tidy;
	
	public static void main(String[] args) {
		new SC2WebScraper().getUnits();
	}
	
	public SC2WebScraper() {
		tidy = new Tidy();
	    tidy.setXHTML(true);
	    tidy.setShowWarnings(false);
	    tidy.setShowErrors(0);
	}
	
	public List<String> getUnits() {
		List<String> units = new ArrayList<String>();
		Document doc;
		URL url;
		try {
			url = new URL(URL);		
			doc = tidy.parseDOM(url.openStream(), null);
			XPath xpath = XPathFactory.newInstance().newXPath();
			XPathExpression expr = xpath.compile("//div[@class = 'table-hots']//div[@class = 'unit-datatable']//div//div//div//table//tr//td[@class = 'unit-title']//span//text()");
//		    XPathExpression expr = xpath.compile("//div[@class = 'table-hots']//div[@class = 'unit-datatable']//div//div//div//table//tr//td//span//text() |" +
//		    									 "//div[@class = 'table-hots']//div[@class = 'unit-datatable']//div//div//div//table//tr//td//text()");
			NodeList nodes = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
			
			for(int i = 0; i < nodes.getLength(); i++) {
				if(!nodes.item(i).getNodeValue().trim().isEmpty()) {
					System.out.println(nodes.item(i).getNodeValue().trim());
//					System.out.println(URL + nodes.item(i).getNodeValue().trim().toLowerCase());
					getUnit(new URL(URL + nodes.item(i).getNodeValue().trim().toLowerCase().replace(' ', '-')));
				}
				
			}
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
		return units;
	}
	
	public String getUnit(URL url) {
		String detail = "";
		
		Document doc;
		try {	
			doc = tidy.parseDOM(url.openStream(), null);
			
			System.out.println(getUnitOutline(doc));
			getUnitStats(doc);
			getProductionStats(doc);
			getCombatStats(doc);
			getFieldManual(doc);
			getStrongAgainst(doc);
			getWeakAgainst(doc);
			getCounterMeasures(doc);
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		
		return detail;
	}
	
	public String getUnitOutline(Document doc) throws XPathExpressionException{
		String detail = "";
		XPath xpath = XPathFactory.newInstance().newXPath();
	    XPathExpression expr = xpath.compile("//div[@class = 'outline']//text()");
		NodeList nodes = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
		
		if(!nodes.item(0).getNodeValue().trim().isEmpty()) {
			detail = nodes.item(0).getNodeValue().trim();
		}
		
		return detail;
	}
	
	public String getUnitStats(Document doc) throws XPathExpressionException {
		String url = "";
		XPath xpath = XPathFactory.newInstance().newXPath();
	    XPathExpression expr = xpath.compile("//div[@class = 'stat-data-hots']//div//div[@class = 'basic-stats']//table//tr//td//text() | " +
	    									 "//div[@class = 'stat-data-hots']//div//div[@class = 'basic-stats']//table//tr//td//@class");
		NodeList nodes = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
		
		for(int i = 0; i < nodes.getLength(); i++) {
			if(!nodes.item(i).getNodeValue().trim().isEmpty()) {
				System.out.println(nodes.item(i).getNodeValue().trim());
			}
		}
		
		
		return url;
	}
	
	public String getProductionStats(Document doc) throws XPathExpressionException {
		String url = "";
		XPath xpath = XPathFactory.newInstance().newXPath();
	    XPathExpression expr = xpath.compile("//div[@class = 'stat-data-hots']//div//div[@class = 'production-stats']//table//tr//td//text() | " +
	    									 "//div[@class = 'stat-data-hots']//div//div[@class = 'production-stats']//table//tr//td//@class");
		NodeList nodes = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
		
		for(int i = 0; i < nodes.getLength(); i++) {
			if(!nodes.item(i).getNodeValue().trim().isEmpty()) {
				System.out.println(nodes.item(i).getNodeValue().trim());
			}
		}
		
		
		return url;
	}
	
	public String getCombatStats(Document doc) throws XPathExpressionException {
		String url = "";
		XPath xpath = XPathFactory.newInstance().newXPath();
	    XPathExpression expr = xpath.compile("//div[@class = 'stat-data-hots']//div//div[@class = 'combat-stats']//div//table//tr//td//text() | " +
	    									 "//div[@class = 'stat-data-hots']//div//div[@class = 'combat-stats']//div//table//tr//td//@class | " + 
	    									 "//div[@class = 'stat-data-hots']//div//div[@class = 'combat-stats']//div//table//tr//td//span//@data-tooltip");
		NodeList nodes = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
		
		for(int i = 0; i < nodes.getLength(); i++) {
			if(!nodes.item(i).getNodeValue().trim().isEmpty()) {
				System.out.println(nodes.item(i).getNodeValue().trim());
			}
		}
		
		
		return url;
	}
	
	public void getFieldManual(Document doc) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
	    XPathExpression expr = xpath.compile("//div[@class = 'page-section unit-manual']//div//ul//li//text()");
		NodeList nodes = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
		
		for(int i = 0; i < nodes.getLength(); i++) {
			if(!nodes.item(i).getNodeValue().trim().isEmpty()) {
				System.out.println(nodes.item(i).getNodeValue().trim());
			}
		}
	}
	
	public void getStrongAgainst(Document doc) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
	    XPathExpression expr = xpath.compile("//div[@class = 'strong']/ul/li/a/@href");
		NodeList nodes = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
		
		for(int i = 0; i < nodes.getLength(); i++) {
			if(!nodes.item(i).getNodeValue().trim().isEmpty()) {
				System.out.println(nodes.item(i).getNodeValue().trim().replace("./", ""));
			}
		}
	}
	
	public void getWeakAgainst(Document doc) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
	    XPathExpression expr = xpath.compile("//div[@class = 'weak']/ul/li/a/@href");
		NodeList nodes = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
		
		for(int i = 0; i < nodes.getLength(); i++) {
			if(!nodes.item(i).getNodeValue().trim().isEmpty()) {
				System.out.println(nodes.item(i).getNodeValue().trim().replace("./", ""));
			}
		}
	}
	
	public void getCounterMeasures(Document doc) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
	    XPathExpression expr = xpath.compile("//div[@class = 'page-section countermeasures']/div/div/ul/li/text() |" +
	    									 "//div[@class = 'page-section countermeasures']/div/div/text() |" +
	    									 "//div[@class = 'page-section countermeasures']/div/div/em/text()");
		NodeList nodes = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
		
		for(int i = 0; i < nodes.getLength(); i++) {
			if(!nodes.item(i).getNodeValue().trim().isEmpty()) {
				System.out.println(nodes.item(i).getNodeValue().trim().replace("./", ""));
			}
		}
	}
}
