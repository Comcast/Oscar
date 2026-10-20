/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.examples;

import com.comcast.oscar.ber.OIDToJSONArray;
import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.configurationfile.ConfigurationFileException;
import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;
import com.comcast.oscar.dictionary.TlvDictionary;
import com.comcast.oscar.tlv.TlvAssembler;
import com.comcast.oscar.tlv.TlvException;
import com.comcast.oscar.utilities.HexString;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** */
public class InsertDigitMapToConfigurationBinaryTest {

  private static final Logger logger =
      LogManager.getLogger(InsertDigitMapToConfigurationBinaryTest.class);

  /**
   * @param args
   */
  @SuppressWarnings("deprecation")
  public static void main(String[] args) {

    File fDigitMap = null;
    File fPacketCableTxt = null;
    File fPacketCableBin = null;

    // Get DigitMap
    try {

      fDigitMap =
          new File(
              new java.io.File(".").getCanonicalPath()
                  + File.separatorChar
                  + "testfiles"
                  + File.separatorChar
                  + "digitMap.txt");

    } catch (IOException e) {
      e.printStackTrace();
    }

    // OID
    String sOID = "enterprises.4491.2.2.8.2.1.1.3.1.1.2.1";

    // Convert File to ByteArray
    byte[] bDigitMap = HexString.fileToByteArray(fDigitMap);

    // Need to get the JSON Dictionary Object, in this case, we need to use Snmp64
    TlvDictionary dsqSnmp64 = new TlvDictionary(TlvDictionary.PACKET_CABLE_DICTIONARY_TABLE_NAME);

    // Get JSON Dictionary Object
    JSONObject joDictSnmp64 = dsqSnmp64.getTlvDictionary(64);

    // Create JSON ARRAY for JSON Snmp64 Dictionary
    OIDToJSONArray otjoSnmp64 = new OIDToJSONArray(sOID, bDigitMap);

    try {
      joDictSnmp64 = otjoSnmp64.updateSnmpJsonObject(joDictSnmp64);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    // Validate: http://www.jsoneditoronline.org/
    logger.debug("Snmp64 - JSON-DICTIONARY: {}", joDictSnmp64);

    /* Convert to TLV */

    // Create a 	TlvAssembler for the JSON Dictionary
    TlvAssembler taSnmp64 = null;

    // Create an JSONArray Object - Need to make this more clean
    JSONArray jaSnmp64 = new JSONArray();

    // Add JSONObject to JSONArray for TlvAssembler
    jaSnmp64.put(joDictSnmp64);

    // Get TlvAssembler() with Snmp64 TLV Encoding
    taSnmp64 = new TlvAssembler(jaSnmp64);

    // Display JSONObject
    logger.debug("Snmp64: {}", taSnmp64);

    /*Insert into Configuration File*/

    /********************************
     * 		Text To Binary
     ********************************/

    try {

      fPacketCableTxt =
          new File(
              new java.io.File(".").getCanonicalPath()
                  + File.separatorChar
                  + "testfiles"
                  + File.separatorChar
                  + "IMS-PKT-CABLE-CONFIG-NO-DIGITMAP.txt");

    } catch (IOException e) {
      e.printStackTrace();
    }

    ConfigurationFileImport cfiPacketCable = null;

    try {
      try {
        cfiPacketCable = new ConfigurationFileImport(fPacketCableTxt);
      } catch (ConfigurationFileException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    ConfigurationFile cfSnmp64 =
        new ConfigurationFile(ConfigurationFile.PKT_CBL_VER_20, cfiPacketCable.getTlvBuilder());

    // Add DigitMap
    cfSnmp64.add(taSnmp64);

    ConfigurationFile cfPacketCAble =
        new ConfigurationFile(ConfigurationFile.PKT_CBL_VER_20, cfiPacketCable.getTlvBuilder());

    cfPacketCAble.commit();

    try {
      fPacketCableBin =
          new File(
              new java.io.File(".").getCanonicalPath()
                  + File.separatorChar
                  + "testfiles"
                  + File.separatorChar
                  + File.separatorChar
                  + "output"
                  + File.separatorChar
                  + "IMS-PKT-CABLE-CONFIG-WITH-DIGIT-MAP.bin");

    } catch (IOException e) {
      e.printStackTrace();
    }

    cfPacketCAble.setConfigurationFileName(fPacketCableBin);

    if (cfPacketCAble.writeToDisk()) {
      System.out.println("Write to File: ");
    } else {
      System.out.println("Write to File FAILED: ");
    }

    /********************************
     * 		Binary To Text
     ********************************/

    System.out.println(
        "+========================================================================================================================+");

    try {
      fPacketCableBin =
          new File(
              new java.io.File(".").getCanonicalPath()
                  + File.separatorChar
                  + "testfiles"
                  + File.separatorChar
                  + "IMS-PKT-CABLE-CONFIG-NO-DIGITMAP.bin");

    } catch (IOException e) {
      e.printStackTrace();
    }

    ConfigurationFileExport cfePacketCable = new ConfigurationFileExport(fPacketCableBin);

    try {
      cfSnmp64 =
          new ConfigurationFile(ConfigurationFile.PKT_CBL_VER_20, cfePacketCable.getTlvBuilder());
    } catch (TlvException e) {
      e.printStackTrace();
    }

    // Add Snmp64 DigitMap
    cfSnmp64.add(taSnmp64);

    ConfigurationFileExport cfeSnmp64Insert = new ConfigurationFileExport(cfSnmp64);

    System.out.println(cfeSnmp64Insert.toPrettyPrint(0));
  }
}
