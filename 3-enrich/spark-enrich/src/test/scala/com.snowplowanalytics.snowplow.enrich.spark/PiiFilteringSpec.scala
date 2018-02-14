/*
 * Copyright (c) 2012-2018 Snowplow Analytics Ltd. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at
 * http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and
 * limitations there under.
 */
package com.snowplowanalytics.snowplow.enrich.spark

import org.specs2.mutable.Specification

object PiiFilteringSpec {
  // Concatenate ALL lines from ALL other jobs
  val lines = bad.BadTrackerCfLinesSpec.lines.lines ++          // 3 bad
              bad.CorruptedCfLinesSpec.lines.lines ++           // 1 bad
              bad.InvalidCfLinesSpec.lines.lines ++             // 3 bad
              bad.UnsupportedPayloadCfLinesSpec.lines.lines ++  // 1 bad = 8 BAD
              good.Aug2013CfLineSpec.lines.lines ++             // 1 good
              good.Sep2013CfLineSpec.lines.lines ++             // 1 good
              good.Oct2013CfLineSpec.lines.lines ++             // 1 good
              good.LateOct2013CfLineSpec.lines.lines ++         // 1 good
              good.Apr2014CfLineSpec.lines.lines ++             // 1 good
              good.FutureCfLineSpec.lines.lines ++              // 1 good
              good.PagePingCfLineSpec.lines.lines ++            // 1 good
              good.PageViewCfLineSpec.lines.lines ++            // 1 good
              good.RefererParserCfLineSpec.lines.lines ++       // 1 good
              good.CampaignAttributionCfLineSpec.lines.lines ++ // 1 good
              good.StructEventCfLineSpec.lines.lines ++         // 1 good
              good.UnstructEventCfLineSpec.lines.lines ++       // 1 good
              good.UnstructEventCfLineSpec.lines.lines ++       // 1 good
              good.TransactionCfLineSpec.lines.lines ++         // 1 good
              good.TransactionItemCfLineSpec.lines.lines ++     // 1 good = 15 GOOD
              misc.DiscardableCfLinesSpec.lines.lines           // 2 discarded
}

/** Pii filtering test which runs using all of the individual good, bad and misc tests */
class PiiFilteringSpec extends Specification with EnrichJobSpec {
  import EnrichJobSpec._
  override def appName = "pii-filter-cf"
  sequential
  "A job which processes a CloudFront file" should{
      "filter out pii events" in
    {
      runEnrichJob(Lines(MasterCfSpec.lines: _*), "cloudfront", "1", false, List("geo"))
      val Some(goods) = readPartFile(dirs.output)
      val Some(bads) = readPartFile(dirs.badRows)
      goods must not(contain("pii"))
      bads must not(contain("pii"))
    }}
}
