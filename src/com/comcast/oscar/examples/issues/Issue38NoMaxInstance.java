package com.comcast.oscar.examples.issues;

import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvException;
import com.comcast.oscar.utilities.HexString;

public class Issue38NoMaxInstance {

  @SuppressWarnings("deprecation")
  public static void main(String[] args) throws TlvException {

    String sConfigFileHex =
        "03:01:01:12:01:01:18:25:01:02:00:01:06:01:07:07:01:01:09:04:00:00:0B:E4:0C:02:00:00:0D:02:00:C8:0E:02:0B:E4:0F:01:02:10:04:00:00:00:80:19:24:01:02:00:65:06:01:07:07:01:01:08:04:00:0F:42:40:09:04:00:00:05:F2:0C:02:00:00:0D:02:00:C8:0E:04:00:00:00:00:1D:01:01:11:36:01:04:00:00:00:0A:02:04:00:00:00:0A:03:04:00:00:02:58:04:04:00:00:00:0A:05:04:00:00:00:0A:06:04:00:00:02:58:07:04:00:00:00:3C:08:04:00:00:00:01:09:04:00:00:00:04:1C:02:00:14:19:4C:01:02:00:0D:06:01:07:07:01:05:08:04:00:0F:A0:00:0C:02:00:00:0D:02:00:C8:0E:04:00:00:00:00:2B:11:08:03:00:00:CA:30:10:01:08:00:00:00:1E:00:00:00:01:2B:0F:08:03:00:00:0C:04:08:00:00:00:14:00:00:00:01:04:08:65:4D:54:41:2D:44:53:00:18:4F:01:02:00:17:06:01:07:07:01:05:08:04:00:01:F4:00:0C:02:00:00:0D:02:00:C8:0F:01:02:10:04:00:00:00:80:2B:11:08:03:00:00:CA:30:10:01:08:00:00:00:1E:00:00:00:01:2B:0F:08:03:00:00:0C:04:08:00:00:00:14:00:00:00:01:04:08:65:4D:54:41:2D:55:53:00:17:1A:01:01:0D:03:02:00:0D:06:01:01:0A:0E:01:0C:00:15:D1:7A:64:BA:FF:FF:FF:FF:FF:FF:16:14:01:01:17:03:02:00:17:06:01:01:0A:08:02:06:00:15:D1:7A:64:BA:06:10:F9:83:CC:02:72:3B:0D:56:BB:D0:1A:9F:76:28:98:B0:07:10:7D:64:75:80:0A:6C:45:A7:DF:D8:A3:D7:35:35:3E:07:FF";

    String sConfigFileHexTLV25 =
        "19:4C:"
            + "01:02:00:0D:"
            + "06:01:07:"
            + "07:01:05:"
            + "08:04:00:0F:A0:00:"
            + "0C:02:00:00:"
            + "0D:02:00:C8:"
            + "0E:04:00:00:00:00:"
            + "2B:11:08:03:00:00:CA:30:10:01:08:00:00:00:1E:00:00:00:01:"
            + "2B:0F:08:03:00:00:0C:04:08:00:00:00:14:00:00:00:01:"
            + "04:08:65:4D:54:41:2D:44:53:00:"
            + "FF";
    sConfigFileHexTLV25 =
        "19:13:" + "2B:11:08:03:00:00:CA:30:10:01:08:00:00:00:1E:00:00:00:01:" + "FF";
    sConfigFileHexTLV25 =
        "19:35:"
            + "2B:11:08:03:00:00:CA:30:10:01:08:00:00:00:1E:00:00:00:01:"
            + "2B:0F:08:03:00:00:0C:04:08:00:00:00:14:00:00:00:01:"
            + "2B:0F:08:03:00:00:0C:04:08:00:00:00:14:00:00:00:02:"
            + "FF";

    HexString hs = new HexString(HexString.toByteArray(sConfigFileHex));

    TlvBuilder tb = new TlvBuilder();

    tb.add(hs);

    ConfigurationFileExport cfe =
        new ConfigurationFileExport(tb, ConfigurationFileExport.DOCSIS_VER_30);

    System.out.println(cfe.toPrettyPrint(ConfigurationFileExport.EXPORT_FOUND_TLV));
  }
}
