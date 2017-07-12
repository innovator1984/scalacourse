# scalacourse
The exercises from Coursera - Scala - https://www.coursera.org/learn/progfun1/


=====


a_1_8, a_1_9



Note: If you have paid for the Certificate, please make sure you are submitting to the required assessment and not the optional assessment. If you mistakenly use the token from the wrong assignment, your grades will not appear

Attention: You are allowed to submit a unlimited number of times for grade purposes. Once you have submitted your solution, you should see your grade and a feedback about your code on the Coursera website within 10 minutes. If you want to improve your grade, just submit an improved solution. The best of all your submissions will count as the final grade.

Mechanics

Download the recfun.zip handout archive file and extract it somewhere on your machine.

This assignment counts towards your final grade. Please refer to the Grading Policy for more details.

Do not forget to submit your work using the submit task from SBT. Please refer to the example assignment for instructions.


Exercise 1: Pascal’s Triangle

The following pattern of numbers is called Pascal’s triangle.

    1
   1 1
  1 2 1
 1 3 3 1
1 4 6 4 1
   ...

The numbers at the edge of the triangle are all 1, and each number inside the triangle is the sum of the two numbers above it. Write a function that computes the elements of Pascal’s triangle by means of a recursive process.

Do this exercise by implementing the pascal function in Main.scala, which takes a column c and a row r, counting from 0 and returns the number at that spot in the triangle. For example, pascal(0,2)=1,pascal(1,2)=2 and pascal(1,3)=3.

def pascal(c: Int, r: Int): Int

Exercise 2: Parentheses Balancing

Write a recursive function which verifies the balancing of parentheses in a string, which we represent as a List[Char] not a String. For example, the function should return true for the following strings:

    (if (zero? x) max (/ 1 x))
    I told him (that it’s not (yet) done). (But he wasn’t listening)

The function should return false for the following strings:

    :-)
    ())(

The last example shows that it’s not enough to verify that a string contains the same number of opening and closing parentheses.

Do this exercise by implementing the balance function in Main.scala. Its signature is as follows:


def balance(chars: List[Char]): Boolean

There are three methods on List[Char] that are useful for this exercise:

    chars.isEmpty: Boolean returns whether a list is empty
    chars.head: Char returns the first element of the list
    chars.tail: List[Char] returns the list without the first element

Hint: you can define an inner function if you need to pass extra parameters to your function.

Testing: You can use the toList method to convert from a String to aList[Char]: e.g. "(just an) example".toList.

Exercise 3: Counting Change

Write a recursive function that counts how many different ways you can make change for an amount, given a list of coin denominations. For example, there are 3 ways to give change for 4 if you have coins with denomination 1 and 2: 1+1+1+1, 1+1+2, 2+2.

Do this exercise by implementing the countChange function inMain.scala. This function takes an amount to change, and a list of unique denominations for the coins. Its signature is as follows:

def countChange(money: Int, coins: List[Int]): Int

Once again, you can make use of functions isEmpty, head and tail on the list of integers coins.

Hint: Think of the degenerate cases. How many ways can you give change for 0 CHF(swiss money)? How many ways can you give change for >0 CHF, if you have no coins?


=====


a_2_8, a_2_9


Note: If you have paid for the Certificate, please make sure you are submitting to the required assessment and not the optional assessment. If you mistakenly use the token from the wrong assignment, your grades will not appear

Attention: You are allowed to submit an unlimited number of times for grade purposes. Once you have submitted your solution, you should see your grade and a feedback about your code on the Coursera website within 10 minutes. If you want to improve your grade, just submit an improved solution. The best of all your submissions will count as the final grade.

In this assignment, you will work with a functional representation of sets based on the mathematical notion of characteristic functions. The goal is to gain practice with higher-order functions.

Download the funsets.zip handout archive file and extract it somewhere on your machine. Write your solutions by completing the stubs in theFunSets.scala file.

Write your own tests! For this assignment, we don’t give you tests but instead the FunSetSuite.scala file contains hints on how to write your own tests for the assignment.

Representation

We will work with sets of integers.

As an example to motivate our representation, how would you represent the set of all negative integers? You cannot list them all… one way would be so say: if you give me an integer, I can tell you whether it’s in the set or not: for 3, I say ‘no’; for -1, I say yes.

Mathematically, we call the function which takes an integer as argument and which returns a boolean indicating whether the given integer belongs to a set, the characteristic function of the set. For example, we can characterize the set of negative integers by the characteristic function (x: Int) => x < 0.

Therefore, we choose to represent a set by its characteristic function and define a type alias for this representation:

type Set = Int => Boolean

Using this representation, we define a function that tests for the presence of a value in a set:

def contains(s: Set, elem: Int): Boolean = s(elem)

Basic Functions on Sets

Let’s start by implementing basic functions on sets.

Define a function singletonSet which creates a singleton set from one integer value: the set represents the set of the one given element. Now that we have a way to create singleton sets, we want to define a function that allow us to build bigger sets from smaller ones.

Define the functions union,intersect, and diff, which takes two sets, and return, respectively, their union, intersection and differences. diff(s, t) returns a set which contains all the elements of the set s that are not in the set t.

Define the function filter which selects only the elements of a set that are accepted by a given predicate p. The filtered elements are returned as a new set.

Queries and Transformations on Sets

In this part, we are interested in functions used to make requests on elements of a set. The first function tests whether a given predicate is true for all elements of the set. This forall function has the following signature:

def forall(s: Set, p: Int => Boolean): Boolean

Note that there is no direct way to find which elements are in a set. contains only allows to know whether a given element is included. Thus, if we wish to do something to all elements of a set, then we have to iterate over all integers, testing each time whether it is included in the set, and if so, to do something with it. Here, we consider that an integer x has the property -1000 <= x <= 1000 in order to limit the search space.

Implement forall using linear recursion. For this, use a helper function nested inforall.

Using forall, implement a function exists which tests whether a set contains at least one element for which the given predicate is true. Note that the functions forall and exists behave like the universal and existential quantifiers of first-order logic.

Finally, write a function map which transforms a given set into another one by applying to each of its elements the given function.

Extra Hints

Be attentive in the video lectures on how to write anonymous functions in Scala.

Sets are represented as functions. Think about what it means for an element to belong to a set, in terms of function evaluation. For example, how do you represent a set that contains all numbers between 1 and 100?

Most of the solutions for this assignment can be written as one-liners. If you have more, you probably need to rethink your solution. In other words, this assignment needs more thinking (whiteboard, pen and paper) than coding ;-).

