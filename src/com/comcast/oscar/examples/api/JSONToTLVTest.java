package com.comcast.oscar.examples.api;

import com.comcast.oscar.tlv.TlvAssembler;
import org.json.JSONArray;
import org.json.JSONException;

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
public class JSONToTLVTest {

  /**
   * @param args
   */
  public static void main(String[] args) {

    System.out.println(
        "+-----------------------------------------TLV:"
            + " 3-NetworkAccess--------------------------------------------------------------------+");
    String sTlv3JsonArray =
        "[{\"LengthMax\":0,\"LengthMin\":0,\"Value\":1,\"TlvName\":\"NetworkAccess\",\"DataType\":\"integer\",\"AreSubTypes\":false,\"Type\":\"3\",\"SupportDocsisVersion\":\"1.0\",\"ParentTypeList\":[-1,3]}]";

    TlvAssembler taTlv3JsonArray = null;

    try {
      taTlv3JsonArray = new TlvAssembler(new JSONArray(sTlv3JsonArray));
    } catch (JSONException e) {
      e.printStackTrace();
    } finally {
      taTlv3JsonArray = new TlvAssembler();
    }

    System.out.println("TLV: " + taTlv3JsonArray.toString());
  }
}
