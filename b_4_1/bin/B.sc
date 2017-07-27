trait Subscriber {
  def handler(pub: Publisher): Unit
}

trait Publisher {
  private var subscribers: Set[Subscriber] = Set()
  def subscribe(subscriber: Subscriber): Unit =
    subscribers += subscriber
    
  def unsubscriba(subscriber: Subscriber): Unit =
    subscribers -= subscriber
    
  def publish(): Unit =
    subscribers.foreach(_.handler(this))
}

class BankAccount extends Publisher {
  private var balance = 0
  def currentBalance = balance
  def deposit(amount: Int): Unit = {
    if (amount > 0) {
      balance = balance + amount
      publish()
    }
    
  }
  def withdraw(amount: Int): Int = {
    if (0 < amount && amount <= balance) {
      balance = balance - amount
      publish()
      balance
    } else throw new Error("insufficient funds")
  }
}

class Consolidator(observed: List[BankAccount]) extends Subscriber {
  observed.foreach(_.subscribe(this))

  private var total: Int = _
  compute()
  
  private def compute() =
    total = observed.map(_.currentBalance).sum
    
  def handler(pub: Publisher) = compute()
  
  def totalBalance = total
}

object B {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  
  val a = new BankAccount                         //> a  : BankAccount = BankAccount@3d24753a
  val b = new BankAccount                         //> b  : BankAccount = BankAccount@59a6e353
  val c = new Consolidator(List(a, b))            //> c  : Consolidator = Consolidator@60addb54
  c.totalBalance                                  //> res0: Int = 0
  a deposit 20
  c.totalBalance                                  //> res1: Int = 20
  b deposit 30
  c.totalBalance                                  //> res2: Int = 50
}