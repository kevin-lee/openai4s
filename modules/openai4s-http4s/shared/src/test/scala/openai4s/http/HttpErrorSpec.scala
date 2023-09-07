package openai4s.http

import _root_.cats.effect.IO
import _root_.cats.syntax.all.*
import hedgehog.*
import hedgehog.runner.*
import io.circe.Json
import io.circe.syntax.*
import org.http4s.{InvalidMessageBodyFailure, Method, Request, Status, Uri}

import scala.util.control.NoStackTrace

/** @author Kevin Lee
  * @since 2023-09-03
  */
object HttpErrorSpec extends Properties {
  override def tests: List[Test] = List(
    property("test Show[HttpError].show", testShow),
    property("test Show[HttpError.UnexpectedStatus.Body].show", testHttpErrorUnexpectedStatusBodyShow),
  )

  def testShow: Property =
    for {
      errorAndExpected <- genHttpErrorAndExpected[IO].log("(error, expected)")
      (error, expected) = errorAndExpected
    } yield {
      val actual = error.show
      actual ==== expected
    }

  def genHttpErrorAndExpected[F[*]]: Gen[(HttpError[F], String)] =
    for {
      request      <- genRequest[F]
      message      <- Gen.string(Gen.alphaNum, Range.linear(1, 10))
      errorAndShow <- Gen.choice1(
                        {
                          val cause = TestError(message)
                          Gen.constant(
                            (
                              HttpError.connectionError(request, cause),
                              s"ConnectionError(request=${request.method.show} ${request.uri.show}, cause=${cause.getMessage})",
                            )
                          )
                        }, {
                          val cause = TestError(message)
                          Gen.constant(
                            (
                              HttpError.responseError(request, cause),
                              s"ResponseError(request=${request.method.show} ${request.uri.show}, cause=${cause.getMessage})",
                            )
                          )
                        }, {
                          val cause = InvalidMessageBodyFailure(message, TestError(message).some)
                          Gen.constant(
                            (
                              HttpError.decodingError(request, cause),
                              s"DecodingError(request=${request.method.show} ${request.uri.show}, cause=${cause.getMessage})",
                            )
                          )
                        },
                        for {
                          code <- Gen.elementUnsafe(
                                    (400 to 418).toList ++
                                      (421 to 426).toList ++
                                      List(
                                        428,
                                        429,
                                        431,
                                        451,
                                      ) ++
                                      (500 to 511).toList
                                  )

                          body <- genBody
                          errorAndExpected = {
                            @SuppressWarnings(Array("org.wartremover.warts.Throw"))
                            val status = Status.fromInt(code).fold(throw _, identity) // scalafix:ok DisableSyntax.throw
                            (
                              HttpError.unexpectedStatus(request, status, body),
                              s"UnexpectedStatus(request=${request.method.show} ${request.uri.show}, status=${status.show}, body=${body.show})",
                            )
                          }
                        } yield errorAndExpected,
                      )
    } yield errorAndShow

  def testHttpErrorUnexpectedStatusBodyShow: Property =
    for {
      body <- genBody.log("body")
    } yield {
      val actual = body.show
      body match {
        case HttpError.UnexpectedStatus.Body.StringBody(value) =>
          val expected = value
          actual ==== expected

        case HttpError.UnexpectedStatus.Body.JsonBody(value) =>
          val expected = value.noSpaces
          actual ==== expected

        case HttpError.UnexpectedStatus.Body.WithCause(value) =>
          val expected = value.getMessage
          actual ==== expected
      }
    }

  def genBody: Gen[HttpError.UnexpectedStatus.Body] =
    for {
      message <- Gen.string(Gen.alphaNum, Range.linear(1, 10))
      body    <- Gen.choice1(
                   Gen.constant(HttpError.UnexpectedStatus.Body.stringBody(message)),
                   genJson.map(HttpError.UnexpectedStatus.Body.jsonBody),
                   Gen.constant(HttpError.UnexpectedStatus.Body.withCause(TestError(message))),
                 )
    } yield body

  def genJson: Gen[Json] =
    for {
      names  <- Gen.string(Gen.alpha, Range.linear(1, 5)).list(Range.linear(1, 3))
      values <- Gen
                  .string(Gen.alpha, Range.linear(1, 5))
                  .list(Range.singleton(names.length))
                  .map(_.map(_.asJson))
      pairs = names.zip(values)
    } yield Json.obj(pairs*)

  def genRequest[F[*]]: Gen[Request[F]] =
    for {
      method <- Gen.element1(Method.GET, Method.POST, Method.PUT, Method.DELETE, Method.PATCH)
      uri    <- Gen.string(Gen.alpha, Range.linear(2, 10)).list(Range.linear(1, 5)).map(_.mkString("."))
    } yield Request(method, Uri.unsafeFromString(uri))

  final case class TestError(message: String) extends Exception(message) with NoStackTrace {
    override def getMessage: String = message
  }

}
