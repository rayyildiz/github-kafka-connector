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

import org.apache.kafka.common.config.ConfigDef.{Importance, Type}
import org.apache.kafka.common.config.{AbstractConfig, ConfigDef, ConfigException}

import scala.collection.JavaConverters._

class GithubSourceConfig(props: util.Map[String, String]) extends AbstractConfig(GithubSourceConfig.config, props) {
  val topic: String = getString(GithubSourceConfig.TOPIC_CONFIG)
  val authUsername: String = getString(GithubSourceConfig.AUTH_USERNAME_CONFIG)
  val authPassword: String = getPassword(GithubSourceConfig.AUTH_PASSWORD_CONFIG).value()
  val githubOwner: String = getString(GithubSourceConfig.GITHUB_OWNER_CONFIG)
  val githubRepo: String = getString(GithubSourceConfig.GITHUB_REPOSITORY_CONFIG)
  val githubBatch: Int = getInt(GithubSourceConfig.GITHUB_BATCH_SIZE_CONFIG)
}

object GithubSourceConfig {
  val VERSION = "0.1.0"
  val GITHUB_OWNER_CONFIG = "github.owner"
  val GITHUB_REPOSITORY_CONFIG = "github.repository"
  val GITHUB_BATCH_SIZE_CONFIG = "github.batch"
  val AUTH_USERNAME_CONFIG = "github.auth.username"
  val AUTH_PASSWORD_CONFIG = "github.auth.password"
  val TOPIC_CONFIG = "topic"

  def apply(map: Map[String, String]): GithubSourceConfig = new GithubSourceConfig(map.asJava)

  val config: ConfigDef = new ConfigDef()
    .define(GITHUB_OWNER_CONFIG, Type.STRING, Importance.HIGH, "Github owner name")
    .define(GITHUB_REPOSITORY_CONFIG, Type.STRING, Importance.HIGH, "Repository name")
    .define(GITHUB_BATCH_SIZE_CONFIG, Type.INT, 100, new BatchSizeValidator(), Importance.HIGH, "Batch size")
    .define(TOPIC_CONFIG, Type.STRING, Importance.HIGH, "Target topic name")
    .define(AUTH_USERNAME_CONFIG, Type.STRING, "", Importance.MEDIUM, "github oauth Username")
    .define(AUTH_PASSWORD_CONFIG, Type.PASSWORD, "", Importance.MEDIUM, "github oauth password")
}

class BatchSizeValidator extends ConfigDef.Validator {
  override def ensureValid(name: String, value: Any): Unit = {
    val batchSize = value.asInstanceOf[Int]
    if (!(1 <= batchSize && batchSize <= 250)) {
      throw new ConfigException(name, value, "Batch size must be between 1-250")
    }
  }
}
