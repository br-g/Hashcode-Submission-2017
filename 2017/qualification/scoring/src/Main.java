public class Main {
		
	public static void main(String[] args) {
		int finalScore = 0;
		
		finalScore += scoreInstance("../input/trending_today.in", "../output/trending_today.out");
		finalScore += scoreInstance("../input/videos_worth_spreading.in", "../output/videos_worth_spreading.out");
		finalScore += scoreInstance("../input/me_at_the_zoo.in", "../output/me_at_the_zoo.out");
		finalScore += scoreInstance("../input/kittens.in", "../output/kittens.out");

		System.out.println("\nTotal: " + finalScore);
	}

	static int scoreInstance(String inputPath, String outputPath) {
		Scoring sc = new Scoring();
		sc.loadInput(inputPath);
		sc.loadOutput(outputPath);

		if (!sc.checkCachesCapacity()) {
			return 0;
		}
		
		int instanceScore = sc.getScore();
		System.out.println(outputPath + ": " + instanceScore);
		return instanceScore;
	}
}