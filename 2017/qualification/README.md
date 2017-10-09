# Qualification 2017 - Streaming videos

Problem statement: https://github.com/BGodefroyFR/Google-Hashcode-Gym/blob/master/2017/qualification/task.pdf
Competition ranking: https://hashcode.withgoogle.com/hashcode_2017.html#qualificationRound


## Scores
| Instance               | Score         | CPU time |
| ---------------------- |:-------------:| :-------:|
| me_at_the_zoo          | 512,349       | < 1s     |
| trending_today         | 499,966       | ~1 min   |
| kittens                | 1,024,564     | ~10 min  |
| videos_worth_spreading | 606,516       | ~10s     |

**Total:** 2,643,395       
**Rank in competition:** #9    
    
**Best in competition:** 2,651,999 (team *Ababahalamaha*)    


## Installation

##### Run solution
```
cd 2017/qualification/solution
./build.sh
./run.sh
```

##### Score solution
```
cd 2017/qualification/scoring
./build.sh
./run.sh
```


## Algorithm
This approach considers the association of caches and videos (called "pairs").
The implemented solution relies on the following pseudo-code:
```
Generate all pairs <video, cache>
Score pairs

while pairs scores haven't converged
    for all pairs P taken by decreasing score
        Recompute P.score
        
for all pairs P taken by decreasing score
    if P.cache has enough remaining capacity
        Add P.video to P.cache 
```

The recomputation of pairs scores takes into account the pairs which currently have a higher score, like if pairs with higher scores have already been processed (i.e. video added to cache). That is, the gain of a pair could be lowered by pairs already processed (particularly by pairs with the same video).

In practice, depending on the instance of the problem, the number of iterations before convergence varies a lot. For *me_at_the_zoo* (183 pairs), 342 iterations are required while only 36 iterations are needed for *kittens* (4,999,996 pairs).

| Instance               | Nb pairs      | Nb iterations |
| ---------------------- |:-------------:| :------------:|
| me_at_the_zoo          | 183           | 342           |
| trending_today         | 1,000,000     | 100           |
| kittens                | 4,999,996     | 36            |
| videos_worth_spreading | 135,023       | 53            |
    
<br />    
        
The heuristic for pairs scoring takes into account the latency gain of the pair (i.e. how much latency would be reduced) and the size of the video, because space on caches is limited. The scoring function depends on some parameters which have been tuned for each instance of the problem.    
For *me_at_the_zoo* and *videos_worth_spreading*, adding some random to the scoring function has shown to improve results. Indeed, it probably helps the algorithm to get out of local minima.


## Other approaches
This is some other algorithms which have been attempted for this challenge.

#### Greedy knapsack assuming caches independence
This consists in solving the knapsack problem for each cache independenly. Since caches capacity and pretty big and there are a lot of video candidates for each cache (at least for *trending_today*, *kittens* and *videos_worth_spreading*), the knapsack problem needs to be solved greedily.    
This algorithm is very fast but doesn't perform that well.

| Instance               | Score         |
| ---------------------- |:-------------:|
| me_at_the_zoo          | 495,244       |
| trending_today         | 963,273       |
| kittens                | 499,966       |
| videos_worth_spreading | 595,910       |

**Total:** 2,554,393       
**Rank in competition:** #74  

#### Exact solving with Mixed Integer Programming
This approach consists in computing the exact solution using a MPI solver [[blog post](http://andijcr.github.io/blog/2017/03/15/hashCode-integer-programming-solution)]. In practice, this is tractable only for *me_at_the_zoo*, the smallest instance of the problem and this achieves the best possible score for it: *516,557*.

#### Greedy algorithm for *trending_today*
The *trending_today* instance has a very special structure: every endpoint is connected to every cache with same latency and every endpoint has the same datacenter latency. Therefore, this instance corresponds to the *multiple knapsack problem*. Solved greedily, a score of *499,994* could be obtained, which represents a very small improvement in comparison to the algorithms above.
