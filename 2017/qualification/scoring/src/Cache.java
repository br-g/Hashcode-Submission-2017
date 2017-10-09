import java.util.HashSet;

public class Cache {
	int id;
	HashSet<Integer> videos;

	public Cache(int id) {
		this.id = id;
		videos = new HashSet<Integer>();
	}
}