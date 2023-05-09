package dapex.entities

import dapex.entities.Method.{INSERT, PROCESS, RESPONSE, SELECT, UPDATE}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

class DapexMessageTest extends AnyFlatSpec with DapexMessageFixture {

  it should "verify endpoint" in {
    DapexMessage.checkEndPointValidity(dapexMessage) shouldBe true
  }

  it should "fail endpoint verification when endpoint is empty" in {
    val badMessage = dapexMessage.copy(endpoint = dapexMessage.endpoint.copy(resource = ""))

    DapexMessage.checkEndPointValidity(badMessage) shouldBe false
  }

  it should "fail endpoint verification when endpoint is null" in {
    val badMessage = dapexMessage.copy(endpoint = dapexMessage.endpoint.copy(resource = null))

    DapexMessage.checkEndPointValidity(badMessage) shouldBe false
  }

  it should "fail endpoint verification when method is an empty string" in {
    val badMessage = dapexMessage.copy(endpoint = dapexMessage.endpoint.copy(method = ""))

    DapexMessage.checkEndPointValidity(badMessage) shouldBe false
  }

  it should "fail endpoint verification when method is null" in {
    val badMessage = dapexMessage.copy(endpoint = dapexMessage.endpoint.copy(method = null))

    DapexMessage.checkEndPointValidity(badMessage) shouldBe false
  }

  it should "verify select request" in {
    val select = getMessage(SELECT)

    DapexMessage.isValid(select) shouldBe true
  }

  it should "verify update request" in {
    val update = getMessage(UPDATE)

    DapexMessage.isValid(update) shouldBe true
  }

  it should "not verify update request when update section is empty" in {
    val update = getMessage(UPDATE).copy(update = Vector())

    DapexMessage.isValid(update) shouldBe false
  }

  it should "verify insert request" in {
    val insert = getMessage(INSERT)

    DapexMessage.isValid(insert) shouldBe true
  }

  it should "not verify insert request when insert section is empty" in {
    val insert = getMessage(INSERT).copy(insert = Vector())

    DapexMessage.isValid(insert) shouldBe false
  }

  it should "verify process request" in {
    val process = getMessage(PROCESS)

    DapexMessage.isValid(process) shouldBe true
  }

  it should "verify a response" in {
    val response = getMessage(RESPONSE)

    DapexMessage.isValid(response) shouldBe true
  }

  it should "not verify a response when it is not defined" in {
    val response = getMessage(RESPONSE).copy(response = None)

    DapexMessage.isValid(response) shouldBe false
  }
}
