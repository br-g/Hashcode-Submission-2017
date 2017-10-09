import java.util.ArrayList;
import java.util.Random;

// Represents a pair <cache, video>.
public class CacheVideoPair implements Comparable<CacheVideoPair> {
	int id;
	double score;

	Cache cache;
	Video video;
	ArrayList<Request> requests;
	
	public CacheVideoPair(int id) {
		this.id = id;
		requests = new ArrayList<Request>();
	}

	// Updates requests best latency, if the current pair is processed.
	public void updateRequestBestLatency() {
		for (Request curRequest: requests) {
			curRequest.bestLatency = Math.min(curRequest.bestLatency, 
				curRequest.endpoint.cachesLatency.get(cache));
		}
	}

	// Heuristic for the current pair.
	// Computes a score using the potential latency gain and the size of the video.
	// Parameters:
	//   - randomRange: the proportion of random in the score
	//   - randomEvolution: sets the evolution of randomness in score calculation, over iterations
	//   - iterationNum: the number of iterations processed so far in the ranking algorithm
	//   - sizeExp: sets the influence of the size of the video in the score calculation
	public double computeScore(double randomRange, double randomEvolution, int iterationNum, double sizeExp) {
		long gain = 0;

		for (Request curRequest: requests) {
			gain += Math.max(0,
					curRequest.nbRequest * 
						(curRequest.bestLatency - curRequest.endpoint.cachesLatency.get(cache)));
		}

		score = (double)gain / (double)Math.pow(video.size, sizeExp);

		// Adds some random
		if (randomRange > 0.0) {
			Random rand = new Random();
			double randomFactor = 1.0 + randomRange * Math.pow(randomEvolution, iterationNum) * rand.nextDouble();
			score *= randomFactor;
		}

		return score;
	}

	@Override
    public int compareTo(CacheVideoPair o) {
    	return new Double(o.score).compareTo(new Double(this.score));
    }
}