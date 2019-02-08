/*
 * Copyright (c) 2018 Ramazan AYYILDIZ
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.rayyildiz.connect
import java.util

import org.apache.kafka.connect.data.Struct
import org.apache.kafka.connect.source.{SourceRecord, SourceTask}

import scala.collection.JavaConverters._
class GithubSourceTask extends SourceTask with Logging {
  private val LAST_ISSUE_NUMBER = "last_issue"

  private var config: GithubSourceConfig = null
  private var lastIssueNumber = 0

  override def start(props: util.Map[String, String]): Unit = {
    log.info("Task is starting")
    config = GithubSourceConfig(props)

    val lastOffset = context.offsetStorageReader().offset(sourcePartition.asJava)
    if (lastOffset == null) {
      log.info("It is the first time to fetch data")
      lastIssueNumber = 1
    } else {
      val obj = lastOffset.getOrDefault(LAST_ISSUE_NUMBER, 0)
      if (obj != null && obj.isInstanceOf[Int]) {
        lastIssueNumber = obj.asInstanceOf[Int]
      }
    }
    log.info(s"Last issue number is $lastIssueNumber")
  }

  override def poll(): util.List[SourceRecord] = {

    log.info("Task is polling")
    val url = s"https://api.github.com/repos/${config.githubOwner}/${config.githubRepo}/issues"

    GithubApi.call(url).map(issues => {})

    ???
  }
  override def stop(): Unit = ???
  override def version(): String = GithubSourceConfig.VERSION

  private def generateSourceRecord(issue: Issue): SourceRecord = new SourceRecord(
    sourcePartition,
    sourceOffset(issue.id).asJava,
    config.topic,
    null, // partition will be inferred by the framework
    GithubSchema.keySchema,
    buildRecordKey(issue),
    GithubSchema.issueSchema,
    buildRecordValue(issue)
  )

  def sourcePartition: Map[String, String] =
    Map(GithubSourceConfig.GITHUB_OWNER_CONFIG -> config.githubOwner, GithubSourceConfig.GITHUB_REPOSITORY_CONFIG -> config.githubRepo)

  def sourceOffset(id: Int): Map[String, String] = Map("issue_id" -> id.toString)

  def buildRecordKey(issue: Issue): Struct =
    new Struct(GithubSchema.keySchema)
      .put("owner", config.githubOwner)
      .put("repo", config.githubRepo)
      .put("issue_id", issue.id)

  def buildRecordValue(issue: Issue): Struct = {
    val issueStruct = new Struct(GithubSchema.issueSchema)
      .put("id", issue.id)
      .put("node_id", issue.node_id)
      .put("title", issue.title)
      .put("state", issue.state)
      .put("created_at", issue.created_at)
      .put("body", issue.body)

    if (issue.updated_at.isDefined) issueStruct.put("updated_at", issue.updated_at.get)

    if (issue.pr.isDefined) {
      val structPr = new Struct(GithubSchema.prSchema)
        .put("url", issue.pr.get.url)
        .put("diff_url", issue.pr.get.diffUrl)

      issueStruct.put("pull_request", structPr)
    }

    val userStruct = new Struct(GithubSchema.userSchema)
      .put("id", issue.user.id)
      .put("login", issue.user.login)
      .put("site_admin", issue.user.site_admin)

    issueStruct.put("user", userStruct)

    issueStruct
  }
}
