import language.experimental.erasedDefinitions

object scalax:
  erased class CanThrow[-E <: Exception]

  infix type throws[R, +E <: Exception] = CanThrow[E] ?=> R

  class Fail extends Exception

  def raise[E <: Exception](e: E): Nothing throws E = throw e

import scalax._

def foo(x: Boolean): Int throws Fail =
  if x then 1 else raise(Fail())

def bar(x: Boolean)(using CanThrow[Fail]): Int = foo(x)
def baz: Int throws Exception = foo(false)

class Result[T]:
  var value: T = scala.compiletime.uninitialized

def tryCatch[R, E <: Exception](body: => R throws E)(c: E => Unit, f: => Unit = ()): R =
  val res = new Result[R]
  try
    given CanThrow[E] = ???
    res.value = body
  catch c.asInstanceOf[Throwable => Unit]
  finally f
  res.value

@main def Test =
  tryCatch({
    println(foo(true))
    println(foo(false))
  })({
    case ex: Fail =>
      println("failed")
  })
  tryCatch(
    println(baz)
  )({
    case ex: Fail =>
      println("failed")
  })
