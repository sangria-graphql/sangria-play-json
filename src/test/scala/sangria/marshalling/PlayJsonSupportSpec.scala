package sangria.marshalling

import org.scalatest.{Matchers, WordSpec}
import play.api.libs.json._

import sangria.marshalling.testkit.{InputHandlingBehaviour, MarshallingBehaviour, Comment, Article}
import sangria.marshalling.playJson._


class PlayJsonSupportSpec extends WordSpec with Matchers with MarshallingBehaviour with InputHandlingBehaviour {
  implicit val commentFormat = Json.format[Comment]
  implicit val articleFormat = Json.format[Article]

  "PlayJson integration" should {
    behave like `value (un)marshaller` (PlayJsonResultMarshaller)

    behave like `AST-based input unmarshaller` (playJsonFromInput[JsValue])
    behave like `AST-based input marshaller` (PlayJsonResultMarshaller)

    behave like `case class input unmarshaller`
    behave like `case class input marshaller` (PlayJsonResultMarshaller)
  }

  val toRender = Json.obj(
    "a" → Json.arr(JsNull, JsNumber(123), Json.arr(Json.obj("foo" → JsString("bar")))),
    "b" → Json.obj(
      "c" → JsBoolean(true),
      "d" → JsNull))

  "InputUnmarshaller" should {
    "throw an exception on invalid scalar values" in {
      an [IllegalStateException] should be thrownBy
          PlayJsonInputUnmarshaller.getScalarValue(Json.obj())
    }

    "throw an exception on variable names" in {
      an [IllegalArgumentException] should be thrownBy
          PlayJsonInputUnmarshaller.getVariableName(JsString("$foo"))
    }

    "render JSON values" in {
      val rendered = PlayJsonInputUnmarshaller.render(toRender)

      rendered should be ("""{"a":[null,123,[{"foo":"bar"}]],"b":{"c":true,"d":null}}""")
    }
  }

  "ResultMarshaller" should {
    "render pretty JSON values" in {
      val rendered = PlayJsonResultMarshaller.renderPretty(toRender)

      rendered.replaceAll("\r", "") should be (
        """{
          |  "a" : [ null, 123, [ {
          |    "foo" : "bar"
          |  } ] ],
          |  "b" : {
          |    "c" : true,
          |    "d" : null
          |  }
          |}""".stripMargin.replaceAll("\r", ""))
    }

    "render compact JSON values" in {
      val rendered = PlayJsonResultMarshaller.renderCompact(toRender)

      rendered should be ("""{"a":[null,123,[{"foo":"bar"}]],"b":{"c":true,"d":null}}""")
    }
  }
}
