package com.comcast.oscar.tlv;

import static com.comcast.oscar.cablelabsdefinitions.Constants.END_OF_FILE;
import static com.comcast.oscar.cablelabsdefinitions.Constants.PAD;

import com.comcast.oscar.dictionary.TlvRegistry;
import com.comcast.oscar.tlv.TlvDisassemblyReport.Entry;
import com.comcast.oscar.tlv.TlvDisassemblyReport.Status;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** Bounded wire-order traversal, deliberately independent of legacy buffer searches. */
final class TlvDiagnosticWalker {
  private final byte[] bytes;
  private final DisassemblyOptions options;
  private final TlvRegistry registry;
  private int entryCount;
  private boolean stopped;

  TlvDiagnosticWalker(byte[] bytes, DisassemblyOptions options) {
    this.bytes = bytes;
    this.options = options;
    registry =
        TlvRegistry.forFamily(
            options.family() == DisassemblyOptions.Family.DOCSIS ? "docsis" : "dpoe");
  }

  TlvDisassemblyReport inspect() {
    if (bytes.length == 0) {
      return new TlvDisassemblyReport(
          bytes,
          List.of(
              new Entry(List.of(), 1, "Empty input", 0, 0, -1, 0, Status.EMPTY_INPUT, List.of())));
    }
    return new TlvDisassemblyReport(bytes, walk(0, bytes.length, List.of()));
  }

  private List<Entry> walk(int startOffset, int parentEnd, List<Integer> parentPath) {
    var entries = new ArrayList<Entry>();
    var occurrences = new HashMap<Integer, Integer>();
    int offset = startOffset;
    while (offset < parentEnd && !stopped) {
      if (entryCount >= options.maxEntries()) {
        entries.add(
            new Entry(
                parentPath,
                1,
                "Uninspected remainder",
                offset,
                offset,
                -1,
                parentEnd,
                Status.ENTRY_LIMIT,
                List.of()));
        stopped = options.stopAtFirstFailure();
        break;
      }
      entryCount++;
      int type = Byte.toUnsignedInt(bytes[offset]);
      var path = new ArrayList<>(parentPath);
      path.add(type);
      int occurrence = occurrences.merge(type, 1, Integer::sum);
      if (parentPath.isEmpty() && (type == PAD || type == END_OF_FILE)) {
        entries.add(
            new Entry(
                path,
                occurrence,
                type == PAD ? "Padding" : "End",
                offset,
                offset + 1,
                0,
                offset + 1,
                type == PAD ? Status.PADDING : Status.END,
                List.of()));
        offset++;
        if (type == END_OF_FILE) {
          if (offset < parentEnd) {
            boolean padding = true;
            for (int trailingOffset = offset; trailingOffset < parentEnd; trailingOffset++) {
              if (Byte.toUnsignedInt(bytes[trailingOffset]) != PAD) {
                padding = false;
                break;
              }
            }
            entries.add(
                new Entry(
                    List.of(),
                    1,
                    "After end marker",
                    offset,
                    offset,
                    parentEnd - offset,
                    parentEnd,
                    padding ? Status.PADDING : Status.TRAILING_DATA,
                    List.of()));
          }
          break;
        }
        continue;
      }
      var definition = registry.find(path);
      String name = definition == null ? "Unknown" : definition.name();
      int lengthWidth = definition == null ? 1 : definition.lengthBytes();
      if (lengthWidth > parentEnd - offset - 1) {
        entries.add(
            new Entry(
                path,
                occurrence,
                name,
                offset,
                parentEnd,
                -1,
                parentEnd,
                Status.TRUNCATED_HEADER,
                List.of()));
        stopped = options.stopAtFirstFailure();
        break;
      }
      int length = 0;
      for (int lengthIndex = 1; lengthIndex <= lengthWidth; lengthIndex++) {
        length = (length << 8) | Byte.toUnsignedInt(bytes[offset + lengthIndex]);
      }
      int valueOffset = offset + 1 + lengthWidth;
      if (length > parentEnd - valueOffset) {
        entries.add(
            new Entry(
                path,
                occurrence,
                name,
                offset,
                valueOffset,
                length,
                parentEnd,
                Status.TRUNCATED_VALUE,
                List.of()));
        stopped = options.stopAtFirstFailure();
        break;
      }
      int endOffset = valueOffset + length;
      Status status = definition == null ? Status.UNKNOWN_TYPE : Status.OK;
      List<Entry> children = List.of();
      if (definition != null) {
        if (length < definition.lengthMin()
            || (definition.lengthMax() > 0 && length > definition.lengthMax())) {
          status = Status.INVALID_LENGTH;
        }
        if (!registry.children(path).isEmpty()
            && length > 0
            && !(options.stopAtFirstFailure() && status == Status.INVALID_LENGTH)) {
          if (path.size() >= options.maxDepth()) {
            status = Status.DEPTH_LIMIT;
          } else {
            children = walk(valueOffset, endOffset, path);
          }
        }
      }
      entries.add(
          new Entry(
              path, occurrence, name, offset, valueOffset, length, endOffset, status, children));
      if (options.stopAtFirstFailure() && status != Status.OK && status != Status.UNKNOWN_TYPE) {
        stopped = true;
      }
      offset = endOffset;
    }
    return List.copyOf(entries);
  }
}
