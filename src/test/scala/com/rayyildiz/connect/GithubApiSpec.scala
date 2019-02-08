package com.rayyildiz.connect
import org.scalatest.{FlatSpec, Matchers}

class GithubApiSpec extends FlatSpec with Matchers {

  "GithubApi" should "call repository" in {
    val body= GithubApi.call("https://api.github.com/repos/kubernetes/kubernetes/issues")

    body.isLeft shouldBe(false)
    body.isRight shouldBe(true)

  }

}
