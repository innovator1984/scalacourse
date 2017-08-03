class A {
  def hello(x: String) = println(x)

  def f(a: Float, b: Float): Unit = println("RES: " + (a / b toInt))
}

val a = new A()
a.f(3.0f, 1.5f)

object B {
  def hello2(x: String) = println(x)
}

trait C {
  def hello3(x: String)
}

abstract class D {
  def hello4(x: String) = println(x)
}

class A2 extends A {
  override def hello(x: String) = println(x)
}

// class B2 extends B {
//  def hello2(x: String) = println(x)
// }

class C2 extends C {
  def hello3(x: String) = println(x)
}

class D2 extends D {
  override def hello4(x: String) = println(x)
}
