package com.comcast.oscar.test;

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

/** */
public interface TestMethod {

  public String SHARED_SECRET = "SHAREDSECRET";

  public void TextToBinaryFullDOCSISConfiguration();

  /** */
  public void TextToBinaryDOCSIS();

  /** */
  public void TextToBinaryPacketCable();

  /** */
  public void TextToBinaryInsertManufactureCVC();

  /** */
  public void TextToBinaryInsertCoSignCVC();

  /** */
  public void TextToBinaryInsertDownstreamFrequecy();

  /** */
  public void TextToBinaryInsertMaxCPE();

  /** */
  public void TextToBinaryInsertTLV();

  /** */
  public void TextToBinaryInsertTFTPServerAddress();

  /** */
  public void TextToBinaryPacketCableInsertDigitMap();

  /** */
  public void BinaryToTextDOCSIS();

  /** */
  public void BinaryToTextPacketCable();

  /** */
  public void GetDigitMapFromBinary();

  /** */
  public void BinaryToBinaryDOCSIS();

  /** */
  public void BinaryToBinaryPacketCable();

  /** */
  public void BinaryToTextDOCSISMaxCPE();

  /** */
  public void BinaryToTextDOCSISFirmwareFileName();

  /** */
  public void BinaryToTextDOCSISManCVC();

  /** */
  public void BinaryToTextDOCSISCoSignCVC();

  /** */
  public void BinaryToTextDOCSISTFTPServerAddress();

  /** */
  public void BinaryToTextDOCSIS_TLV_MaxCPE_Firmware_Man_CoSignCVC();

  /** */
  public void BinaryToBinaryDOCSIS_TLV_MaxCPE_Firmware_Man_CoSignCVC();

  /** */
  public void PrintDefaultDOCSISConfigurationTemplate();

  /** */
  public void PrintDefaultPacketCableConfigurationTemplate();

  /** */
  public void PrintTlvDotNotationDOCSISDefinition();

  /** */
  public void PrintTlvDotNotationPacketCableDefinition();

  /** */
  public void PrintJSONToTlv();

  /** */
  public void PrintTextToBinaryPacketCableJSONDump();

  /** */
  public void PrintTextToBinaryDOCSISJSONDump();

  /** */
  public void PrintBinaryToTextPacketCableJSONDump();

  /** */
  public void PrintBinaryToTextDOCSISJSONDump();

  /** */
  public void PrintDOCSISTlvToJSON();

  /** */
  public void PrintPacketCableTlvToJSON();

  /** */
  public void BinaryToTextPacketCableInsertDigitMap();

  /** */
  public void PrintTextToBinaryPacketCableHexDump();

  /** */
  public void PrintTextToBinaryDOCSISHexDump();

  /** */
  public void PrintBinaryToTextDOCSISHexDump();

  /** */
  public void PrintBinaryToTextPacketCableHexDump();

  /** */
  public void PrintBinaryToTextPacketCableDigitMap();

  /** */
  public void PrintTextToBinaryPacketCableDigitMap();

  /** */
  public void BinaryToBinaryInsertMultiDigitMap();

  /** */
  public void BinaryToTextInsertMultiDigitMap();

  /** */
  public void TextToBinaryInsertMultiDigitMap();

  /** */
  public void TextToTextMergeTemplateDOCSIS();

  /** */
  public void BinaryToTextInsertOID();

  /** */
  public void TextToBinaryInsertOID();
}
