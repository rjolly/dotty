import java.util.Date

given Conversion[String, Int] = _.length
given Conversion[Int, String] = _.toString
given Conversion[Int, Date] = new Date(_)

def f(x: String): Int = ~x
def g(x: Int): String = ~x
def h(x: Int): Date = ~x
