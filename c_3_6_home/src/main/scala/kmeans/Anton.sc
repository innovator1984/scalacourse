def fun(x: Int): Int = x * x

val f1 = fun(2)
val f2 = fun _

f2(3)

def curr(x:Int)(y:Int)(z:Int): Int = x+y*z

val c1 = curr(2) _
c1(2)

val c0 = curr(5) _
curr(5)(3)(1)
c0(3)(1)
