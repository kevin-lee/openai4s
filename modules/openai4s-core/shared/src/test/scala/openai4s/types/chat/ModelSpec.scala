package openai4s.types.chat

import cats.syntax.all.*
import hedgehog.*
import hedgehog.runner.*
import openai4s.compat.TypesCompat

/** @author Kevin Lee
  * @since 2023-11-11
  */
object ModelSpec extends Properties with TypesCompat {
  override def tests: List[Test] = List(
    property("test Model.fromString", testFromString),
    property("test Model.supportedValues", testSupportedValues),
  )

  def testFromString: Property =
    for {
      supportedModel <- Gens.genModel.log("supportedModel")
      isSupported    <- Gen
                          .frequency1(
                            95 -> Gen.constant(true),
                            5  -> Gen.constant(false),
                          )
                          .log("isSupported")

      input    <- (if (isSupported)
                     Gen
                       .constant(supportedModel.value.value)
                   else
                     Gen.string(Gen.unicode, Range.linear(1, 10)))
                    .log("input")
      expected <- (
                    if (isSupported) Gen.constant(supportedModel.asRight)
                    else Gen.constant(s"Unknown model: $input".asLeft)
                  ).log("expected")
    } yield {
      val actual = Model.fromString(input)
      actual ==== expected
    }

  def testSupportedValues: Property =
    for {
      input <-
        Gen
          .frequency1(
            95 -> Gens.genModel,
            5  -> StringGens.genNonEmptyStringMinMax(Gen.unicode, PosInt(1), PosInt(10)).map(Model.unsupported),
          )
          .log("input")
    } yield {
      val expected = input match {
        case Model.Unsupported(_) => false
        case _ => true
      }
      val actual   = Model.supportedValues.contains(input)
      actual ==== expected
    }

}
