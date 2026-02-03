package com.comcast.oscar.examples;

import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.configurationfile.ConfigurationFileException;
import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;
import com.comcast.oscar.test.TestDirectoryStructure;
import java.io.FileNotFoundException;

public class BSODConfigurationFileTest {

  @SuppressWarnings("deprecation")
  public static void main(String[] args) {

    ConfigurationFileImport cfi = null;

    try {
      cfi = new ConfigurationFileImport(TestDirectoryStructure.fInputDirFileName("bsod.txt"));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (ConfigurationFileException e) {
      e.printStackTrace();
    }

    // Convert to a Configuration File Object
    ConfigurationFile cf =
        new ConfigurationFile(ConfigurationFile.DOCSIS_VER_30, cfi.getTlvBuilder());

    ConfigurationFileExport cfe = new ConfigurationFileExport(cf);

    System.out.print(cfe.toPrettyPrint(1));
  }
}
