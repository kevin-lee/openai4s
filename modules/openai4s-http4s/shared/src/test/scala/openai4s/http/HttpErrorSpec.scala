package openai4s.http

import _root_.cats.effect.IO
import _root_.cats.syntax.all.*
import hedgehog.*
import hedgehog.runner.*
import org.http4s.{InvalidMessageBodyFailure, Method, Request, Status, Uri}

import scala.util.control.NoStackTrace

/** @author Kevin Lee
  * @since 2023-09-03
  */
object HttpErrorSpec extends Properties {
  override def tests: List[Test] = List(
    property("test Show[HttpError].show", testShow)
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
      errorAndShow <-
        Gen.choice1(
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
          }, {
            Gen
              .choice1(
                Gen.elementUnsafe(
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
              )
              .map { code =>
                @SuppressWarnings(Array("org.wartremover.warts.Throw"))
                val status = Status.fromInt(code).fold(throw _, identity) // scalafix:ok DisableSyntax.throw
                (
                  HttpError.unexpectedStatus(request, status, message.some),
                  s"UnexpectedStatus(request=${request.method.show} ${request.uri.show}, status=${status.show}, body=Some($message))",
                )
              }
          },
        )
    } yield errorAndShow

  def genRequest[F[*]]: Gen[Request[F]] =
    for {
      method <- Gen.element1(Method.GET, Method.POST, Method.PUT, Method.DELETE, Method.PATCH)
      uri    <- Gen.string(Gen.alpha, Range.linear(2, 10)).list(Range.linear(1, 5)).map(_.mkString("."))
    } yield Request(method, Uri.unsafeFromString(uri))

  final case class TestError(message: String) extends Exception(message) with NoStackTrace {
    override def getMessage: String = message
  }

}