If you are having some trouble with terminology, have a look at the glossary.


======


a_3_4, a_3_5


Note: If you have paid for the Certificate, please make sure you are submitting to the required assessment and not the optional assessment. If you mistakenly use the token from the wrong assignment, your grades will not appear

Attention: You are allowed to submit an unlimited number of times for grade purposes. Once you have submitted your solution, you should see your grade and a feedback about your code on the Coursera website within 10 minutes. If you want to improve your grade, just submit an improved solution. The best of all your submissions will count as the final grade.

Download the objsets.zip handout archive file.

In this assignment you will work with an object-oriented representations based on binary trees.


Object-Oriented Sets


For this part, you will earn credit by completing the TweetSet.scala file. This file defines an abstract class TweetSet with two concrete subclasses,Empty which represents an empty set, and NonEmpty(elem: Tweet, left: TweetSet, right: TweetSet), which represents a non-empty set as a binary tree rooted at elem. The tweets are indexed by their text bodies: the bodies of all tweets on the left are lexicographically smaller than elem and all bodies of elements on the right are lexicographically greater.

Note also that these classes are immutable: the set-theoretic operations do not modify this but should return a new set.

Before tackling this assignment, we suggest you first study the already implemented methods contains and incl for inspiration.

Filtering

Implement filtering on tweet sets. Complete the stubs for the methods filter and filterAcc. filter takes as argument a function, the predicate, which takes a tweet and returns a boolean. filter then returns the subset of all the tweets in the original set for which the predicate is true. For example, the following call:

tweets.filter(tweet => tweet.retweets > 10)

applied to a set tweets of two tweets, say, where the first tweet was not retweeted and the second tweet was retweeted 20 times should return a set containing only the second tweet.

Hint: start by defining the helper method filterAcc which takes an accumulator set as a second argument. This accumulator contains the ongoing result of the filtering.

/** This method takes a predicate and returns a subset of all the elements
 *  in the original set for which the predicate is true.
 */
def filter(p: Tweet => Boolean): TweetSet
def filterAcc(p: Tweet => Boolean, acc: TweetSet): TweetSet

The definition of filter in terms of filterAcc should then be straightforward.

Taking Unions

Implement union on tweet sets. Complete the stub for the method union. The method union takes another set that, and computes a new set which is the union of this and that, i.e. a set that contains exactly the elements that are either in this or in that, or in both.

