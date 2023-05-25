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
import org.apache.kafka.connect.data.{Schema, SchemaBuilder}

object GithubSchema {

  val userSchema: Schema = SchemaBuilder
    .struct()
    .name("GITHUB_USER_SCHEMA")
    .version(1)
    .field("login", Schema.STRING_SCHEMA)
    .field("id", Schema.INT32_SCHEMA)
    .field("site_admin", Schema.BOOLEAN_SCHEMA)
    .build()

  val prSchema: Schema = SchemaBuilder
    .struct()
    .name("GITHUB_PR_SCHEMA")
    .version(1)
    .field("url", Schema.STRING_SCHEMA)
    .field("diff_url", Schema.STRING_SCHEMA)
    .optional()
    .build()

  val labelItemSchema: Schema = SchemaBuilder
    .struct()
    .name("GITHUB_LABEL_SCHEMA_ITEM")
    .version(1)
    .field("id", Schema.INT64_SCHEMA)
    .field("name", Schema.STRING_SCHEMA)
    .field("color", Schema.STRING_SCHEMA)
    .build()

  val labelSchema: Schema = SchemaBuilder
    .array(labelItemSchema)
    .version(1)
    .optional()
    .build()

  val issueSchema: Schema = SchemaBuilder
    .struct()
    .name("GITHUB_ISSUE_SCHEMA")
    .version(1)
    .field("id", Schema.INT32_SCHEMA)
    .field("node_id", Schema.STRING_SCHEMA)
    .field("title", Schema.STRING_SCHEMA)
    .field("state", Schema.STRING_SCHEMA)
    .field("created_at", Schema.INT64_SCHEMA)
    .field("updated_at", Schema.OPTIONAL_INT64_SCHEMA)
    .field("body", Schema.OPTIONAL_STRING_SCHEMA)
    .field("pull_request", prSchema) // optional
    .field("user", userSchema) //mandatory
    .field("labels", labelSchema) // optional
    .build()

  val keySchema: Schema = SchemaBuilder
    .struct()
    .name("KEY_SCHEMA")
    .version(1)
    .field("owner", Schema.STRING_SCHEMA)
    .field("repository", Schema.STRING_SCHEMA)
    .field("issue_id", Schema.INT32_SCHEMA)
    .build()

}
