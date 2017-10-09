public class Request {
	int id;
	int nbRequest;
	Video video;
	Endpoint endpoint;
	int bestLatency;

	public Request(int id) {
		this.id = id;
	}
}