package dapex.entities

import enumeratum.values.{StringCirceEnum, StringEnum, StringEnumEntry}

sealed trait Method extends StringEnumEntry {

  def value: String
}

case object Method extends StringEnum[Method] with StringCirceEnum[Method] {

  case object SELECT extends Method {
    val value: String = "select"
  }

  case object UPDATE extends Method {
    val value: String = "update"
  }

  case object INSERT extends Method {
    val value: String = "insert"
  }

  case object PROCESS extends Method {
    val value: String = "process"
  }

  case object RESPONSE extends Method {
    val value: String = "response"
  }

  case object UNSUPPORTED extends Method {
    val value: String = "unsupported"
  }

  val values = findValues

  def fromString(s: String): Method =
    Option(s) match {
      case Some(value) =>
        value.toLowerCase match {
          case "select" => SELECT
          case "update" => UPDATE
          case "insert" => INSERT
          case "process" => PROCESS
          case "response" => RESPONSE
          case _ => UNSUPPORTED
        }
      case _ => UNSUPPORTED
    }
}
