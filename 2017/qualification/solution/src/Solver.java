import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileNotFoundException;

public class Solver {

	int nbVideo;
	int nbEndpoint;
	int nbRequest;
	int nbCache;
	int cachesCapacity;

	ArrayList<Request> requests;
	ArrayList<Endpoint> endpoints;
	ArrayList<Cache> caches;
	ArrayList<Video> videos;

	// Fills caches by generating and ranking all valuable <video, cache> pairs.
	// Returns the number of iterations before ranking convergence.
	// Parameters:
	//   - randomRange: the proportion of random in <video, cache> pairs scoring
	//   - randomEvolution: sets the evolution of randomness in <video, cache> pairs scoring, over iterations
	//   - sizeExp: sets the influence of the size of the video in <video, cache> pairs scoring
	public int fillCaches_iterativePairsRanking(double randomRange, double randomEvolution, double sizeExp) {

		// Generates and stores all pairs <video, cache> of the instance (only those enabling a gain).
		System.out.println("Generating pairs...");
		ArrayList<CacheVideoPair> pairs = new ArrayList<CacheVideoPair>();
		HashMap<Cache, HashMap<Video, CacheVideoPair>> pairsIndex = new HashMap<Cache, HashMap<Video, CacheVideoPair>>();

		int nextPairId = 0;
		for (Request curRequest: requests) {
			for (Cache curCache: curRequest.endpoint.caches) {
				if (!pairsIndex.containsKey(curCache)) {
					pairsIndex.put(curCache, new HashMap<Video, CacheVideoPair>());
				}
				if (!pairsIndex.get(curCache).containsKey(curRequest.video)) {
					CacheVideoPair newPair = new CacheVideoPair(nextPairId++);
					newPair.cache = curCache;
					newPair.video = curRequest.video;
					pairsIndex.get(curCache).put(curRequest.video, newPair);
					pairs.add(newPair);
				}
				pairsIndex.get(curCache).get(curRequest.video).requests.add(curRequest);
			}
		}

		System.out.println("Found " + nextPairId + " pairs.");
		System.out.println("Initial Ranking...");

		// Scores & sorts <video, cache> pairs, using heuristic.
		for (CacheVideoPair curPair: pairs) {
			curPair.computeScore(randomRange, randomEvolution, 0, sizeExp);
		}
		Collections.sort(pairs);

		// Updates scores and ranking iteratively.
		int iterationCount = 0;
		boolean isRankingConverged = false;
		while (!isRankingConverged) {

			System.out.println("it. " + iterationCount);

			// Resets best latency
			for (Request curRequest: requests) {
				curRequest.bestLatency = curRequest.endpoint.datacenterLatency;
			}

			isRankingConverged = true;

			// Simulates videos addition to caches and updates scores
			int[] cachesUsage = new int[nbCache];
			for (CacheVideoPair curPair: pairs) {
				isRankingConverged &= 
					(curPair.score == curPair.computeScore(randomRange, randomEvolution, iterationCount, sizeExp));

				if (cachesUsage[curPair.cache.id] + curPair.video.size <= cachesCapacity) {
					cachesUsage[curPair.cache.id] += curPair.video.size;
					curPair.updateRequestBestLatency();
				}
			}

			// Recomputes pairs ranking
			Collections.sort(pairs);

			iterationCount ++;
		}

		// Processes best ranked <Cache, Video> pairs
		for (CacheVideoPair curPair: pairs) {
			if (curPair.score <= 0.0) {
				break;
			}
			curPair.cache.addVideo(curPair.video);
		}

		return iterationCount;
	}

	// Loads input data from file
    public boolean loadInput(String filePath) {
    	try {
	        BufferedReader br = new BufferedReader(new FileReader(filePath));
	        String[] lineData = null;
	        
	        // First line
	        lineData = br.readLine().split(" ");
			nbVideo = Integer.parseInt(lineData[0]);
			nbEndpoint = Integer.parseInt(lineData[1]);
			nbRequest = Integer.parseInt(lineData[2]);
			nbCache = Integer.parseInt(lineData[3]);
			cachesCapacity = Integer.parseInt(lineData[4]);

			// Create lists of entities
			requests = new ArrayList<Request>(nbRequest);
			for (int i = 0; i < nbRequest; i++) {
				requests.add(new Request(i));
			}
			endpoints = new ArrayList<Endpoint>(nbEndpoint);
			for (int i = 0; i < nbEndpoint; i++) {
				endpoints.add(new Endpoint(i));
			}
			caches = new ArrayList<Cache>(nbCache);
			for (int i = 0; i < nbCache; i++) {
				caches.add(new Cache(i, cachesCapacity));
			}
			videos = new ArrayList<Video>(nbVideo);
			for (int i = 0; i < nbVideo; i++) {
				videos.add(new Video(i));
			}

			// video sizes
			lineData = br.readLine().split(" ");
			int videoCount = 0;
			for (String s: lineData) {
				videos.get(videoCount).size = Integer.parseInt(s);
				videoCount ++;
			}

			// endpoints
			for (int endpointId = 0; endpointId < nbEndpoint; endpointId++) {
				lineData = br.readLine().split(" ");
				endpoints.get(endpointId).datacenterLatency = Integer.parseInt(lineData[0]);
				
				int nbConnectedCaches = Integer.parseInt(lineData[1]);
				for (int i = 0; i < nbConnectedCaches; i++) {
					lineData = br.readLine().split(" ");
					int cacheId = Integer.parseInt(lineData[0]);
					int latency = Integer.parseInt(lineData[1]);

					endpoints.get(endpointId).caches.add(caches.get(cacheId));
					endpoints.get(endpointId).cachesLatency.put(caches.get(cacheId), latency);
				}
			}

			// requests
			for (int requestId = 0; requestId < nbRequest; requestId++) {
				lineData = br.readLine().split(" ");
				int videoId = Integer.parseInt(lineData[0]);
				int endpointId = Integer.parseInt(lineData[1]);
				int nbRequest = Integer.parseInt(lineData[2]);

				requests.get(requestId).video = videos.get(videoId);
				requests.get(requestId).endpoint = endpoints.get(endpointId);
				requests.get(requestId).video = videos.get(videoId);
				requests.get(requestId).nbRequest = nbRequest;
				requests.get(requestId).bestLatency = endpoints.get(endpointId).datacenterLatency;
			}

   		} catch (IOException e) {
        	System.out.println(e);
        	return false;
        }

        return true;
    }

    // Exports results to file
    public boolean exportResult(String filePath) {
		try {
		    PrintWriter writer = new PrintWriter(filePath, "UTF-8");
		    writer.println(nbCache);

		    for (Cache curCache: caches) {
		    	String line = curCache.id + " ";
		    	for (Video v: curCache.addedVideos) {
		    		line += v.id + " ";
		    	}
		    	writer.println(line);
		    }

		    writer.close();
		} catch (IOException e) {
		   System.out.println(e);
		   return false;
		}

		return true;
	}
}