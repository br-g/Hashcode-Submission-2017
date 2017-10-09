import java.util.HashSet;

public class Cache {
	int id;
	HashSet<Video> addedVideos;
	int remainingCapacity;

	public Cache(int id, int capacity) {
		this.id = id;
		addedVideos = new HashSet<Video>();
		remainingCapacity = capacity;
	}

	// Tries to add a video to the cache.
	// Returns true if it is a success, false otherwise.
	public boolean addVideo(Video video) {
		if (remainingCapacity < video.size || addedVideos.contains(video.id)) {
			return false;
		}

		addedVideos.add(video);
		remainingCapacity -= video.size;
		return true;
	}
}