# Dropquotes Solver

This Java program solves Dropquotes puzzles, such as those found at http://www.dropquotes.com.

# License
My code (i.e., anything in the com.dixonnet package) is available under the MIT license. The program contains code from

* The Kyoto Language Modeling Toolkit, [KYLM](https://github.com/neubig/kylm), available under LGPL

* Ben Fowler's Dancing Links implementation, [DLX](https://github.com/benfowler/dancing-links), available under GPL

* Word lists and frequency tables from [Keith Vertanen](http://www.keithv.com/software/giga/).

It also has build and runtime dependencies on 

* [JSOUP](http://jsoup.org), available under the MIT license.

* [jopt-simple](https://pholser.github.io/jopt-simple/index.html), available under the MIT license.

# Installation
The solver is built using Gradle, so it shouldn't require any dependencies other than java. You should be able to
clone and build the application using the following steps

```bash
git clone git clone https://github.com/adnamsale/dropquotes.git
cd dropquotes
./gradlew installApp
```

After building, you can run the application with the command

```bash
./build/install/dropquotes/bin/dropquotes
```

Alternatively, the project contains an IntelliJ IDEA project, so you can clone the repo and open the project in IDEA.

## Command Line Arguments
```bash
dropquotes [-d<s/m/l>] [file to solve]
```
 
Dropquotes takes two optional arguments. The first, specified with `-d`, is the size of the dictionary to use. The 
solver can only find solutions where all the words appear in its dictionary. Using a smaller dictionary will result
in a much shorter runtime, but may fail to find any solutions. The large dictionary will almost always find a solution,
but the runtime for long problems may be prohibitive. If omitted, the default is `-dm`, the medium dictionary of 64k words.
 
The second argument is the name of a file to process. If omitted, the solver will use the internal puzzle stored in
the `test.html` resource. The file should be an HTML source file saved from the http://www.dropquotes.com/play.php site.
Be sure to save the page using the 'Page Source' option to save the raw HTML; do not save as a 'Web Archive' or similar.

# How It Works
There are three steps to the solution.

First, find all of the words that can fit in the available spaces with the letters available. This is done naively by
simply considering all options for the first letter then all options for the second letter etc. As each new letter is added, 
we check whether the resulting string is a valid prefix and, if not, immediately move on to the next alternative for that letter.
Even with the large dictionary, this stage takes a negligible time.

Second, consider all of the ways that the available words can be placed in the grid ensuring that each available letter
is used once and once only. This is a textbook case of the [Exact Cover](http://en.wikipedia.org/wiki/Exact_cover)
problem and can be efficiently solved by the [Dancing Links](http://en.wikipedia.org/wiki/Dancing_Links) algorithm. In
the language of dancing links, each row in the matrix corresponds to placing a particular word in a particular position
in the grid. Each column represents a constraint, either that a particular letter must be used in a particular column or
that a a particular position in the grid needs to be filled. The latter constraints are necessary to prevent the algorithm
finding a solution that tries to place two words simultaneously in the same position in the answer grid. 

Finally, the previous stage will typically yield more than one solution. To pick the best, we rank them based on word
and word-pair frequency in the Giga 20k database. This is a collection of data about the 20000 most common words in
a studied corpus. Given two solutions, we prefer the one that contains more words known to the database i.e., more 
common words. Given two solutions with an equal percentage of known words, we prefer the one with a higher percentage
of known word pairs. This is a simple heuristic, but it works well. An alternative heuristic here might be to
 perform a Google search on the quote author and proposed solution.

# Author
Mark Dixon (mark@lowla.io)

Twitter: adnamsale
