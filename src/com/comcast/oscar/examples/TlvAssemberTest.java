package com.comcast.oscar.examples;

/*
	Copyright 2015 Comcast Cable Communications Management, LLC
	___________________________________________________________________
	Licensed under the Apache License, Version 2.0 (the "License")
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	http://www.apache.org/licenses/LICENSE-2.0
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
	
	@author Maurice Garcia (maurice.garcia.2015@gmail.com)

*/


import org.json.JSONArray;
import org.json.JSONException;

import com.comcast.oscar.tlv.TlvAssembler;

/**
 */
public class TlvAssemberTest {

	/**
	 * Method main.
	 * @param args String[]
	 */
	public static void main(String[] args) {
		
		System.out.println("+-----------------------------------------TLV: 3-NetworkAccess--------------------------------------------------------------------+");
		String sTlv3JsonArray = "[{\"LengthMax\":0,\"LengthMin\":0,\"Value\":1,\"TlvName\":\"NetworkAccess\",\"DataType\":\"integer\",\"AreSubTypes\":false,\"Type\":\"3\",\"SupportDocsisVersion\":\"1.0\",\"ParentTypeList\":[-1,3]}]";

		TlvAssembler taTlv3JsonArray = null;
		
		try {
			taTlv3JsonArray = new TlvAssembler(new JSONArray(sTlv3JsonArray));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		System.out.println("Tlv3JsonArray: " + taTlv3JsonArray.toString());

		System.out.println("+----------------------------------------TLV: 11-OID---------------------------------------------------------------------+");
		String sTlv11JsonArray = "[{\"ByteLength\":1,\"LengthMax\":0,\"LengthMin\":0,\"Value\":{\"OID\":\"1.3.6.1.6.3.16.1.2.1.3.1.12.114.101.97.100.119.114.105.116.101.115.101.99\",\"VALUE\":\"readwritegroup\",\"DATA_TYPE\":\"4\"},\"TlvName\":\"SnmpMib\",\"DataType\":\"oid\",\"AreSubTypes\":false,\"Type\":\"11\",\"SupportDocsisVersion\":\"1.0\",\"ParentTypeList\":[-1,11]}]";

		TlvAssembler taTlv11JsonArray = null;
		
		try {
			taTlv11JsonArray = new TlvAssembler(new JSONArray(sTlv11JsonArray));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		System.out.println("Tlv11JsonArray: " + taTlv11JsonArray.toString());
		
		System.out.println("+----------------------------------------TLV: 17-BaselinePrivacy--------------------------------------------------------------------+");		
		String sTlv17JsonArray = "[{\"LengthMax\":0,\"LengthMin\":0,\"TlvName\":\"BaselinePrivacy\",\"DataType\":\"integer\",\"AreSubTypes\":true,\"Type\":\"17\",\"SubTypeArray\":[{\"LengthMax\":4,\"LengthMin\":0,\"Value\":10,\"TlvName\":\"AuthorizeWaitTimeout\",\"DataType\":\"integer\",\"AreSubTypes\":false,\"Type\":\"1\",\"SupportDocsisVersion\":\"1.0\",\"ParentTypeList\":[-1,17,1]},{\"LengthMax\":4,\"LengthMin\":0,\"Value\":10,\"TlvName\":\"ReauthorizeWaitTimeout\",\"DataType\":\"integer\",\"AreSubTypes\":false,\"Type\":\"2\",\"SupportDocsisVersion\":\"1.0\",\"ParentTypeList\":[-1,17,2]},{\"LengthMax\":4,\"LengthMin\":0,\"Value\":600,\"TlvName\":\"KekGraceTime\",\"DataType\":\"integer\",\"AreSubTypes\":false,\"Type\":\"3\",\"SupportDocsisVersion\":\"1.0\",\"ParentTypeList\":[-1,17,3]},{\"LengthMax\":4,\"LengthMin\":0,\"Value\":10,\"TlvName\":\"OpWaitTimeout\",\"DataType\":\"integer\",\"AreSubTypes\":false,\"Type\":\"4\",\"SupportDocsisVersion\":\"1.0\",\"ParentTypeList\":[-1,17,4]},{\"LengthMax\":4,\"LengthMin\":0,\"Value\":10,\"TlvName\":\"RekeyWaitTimeout\",\"DataType\":\"integer\",\"AreSubTypes\":false,\"Type\":\"5\",\"SupportDocsisVersion\":\"1.0\",\"ParentTypeList\":[-1,17,5]},{\"LengthMax\":4,\"LengthMin\":0,\"Value\":600,\"TlvName\":\"TekGraceTime\",\"DataType\":\"integer\",\"AreSubTypes\":false,\"Type\":\"6\",\"SupportDocsisVersion\":\"1.0\",\"ParentTypeList\":[-1,17,6]},{\"LengthMax\":4,\"LengthMin\":0,\"Value\":60,\"TlvName\":\"AuthorizeRejectWaitTimeout\",\"DataType\":\"integer\",\"AreSubTypes\":false,\"Type\":\"7\",\"SupportDocsisVersion\":\"1.0\",\"ParentTypeList\":[-1,17,7]},{\"LengthMax\":4,\"LengthMin\":0,\"Value\":1,\"TlvName\":\"SAMapWaitTimeout\",\"DataType\":\"integer\",\"AreSubTypes\":false,\"Type\":\"8\",\"SupportDocsisVersion\":\"1.0\",\"ParentTypeList\":[-1,17,8]},{\"LengthMax\":4,\"LengthMin\":0,\"Value\":4,\"TlvName\":\"SAMapMaxRetries\",\"DataType\":\"integer\",\"AreSubTypes\":false,\"Type\":\"9\",\"SupportDocsisVersion\":\"1.0\",\"ParentTypeList\":[-1,17,9]}],\"SupportDocsisVersion\":\"1.0\"}]";

		TlvAssembler taTlv17JsonArray = null;
		
		try {
			taTlv17JsonArray = new TlvAssembler(new JSONArray(sTlv17JsonArray));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		System.out.println("Tlv17JsonArray: " + taTlv17JsonArray.toString());

		System.out.println("+----------------------------------------TLV: 32-Co-CVC---------------------------------------------------------------------+");		
		String sTlv32JsonArray = "[{\"LengthMax\":0,\"LengthMin\":0,\"Value\":\"308203A63082028EA00302010202106C0943EC39872D8FECDA6C489E5E9030300D06092A864886F70D0101050500308197310B300906035504061302555331393037060355040A133044617461204F766572204361626C65205365727669636520496E746572666163652053706563696669636174696F6E7331153013060355040B130C4361626C65204D6F64656D73313630340603550403132D444F43534953204361626C65204D6F64656D20526F6F7420436572746966696361746520417574686F72697479301E170D3031303931323030303030305A170D3133303931313233353935395A306A310B300906035504061302555331223020060355040A1319417272697320496E7465726163746976652C204C2E4C2E432E310F300D060355040B1306444F43534953312630240603550403131D436F646520566572696669636174696F6E20436572746966696361746530820122300D06092A864886F70D01010105000382010F003082010A0282010100D40C5AEACFD1E9AB5F9E5088CA835EEB487369852CCD9A92E2F7FDE9B28CAACA21189C0B696CE1F2666C0BC8C7183877CE519E527030DAF8437538EC12CAA44922472397F5259E067131D046F67B1E2B25EF37B443B89D3F86DB6655252595C427A6D641DB7B392E8E01D7BBDF73D2138A85553747D6C0685F76BC80C82E755F8ACC87C65650A120FE492F14B48C97A806327949C5FAAA438AC5F0A976474FD26CDE4556D52BFA5DC7508FA7F150FA454C681F5108085296F2C94C371B57CD193E8939D6268C8CD96E3E8A06CABA6D5CB82989C1751E21E77F09CA73DA4DAA1FDE5BC68BED4B8EB5B0EA3ABB23D4249790B2A093DDE0806573138CBB99FDCEB6FFC10203010001A31A301830160603551D250101FF040C300A06082B06010505070303300D06092A864886F70D0101050500038201010094F271A8BBBAC1D7FC054260170B59F0B6AC9DA70D0148699E0D240B033D9C34918F7737CD45B822EDF9E1BEC4C5B030D878B6378B5A7995D032E933EC0AE0DB436A3F52181FFEE08E0CD4F6201F111520B00F4CF1BEF41C9C58EF166582B5E7F1AA1AFCD7BADEB7B0097845C2D95EE89F66767F3BC080C2994E8C39CA9F22BCC22151BA9221B7CA87E81FDAD3FCF48FEB61C739EC751AC00AFACD88CCC99D11EE6A96C5A591F625875ED7AF60460E7330DEE05EC183D1717AF3F2702DF0253FD1A49CBCA6A654B37CB7846F91C11E3A83D1E68C28AA25A35D9849393B009B9A5328E5729017D35BB841D62F950007BA7D73E2FFF4E14D0F3A49193805495714811B\",\"TlvName\":\"Co-CVC\",\"DataType\":\"multi_tlv_byte_array\",\"AreSubTypes\":false,\"Type\":\"32\",\"SupportDocsisVersion\":\"1.0\",\"ParentTypeList\":[-1,32]}]";

		TlvAssembler taTlv32JsonArray = null;
		
		try {
			taTlv32JsonArray = new TlvAssembler(new JSONArray(sTlv32JsonArray));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		System.out.println("Tlv17JsonArray: " + taTlv32JsonArray.toString());		
	}

}
