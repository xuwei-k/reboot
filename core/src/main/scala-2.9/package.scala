import com.ning.http.client.{AsyncHttpClient, AsyncHandler, Request}

/** This will hold all the explicits **/
package object dispatch{

  /** Type alias for RequestBuilder, our typical request definitions */
  type Req = com.ning.http.client.RequestBuilder
  /** Type alias for Response, avoid need to import */
  type Res = com.ning.http.client.Response
  /** Type alias for URI, avoid need to import */
  type Uri = java.net.URI

  /** type alias for dispatch future/ scala future **/
  type Future[+A] = dispatch.DispatchFuture[A]

  @deprecated("Use dispatch.HttpExecutor")
  type Executor = HttpExecutor

  implicit def implyRequestVerbs(builder: Req) =
    new DefaultRequestVerbs(builder)

  implicit def implyRequestHandlerTuple(builder: Req) =
    new RequestHandlerTupleBuilder(builder)

  implicit def implyRunnable[U](f: () => U) = new java.lang.Runnable {
    def run() { f() }
  }

  implicit val durationOrdering = Ordering.by[Duration,Long] {
    _.millis
  }

  /** This method turns a request into a Future **/
  def requestHandlerToFuture[T](request: Request, handler: AsyncHandler[T], http: HttpExecutor): Future[T] =
    new ListenableFuturePromise(
      http.client.executeRequest(request, handler),
      http.promiseExecutor,
      http
    )
}
