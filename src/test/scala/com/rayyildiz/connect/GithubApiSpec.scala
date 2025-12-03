package com.rayyildiz.connect

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GithubApiSpec extends AnyWordSpec with Matchers {

  "GithubApi" should {
    "call repository" in {
      val body = GithubApi.call("https://api.github.com/repos/kubernetes/kubernetes/issues")

      body.isLeft shouldBe false
      body.isRight shouldBe true
    }
  }
}
