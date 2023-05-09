package dapex.dropoff.fixture

import dapex.entities.DapexMessageFixture
import dapex.entities.Method.{SELECT, UPDATE}

trait DropOffFixture extends DapexMessageFixture {

  val selectRequestMessage = getMessage(SELECT)

  val updateRequestMessage = getMessage(UPDATE)

}
