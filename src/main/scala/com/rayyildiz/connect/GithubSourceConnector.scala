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

import org.apache.kafka.common.config.ConfigDef
import org.apache.kafka.connect.connector.Task
import org.apache.kafka.connect.errors.ConnectException
import org.apache.kafka.connect.source.SourceConnector

import scala.collection.JavaConverters._
import scala.util.{Failure, Try}

class GithubSourceConnector extends SourceConnector with Logging {
  private var configProps: util.Map[String, String] = null

  override def start(props: util.Map[String, String]): Unit = {
    log.info(s"Starting github kafka source connector for $props")
    this.configProps = props

    Try(new GithubSourceConfig(configProps)) match {
      case Failure(exception) => {
        log.error("Could not start github source kafka connector", exception)
        throw new ConnectException("could not start kafka table storage: " + exception.getMessage, exception)
      }
      case _ => log.info("Successfully created task configuration")
    }
  }

  override def stop(): Unit = log.info("Stopping github source kafka connector")
  override def config(): ConfigDef = GithubSourceConfig.config
  override def version(): String = GithubSourceConfig.VERSION
  override def taskClass(): Class[_ <: Task] = classOf[GithubSourceTask]

  override def taskConfigs(maxTasks: Int): util.List[util.Map[String, String]] = {
    log.info(s"Setting taskConfig for $maxTasks workers")
    (1 to maxTasks).map(_ => configProps).toList.asJava
  }
}
