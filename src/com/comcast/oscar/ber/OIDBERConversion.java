package com.comcast.oscar.ber;

import com.comcast.oscar.utilities.BinaryConversion;
import com.comcast.oscar.utilities.HexString;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @bannerLicense Copyright 2015 Comcast Cable Communications Management, LLC<br>
 *     ___________________________________________________________________<br>
 *     Licensed under the Apache License, Version 2.0 (the "License")<br>
 *     you may not use this file except in compliance with the License.<br>
 *     You may obtain a copy of the License at<br>
 *     http://www.apache.org/licenses/LICENSE-2.0<br>
 *     Unless required by applicable law or agreed to in writing, software<br>
 *     distributed under the License is distributed on an "AS IS" BASIS,<br>
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.<br>
 *     See the License for the specific language governing permissions and<br>
 *     limitations under the License.<br>
 * @author Maurice Garcia (mgarcia01752@outlook.com)
 */
public class OIDBERConversion {
  // Log4J2 logging
  private static final Logger logger = LogManager.getLogger(OIDBERConversion.class);

  private Map<String, Object> mssOidDataTypeValue;

  public final String OID = "OID";
  public final String DATA_TYPE = "DATA_TYPE";
  public final String VALUE = "VALUE";

  /**
   * @param mssOidDataTypeValue
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public OIDBERConversion(Map mssOidDataTypeValue) {
    this.mssOidDataTypeValue = mssOidDataTypeValue;
  }

  /**
   * @return String
   */
  @Override
  public String toString() {

    // Add BER Result
    try {
      mssOidDataTypeValue.put("BER", new HexString(getBER()).toString());
    } catch (Exception e) {
      e.printStackTrace();
    }

    return mssOidDataTypeValue.toString();
  }

  /**
   * @return byte[]
   */
  public byte[] getOIDBER() {

    ByteArrayOutputStream boasOID;

    boasOID = (ByteArrayOutputStream) BERService.encodeOID(stringValue(OID));

    return boasOID.toByteArray();
  }

  /**
   * @return byte[]
   * @throws Exception
   */
  public byte[] getBER() throws Exception {

    int iDataType = Integer.decode(stringValue(DATA_TYPE));

    if (logger.isDebugEnabled()) {
      logger.debug("OIDBERConversion.getBER() DT: " + iDataType);
      logger.debug("OIDBERConversion.getBER() VA: " + stringValue(VALUE));
    }

    if (BERService.isNumberDataType(iDataType)) {

      if (logger.isDebugEnabled())
        logger.debug("OIDBERConversion.getBER() DT-INTEGER: " + iDataType);

      return BERService.setOIDEncodingToByteArray(
          stringValue(OID), (byte) iDataType, Long.decode(stringValue(VALUE)));

    } else if (BERService.isStringDataType(iDataType)) {

      if (logger.isDebugEnabled())
        logger.debug("OIDBERConversion.getBER() DT-STRING: " + iDataType);

      return BERService.setOIDEncodingToByteArray(
          stringValue(OID), (byte) iDataType, stringValue(VALUE));

    } else if (iDataType == BinaryConversion.byteToUnsignedInteger(BERService.HEX)) {

      if (logger.isDebugEnabled()) {
        logger.debug("OIDBERConversion.getBER() DT-HEX: " + iDataType);
        logger.debug("OIDBERConversion.getBER() HEX:    " + stringValue(VALUE));
      }

      return BERService.setOIDEncodingToByteArray(
          stringValue(OID), (byte) iDataType, stringValue(VALUE));
    } else {
      return null;
    }
  }

  private String stringValue(String key) {
    Object value = mssOidDataTypeValue.get(key);
    return value == null ? null : String.valueOf(value);
  }
}
