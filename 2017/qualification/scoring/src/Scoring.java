import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileNotFoundException;

public class Scoring {
	int nbVideo;
	int nbEndpoint;
	int nbRequest;
	int nbCache;
	int cachesCapacity;

	ArrayList<Request> requests;
	ArrayList<Endpoint> endpoints;
	ArrayList<Cache> caches;
	ArrayList<Integer> videoSizes;

	public boolean loadInput(String filePath) {
    	try {
	        BufferedReader br = new BufferedReader(new FileReader(filePath));
	        String[] lineData = null;
	        
	        // First line, general numbers
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
				caches.add(new Cache(i));
			}

			// video sizes
			videoSizes = new ArrayList<Integer>(nbVideo);
			lineData = br.readLine().split(" ");
			for (String s: lineData) {
				videoSizes.add(Integer.parseInt(s));
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

					endpoints.get(endpointId).addCache(caches.get(cacheId), latency);
				}
			}

			// requests
			for (int requestId = 0; requestId < nbRequest; requestId++) {
				lineData = br.readLine().split(" ");
				requests.get(requestId).videoId = Integer.parseInt(lineData[0]);
				requests.get(requestId).endpoint = endpoints.get(Integer.parseInt(lineData[1]));
				requests.get(requestId).nbRequest = Integer.parseInt(lineData[2]);
			}

   		} catch (IOException e) {
        	System.out.println(e);
        	return false;
        }

        return true;
    }

    public boolean loadOutput(String filePath) {
    	try {
            FileReader fileReader = new FileReader(filePath);
	        BufferedReader br = new BufferedReader(fileReader);
	        String[] lineData = null;
	        
	        lineData = br.readLine().split(" ");
			int nbUsedCaches = Integer.parseInt(lineData[0]);

			for (int i = 0; i < nbUsedCaches; i++) {
				lineData = br.readLine().split(" ");

				Cache curCache = caches.get(Integer.parseInt(lineData[0]));

				for (int j = 1; j < lineData.length; j++) {
					curCache.videos.add(Integer.parseInt(lineData[j]));
				}
			}
   		} catch (IOException e) {
        	System.out.println(e);
        	return false;
        }

        return true;
    }

    public boolean checkCachesCapacity() {
    	for (Cache curCache: caches) {
    		int consumedCapacity = 0;

    		for (int curVideoId: curCache.videos) {
    			consumedCapacity += videoSizes.get(curVideoId);
    		}

    		if (consumedCapacity > cachesCapacity) {
    			System.out.println("Error: cache " + curCache.id + " has not enough capacity.");
    			System.out.println("	available: " + cachesCapacity);
    			System.out.println("	used: " + consumedCapacity);
    			return false;
    		}
    	}

    	return true;
    }

    public int getScore() {
    	long savedTimeSum = 0; // in ms
    	long totalNbRequests = 0;

    	for (Request curRequest: requests) {
    		Endpoint curEndpoint = curRequest.endpoint;

    		int bestLatency = curEndpoint.datacenterLatency;
    		for (Cache curCache: curEndpoint.caches) {
    			if (curCache.videos.contains(curRequest.videoId)) {
    				int lat = curEndpoint.cachesLatency.get(curCache);
    				bestLatency = Math.min(bestLatency, lat);
    			}
    		}

    		savedTimeSum += (long)((curEndpoint.datacenterLatency - bestLatency) * curRequest.nbRequest);
    		totalNbRequests += curRequest.nbRequest;
    	}

    	return (int)((savedTimeSum * new Long(1000)) / totalNbRequests);
    }
}