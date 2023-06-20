package dapex.dropoff.fixture

import dapex.messaging.DapexMessage
import dapex.messaging.Method.{SELECT, UPDATE}
import dapex.test.DapexMessageFixture

trait DropOffFixture extends DapexMessageFixture {

  val selectRequestMessage: DapexMessage = this.getMessage(SELECT)

  val updateRequestMessage: DapexMessage = this.getMessage(UPDATE)

}
