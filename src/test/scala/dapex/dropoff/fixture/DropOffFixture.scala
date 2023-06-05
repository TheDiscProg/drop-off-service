package dapex.dropoff.fixture

import dapex.messaging.{DapexMessage, DapexMessageFixture}
import dapex.messaging.Method.{SELECT, UPDATE}

trait DropOffFixture extends DapexMessageFixture {

  val selectRequestMessage: DapexMessage = this.getMessage(SELECT)

  val updateRequestMessage: DapexMessage = this.getMessage(UPDATE)

}
