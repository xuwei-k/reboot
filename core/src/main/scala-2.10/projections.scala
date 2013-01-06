package dispatch

import concurrent.Future
import scala.util.Failure

// handy projections

object PromiseEither {
  class EitherDelegate[A,B](underlying: Future[Either[A,B]])
    extends DelegateFuture[Either[A,B]](underlying) {
    def delegate = underlying
  }

  case class LeftProjection[A,B](underlying: Future[Either[A,B]]){
   // extends EitherDelegate[A,B](underlying) { self =>

    def flatMap[BB >: B,X](f: A => Future[Either[X,BB]]):
    Future[Either[X,BB]] =
      underlying.flatMap { a =>
        a match {
          case Left(x) => f.apply(x)
          case Right(x) =>
            Future.failed[Either[X,BB]](
              new NoSuchElementException("Future.failed not completed with a throwable.")
            )
        }
      }

    def map[X](f: A => X): Future[Either[X,B]] =
      underlying.map { a =>
        a match {
          case Left(x) => Left(f.apply(x))
          case Right(x) => Right(x)
        }
      }

    def foreach[U](f: A => U) {
      underlying.foreach { a =>
        a match {
          case Left(x) => f.apply(x)
          case Right(x) =>
        }
      }
    }
  }

  case class RightProjection[A,B](underlying: Future[Either[A,B]]){
   // extends EitherDelegate[A,B](underlying) { self =>

    def flatMap[AA >: A,Y](f: B => Future[Either[AA,Y]]):
    Future[Either[AA,Y]] =
      underlying.flatMap { a =>
        a match {
          case Left(x) => Future.failed[Either[AA,Y]](
            new NoSuchElementException("Future.failed not completed with a throwable.")
          )
          case Right(x) => f.apply(x)
        }
      }

    def map[Y](f: B => Y): Future[Either[A,Y]] =
      underlying.map { a =>
        a match {
          case Left(x) => Left(x)
          case Right(x) => Right(f.apply(x))
        }
      }

    def foreach(f: B => Unit) {
      underlying.foreach { a =>
        a match {
          case Left(x) =>
          case Right(x) => f.apply(x)
        }
      }
    }
     /*
    def values[A1 >: A, C]
    (implicit ev: RightProjection[A, B] <:<
      RightProjection[A1, Iterable[C]]) =
      new PromiseRightIterable.Values(underlying, this)
      */
  }

}

/*
object PromiseIterable {

  class Flatten[A](val underlying: Future[Iterable[A]]) {
    import underlying.http.promise

    def flatMap[Iter[B] <: Iterable[B], B](f: A => Future[Iter[B]]) =
      underlying.flatMap { iter =>
        promise.all(iter.map(f)).map { _.flatten }
      }

    def map[Iter[B] <: Iterable[B], B](f: A => Iter[B])
    : Future[Iterable[B]] =
      underlying.map { _.map(f) }.map { _.flatten }
    def foreach(f: A => Unit) {
      underlying.foreach { _.foreach(f) }
    }
    def withFilter(p: A => Boolean) =
      new Flatten(underlying.map { _.filter(p) })
    def filter(p: A => Boolean) = withFilter(p)
  }
  class Values[A](underlying: Future[Iterable[A]]) {
    import underlying.http.promise
    def flatMap[B](f: A => Future[B]) =
      underlying.flatMap { iter =>
        promise.all(iter.map(f))
      }
    def map[B](f: A => B): Future[Iterable[B]] =
      underlying.map { _.map(f) }
    def foreach(f: A => Unit) {
      underlying.foreach { _.foreach(f) }
    }
    def withFilter(p: A => Boolean) =
      new Values(underlying.map { _.filter(p) })
    def filter(p: A => Boolean) = withFilter(p)
    def flatten = new Flatten(underlying)
  }
}

object PromiseRightIterable {
  import PromiseEither.RightProjection
  type RightIter[E,A] = RightProjection[E,Iterable[A]]

  private def flatRight[L,R](eithers: Iterable[Either[L,R]]) = {
    val start: Either[L,Seq[R]] = Right(Seq.empty)
    (start /: eithers) { (a, e) =>
      for {
        seq <- a.right
        cur <- e.right
      } yield (seq :+ cur)
    }
  }
  class Flatten[E,A](parent: Future[_], underlying: RightIter[E,A]) {
    import parent.http.promise
    def flatMap[Iter[B] <: Iterable[B], B]
    (f: A => Promise[Either[E,Iter[B]]]) =
      underlying.flatMap { iter =>
        promise.all(iter.map(f)).map { eths =>
          flatRight(eths).right.map { _.flatten }
        }
      }
    def map[Iter[B] <: Iterable[B], B](f: A => Iter[B]) =
      underlying.flatMap { iter =>
        promise(Right(iter.map(f).flatten))
      }
    def foreach(f: A => Unit) {
      underlying.foreach { _.foreach(f) }
    }
    def withFilter(p: A => Boolean) =
      new Values(parent, underlying.map { _.filter(p) }.right)
    def filter(p: A => Boolean) = withFilter(p)
  }
  class Values[E,A](parent: Future[_], underlying: RightIter[E,A]) {
    import parent.http.promise
    def flatMap[B](f: A => Future[Either[E,B]]) =
      underlying.flatMap { iter =>
        promise.all(iter.map(f)).map(flatRight)
      }
    def map[B](f: A => B) =
      underlying.flatMap { iter =>
        promise(Right(iter.map(f)))
      }
    def foreach(f: A => Unit) {
      underlying.foreach { _.foreach(f) }
    }
    def flatten = new Flatten(parent, underlying)
    def withFilter(p: A => Boolean) =
      new Values(parent, underlying.map { _.filter(p) }.right)
    def filter(p: A => Boolean) = withFilter(p)
  }
}
*/

