package com.rayyildiz.connect
import org.apache.kafka.common.config.ConfigException
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.JavaConverters._

class GithubSourceConfigSpec extends FlatSpec with Matchers {

  "GithubConfig" should "parse required config" in {

    val map = Map(
      GithubSourceConfig.TOPIC_CONFIG -> "topic-github",
      GithubSourceConfig.GITHUB_OWNER_CONFIG -> "rayyildiz",
      GithubSourceConfig.GITHUB_REPOSITORY_CONFIG -> "dotfiles",
      GithubSourceConfig.AUTH_USERNAME_CONFIG -> "username",
      GithubSourceConfig.AUTH_PASSWORD_CONFIG -> "randomly generated text",
      GithubSourceConfig.GITHUB_BATCH_SIZE_CONFIG -> "20"
    )

    val config = GithubSourceConfig(map.asJava)

    config.githubOwner should be("rayyildiz")
    config.githubRepo should be("dotfiles")
    config.topic should be("topic-github")
  }

  it should "batch size test if batch size is bigger than 250" in {
    val map = Map(
      GithubSourceConfig.TOPIC_CONFIG -> "topic-github",
      GithubSourceConfig.GITHUB_OWNER_CONFIG -> "rayyildiz",
      GithubSourceConfig.GITHUB_REPOSITORY_CONFIG -> "dotfiles",
      GithubSourceConfig.GITHUB_BATCH_SIZE_CONFIG -> "251"
    )
    assertThrows[ConfigException](GithubSourceConfig(map.asJava))
  }

  it should "batch size test if batch size is less than 0" in {
    val map = Map(
      GithubSourceConfig.TOPIC_CONFIG -> "topic-github",
      GithubSourceConfig.GITHUB_OWNER_CONFIG -> "rayyildiz",
      GithubSourceConfig.GITHUB_REPOSITORY_CONFIG -> "dotfiles",
      GithubSourceConfig.GITHUB_BATCH_SIZE_CONFIG -> "0"
    )
    assertThrows[ConfigException](GithubSourceConfig(map.asJava))
  }

  it should "batch size test is not a text" in {
    val map = Map(
      GithubSourceConfig.TOPIC_CONFIG -> "topic-github",
      GithubSourceConfig.GITHUB_OWNER_CONFIG -> "rayyildiz",
      GithubSourceConfig.GITHUB_REPOSITORY_CONFIG -> "dotfiles",
      GithubSourceConfig.GITHUB_BATCH_SIZE_CONFIG -> "NOT NUMBER"
    )
    assertThrows[ConfigException](GithubSourceConfig(map.asJava))
  }
}
