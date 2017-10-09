import java.util.ArrayList;
import java.util.HashMap;

public class Endpoint {
	int id;
	ArrayList<Cache> caches;
	HashMap<Cache, Integer> cachesLatency;
	int datacenterLatency;

	public Endpoint(int id) {
		this.id = id;
		caches = new ArrayList<Cache>();
		cachesLatency = new HashMap<Cache, Integer>();
	}

	public void addCache(Cache c, int latency) {
		caches.add(c);
		cachesLatency.put(c, latency);
	}
}