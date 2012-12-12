import com.ning.http.client.{ListenableFuture, AsyncHttpClient, AsyncHandler, Request}
import scala.concurrent.Promise
import scala.util.{Failure, Success}

package object dispatch {
  /** Type alias for RequestBuilder, our typical request definitions */
  type Req = com.ning.http.client.RequestBuilder
  /** Type alias for Response, avoid need to import */
  type Res = com.ning.http.client.Response
  /** Type alias for URI, avoid need to import */
  type Uri = java.net.URI

  /** type alias for dispatch future/ scala future **/
  type Future[+A] = scala.concurrent.Future[A]

  @deprecated("Use dispatch.HttpExecutor")
  type Executor = HttpExecutor

  implicit val exec = scala.concurrent.ExecutionContext.Implicits.global

  implicit def implyRequestVerbs(builder: Req) =
    new DefaultRequestVerbs(builder)

  implicit def implyRequestHandlerTuple(builder: Req) =
    new RequestHandlerTupleBuilder(builder)

  implicit def implyRunnable[U](f: () => U) = new java.lang.Runnable {
    def run() { f() }
  }

  implicit def future2Pimped[T](f: Future[T]): PimpedFuture[T] = PimpedFuture[T](f)

  implicit def left2Future[A,B](f: PromiseEither.LeftProjection[A,B]): PromiseEither.EitherDelegate[A,B] =
    new PromiseEither.EitherDelegate[A,B](f.underlying)

  implicit def right2Future[A,B](f: PromiseEither.RightProjection[A,B]): PromiseEither.EitherDelegate[A,B] =
    new PromiseEither.EitherDelegate[A,B](f.underlying)


  /*
  implicit val durationOrdering = Ordering.by[Duration,Long] {
    _.millis
  }
  */

  def requestHandlerToFuture[T](request: Request, handler: AsyncHandler[T], http: HttpExecutor): Future[T] =
      bridge[T](http.client.executeRequest(request, handler))(http.promiseExecutor)

  def bridge[T](listenableFuture: ListenableFuture[T])(implicit ex: java.util.concurrent.Executor): Future[T] = {
    println("\n\nStarting request\n\n")
    val promise = scala.concurrent.Promise[T]()
    listenableFuture.addListener(new Runnable {
      def run: Unit = promise.complete(
        try Success(listenableFuture.get(10, java.util.concurrent.TimeUnit.SECONDS))
        catch {
          case e => Failure(e)
        }
      )
    }, ex)
    promise.future
  }



}
