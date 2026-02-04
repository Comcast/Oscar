package com.comcast.oscar.examples;

import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;
import com.comcast.oscar.test.TestDirectoryStructure;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvException;
import com.comcast.oscar.utilities.HexString;
import java.io.File;

public class DPoEConfigurationFileTest {

  public static void main(String[] args) {

    /* BINARY FILE TO TEXT */
    File dpoeConfigFile = TestDirectoryStructure.fInputDirFileName("DPoE.bin");

    System.out.println(TlvBuilder.tlvDump(HexString.fileToByteArray(dpoeConfigFile)));

    System.out.println(
        "-------------------------------------------------------------------------------------");

    ConfigurationFileExport cfe =
        new ConfigurationFileExport(dpoeConfigFile, ConfigurationFileExport.DPOE_VER_20);

    System.out.println(cfe.toPrettyPrint(ConfigurationFileExport.EXPORT_FOUND_TLV));

    if (false) {

      /*CONFIGURATION FILE OBJECT TO TEXT*/
      ConfigurationFile cf = null;
      try {
        cf = new ConfigurationFile(ConfigurationFile.DPOE_VER_20, cfe.getTlvBuilder());
      } catch (TlvException e) {
        e.printStackTrace();
      }

      cfe = new ConfigurationFileExport(cf);

      System.out.println(cfe.toPrettyPrint(ConfigurationFileExport.EXPORT_FOUND_TLV));

      /*TlvBuilder to Text*/

      TlvBuilder tb = new TlvBuilder();
      try {
        tb.add(new HexString(cf.toByteArray()));
      } catch (TlvException e) {
        e.printStackTrace();
      }

      cfe = new ConfigurationFileExport(tb, ConfigurationFileExport.DPOE_VER_20);

      System.out.println(cfe.toPrettyPrint(ConfigurationFileExport.EXPORT_FOUND_TLV));

      /*Text to Text*/
      ConfigurationFileImport cfi =
          new ConfigurationFileImport(
              new StringBuilder(cfe.toPrettyPrint(ConfigurationFileExport.EXPORT_FOUND_TLV)));

      cf = new ConfigurationFile(ConfigurationFile.DPOE_VER_20, cfi.getTlvBuilder());

      cfe = new ConfigurationFileExport(cf);

      System.out.println(cfe.toPrettyPrint(ConfigurationFileExport.EXPORT_FOUND_TLV));

      /*Text To Binary*/
      cfi =
          new ConfigurationFileImport(
              new StringBuilder(cfe.toPrettyPrint(ConfigurationFileExport.EXPORT_FOUND_TLV)));

      cf = new ConfigurationFile(ConfigurationFile.DPOE_VER_20, cfi.getTlvBuilder());

      cf.setConfigurationFileName(TestDirectoryStructure.fOutputDirFileName("DPoE-1-Test.bin"));

      cf.writeToDisk();

      /*Binary File To Text*/

      tb = new TlvBuilder();

      try {
        tb.add(
            new HexString(
                HexString.fileToByteArray(
                    TestDirectoryStructure.fOutputDirFileName("DPoE-1-Test.bin"))));
      } catch (TlvException e) {
        e.printStackTrace();
      }

      cf = new ConfigurationFile(ConfigurationFile.DPOE_VER_20, tb);

      cfe = new ConfigurationFileExport(cf);

      System.out.println(cfe.toPrettyPrint(ConfigurationFileExport.EXPORT_FOUND_TLV));
    }
  }
}