def union(that: TweetSet): TweetSet

Note that in this exercise it is your task to find out in which class(es) to define the union method (should it be abstract in class TweetSet?).

Sorting Tweets by Their Influence

The more often a tweet is “re-tweeted” (that is, repeated by a different user with or without additions), the more influential it is.

The goal of this part of the exercise is to add a method descendingByRetweet to TweetSet which should produce a linear sequence of tweets (as an instance of class TweetList), ordered by their number of retweets:

def descendingByRetweet: TweetList

This method reflects a common pattern when transforming data structures. While traversing one data structure (in this case, a TweetSet), we’re building a second data structure (here, an instance of class TweetList). The idea is to start with the empty list Nil (containing no tweets), and to find the tweet with the most retweets in the input TweetSet. This tweet is removed from the TweetSet (that is, we obtain a new TweetSet that has all the tweets of the original set except for the tweet that was “removed”; this immutable set operation, remove, is already implemented for you), and added to the result list by creating a new Cons. After that, the process repeats itself, but now we are searching through a TweetSet with one less tweet.

Hint: start by implementing the method mostRetweeted which returns the most popular tweet of a TweetSet.

Tying everything together

In the last step of this assignment your task is to detect influential tweets in a set of recent tweets. We are providing you with a TweetSet containing several hundred tweets from popular tech news sites in the past few days, located in the TweetReader object (file “TweetReader.scala”).TweetReader.allTweets returns an instance of TweetSet containing a set of all available tweets.

Furthermore, you are given two lists of keywords. The first list corresponds to keywords associated with Google and Android smartphones, while the second list corresponds to keywords associated with Apple and iOS devices. Your objective is to detect which platform has generated more interest or activity in the past few days.

As a first step, use the functionality you implemented in the first parts of this assignment to create two different TweetSets, googleTweets andappleTweets. The first TweetSet, googleTweets, should contain all tweets that mention (in their “text”) one of the keywords in the google list. The second TweetSet, appleTweets, should contain all tweets that mention one of the keyword in the apple list. Their signature is as follows:

lazy val googleTweets: TweetSet
lazy val appleTweets: TweetSet

Hint: use the exists method of List and contains method of classjava.lang.String.

From the union of those two TweetSets, produce trending, an instance of class TweetList representing a sequence of tweets ordered by their number of retweets:

lazy val trending: TweetList


=====


a_4_8, a_4_9


Note: If you have paid for the Certificate, please make sure you are submitting to the required assessment and not the optional assessment. If you mistakenly use the token from the wrong assignment, your grades will not appear

Attention: Once you have submitted your solution, you should see your grade and a feedback about your code on the Coursera website within 10 minutes. If you want to improve your grade, just submit an improved solution.

Download the patmat.zip handout archive file and extract it somewhere on your machine.

Huffman coding is a compression algorithm that can be used to compress lists of characters.

In a normal, uncompressed text, each character is represented by the same number of bits (usually eight). In Huffman coding, each character can have a bit representation of a different length, depending on how common a character is: the characters that appear often in a text are represented by a shorter bit sequence than those being used more rarely. Every huffman code defines the specific bit sequences used to represent each character.

A Huffman code can be represented by a binary tree whose leaves represent the characters that should be encoded. The code tree below can represent the characters A to H.


PICTURE


The leaf nodes have associated with them a weight which denotes the frequency of appearance of that character. In the example below, the character A has the highest weight 8, while F for example has weight 1.

Every branching node of the code tree can be thought of as a set containing the characters present in the leaves below it. The weight of a branching node is the total weight of the leaves below it: this information is necessary for the construction of the tree.

Note that a given encoding is only optimal if the character frequencies in the encoded text match the weights in the code tree.

Finally, observe the recursive structure of the coding tree: every sub-tree is itself a valid code tree for a smaller alphabet.
Encoding

For a given Huffman tree, one can obtain the encoded representation of a character by traversing from the root of the tree to the leaf containing the character. Along the way, when a left branch is chosen, a 0 is added to the representation, and when a right branch is chosen, 1 is added to the representation. Thus, for the Huffman tree above, the character D is encoded as 1011.
Decoding

Decoding also starts at the root of the tree. Given a sequence of bits to decode, we successively read the bits, and for each 0, we choose the left branch, and for each 1 we choose the right branch. When we reach a leaf, we decode the corresponding character and then start again at the root of the tree. As an example, given the Huffman tree above, the sequence of bits,10001010 corresponds to BAC.
Implementation

