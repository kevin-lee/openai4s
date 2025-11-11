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
    property("test Eq[Model] === case", testEqModelSame),
    property("test Eq[Model] =!= case", testEqModelDifferent),
  )

  def testFromString: Property =
    for {
      supportedModel <- Gens.genModel.log("supportedModel")

      isSupported <- Gen
                       .frequency1(
                         95 -> Gen.constant(true),
                         5  -> Gen.constant(false),
                       )
                       .log("isSupported")

      input <- (
                 if (isSupported)
                   Gen.constant(supportedModel.value.value)
                 else
                   Gen.string(Gen.unicode, Range.linear(1, 10))
               )
                 .log("input")

      expected <- (
                    if (isSupported) Gen.constant(supportedModel.asRight)
                    else
                      Gen.constant(
                        (s"Unknown model: $input. If not it's supported yet by openai4s, " +
                          "but it is actually supported by openai, please use Model.userInput() create your own model.").asLeft
                      )
                  ).log("expected")
    } yield {
      val actual = Model.fromString(input)
      actual ==== expected
    }

  def testSupportedValues: Property =
    for {
      value <- StringGens.genNonEmptyString(Gen.unicode, PosInt(10)).log("value")
      input <- Gen
                 .frequency1(
                   95 -> Gens.genModel,
                   5  -> Gen.constant(Model.userInput(value)),
                 )
                 .log("input")
    } yield {
      val expected = input match {
        case Model.UserInput(_) => false
        case _ => true
      }

      val actual = Model.supportedValues.contains(input)
      actual ==== expected
    }

  def testEqModelSame: Property = for {
    model <- Gens.genModel.log("model")
  } yield {
    Result
      .diffNamed(s"${model.value.value} === ${model.value.value}", model, model)(_ === _)
      .log(s"${model.value.value} === ${model.value.value} returned false")
  }

  def testEqModelDifferent: Property = for {
    model1And2 <- Gens.genDifferentModelPair.log("(model1, model2)")
    (model1, model2) = model1And2
  } yield {
    Result
      .diffNamed(s"${model1.value.value} =!= ${model2.value.value}", model1, model2)(_ =!= _)
      .log(s"${model1.value.value} =!= ${model2.value.value} returned false")
  }

}
