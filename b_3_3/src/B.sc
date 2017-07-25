class BankAccount {
  private var balance = 0
  def deposit(amount: Int): Unit = {
    if (amount > 0) balance = balance + amount
  }
  def withdraw(amount: Int): Int = {
    if (0 < amount && amount <= balance) {
      balance = balance - amount
      balance
    } else throw new Error("insufficient funds")
  }
}

class BankAccountProxy(ba: BankAccount) {
  def deposit(amount: Int): Unit = ba.deposit(amount)
  def withdraw(amount: Int): Int = ba.withdraw(amount)
}

object B {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  
  val acct = new BankAccount                      //> acct  : BankAccount = BankAccount@6a5fc7f7
  acct deposit 50
  acct withdraw 20                                //> res0: Int = 30
  acct withdraw 20                                //> res1: Int = 10
  acct withdraw 15                                //> java.lang.Error: insufficient funds
                                                  //| 	at BankAccount.withdraw(B.scala:10)
                                                  //| 	at B$$anonfun$main$1.apply$mcV$sp(B.scala:26)
                                                  //| 	at org.scalaide.worksheet.runtime.library.WorksheetSupport$$anonfun$$exe
                                                  //| cute$1.apply$mcV$sp(WorksheetSupport.scala:76)
                                                  //| 	at org.scalaide.worksheet.runtime.library.WorksheetSupport$.redirected(W
                                                  //| orksheetSupport.scala:65)
                                                  //| 	at org.scalaide.worksheet.runtime.library.WorksheetSupport$.$execute(Wor
                                                  //| ksheetSupport.scala:75)
                                                  //| 	at B$.main(B.scala:19)
                                                  //| 	at B.main(B.scala)
                                                  
  def REPEAT(command: => Unit)(condition: => Boolean): Unit = {
    command
    if (condition) ()
    else REPEAT(command)(condition)
  }
  
  
/*
  def cons[T](hd: T, tl: => Stream[T]) = new Stream[T] {
    def head = hd
    private var tlOpt: Option[Stream[T]] = None
    def tail: T = tlOpt match {
      case Some(x) => x
      case None => tlOpt = Some(tl); tail
    }
  }
*/

}