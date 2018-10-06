package sangria.marshalling

import play.api.libs.json._

import scala.util.Try

object playJson extends PlayJsonSupportLowPrioImplicits {
  implicit object PlayJsonResultMarshaller extends ResultMarshaller {
    type Node = JsValue
    type MapBuilder = ArrayMapBuilder[Node]

    def emptyMapNode(keys: Seq[String]) = new ArrayMapBuilder[Node](keys)
    def addMapNodeElem(builder: MapBuilder, key: String, value: Node, optional: Boolean) = builder.add(key, value)

    def mapNode(builder: MapBuilder) = JsObject(builder.toSeq)
    def mapNode(keyValues: Seq[(String, JsValue)]) = JsObject(keyValues)

    def arrayNode(values: Vector[JsValue]) = Json.arr(values)

    def optionalArrayNodeValue(value: Option[JsValue]) = value match {
      case Some(v) ⇒ v
      case None ⇒ nullNode
    }

    def scalarNode(value: Any, typeName: String, info: Set[ScalarValueInfo]) = value match {
      case v: String ⇒ JsString(v)
      case true ⇒ JsTrue
      case false ⇒ JsFalse
      case v: Int ⇒ JsNumber(BigDecimal(v))
      case v: Long ⇒ JsNumber(BigDecimal(v))
      case v: Float ⇒ JsNumber(BigDecimal(v.toDouble))
      case v: Double ⇒ JsNumber(BigDecimal(v))
      case v: BigInt ⇒ JsNumber(BigDecimal(v))
      case v: BigDecimal ⇒ JsNumber(v)
      case v ⇒ throw new IllegalArgumentException("Unsupported scalar value: " + v)
    }

    def enumNode(value: String, typeName: String) = JsString(value)

    def nullNode = JsNull

    def renderCompact(node: JsValue) = Json.stringify(node)
    def renderPretty(node: JsValue) = Json.prettyPrint(node)
  }

  implicit object PlayJsonMarshallerForType extends ResultMarshallerForType[JsValue] {
    val marshaller = PlayJsonResultMarshaller
  }

  implicit object PlayJsonInputUnmarshaller extends InputUnmarshaller[JsValue] {
    def getRootMapValue(node: JsValue, key: String) = node.asInstanceOf[JsObject].value get key

    def isListNode(node: JsValue) = node.isInstanceOf[JsArray]
    def getListValue(node: JsValue) = node.asInstanceOf[JsArray].value

    def isMapNode(node: JsValue) = node.isInstanceOf[JsObject]
    def getMapValue(node: JsValue, key: String) = node.asInstanceOf[JsObject].value get key
    def getMapKeys(node: JsValue) = node.asInstanceOf[JsObject].keys

    def isDefined(node: JsValue) = node != JsNull
    def getScalarValue(node: JsValue) = node match {
      case JsBoolean(b) ⇒ b
      case JsNumber(d) ⇒ d.toBigIntExact getOrElse d
      case JsString(s) ⇒ s
      case _ ⇒ throw new IllegalStateException(s"$node is not a scalar value")
    }

    def getScalaScalarValue(node: JsValue) = getScalarValue(node)

    def isEnumNode(node: JsValue) = node.isInstanceOf[JsString]

    def isScalarNode(node: JsValue) = node match {
      case _: JsBoolean | _: JsNumber | _: JsString ⇒ true
      case _ ⇒ false
    }

    def isVariableNode(node: JsValue) = false
    def getVariableName(node: JsValue) = throw new IllegalArgumentException("variables are not supported")

    def render(node: JsValue) = Json.stringify(node)
  }

  private object PlayJsonToInput extends ToInput[JsValue, JsValue] {
    def toInput(value: JsValue) = (value, PlayJsonInputUnmarshaller)
  }

  implicit def playJsonToInput[T <: JsValue]: ToInput[T, JsValue] =
    PlayJsonToInput.asInstanceOf[ToInput[T, JsValue]]

  implicit def playJsonWriterToInput[T : Writes]: ToInput[T, JsValue] =
    new ToInput[T, JsValue] {
      def toInput(value: T) = implicitly[Writes[T]].writes(value) → PlayJsonInputUnmarshaller
    }

  private object PlayJsonFromInput extends FromInput[JsValue] {
    val marshaller = PlayJsonResultMarshaller
    def fromResult(node: marshaller.Node) = node
  }

  implicit def playJsonFromInput[T <: JsValue]: FromInput[T] =
    PlayJsonFromInput.asInstanceOf[FromInput[T]]

  implicit def playJsonReaderFromInput[T : Reads]: FromInput[T] =
    new FromInput[T] {
      val marshaller = PlayJsonResultMarshaller
      def fromResult(node: marshaller.Node) = implicitly[Reads[T]].reads(node) match {
        case JsSuccess(v, _) ⇒ v
        case JsError(errors) ⇒
          val formattedErrors = errors.toVector.flatMap {
            case (JsPath(nodes), es) ⇒ es.map(e ⇒ s"At path '${nodes mkString "."}': ${e.message}")
          }

          throw InputParsingError(formattedErrors)
      }
    }

  implicit object PlayJsonInputParser extends InputParser[JsValue] {
    def parse(str: String) = Try(Json.parse(str))
  }
}

trait PlayJsonSupportLowPrioImplicits {
  implicit val PlayJsonInputUnmarshallerJObject =
    playJson.PlayJsonInputUnmarshaller.asInstanceOf[InputUnmarshaller[JsObject]]
}
