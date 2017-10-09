import java.util.ArrayList;
import java.util.HashMap;

public class Endpoint {
	int id;
	int datacenterLatency;
	ArrayList<Cache> caches;
	HashMap<Cache, Integer> cachesLatency;

	public Endpoint(int id) {
		this.id = id;
		caches = new ArrayList<Cache>();
		cachesLatency = new HashMap<Cache, Integer>();
	}
}