In Scala, a Huffman tree can be represented as follows:

abstract class CodeTree
case class Fork (left: CodeTree, right: CodeTree, chars: List[Char], weight: Int) extends CodeTree
case class Leaf(char: Char, weight: Int) extends CodeTree


To begin, implement the following two (hint: very simple) functions using pattern matches on the code tree:

    weight which returns the total weight of a given Huffman tree.def weight(tree: CodeTree): Int = tree match ...
    chars which returns the list of characters defined in a given Huffman tree.def chars(tree: CodeTree): List[Char] = tree match ...

Using these functions, it’s possible to define makeCodeTree, a function which facilitates the creation of Huffman trees by automatically calculating the list of characters and the weight when creating a node. This function is already implemented in the handout template:

def makeCodeTree(left: CodeTree, right: CodeTree) =
  Fork(left, right, chars(left) ::: chars(right), weight(left) + weight(right))

Using makeCodeTree, code trees can be constructed manually in the following way:

val sampleTree = makeCodeTree(
  makeCodeTree(Leaf('x', 1), Leaf('e', 1)),
  Leaf('t', 2)
)

Constructing Huffman Trees

Given a text, it’s possible to calculate and build an optimal Huffman tree in the sense that the encoding of that text will be of the minimum possible length, meanwhile keeping all information (i.e., it is lossless).

To obtain an optimal tree from a list of characters, you have to define a function createCodeTree with the following signature:

def createCodeTree(chars: List[Char]): CodeTree = ...

Proceed with the following steps to break up this assignment into smaller parts (the handout template contains more detailed documentation):

    Begin by writing a function times which calculates the frequency of each character in the text: def times(chars: List[Char]): List[(Char, Int)] = ...
    Then, write a function makeOrderedLeafList which generates a list containing all the leaves of the Huffman tree to be constructed (the case Leaf of the algebraic datatype CodeTree). The list should be ordered by ascending weights where the weight of a leaf is the number of times (or the frequency) it appears in the given text: def makeOrderedLeafList(freqs: List[(Char, Int)]): List[Leaf] = ...
    Write a simple function singleton which checks whether a list of code trees contains only one single tree. def singleton(trees: List[CodeTree]): Boolean = ...
    Write a function combine which (1) removes the two trees with the lowest weight from the list constructed in the previous step, and (2) merges them by creating a new node of type Fork. Add this new tree to the list - which is now one element shorter - while preserving the order (by weight). def combine(trees: List[CodeTree]): List[CodeTree] = ...
    Write a function until which calls the two functions defined above until this list contains only a single tree. This tree is the optimal coding tree. The function until can be used in the following way: until(singleton, combine)(trees) where the argument trees is of the type List[CodeTree].
    Finally, use the functions defined above to implement the function createCodeTree which respects the signature shown above.

Decoding

Define the function decode which decodes a list of bits (which were already encoded using a Huffman tree), given the corresponding coding tree.

type Bit = Int
def decode(tree: CodeTree, bits: List[Bit]): List[Char] = ...

Use this function and the frenchCode code tree to decode the bit sequence in secret. Store the resulting character sequence in decodedSecret.

Encoding

This section deals with the Huffman encoding of a sequence of characters into a sequence of bits.

…Using a Huffman Tree

Define the function encode which encodes a list of characters using Huffman coding, given a code tree.

def encode(tree: CodeTree)(text: List[Char]): List[Bit] = ...

Your implementation must traverse the coding tree for each character, a task that should be done using a helper function.

…Using a Coding Table

The previous function is simple, but very inefficient. You goal is now to define quickEncode which encodes an equivalent representation, but more efficiently.

def quickEncode(tree: CodeTree)(text: List[Char]): List[Bit] = ...

Your implementation will build a coding table once which, for each possible character, gives the list of bits of its code. The simplest way - but not the most efficient - is to encode the table of characters as a list of pairs.

type CodeTable = List[(Char, List[Bit])]

The encoding must then be done by accessing the table, via a functioncodeBits.

def codeBits(table: CodeTable)(char: Char): List[Bit] = ...

The creation of the table is defined by convert which traverses the coding tree and constructs the character table.

def convert(t: CodeTree): CodeTable = ...

Implement the function convert by using the function mergeCodeTablesbelow:

def mergeCodeTables(a: CodeTable, b: CodeTable): CodeTable = ...


