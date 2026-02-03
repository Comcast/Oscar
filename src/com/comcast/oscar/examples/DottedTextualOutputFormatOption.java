package com.comcast.oscar.examples;

import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.test.TestDirectoryStructure;

public class DottedTextualOutputFormatOption {

  public static void main(String[] args) {

    ConfigurationFileExport cfe =
        new ConfigurationFileExport(
            TestDirectoryStructure.fInputDirFileName("bsod.cm"),
            ConfigurationFileExport.DOCSIS_VER_31);

    cfe.setDotTextOIDOutputFormat(ConfigurationFileExport.DOTTED_OID_FORMAT);

    System.out.println(cfe.toPrettyPrint(ConfigurationFileExport.EXPORT_FOUND_TLV));

    cfe.setDotTextOIDOutputFormat(ConfigurationFileExport.TEXTUAL_OID_FORMAT);

    System.out.println(cfe.toPrettyPrint(ConfigurationFileExport.EXPORT_FOUND_TLV));
  }
}
