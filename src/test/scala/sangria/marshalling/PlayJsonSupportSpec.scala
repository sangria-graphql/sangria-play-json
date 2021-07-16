package sangria.marshalling

import play.api.libs.json._

import sangria.marshalling.testkit._
import sangria.marshalling.playJson._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PlayJsonSupportSpec
    extends AnyWordSpec
    with Matchers
    with MarshallingBehaviour
    with InputHandlingBehaviour
    with ParsingBehaviour {
  implicit val commentFormat: OFormat[Comment] = Json.format[Comment]
  implicit val articleFormat: OFormat[Article] = Json.format[Article]

  "PlayJson integration" should {
    behave.like(`value (un)marshaller`(PlayJsonResultMarshaller))

    behave.like(`AST-based input unmarshaller`(playJsonFromInput[JsValue]))
    behave.like(`AST-based input marshaller`(PlayJsonResultMarshaller))

    behave.like(`case class input unmarshaller`)
    behave.like(`case class input marshaller`(PlayJsonResultMarshaller))

    behave.like(
      `input parser`(ParseTestSubjects(
        complex = """{"a": [null, 123, [{"foo": "bar"}]], "b": {"c": true, "d": null}}""",
        simpleString = "\"bar\"",
        simpleInt = "12345",
        simpleNull = "null",
        list = "[\"bar\", 1, null, true, [1, 2, 3]]",
        syntaxError = List("[123, \"FOO\" \"BAR\"")
      )))
  }

  val toRender: JsObject = Json.obj(
    "a" -> Json.arr(JsNull, JsNumber(123), Json.arr(Json.obj("foo" -> JsString("bar")))),
    "b" -> Json.obj("c" -> JsBoolean(true), "d" -> JsNull))

  "InputUnmarshaller" should {
    "throw an exception on invalid scalar values" in {
      an[IllegalStateException] should be thrownBy
        PlayJsonInputUnmarshaller.getScalarValue(Json.obj())
    }

    "throw an exception on variable names" in {
      an[IllegalArgumentException] should be thrownBy
        PlayJsonInputUnmarshaller.getVariableName(JsString("$foo"))
    }

    "render JSON values" in {
      val rendered = PlayJsonInputUnmarshaller.render(toRender)

      rendered should be("""{"a":[null,123,[{"foo":"bar"}]],"b":{"c":true,"d":null}}""")
    }
  }

  "ResultMarshaller" should {
    "render pretty JSON values" in {
      val rendered = PlayJsonResultMarshaller.renderPretty(toRender)

      rendered.replaceAll("\r", "") should be("""{
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

      rendered should be("""{"a":[null,123,[{"foo":"bar"}]],"b":{"c":true,"d":null}}""")
    }
  }
}
