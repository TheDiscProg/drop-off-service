package dapex.entities

trait DapexMessageFixture {

  val endpoint = Endpoint(resource = "customerRepo", method = "select")

  val client = Client(
    clientId = "client1",
    requestId = "request1",
    sourceEndpoint = "client",
    authorisation = "securitytoken"
  )

  val originator =
    Originator(clientId = "client1", requestId = "request1", sourceEndpoint = "client")

  val criteria = Vector(
    Criterion(field = "customerId", value = "1", operator = "eq")
  )

  val update = Vector(
    UpdateDatumValue(
      field = "firstName",
      oldValue = Some("marky"),
      newValue = "Mark"
    )
  )

  val insert = Vector(
    FieldValuePair("firstName", "John"),
    FieldValuePair("surname", "Smith")
  )

  val process = Vector(
    FieldValuePair("arg1", "1230")
  )

  val response = Response(
    status = "okay",
    message = "No message",
    data = Vector(
      FieldValuePair("firstName", "John"),
      FieldValuePair("surname", "Smith")
    )
  )

  val dapexMessage = DapexMessage(
    endpoint = endpoint,
    client = client,
    originator = originator,
    criteria = criteria,
    update = update,
    insert = insert,
    process = process,
    response = Some(response)
  )

  def getMessage(method: Method): DapexMessage =
    method match {
      case Method.SELECT =>
        dapexMessage.copy(
          endpoint = dapexMessage.endpoint.copy(method = "select"),
          update = Vector(),
          insert = Vector(),
          process = Vector(),
          response = None
        )
      case Method.UPDATE =>
        dapexMessage.copy(
          endpoint = dapexMessage.endpoint.copy(method = "update"),
          criteria = criteria :+ Criterion(field = "customerId", value = "1", operator = "eq"),
          insert = Vector(),
          process = Vector(),
          response = None
        )
      case Method.INSERT =>
        dapexMessage.copy(
          endpoint = dapexMessage.endpoint.copy(method = "insert"),
          criteria = Vector(),
          update = Vector(),
          process = Vector(),
          response = None
        )
      case Method.PROCESS =>
        dapexMessage.copy(
          endpoint = dapexMessage.endpoint.copy(method = "process"),
          criteria = Vector(),
          update = Vector(),
          insert = Vector(),
          response = None
        )
      case Method.RESPONSE =>
        dapexMessage.copy(
          endpoint = dapexMessage.endpoint.copy(method = "response"),
          criteria = Vector(),
          update = Vector(),
          insert = Vector(),
          process = Vector()
        )
      case _ =>
        dapexMessage.copy(
          endpoint = dapexMessage.endpoint.copy(method = "unsupported"),
          criteria = Vector(),
          update = Vector(),
          insert = Vector(),
          process = Vector(),
          response = None
        )
    }
}
