package com.comcast.oscar.examples;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.comcast.oscar.cablelabsdefinitions.Constants;
import com.comcast.oscar.configurationfile.ConfigurationFileException;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;
import com.comcast.oscar.netsnmp.NetSNMP;
import com.comcast.oscar.test.TestDirectoryStructure;

public class NetSnmptranslateLocalAccess {

	public static void main(String[] args) {

		NetSNMP netSNMP = new NetSNMP();

		ConfigurationFileImport cfi = null;
		
		try {
			try {
				cfi = new ConfigurationFileImport(TestDirectoryStructure.fInputDirFileName("DOCSIS-GOLDEN.bin.txt"));
			} catch (ConfigurationFileException e) {
				e.printStackTrace();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		JSONArray ja = cfi.getTopLevelTLVJSON(Constants.SNMP_MIB_OBJ);
		
		JSONObject jo = null;
		
		for (int iCounter = 0; iCounter < ja.length(); iCounter++) {
			
			try {
				jo = ja.getJSONObject(iCounter);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			JSONArray jaValue = null;
			
			try {
				jaValue = jo.getJSONArray("Value");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			try {
				
				System.out.println(jaValue.getJSONObject(0).getString("OID"));
				
				netSNMP.add(jaValue.getJSONObject(0).getString("OID"));
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
		
		ArrayList<String> als = netSNMP.OidName2OidDec();
		
		System.out.println(als.toString());
		
		netSNMP.clear();
		
		netSNMP.addAll(als);
		
		als = netSNMP.OidDec2OidName();
		
		System.out.println(als.toString());
		
	}
	
}
