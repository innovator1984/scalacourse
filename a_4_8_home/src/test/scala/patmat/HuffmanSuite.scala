package patmat

import org.scalatest.FunSuite

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import patmat.Huffman._

@RunWith(classOf[JUnitRunner])
class HuffmanSuite extends FunSuite {
	trait TestTrees {
		val t1 = Fork(Leaf('a',2), Leaf('b',3), List('a','b'), 5)
		val t2 = Fork(Fork(Leaf('a',2), Leaf('b',3), List('a','b'), 5), Leaf('d',4), List('a','b','d'), 9)
	}


  test("weight of a larger tree") {
    new TestTrees {
      assert(weight(t1) === 5)
    }
  }


  test("chars of a larger tree") {
    new TestTrees {
      assert(chars(t2) === List('a','b','d'))
    }
  }


  test("string2chars(\"hello, world\")") {
    assert(string2Chars("hello, world") === List('h', 'e', 'l', 'l', 'o', ',', ' ', 'w', 'o', 'r', 'l', 'd'))
  }


  test("makeOrderedLeafList for some frequency table") {
    assert(makeOrderedLeafList(List(('t', 2), ('e', 1), ('x', 3))) === List(Leaf('e',1), Leaf('t',2), Leaf('x',3)))
  }


  test("combine of some leaf list") {
    val leaflist = List(Leaf('e', 1), Leaf('t', 2), Leaf('x', 4))
    assert(combine(leaflist) === List(Fork(Leaf('e',1),Leaf('t',2),List('e', 't'),3), Leaf('x',4)))
  }


  test("decode and encode a very short text should be identity") {
    new TestTrees {
      assert(decode(t1, encode(t1)("ab".toList)) === "ab".toList)
    }
  }

  // ----- MY TESTS -----

  test("CodeTree.Fork pos 01") {
    // TODO 1.A implement two very simple functions
    // val a: List[Int] = List(1, 2, 3)
    // val b: List[Int] = List(1, 2, 3)
    // val c: List[Int] = a
    // println(if(a == c) "a == c" else "! a == c")
    // println(if(a == b) "a == b" else "! a == b")
    // println(if(a === c) "a === c" else "! a === c")
    // println(if(a === b) "a === b" else "! a === b")
    // println(if(a.eq(c)) "a eq c" else "! a eq c")
    // println(if(a.eq(b)) "a eq b" else "! a eq b")
    assert(true)
  }

  test("CodeTree.Fork neg 01") {
    // TODO 1.A implement two very simple functions
    assert(true)
  }

  test("CodeTree.Leaf pos 01") {
    // TODO 1.B implement two very simple functions
    assert(true)
  }

  test("CodeTree.Leaf neg 01") {
    // TODO 1.B implement two very simple functions
    assert(true)
  }

  test("weight pos 01") {
    // TODO 1.C implement two very simple functions
    assert(true)
  }

  test("weight neg 01") {
    // TODO 1.C implement two very simple functions
    assert(true)
  }

  test("chars pos 01") {
    // TODO 1.D implement two very simple functions
    assert(true)
  }

  test("chars neg 01") {
    // TODO 1.D implement two very simple functions
    assert(true)
  }

  test("makeCodeTree pos 01") {
    // TODO 1.E implement two very simple functions
    assert(true)
  }

  test("makeCodeTree neg 01") {
    // TODO 1.E implement two very simple functions
    assert(true)
  }

  test("times pos 01") {
    // TODO 2.A Constructing Huffman Trees
    assert(true)
  }

  test("times neg 01") {
    // TODO 2.A Constructing Huffman Trees
    assert(true)
  }

  test("makeOrderedLeafList pos 01") {
    // TODO 2.B Constructing Huffman Trees
    assert(true)
  }

  test("makeOrderedLeafList neg 01") {
    // TODO 2.B Constructing Huffman Trees
    assert(true)
  }

  test("singleton pos 01") {
    // TODO 2.C Constructing Huffman Trees
    assert(true)
  }

  test("singleton neg 01") {
    // TODO 2.C Constructing Huffman Trees
    assert(true)
  }

  test("combine pos 01") {
    // TODO 2.D Constructing Huffman Trees
    assert(true)
  }

  test("combine neg 01") {
    // TODO 2.D Constructing Huffman Trees
    assert(true)
  }

  test("until pos 01") {
    // TODO 2.E Constructing Huffman Trees
    assert(true)
  }

  test("until neg 01") {
    // TODO 2.E Constructing Huffman Trees
    assert(true)
  }

  test("createCodeTree pos 01") {
    // TODO 2.F Constructing Huffman Trees
    assert(true)
  }

  test("createCodeTree neg 01") {
    // TODO 2.F Constructing Huffman Trees
    assert(true)
  }

  test("decode pos 01") {
    // TODO 3.A Decoding
    assert(true)
  }

  test("decode neg 01") {
    // TODO 3.A Decoding
    assert(true)
  }

  test("decodedSecret pos 01") {
    // TODO 3.B Decoding
    assert(true)
  }

  test("decodedSecret neg 01") {
    // TODO 3.B Decoding
    assert(true)
  }

  test("encode pos 01") {
    // TODO 4.A Encoding
    assert(true)
  }

  test("encode neg 01") {
    // TODO 4.A Encoding
    assert(true)
  }

  test("quickEncode pos 01") {
    // TODO 4.B Encoding
    assert(true)
  }

  test("quickEncode neg 01") {
    // TODO 4.B Encoding
    assert(true)
  }

  test("codeBits pos 01") {
    // TODO 4.C Encoding
    assert(true)
  }

  test("codeBits neg 01") {
    // TODO 4.C Decoding
    assert(true)
  }

  test("convert pos 01") {
    // TODO 4.D Encoding
    assert(true)
  }

  test("convert neg 01") {
    // TODO 4.D Encoding
    assert(true)
  }

  test("makeCodeTable pos 01") {
    // TODO 4.E Encoding
    assert(true)
  }

  test("makeCodeTable neg 01") {
    // TODO 4.E Encoding
    assert(true)
  }
}
