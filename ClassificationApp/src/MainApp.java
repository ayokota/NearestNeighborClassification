import java.util.Scanner;


public class MainApp {
	
	public static String testFile = "./sample.txt";
	public static int algorithm = 0;
	public static classifierUtilities utility = new classifierUtilities();
	
	public static String getUserInput() {
		@SuppressWarnings("resource")
		Scanner reader = new Scanner(System.in);
		return reader.nextLine();
	}
	
	public static void start() {
		System.out.println("Welcome to Akiyo Yokota's Feature Selection Algorithm.");
		System.out.println("Type in the name of the file to text : ");
		testFile = getUserInput();
		
		System.out.println("Type the number of the algorithm you want to run.");
		System.out.println("1) Forward Selection");
		System.out.println("2) Backward Elimination");
		System.out.println("3) Akiyo's Special Algorithm.");
		algorithm = Integer.parseInt(getUserInput());
		
		String fileContent = utility.readFile(testFile);
		
		System.out.println("Please wait while I normalize the data...");
		NearestNeighborClassifier classifier = new NearestNeighborClassifier(utility.getFeatureMap(fileContent));
		System.out.println("Done!");
		System.out.println("This dataset has " + classifier.getNumFeatures() 
				+ " features(not including the class attribute), with " + classifier.getNumInstance() + " instances.");
		System.out.println("Running nearest neighbor with all " + classifier.getNumFeatures() + " features, "
				+ "\nBeginning search.");
		if(algorithm ==1) {
			classifier.forwardSelection();
		} else if(algorithm ==2) {
			classifier.backwardElimination();
		} else if(algorithm ==3) {
			classifier.bestKSelection();
		} else {
			System.out.println("Invalid algorithm of choice. Goodbye!");
		}
		
	}

	public static void main(String[] args) {		
		start();
	}
}
