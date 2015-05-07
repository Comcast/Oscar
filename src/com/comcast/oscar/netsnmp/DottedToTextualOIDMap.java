package com.comcast.oscar.netsnmp;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * This class is used to increase the Conversion time while using snmptranslate CLI
 * 
 * @author Maurice
 *
 */
public class DottedToTextualOIDMap extends HashMap<String,String>{

	private static final long serialVersionUID = 1L;
	private ArrayList<String> alsDottedOID = new ArrayList<String>();

	public DottedToTextualOIDMap() {
		super();
	}
	
	public DottedToTextualOIDMap(ArrayList<String> alsDottedOID) {
		this.alsDottedOID.addAll(alsDottedOID);
	}
	
	public void addDottedOIDArrayList(ArrayList<String> alsDottedOID) {
		this.alsDottedOID.addAll(alsDottedOID);
	}
	
}
