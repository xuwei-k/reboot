package dispatch

import com.ning.http.client.ListenableFuture
import java.util.{concurrent => juc}
import juc.TimeUnit
import scala.util.control.Exception.{allCatch,catching}

import scala.util.{Success, Failure, Try}
import concurrent._
import annotation.tailrec
import concurrent.duration.Duration

object Futures {

  def all[T](f: Iterable[Future[T]]) = Future.sequence(f)

  class Factory(http: HttpExecutor) {
    def apply[T](existing: T): Future[T] = {
      Promise.successful(existing).future
    }
    def all[T](f: Iterable[Future[T]]) = Futures.all(f)
  }
  /*
  implicit def iterable[T] = new IterableGuarantor[T]
  implicit def identity[T] = new IdentityGuarantor[T]

  class Factory(http: HttpExecutor) { factory =>
    def all[A](promises: Iterable[Promise[A]]): Promise[Iterable[A]] =
      new Promise[Iterable[A]] {
        def replay = all(for (p <- promises) yield p.replay)
        def claim = promises.map { _() }
        def isComplete = promises.forall { _.isComplete }
        val http = factory.http
        def addListener(f: () => Unit) = {
          val count = new juc.atomic.AtomicInteger(promises.size)
          promises.foreach { p =>
            p.addListener { () =>
              if (count.decrementAndGet == 0)
                f()
            }
          }
        }
      }
    def sleep[T](d: Duration)(todo: => T) =
      new SleepPromise(factory.http, d, todo)
    def apply[T](existing: T): Promise[T] =
      new Promise[T] {
        def claim = existing
        def replay = factory.apply(existing)
        def isComplete = true
        val http = factory.http
        def addListener(f: () => Unit) =
          factory.http.promiseExecutor.execute(f)
      }
  }

  @deprecated("Use Http.promise.all or other HttpExecutor")
  def all[A](promises: Iterable[Promise[A]]): Promise[Iterable[A]] =
    Http.promise.all(promises)

  /** Wraps a known value in a Promise. Useful in binding
    *  some value to other promises in for-expressions. */
  @deprecated("Use Http.promise.apply or other HttpExecutor")
  def apply[T](existing: T): Promise[T] =
    Http.promise(existing)
    */
}


/** This has classes which dispatch's old promise has */
case class PimpedFuture[A](fut: Future[A]) {

  /*
  def flatten[B](implicit pv: Future[A] <:< Future[Future[B]]):Future[B] =
    (this: Future[Future[B]]).flatMap(identity)
  */
  /** Create a left projection of a contained either */
  def left[B,C](implicit ev: Future[A] <:< Future[Either[B, C]]) =
    new PromiseEither.LeftProjection(fut)

  /** Create a right projecti on of a contained either */
  def right[B,C](implicit ev: Future[A] <:< Future[Either[B, C]]) =
    new PromiseEither.RightProjection(fut)

  def either: Future[Either[Throwable, A]] = fut map { Right(_) } recover { case x => Left(x) }

  def apply() = Await.result(fut, scala.concurrent.duration.Duration.Inf)

  def foreach[U](f: (A) ⇒ U) = fut.foreach(f)

  def option: Future[Option[A]] = fut.map(a => Some(a)).recover { case a: Throwable => None }

}

/** This is a future which delegates to another future **/
case class DelegateFuture[A](fut: Future[A]) extends Future[A] {

  @throws(classOf[Exception])
  def result(atMost: scala.concurrent.duration.Duration)(implicit permit: CanAwait): A = fut.result(atMost)(permit)

  @throws(classOf[TimeoutException])
  @throws(classOf[InterruptedException])
  def ready(atMost: scala.concurrent.duration.Duration)(implicit permit: CanAwait): this.type
   = fut.ready(atMost)(permit).asInstanceOf[this.type]

  def onComplete[U](func: Try[A] => U)(implicit executor: ExecutionContext): Unit =
    fut.onComplete(func)(executor)

  def isCompleted: Boolean = fut.isCompleted

  def value: Option[Try[A]] = fut.value

}
/*
/** This is a wrapper which turns a listenableFuture into a future */
class ListenableFutureFuture[A](underlyingIn: => ListenableFuture[A],
                                val http: HttpExecutor)
  extends DelegateFuture[A](listenableToFuture(underlyingIn)) {
}
*/


case class Duration(length: Long, unit: java.util.concurrent.TimeUnit) {
  def millis = unit.toMillis(length)
}

object Duration {
  val None = Duration(-1L, java.util.concurrent.TimeUnit.MILLISECONDS)
  def millis(length: Long) = Duration(length, java.util.concurrent.TimeUnit.MILLISECONDS)
}