=====


a_6_7, a_6_8


Note: If you have paid for the Certificate, please make sure you are submitting to the required assessment and not the optional assessment. If you mistakenly use the token from the wrong assignment, your grades will not appear

Download the forcomp.zip handout archive file and extract it somewhere on your machine.

In this assignment, you will solve the combinatorial problem of finding all the anagrams of a sentence using the Scala Collections API and for-comprehensions.

You are encouraged to look at the Scala API documentation while solving this exercise, which can be found here:

http://www.scala-lang.org/api/current/index.html

Note that Scala uses the `String` from Java, therefore the documentation for strings has to be looked up in the Javadoc API:

http://docs.oracle.com/javase/8/docs/api/java/lang/String.html

The problem

An anagram of a word is a rearrangement of its letters such that a word with a different meaning is formed. For example, if we rearrange the letters of the word `Elvis` we can obtain the word `lives`, which is one of its anagrams.

In a similar way, an anagram of a sentence is a rearrangement of all the characters in the sentence such that a new sentence is formed. The new sentence consists of meaningful words, the number of which may or may not correspond to the number of words in the original sentence. For example, the sentence:

I love you

is an anagram of the sentence:

You olive

In this exercise, we will consider permutations of words anagrams of the sentence. In the above example:

You I love

is considered a separate anagram.

When producing anagrams, we will ignore character casing and punctuation characters.

Your ultimate goal is to implement a method `sentenceAnagrams`, which, given a list of words representing a sentence, finds all the anagrams of that sentence. Note that we used the term meaningful in defining what anagrams are. You will be given a dictionary, i.e. a list of words indicating words that have a meaning.

Here is the general idea. We will transform the characters of the sentence into a list saying how often each character appears. We will call this list the occurrence list. To find anagrams of a word we will find all the words from the dictionary which have the same occurrence list. Finding an anagram of a sentence is slightly more difficult. We will transform the sentence into its occurrence list, then try to extract any subset of characters from it to see if we can form any meaningful words. From the remaining characters we will solve the problem recursively and then combine all the meaningful words we have found with the recursive solution.

Let's apply this idea to our example, the sentence `You olive`. Lets represent this sentence as an occurrence list of characters `eiloouvy`. We start by subtracting some subset of the characters, say `i`. We are left with the characters `eloouvy`.

Looking into the dictionary we see that `i` corresponds to word `I` in the English language, so we found one meaningful word. We now solve the problem recursively for the rest of the characters `eloouvy` and obtain a list of solutions `List(List(love, you), List(you, love))`. We can combine`I` with that list to obtain sentences `I love you` and `I you love`, which are both valid anagrams.


Representation

We represent the words of a sentence with the `String` data type:

type Word = String

Words contain lowercase and uppercase characters, and no whitespace, punctuation or other special characters.

Since we are ignoring the punctuation characters of the sentence as well as the whitespace characters, we will represent sentences as lists of words:

type Sentence = List[Word]

We mentioned previously that we will transform words and sentences into occurrence lists. We represent the occurrence lists as sorted lists of character and integers pairs:

type Occurrences = List[(Char, Int)]

The list should be sorted by the characters in an ascending order. Since we ignore the character casing, all the characters in the occurrence list have to be lowercase. The integer in each pair denotes how often the character appears in a particular word or a sentence. This integer must be positive. Note that positive also means non-zero -- characters that do not appear in the sentence do not appear in the occurrence list either.

Finally, the dictionary of all the meaningful English words is represented as a `List` of words:

val dictionary: List[Word] = loadDictionary

The dictionary already exists for this exercise and is loaded for you using the `loadDictionary` utility method.

Computing Occurrence Lists

The `groupBy` method takes a function mapping an element of a collection to a key of some other type, and produces a `Map` of keys and collections of elements which mapped to the same key. This method groups the elements, hence its name.

Here is one example:

List("Every", "student", "likes", "Scala").groupBy((element: String) => element.length)

produces:

Map(
  5 -> List("Every", "likes", "Scala"),
  7 -> List("student")
)

Above, the key is the `length` of the string and the type of the key is `Int`. Every `String` with the same `length` is grouped under the same key -- its `length`.

Here is another example:

List(0, 1, 2, 1, 0).groupBy((element: Int) => element)

produces:

Map(
  0 -> List(0, 0),
  1 -> List(1, 1),
  2 -> List(2)
)

