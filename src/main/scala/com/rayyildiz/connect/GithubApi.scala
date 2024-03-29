/*
 * Copyright (c) 2018 Ramazan AYYILDIZ
 *
 * Permission is hereby granted, free of char
 * to any person obtaining a copy
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
import java.net.URL

import spray.json.DefaultJsonProtocol._
import spray.json._

object GithubApi extends Logging {

  import com.softwaremill.sttp._
  import com.softwaremill.sttp.sprayJson._

  object IssueFormat {
    implicit val labelFormat: RootJsonFormat[Label] = jsonFormat3(Label)
    implicit val prFormat: RootJsonFormat[PR] = jsonFormat2(PR)
    implicit val userFormat: RootJsonFormat[User] = jsonFormat3(User)
    implicit val issueFormat: RootJsonFormat[Issue] = jsonFormat10(Issue)
  }

  def call(repo: String): Either[String, Seq[Issue]] = {
    log.info(s"Repo URL is $repo")

    implicit val backend = HttpURLConnectionBackend()

    import IssueFormat._

    val response = sttp
      .get(Uri(new URL(repo).toURI))
      .response(asJson[Seq[Issue]])
      .send()

    log.info(s"Response ${response.code}")
    response.body
  }

}
