import java.lang.System;

public class Main {
		
	public static void main(String[] args) {
		solve("../input/me_at_the_zoo.in", "../output/me_at_the_zoo.out", 0.4, 0.9, 1.0);
		solve("../input/kittens.in", "../output/kittens.out", 0.0, 0.0, 0.78);
		solve("../input/trending_today.in", "../output/trending_today.out", 0.0, 0.0, 1.0);
		solve("../input/videos_worth_spreading.in", "../output/videos_worth_spreading.out", 0.2, 0.5, 1.0);	
	}

	// Parameters:
	//   - inputPath: path of the input data
	//   - outputPath: path of the output data
	//   - randomRange: the proportion of random in <video, cache> pairs scoring
	//   - randomEvolution: sets the evolution of randomness in <video, cache> pairs scoring, over iterations
	//   - sizeExp: sets the influence of the size of the video in <video, cache> pairs scoring
	static void solve(String inputPath, String outputPath,
		double randomRange, double randomEvolution, double sizeExp) {

		System.out.println("\n---- Solving " + inputPath);
		System.out.print("-- random range: " + randomRange);
		System.out.print(", random evolution: " + randomEvolution);
		System.out.println(", size exponent: " + sizeExp);

		Solver s = new Solver();
		s.loadInput(inputPath);

		double startTime = System.nanoTime();
		int nbIt = s.fillCaches_iterativePairsRanking(randomRange, randomEvolution, sizeExp);
		int solverTime = (int)((System.nanoTime() - startTime) / (long)1e6); // in ms

		System.out.println("-- Completed in " + nbIt + " iteration(s) (" + solverTime + " ms).");
		System.out.print("-- Exporting result to " + outputPath + ".");
		s.exportResult(outputPath);
	}
}