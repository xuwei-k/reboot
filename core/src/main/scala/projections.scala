package dispatch

// handy projections

object PromiseEither {
  class EitherDelegate[+A,+B](underlying: Promise[Either[A,B]])
  extends DelegatePromise[Either[A,B]] {
    def delegate = underlying
  }

  type EitherSwap[+A,+B] = Either[B,A]

  type IterableProjection[+A,+B,E[+_,+_]] = {
    def foreach[U](f: A => U): Any
    def map[X](f: A => X): E[X,B]
    def flatMap[BB >: B, X](f: A => E[X,BB]): E[X,BB]
  }

  trait GenericProjection[+A,+B,E[+_,+_]] {
    protected def underlying: Promise[E[A,B]]
    protected def project: IterableProjection[A,B,E]
    private class Delegate extends DelegatePromise[E[A,B]] {
      def delegate = underlying
    }
    def map[X](f: A => X): Promise[E[X,B]] =
      new Delegate with Promise[E[X,B]] {
        def claim = project.map(f)
      }
    def flatMap[BB>:B,X](f: A => Promise[E[X,BB]]): Promise[E[X,BB]] =
      new Delegate with Promise[E[X,BB]] {
        def claim = project.flatMap { a => f(a)() }
      }
    def foreach[U](f: A => U): Any =
      underlying.addListener { () =>
        project.foreach(f)
      }
  }

  class LeftProjection[+A,+B](val underlying: Promise[Either[A,B]])
  extends GenericProjection[A,B,Either] {
    def project = underlying().left
  }

  class RightProjection[+A,+B](val underlying: Promise[Either[A,B]])
  extends GenericProjection[B,A,EitherSwap] {
    def project = underlying().right
    def values[A1 >: A, C]
      (implicit ev: RightProjection[A, B] <:< RightProjection[A1, Iterable[C]]): RIVS[A1,C] =
      new PromiseRightIterable.Values[A1,C](this)
  }

  /* More progress can be made on iterables, one hopes. --league */
  trait RIVS[E,A] {
    def flatMap[B](f: A => Promise[Either[E,B]]): Promise[Either[E,Iterable[B]]]
    def map[B](f: A => B): Promise[Either[E,Iterable[B]]]
    def foreach[U](f: A => U): Any
  }
}

object PromiseIterable {

  class Flatten[A](underlying: Promise[Iterable[A]]) {
    def flatMap[Iter[B] <: Iterable[B], B](f: A => Promise[Iter[B]]) =
      underlying.flatMap { iter =>
        Promise.all(iter.map(f)).map { _.flatten }
      }
    def map[Iter[B] <: Iterable[B], B](f: A => Iter[B])
    : Promise[Iterable[B]] =
      underlying.map { _.map(f) }.map { _.flatten }
    def foreach[U](f: A => U): Any =
      underlying.foreach { _.foreach(f) }
    def withFilter(p: A => Boolean) =
      new Flatten(underlying.map { _.filter(p) })
    def filter(p: A => Boolean) = withFilter(p)
  }
  class Values[A](underlying: Promise[Iterable[A]]) {
    def flatMap[B](f: A => Promise[B]) =
      underlying.flatMap { iter =>
        Promise.all(iter.map(f))
      }
    def map[B](f: A => B): Promise[Iterable[B]] =
      underlying.map { _.map(f) }
    def foreach[U](f: A => U): Any =
      underlying.foreach { _.foreach(f) }
    def withFilter(p: A => Boolean) =
      new Values(underlying.map { _.filter(p) })
    def filter(p: A => Boolean) = withFilter(p)
    def flatten = new Flatten(underlying)
  }
}

object PromiseRightIterable {
  import PromiseEither.{RightProjection,RIVS}
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
  class Flatten[E,A](underlying: RightIter[E,A]) {
    def flatMap[Iter[B] <: Iterable[B], B]
    (f: A => Promise[Either[E,Iter[B]]]) =
      underlying.flatMap { iter =>
        Promise.all(iter.map(f)).map { eths =>
          flatRight(eths).right.map { _.flatten }
        }
      }
    def map[Iter[B] <: Iterable[B], B](f: A => Iter[B]) =
      underlying.flatMap { iter =>
        Promise(Right(iter.map(f).flatten))
      }
    def foreach[U](f: A => U): Any =
      underlying.foreach { _.foreach(f) }
    def withFilter(p: A => Boolean) =
      new Values[E,A](underlying.map { _.filter(p) }.right)
    def filter(p: A => Boolean) = withFilter(p)
  }
  class Values[E,A](underlying: RightIter[E,A]) extends RIVS[E,A] {
    def flatMap[B](f: A => Promise[Either[E,B]]) =
      underlying.flatMap { iter =>
        Promise.all(iter.map(f)).map(flatRight)
      }
    def map[B](f: A => B) =
      underlying.flatMap { iter =>
        Promise(Right(iter.map(f)))
    }
    def foreach[U](f: A => U): Any =
      underlying.foreach { _.foreach(f) }
    def flatten = new Flatten(underlying)
    def withFilter(p: A => Boolean) = {
      new Values[E,A](underlying.map { _.filter(p) }.right)
    }
    def filter(p: A => Boolean) = withFilter(p)
  }
}

/*

: Promise[EitherSwap[Iterable[A],E]]
: Promise[RightProjection[E,Iterable[A]]]


*/
