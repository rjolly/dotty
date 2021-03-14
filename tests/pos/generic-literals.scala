type Conv[T] = [X] =>> X => T

def f[U : Conv[BigInt]](x: U) = ???

given string2bigInt: (String => BigInt) = s => BigInt(s)

val res = f(11111111111111111111111111111111111111111)
