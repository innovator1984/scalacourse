object A {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  
	def abs(x:Double) = if (x < 0) -x else x  //> abs: (x: Double)Double
	
	def sqrtIter(guess: Double, x: Double): Double =
	  if (isGoodEnough(guess, x)) guess
	  else sqrtIter(improve(guess, x), x)     //> sqrtIter: (guess: Double, x: Double)Double
	
	def isGoodEnough(guess: Double, x: Double) = abs(guess * guess - x) / x < 0.001
                                                  //> isGoodEnough: (guess: Double, x: Double)Boolean
	
	def improve(guess: Double, x: Double) =(guess + x / guess) / 2
                                                  //> improve: (guess: Double, x: Double)Double
	
	def sqrt(x: Double) = sqrtIter(1.0, x)    //> sqrt: (x: Double)Double
	
	sqrt(0.001)                               //> res0: Double = 0.03162278245070105
	math.sqrt(0.001)                          //> res1: Double = 0.03162277660168379

	sqrt(0.1e-20)                             //> res2: Double = 3.1633394544890125E-11
	math.sqrt(0.1e-20)                        //> res3: Double = 3.1622776601683794E-11

	sqrt(1.0e20)                              //> res4: Double = 1.0000021484861237E10
	math.sqrt(1.0e20)                         //> res5: Double = 1.0E10
 
	sqrt(1.0e50)                              //> res6: Double = 1.0000003807575104E25
	math.sqrt(1.0e50)                         //> res7: Double = 1.0E25
}