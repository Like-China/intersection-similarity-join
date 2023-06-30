# Contact Pre-warning: Contact Matching
This implementation follows the content of the article "Intersection Similarity Join Over Moving Objects", which implements the part of BF-Alg, MJ-Alg and BJ-Alg, and evaluates their performances for different settings.

# Environment
Ubuntu 11
Java SE 1.8.0_91

# balltree
The package 'balltree' implements the indexing structure of our ball-tree with re-partition techniques.

# M-tree
The package 'mtree' implements the indexing construction of M*-tree.

# utils
The package 'utils' implements the classes of Location, Ellipse, ContactPair, etc.

# evaluation implementation
The default parameter settings are listed in Settings.java.
All data source is loaded by Loader.java
The evaluation of BF-Alg, MJ-Alg and BJ-Alg are implemented by BruteEvaluation.java, MTreeEvaluation.java, and BallTreeEvaluation.java, respectively. The BJ-Alg with repartition is conducted in TernaryBallTreeEvaluation.java.
The refinement is implemented in Refine.java
Test.java is the main entrance of this project for overall evaluation with varied parameters.

