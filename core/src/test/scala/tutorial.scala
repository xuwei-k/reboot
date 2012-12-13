//import concurrent.Future
import reboot._
/*
object Tutorial {

  def ipExample: Future[Int] =  {
    val svc = url("http://httpbin.org/ip")
    val ip = Http(svc OK as.String)
    for (c <- ip) yield c.length
  }

  def checkRightOutput = {}

  /** Methods **/

  def weatherSvc(loc: String) =
    url("http://api.wunderground.com/api/%s/forecast/q/CA/%s.xml" format ("0fa9a69d7c9e2ae4", loc))

  def weatherXml(loc: String) =
    Http(weatherSvc(loc) OK as.xml.Elem)

  def extractTemp(xml: scala.xml.Elem) = {
    val seq = for {
      elem <- xml \\ "fahrenheit"
    } yield {
      elem.text.toString.toInt
    }
    seq.head
  }

  def temperature(loc: String) =
    for (xml <- weatherXml(loc))
    yield extractTemp(xml)

  def tempCompare(locA: String, locB:String) = {
    val pa = temperature(locA)
    val pb = temperature(locB)
    for {
      a <- pa
      b <- pb
    } yield a.compare(b)
  }

  def hottest(locs: String*) = {
    val temps =
      for(loc <- locs)
      yield for (t <- temperature(loc))
      yield (t -> loc)
    for (ts <- Futures.all(temps))
    yield ts.max._2
  }

  /** Tests **/

  def checkWeatherStr = {
    for (str <- Http(weatherSvc("New_York") OK as.String))
      println(str)
  }

  def checkWeatherXML = {
    def printer = new scala.xml.PrettyPrinter(90, 2)
    for (xml <- weatherXml("New_York"))
      println(printer.format(xml))
  }

  def tempNewYork = {
    for (t <- temperature("New_York")) println(t)
  }

  def tempNewYorkChicago = {
    val newyork = temperature("New_York")
    val chicago = temperature("Chicago")

    for {
      n <- newyork
      m <- chicago
    } {
      if (n > m) println("It's hotter in New York")
      else  println("It's at least as hot in Madrid")
    }

  }

  def maxTemp = {
    val locs = List("New_York",
      "Chicago",
      "Dallas")
    val temps =
      for(loc <- locs)
      yield for (t <- temperature(loc))
      yield (t -> loc)

    println(temps)

    val hottest =
      for (ts <- Futures.all(temps))
      yield ts.max

    println(hottest)
    hottest
  }

  def failedPromise =
   Http(host("example.com") OK as.String)

  def optFailedPromise =  Http(host("example.com") OK as.String).option

  def weatherXmlOpt(loc: String) =
    Http(weatherSvc(loc) OK as.xml.Elem).option

  def extractTempOpt(xml: scala.xml.Elem) = {
    val seq = for {
      elem <- xml \\ "fahrenheit"
    } yield elem.text.toString.toInt
    seq.headOption
  }

  def temperatureOpt(loc: String) =
    for (xmlOpt <- weatherXmlOpt(loc))
    yield for {
      xml <- xmlOpt
      t <- extractTempOpt(xml)
    } yield t

  def printFailed = {
    val str = failedPromise
    println(str)
    str
  }

  def hottestOpt(locs: String*) = {
    val temps =
      for(loc <- locs)
      yield for (tOpt <- temperatureOpt(loc))
      yield for (t <- tOpt)
        yield (t -> loc)
    for (ts <- Futures.all(temps)) yield {
      val valid = ts.flatten
      for (_ <- valid.headOption)
      yield valid.max._2
    }
  }

  def weatherXmlEither(loc: String):
  Future[Either[String, xml.Elem]] = {
    val res: Future[Either[Throwable, xml.Elem]] =
      Http(weatherSvc(loc) OK as.xml.Elem).either
    for (exc <- res.left)
    yield "Can't connect to weather service: \n" +
      exc.getMessage
  }

  def extractTempEither(xml: scala.xml.Elem):
  Either[String,Int] = {
    val seq = for {
      elem <- xml \\ "fahrenheit"
    } yield elem.text.toString.toInt
    seq.headOption.toRight {
      "Temperature missing in service response"
    }
  }

  def temperatureEither(loc: String):
  Future[Either[String,Int]] =
    for {
      xml <- weatherXmlEither(loc).right
      t <- Futures(extractTempEither(xml)).right
    } yield t

    def hottestEither(locs: String*) = {
      val temps =
        for(loc <- locs)
        yield for (tEither <- temperatureEither(loc))
        yield (loc, tEither)
      for (ts <- Futures.all(temps)) yield {
        val valid = for ((loc, Right(t)) <- ts)
        yield (t, loc)
        val max = for (_ <- valid.headOption)
        yield valid.max._2
        val errors = for ((loc, Left(err)) <- ts)
        yield (loc, err)
        (max, errors)
      }
    }

  def runTutorial = {
    println("Weather str")
    checkWeatherStr
    val input = readLine("prompt> ")
    println("Weather XML")
    checkWeatherXML
    val input2 = readLine("prompt> ")
    println("Weather New York")
    tempNewYork
    val input3 = readLine("prompt> ")
    println("Temp both cities")
    tempNewYorkChicago
    val input4 = readLine("prompt> ")
    println("Max temp")
    maxTemp
    val input5 = readLine("prompt> ")
    println("Failed Promise")
    printFailed
    val input6 = readLine("prompt> ")


  }
}
*/
