package com.comcast.oscar.examples;

import com.comcast.oscar.ber.BEROIDConversion;
import com.comcast.oscar.utilities.HexString;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.snmp4j.smi.OID;

public class BerObjectOnlyTypeTest {

  public static void main(String[] args) {

    String sObjectOnlyDec = "1.3.6";

    OID oid = new OID(sObjectOnlyDec);

    ByteArrayOutputStream baosOID = new ByteArrayOutputStream();

    try {
      oid.encodeBER(baosOID);
    } catch (IOException e) {
      e.printStackTrace();
    }

    // Convert to Hex
    HexString hs = new HexString(baosOID.toByteArray());
    System.out.println(
        "Convert: " + sObjectOnlyDec + " -> " + hs.toString(":") + " <- " + oid.toString());

    /********************************************************************************/
    String sObjectOnlyHex = "300406022B06";

    HexString hsHexOID = new HexString(HexString.toByteArray(sObjectOnlyHex));

    BEROIDConversion ocOID = new BEROIDConversion(hsHexOID.toByteArray());
    System.out.println("Hex -> OID-DOT: " + ocOID.getOidDotNotaion());
    ocOID.getOidValue();
  }
}
