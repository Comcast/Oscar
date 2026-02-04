package com.comcast.oscar.cli.commands;

import com.comcast.oscar.configurationfile.ConfigurationFileException;
import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;
import com.comcast.oscar.tlv.TlvDisassemble;
import com.comcast.oscar.tlv.TlvException;
import java.io.File;
import java.io.FileNotFoundException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

/**
 * @author Allen Flickinger (allen.flickinger@gmail.com)
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
 */
public class JSONDisplay {

  /**
   * Set option parameters for command JSON display
   *
   * @return Option
   */
  public static final Option OptionParameters() {
    OptionBuilder.withValueSeparator(' ');
    OptionBuilder.withLongOpt("json");
    OptionBuilder.withDescription("Display the JSON of the input file.");
    return OptionBuilder.create("j");
  }

  /**
   * Print JSON display from binary file
   *
   * @param file
   * @param tlvDisassemble
   */
  public void printJSONDisplayFromBinary(
      File file, String tlvDisassemble, int configurationFileType) {
    TlvDisassemble td = null;
    ConfigurationFileExport cfe = new ConfigurationFileExport(file, configurationFileType);

    try {
      td = new TlvDisassemble(cfe.getTlvBuilder(), tlvDisassemble);
    } catch (TlvException e) {
      e.printStackTrace();
    }

    System.out.println(td.getTlvDictionary().toString());
  }

  /**
   * Print JSON display from text file
   *
   * @param file
   * @param tlvDisassemble
   */
  public void printJSONDisplayFromText(File file, String tlvDisassemble) {
    TlvDisassemble td = null;
    ConfigurationFileImport cfi = null;

    try {
      cfi = new ConfigurationFileImport(file);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (ConfigurationFileException e) {
      e.printStackTrace();
    }

    td = new TlvDisassemble(cfi.getTlvBuilder(), tlvDisassemble);
    System.out.println(td.getTlvDictionary().toString());
  }
}
