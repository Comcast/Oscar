/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.configurationfile;

/** Selects the leading text-export banner independently of TLV comments and template contents. */
public enum TemplateBannerLevel {
  /** Historical banner with version, copyright, SPDX, authors, and format instructions. */
  FULL,
  /** Copyright and SPDX license identifier only. */
  SPDX,
  /** Copyright, SPDX license identifier, and author attribution. */
  SPDX_AUTHOR,
  /** No leading banner; configuration text and TLV comments remain unchanged. */
  DISABLE
}
