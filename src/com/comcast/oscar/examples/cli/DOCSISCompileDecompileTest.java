package com.comcast.oscar.examples.cli;

/**
 * @author Allen Flickinger (allen.flickinger@gmail.com)
 *     <p>Copyright 2015 Comcast Cable Communications Management, LLC
 *     ___________________________________________________________________
 *     <p>Licensed under the Apache License, Version 2.0 (the "License") you may not use this file
 *     except in compliance with the License. You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in
 *     writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *     WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *     specific language governing permissions and limitations under the License.
 * @version $Revision: 1.0 $
 */
import com.comcast.oscar.cli.CommandRun;
import com.comcast.oscar.utilities.DirectoryStructure;
import java.io.File;

/** */
public class DOCSISCompileDecompileTest {

  /**
   * Method main.
   *
   * @param args String[]
   */
  public static void main(String[] args) {
    /* Define arguments */
    String arguments[] = new String[5];
    arguments[0] = "-c";
    arguments[1] = "-i";
    arguments[2] = "testfiles/DOCSIS-GOLDEN.txt";
    arguments[3] = "-o";
    arguments[4] = "output/DOCSIS-GOLDEN.bin";

    /* Create subdirectories if they do not exist */
    if (!DirectoryStructure.fCertificatesDir().exists())
      DirectoryStructure.fCertificatesDir().mkdir();
    if (!DirectoryStructure.fDbDir().exists()) DirectoryStructure.fDbDir().mkdir();
    if (!DirectoryStructure.fLicensesDir().exists()) DirectoryStructure.fLicensesDir().mkdir();
    if (!DirectoryStructure.fMibsDir().exists()) DirectoryStructure.fMibsDir().mkdir();
    if (!DirectoryStructure.fMibsBinaryDir().exists()) DirectoryStructure.fMibsBinaryDir().mkdir();
    if (!DirectoryStructure.fMibsTextDir().exists()) DirectoryStructure.fMibsTextDir().mkdir();

    /* Import files if they do not exist */
    DirectoryStructure ds = new DirectoryStructure();
    if (!DirectoryStructure.fDictionaryFile().exists()) ds.exportDictionary();

    /* Run commands */
    CommandRun cmds = new CommandRun();
    cmds.run(arguments);

    /* Checks */
    if (new File(arguments[4]).exists()) {
      System.out.println("OUTPUT FILE " + arguments[4] + " exists");
    } else {
      System.err.println("OUTPUT FILE " + arguments[4] + " does not exist");
    }

    /* Define arguments */
    String newArguments[] = new String[3];
    newArguments[0] = "-d";
    newArguments[1] = "-i";
    newArguments[2] = arguments[4];

    /* Run commands */
    cmds.run(newArguments);
  }
}
