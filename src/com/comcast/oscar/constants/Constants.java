/*
 * Copyright 2015 - 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.constants;

public class Constants {

  private static final String TAB = "\t";

  public static final String OSCAR_VERSION = BuildInfo.version();
  public static final String OSCAR_CLI_USAGE =
      "java -jar oscar.jar -k <key> -i <.txt> -o <.bin> -c";
  public static final String OSCAR_CLI_HEADER =
      "/* OSCAR -> OpenSource Cable modem file AssembleR v" + OSCAR_VERSION + " */";
  public static final String OSCAR_CLI_NO_ARGS = "Type --help for information";

  public static final String ERR_MIS_PAR = "Parameter missing, please use correct syntax";
  public static final String ERR_NOT_BIN = "is not a binary file";
  public static final String ERR_DNE = "does not exist";

  public static final String OSCAR_BANNER =
      "OSCAR: OpenSource Cable modem file AssembleR v" + OSCAR_VERSION;

  public static final String APACHE_20_LICENCE_DISCLAIMER =
      TAB
          + OSCAR_BANNER
          + "\n\n"
          + TAB
          + "Copyright 2015 - 2026 Comcast Cable Communications Management, LLC\n"
          + TAB
          + "SPDX-License-Identifier: Apache-2.0\n\n"
          + TAB
          + "Author: Maurice Garcia (mgarcia01752@outlook.com)\n"
          + TAB
          + "Author: Allen Flickinger (allen.flickinger@gmail.com)\n";
}
