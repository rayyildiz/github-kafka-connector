package com.rayyildiz.connect

import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

class GithubApiSpec extends Matchers {

  "GithubApi" should "call repository" in {
    val body= GithubApi.call("https://api.github.com/repos/kubernetes/kubernetes/issues")

    body.isLeft shouldBe(false)
    body.isRight shouldBe(true)

  }

}
