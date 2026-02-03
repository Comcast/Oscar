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

@author Maurice Garcia (mgarcia01752@outlook.com)

*/

import com.comcast.oscar.dictionary.DictionarySQLConstants;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvDisassemble;
import com.comcast.oscar.tlv.TlvException;
import com.comcast.oscar.utilities.BinaryConversion;
import com.comcast.oscar.utilities.HexString;
import java.io.File;

/** */
public class TlvDisassembleTest {

  /**
   * @param args
   */
  public static void main(String[] args) {

    /*
    		System.out.println("+----------------------------------TLV-DISASSEMBLER---------------------------------------------------+");

    		TlvBuilder tlvBuilderTLV24 		= new TlvBuilder();
    		TlvBuilder tlvBuilderTLV24_1	= new TlvBuilder();
    		TlvBuilder tlvBuilderTLV24_4 	= new TlvBuilder();

    		TlvBuilder tlvBuilderTLV24_5 	= new TlvBuilder();
    		TlvBuilder tlvBuilderTLV24_5_1 	= new TlvBuilder();
    		TlvBuilder tlvBuilderTLV24_5_2 	= new TlvBuilder();
    		TlvBuilder tlvBuilderTLV24_5_3 	= new TlvBuilder();

    		//24.1
    		tlvBuilderTLV24_1.add(1, 65535);

    		System.out.println("TLV-TLV-BUILDER-24_1: " + tlvBuilderTLV24_1);

    		//24.4
    		HexString hsNulTermString = new HexString();
    		hsNulTermString.add("Maurice", true);
    		tlvBuilderTLV24_4.add(4, hsNulTermString.toByteArray());

    		System.out.println("TLV-TLV-BUILDER-24_4: " + tlvBuilderTLV24_4 + " -> HexNulStr: " + hsNulTermString);

    		hsNulTermString = new HexString();
    		hsNulTermString.add("Garcia", true);
    		tlvBuilderTLV24_4.add(4, hsNulTermString.toByteArray());

    		System.out.println("TLV-TLV-BUILDER-24_4: " + tlvBuilderTLV24_4 + " -> HexNulStr: " + hsNulTermString);


    		//24.5.1
    		tlvBuilderTLV24_5_1.add(1, 0x01);

    		//24.5.2
    		tlvBuilderTLV24_5_2.add(2, 0x02);

    		//24.5.3
    		HexString hsNulTermString2 = new HexString();
    		hsNulTermString2.add("AdvanceCPE Group", true);
    		tlvBuilderTLV24_5_3.add(4, hsNulTermString2.toByteArray());

    		tlvBuilderTLV24_5.add(tlvBuilderTLV24_5_1);
    		tlvBuilderTLV24_5.add(tlvBuilderTLV24_5_1);
    		tlvBuilderTLV24_5.add(tlvBuilderTLV24_5_2);
    		tlvBuilderTLV24_5.add(tlvBuilderTLV24_5_1);
    		tlvBuilderTLV24_5.add(tlvBuilderTLV24_5_3);
    		tlvBuilderTLV24_5.encapsulateTlv(5);

    		System.out.println("TLV-TLV-BUILDER-24_5: " + tlvBuilderTLV24_5);

    		tlvBuilderTLV24.add(tlvBuilderTLV24_1);
    		tlvBuilderTLV24.add(tlvBuilderTLV24_4);
    		tlvBuilderTLV24.add(tlvBuilderTLV24_5);
    		tlvBuilderTLV24.encapsulateTlv(24);

    		System.out.println("TLV-TLV-BUILDER-24: " + tlvBuilderTLV24);

    		byte[] bParentChildTlvEncodeList = {0x18,0x05,0x01};
    		byte[] bTlvIndexList = TlvBuilder.findTLVIndex(tlvBuilderTLV24.toByteArray(), bParentChildTlvEncodeList);

    		System.out.println("bTlvIndexList: " + new HexString(bTlvIndexList).toString());

    		TlvDisassemble tlvDisassemble = new TlvDisassemble(tlvBuilderTLV24);
    		tlvDisassemble.getTlvDictionary();

    */
    System.out.println(
        "+----------------------------------READ-DOCSIS-FILE---------------------------------------------------+");

    File file = new File("c:\\d10_syscontact.cm");

    TlvDisassemble tdDocsisConfig =
        new TlvDisassemble(file, DictionarySQLConstants.DOCSIS_DICTIONARY_TABLE_NAME);

    System.out.println(
        "+----------------------------------SORT MAJOR"
            + " TLV---------------------------------------------------+");

    int iTLV = 0;

    for (byte[] bTLV : tdDocsisConfig.toListByteArray()) {

      iTLV = BinaryConversion.byteToUnsignedInteger(bTLV[0]);

      System.out.println(
          "+--------------------------------TLV: "
              + iTLV
              + "---------------------------------------------------+");

      System.out.println("TLV: " + new HexString(bTLV).toString());

      TlvBuilder tbTLV = new TlvBuilder();

      try {
        tbTLV.add(new HexString(bTLV));
      } catch (TlvException e) {
        e.printStackTrace();
      }

      TlvDisassemble tlvDisassemble =
          new TlvDisassemble(tbTLV, DictionarySQLConstants.DOCSIS_DICTIONARY_TABLE_NAME);

      System.out.println(tlvDisassemble.getTlvDictionary().toString());
    }
  }
}
