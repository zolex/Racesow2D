package org.racenet.framework;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.util.Log;

public class XMLParser {

	public Document doc = null;
	
	public XMLParser() {
		
    }
	
	public void read(InputStream xml) {
		
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
 
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setByteStream(xml);
            this.doc = db.parse(is);
            xml.close();
 
        } catch (ParserConfigurationException e) {
        	
            Log.e("XMLParser-Error: ", e.getMessage());
            
        } catch (SAXException e) {
        	
            Log.e("XMLParser-Error: ", e.getMessage());

        } catch (IOException e) {
        	
            Log.e("XMLParser-Error: ", e.getMessage());
        }
	}
	
	public String getValue(Element item, String str) {
		
	    NodeList n = item.getElementsByTagName(str);
	    return this.getNodeValue(n.item(0));
	}
	 
	public final String getNodeValue(Node elem) {
		
		Node child;
		if (elem != null) {
			
			if (elem.hasChildNodes()) {
	    	 
				for (child = elem.getFirstChild(); child != null; child = child.getNextSibling()) {
	        	 
					if (child.getNodeType() == Node.TEXT_NODE) {
	            	 
						return child.getNodeValue();
					}
				}
			}
		}
		
		return "";
	 }    
}