`Map`s provide efficient lookup of all the values mapped to a certain key. Any collection of pairs can be transformed into a `Map` using the `toMap` method. Similarly, any `Map` can be transformed into a `List` of pairs using the `toList` method.

In our case, the collection will be a `Word` (i.e. a `String`) and its elements are characters, so the `groupBy` method takes a function mapping characters into a desired key type.

In the first part of this exercise, we will implement the method `wordOccurrences` which, given a word, produces its occurrence list. In one of the previous exercises, we produced the occurrence list by recursively traversing a list of characters.

This time we will use the `groupBy` method from the Collections API (hint: you may additionally use other methods, such as `map` and `toList`). 

def wordOccurrences(w: Word): Occurrences

Next, we implement another version of the method for entire sentences. We can concatenate the words of the sentence into a single word and then reuse the method `wordOccurrences` that we already have.

def sentenceOccurrences(s: Sentence): Occurrences

Computing Anagrams of a Word

To compute the anagrams of a word, we use the simple observation that all the anagrams of a word have the same occurrence list. To allow efficient lookup of all the words with the same occurrence list, we will have to group the words of the dictionary according to their occurrence lists.

lazy val dictionaryByOccurrences: Map[Occurrences, List[Word]]

We then implement the method `wordAnagrams` which returns the list of anagrams of a single word:

def wordAnagrams(word: Word): List[Word]

Computing Subsets of a Set

To compute all the anagrams of a sentence, we will need a helper method which, given an occurrence list, produces all the subsets of that occurrence list.

def combinations(occurrences: Occurrences): List[Occurrences]

The `combinations` method should return all possible ways in which we can pick a subset of characters from `occurrences`. For example, given the occurrence list:

List(('a', 2), ('b', 2))

the list of all subsets is:

List(
  List(),
  List(('a', 1)),
  List(('a', 2)),
  List(('b', 1)),
  List(('a', 1), ('b', 1)),
  List(('a', 2), ('b', 1)),
  List(('b', 2)),
  List(('a', 1), ('b', 2)),
  List(('a', 2), ('b', 2))
)

The order in which you return the subsets does not matter as long as they are all included. Note that there is only one subset of an empty occurrence list, and that is the empty occurrence list itself.

Hint: investigate how you can use for-comprehensions to implement parts of this method.


Computing Anagrams of a Sentence

We now implement another helper method called `subtract` which, given two occurrence lists `x` and `y`, subtracts the frequencies of the occurrence list `y` from the frequencies of the occurrence list `x`:


def subtract(x: Occurrences, y: Occurrences): Occurrences


For example, given two occurrence lists for words `lard` and `r`:

val x = List(('a', 1), ('d', 1), ('l', 1), ('r', 1))
val y = List(('r', 1))

the `subtract(x, y)` is `List(('a', 1), ('d', 1), ('l', 1))`.

The precondition for the `subtract` method is that the occurrence list `y` is a subset of the occurrence list `x` -- if the list `y` has some character then the frequency of that character in `x` must be greater or equal than the frequency of that character in `y`.

When implementing `subtract` you can assume that `y` is a subset of `x`.

Hint: you can use `foldLeft`, and `-`, `apply` and `updated` operations on `Map`.

Now we can finally implement our `sentenceAnagrams` method for sequences.

def sentenceAnagrams(sentence: Sentence): List[Sentence]

Note that the anagram of the empty sentence is the empty sentence itself.

Hint: First of all, think about the recursive structure of the problem: what is the base case, and how should the result of a recursive invocation be integrated in each iteration? Also, using for-comprehensions helps in finding an elegant implementation for this method.

Test the `sentenceAnagrams` method on short sentences, no more than 10 characters. The combinations space gets huge very quickly as your sentence gets longer, so the program may run for a very long time. However for sentences such as `Linux rulez`, `I love you` or `Mickey Mouse` the program should end fairly quickly -- there are not many other ways to say these things.
Further Improvement (Optional)

This part is optional and is not part of an assignment, nor will be graded. You may skip this part freely.

The solution with enlisting all the combinations was concise, but it was not very efficient. The problem is that we have recomputed some anagrams more than once when recursively solving the problem. Think about a concrete example and a situation where you compute the anagrams of the same subset of an occurrence list multiple times.

One way to improve the performance is to save the results obtained the first time when you compute the anagrams for an occurence list, and use the stored result if you need the same result a second time. Try to write a new method `sentenceAnagramsMemo` which does this.


