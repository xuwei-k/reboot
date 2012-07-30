package dispatch

// handy projections

object PromiseEither {
  class EitherDelegate[+A,+B](underlying: Promise[Either[A,B]])
  extends DelegatePromise[Either[A,B]] {
    def delegate = underlying
  }

  /* Trying to introduce type signatures representing what is required
   * of the projections. --league
   */
  trait LP[+A,+B] {
    def map[X](f: A => X): Promise[Either[X,B]]
  }

  trait RP[+A,+B] {
    def flatMap[AA >: A,Y](f: B => Promise[Either[AA,Y]]):
      Promise[Either[AA,Y]]
    def map[Y](f: B => Y): Promise[Either[A,Y]]
    def foreach[U](f: B => U)
    def values[A1 >: A,C](implicit ev: RP[A, B] <:< RP[A1, Iterable[C]]):
      RIVS[A1,C]
  }

  trait RIVS[E,A] {
    def flatMap[B](f: A => Promise[Either[E,B]]): Promise[Either[E,Iterable[B]]]
    def map[B](f: A => B): Promise[Either[E,Iterable[B]]]
    def foreach[U](f: A => U)
  }

  def leftProjection[A,B](underlying: Promise[Either[A,B]]): LP[A,B] =
    new LeftProjection(underlying)

  def rightProjection[A,B](underlying: Promise[Either[A,B]]): RP[A,B] =
    new RightProjection(underlying)

  private class LeftProjection[+A,+B](underlying: Promise[Either[A,B]])
  extends LP[A,B] {
    def flatMap[BB >: B,X](f: A => Promise[Either[X,BB]]) =
      new EitherDelegate(underlying) with Promise[Either[X,BB]] {
        def claim = underlying().left.flatMap { a => f(a)() }
      }
    def map[X](f: A => X) =
      new EitherDelegate(underlying) with Promise[Either[X,B]] {
        def claim = underlying().left.map(f)
      }
    def foreach[U](f: A => U) {
      underlying.addListener { () => underlying().left.foreach(f) }
    }
  }
  private class RightProjection[+A,+B](underlying: Promise[Either[A,B]])
  extends RP[A,B] {
    def flatMap[AA >: A,Y](f: B => Promise[Either[AA,Y]]) =
      new EitherDelegate(underlying) with Promise[Either[AA,Y]] {
        def claim = underlying().right.flatMap { b => f(b)() }
      }
    def map[Y](f: B => Y) =
      new EitherDelegate(underlying) with Promise[Either[A,Y]] {
        def claim = underlying().right.map(f)
      }
    def foreach[U](f: B => U) {
      underlying.addListener { () => underlying().right.foreach(f) }
    }
    def values[A1 >: A, C]
    (implicit ev: RP[A, B] <:< RP[A1, Iterable[C]]) =
      new PromiseRightIterable.Values(this)
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
    def foreach[U](f: A => U) {
      underlying.foreach { _.foreach(f) }
    }
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
    def foreach[U](f: A => U) {
      underlying.foreach { _.foreach(f) }
    }
    def withFilter(p: A => Boolean) =
      new Values(underlying.map { _.filter(p) })
    def filter(p: A => Boolean) = withFilter(p)
    def flatten = new Flatten(underlying)
  }
}

object PromiseRightIterable {
  import PromiseEither.{RP,RIVS}
  type RightIter[E,A] = RP[E,Iterable[A]]

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
    def foreach[U](f: A => U) {
      underlying.foreach { _.foreach(f) }
    }
    def withFilter(p: A => Boolean) =
      new Values(underlying.map { _.filter(p) }.right)
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
    def foreach[U](f: A => U) {
      underlying.foreach { _.foreach(f) }
    }
    def flatten = new Flatten(underlying)
    def withFilter(p: A => Boolean) =
      new Values(underlying.map { _.filter(p) }.right)
    def filter(p: A => Boolean) = withFilter(p)
  }
}
