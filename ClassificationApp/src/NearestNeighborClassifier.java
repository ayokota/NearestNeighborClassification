import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class NearestNeighborClassifier {
	
	private List< Map < Integer, Map< Integer, Double>>> data;
	private int numFeatures;
	private int numInstance;
	private int comparison = 0;
	
	public double distanceCalc(double x, double y) {
		return (x-y) * (x-y);
	}
		
	public int getBestFeature() {
		boolean print = true;	
		int bestFeature = 0;
		double bestScore = 0.0;	
		for(int i = 1; i< (getNumFeatures()+1); i++) {	
			double score = getFeatureScore1D(i);
			
			if(print) {
				System.out.println("\tUsing feature(s) {" + i +"} accuracy is " + score*100.0 +"%");
			}
			if(score > bestScore) {
				bestScore = score;
				bestFeature = i;
			}
		}
		
		return bestFeature;
	}
	
	public Map<Integer, Boolean> initBestFeatures() {
		Map<Integer, Boolean> bestFeatures = new HashMap<Integer, Boolean> ();
		for(int i = 1; i<(numFeatures+1); i++) {
			bestFeatures.put(i, false);
		}
		return bestFeatures;
	}
	
	public void printFeatureUsage(Map<Integer, Boolean> bestFeatures, Integer consideredFeature) {
		System.out.print("\tUsing feature(s) {");
		for(Integer feature : bestFeatures.keySet()) {
			if(bestFeatures.get(feature)&& feature!=-consideredFeature) {		
				System.out.print(feature + ",");
			}
		}
		if(consideredFeature>0)
			System.out.print(consideredFeature + "}, ");
		else
			System.out.print("}, ");	
		
		comparison = comparison + 1;
	}
	
	public void announceBestFeatures(Map<Integer, Boolean> bestFeatures, double bestScore) {
		System.out.print("Feature set {");
		for(Integer feature : bestFeatures.keySet()) {
			if(bestFeatures.get(feature)) {
				System.out.print(feature + ",");
			}
		}
		System.out.println("} was best, accuracy is " + bestScore * 100.0 + "%");

	}
	
	public void decreasingScore(Map<Integer, Boolean> bestFeatures, Integer attemptedFeature, double score) {
		System.out.println("(Warning, Accuracy has decrease! Continuing search in case of local maxima)");
		printFeatureUsage(bestFeatures, attemptedFeature);
		
		System.out.println("accuracy is " + score * 100.0 + "%");
	}
	
	public void finishSearch(Map<Integer, Boolean> bestFeatures, double score) {
		System.out.print("Finished search!! the best feature subset is {");
		for(Integer feature: bestFeatures.keySet()) {
			if(bestFeatures.get(feature)) {
				System.out.print(feature + "," );
			}
		}
		System.out.println("}, which has an accuracy of " + score * 100.0 + "%");
		
		System.out.println("Number of comparison done: " + comparison);
	}
	
	public void forwardSelection() {
		Map<Integer, Boolean> bestFeatures = initBestFeatures();
		int bestFeature = getBestFeature();
		double currentScore = getFeatureScore1D(bestFeature);
		System.out.println("Feature set {" + bestFeature +"} was bestm accuracy is " + currentScore*100.0 + "%");

		bestFeatures.put(bestFeature, true) ;
		
		boolean keepGoing = true;
		while(keepGoing) {
			keepGoing = false;
			
			Integer newFeature = 0;
			double newBestScore = 0;
			for(int i = 1; i<= numFeatures; i++) {
				if(bestFeatures.get(i)) {
					continue;
				}
				double newScore = getFeatureScoreND(bestFeatures, i);
				
				printFeatureUsage(bestFeatures, i);
				System.out.println("accuracy is " + newScore * 100.0 + "%");
				
				if(newScore>newBestScore) {
					newBestScore = newScore;
					newFeature = i;
				}
				
			}
			if(newBestScore>currentScore) {
				currentScore = newBestScore;
				bestFeatures.put(newFeature, true);
				keepGoing= true;
				announceBestFeatures(bestFeatures, currentScore);
			} else {
				decreasingScore(bestFeatures, newFeature, newBestScore);
			}
		}
		finishSearch(bestFeatures, currentScore);

	}
	
	public void bestKSelection() {
		Map<Integer, Double> scoreList = getScoreList();

		List<Pair> sortedScoreList = getSortedScoreList(scoreList);

		Map<Integer, Boolean> bestFeatures = initBestFeatures();
		double currentScore = 0;
		boolean keepGoing = true;
		for(int i = 1 ; i<=numFeatures && keepGoing; i++) {
			Pair topScore = sortedScoreList.get(i-1);
			double newScore = getFeatureScoreND(bestFeatures, topScore.getFirst());
			
			printFeatureUsage(bestFeatures, topScore.getFirst());
			System.out.println("accuracy is " + newScore * 100.0 + "%");
			
			if(newScore > currentScore) {
				currentScore = newScore;
				bestFeatures.put(topScore.getFirst(), true);
				keepGoing = true;
				announceBestFeatures(bestFeatures, currentScore);
			} else {
				decreasingScore(bestFeatures, topScore.getFirst(), newScore);
				keepGoing = false;
			}
		}
		finishSearch(bestFeatures, currentScore);
	}
	
	public void backwardElimination() {
		Map<Integer, Boolean> bestFeatures = initBestFeatures();
		for(Integer feature: bestFeatures.keySet()) 
			bestFeatures.put(feature, true);
		double currentScore = getFeatureScoreND(bestFeatures,0);
		
		System.out.println("Calculated scores with all " + numFeatures +" features,"
				+ " score is " + currentScore);
		
		boolean keepGoing = true;
		while(keepGoing) {
			keepGoing = false;
			
			Integer badFeature = 0;
			double newBestScore = 0;
			
			for(int i = 1; i<= numFeatures; i++) {
				if(!bestFeatures.get(i)) {
					continue;
				}
				double newScore = getFeatureScoreND(bestFeatures, -i);
				//System.out.println(i + "=" + newScore);
				printFeatureUsage(bestFeatures, -i);
				System.out.println("accuracy is " + newScore * 100.0 + "%");


				if(newScore>newBestScore) {
					newBestScore = newScore;
					badFeature = i;
				}
			}
			if(newBestScore>currentScore) {
				
				currentScore = newBestScore;
				bestFeatures.put(badFeature, false);
				keepGoing= true;
				announceBestFeatures(bestFeatures, currentScore);

			} else {
				decreasingScore(bestFeatures, -badFeature, newBestScore);

			}
		}
		finishSearch(bestFeatures, currentScore);

	}
	
	private Map<Integer, Double> getScoreList() {
		Map<Integer, Double> scoreList =  new HashMap<Integer, Double> ();
		for(int i = 1; i<= numFeatures; i++) {
			double score = getFeatureScore1D(i);
			scoreList.put(i, score);
			System.out.println("\tUsing feature(s) {" + i + "} accuracy is " + score*100.0 +"%");
		}
		return scoreList;
	}
	
	private List<Pair> getSortedScoreList(Map<Integer, Double> scoreList) {
		List<Pair> sortedScoreList = new LinkedList<Pair> ();
		
		for(int i = 1; i<=numFeatures; i++) {
			double max = (Collections.max(scoreList.values()));
			for(Entry<Integer,Double> entry : scoreList.entrySet()) {
				if(entry.getValue()==max) {
					sortedScoreList.add(new Pair(entry.getKey(), entry.getValue()));
					scoreList.put(entry.getKey(), 0.0);
				}
			}
		}
		return sortedScoreList;
	}
		
	private List<Integer> createComboFeatures(Map<Integer, Boolean> bestFeatures, int consideredFeature) {
		List<Integer> comboFeature = new LinkedList<Integer> ();
		for(Integer feature: bestFeatures.keySet()) {
			if(bestFeatures.get(feature)==true) {
				comboFeature.add(feature);
			}
		}
		if(consideredFeature==0)
			return comboFeature;
		else if(consideredFeature>0)
			comboFeature.add(consideredFeature);
		else {
			for(Iterator<Integer> itr = comboFeature.listIterator(); itr.hasNext();) {
				if(itr.next()==-consideredFeature) {
					itr.remove();
				}
			}
		}
		return comboFeature;
	}
	
	public double getFeatureScoreND(Map<Integer, Boolean> bestFeatures, int consideredFeature) {
		int correct = 0;
		List<Integer> comboFeatures = createComboFeatures(bestFeatures, consideredFeature);
				
		for( Map < Integer, Map< Integer, Double>> row : data) {//each row
			for(Integer classNO : row.keySet()) { //extract classNo
				Map<Integer, Double> instance = new HashMap<Integer, Double> ();
				for(Integer feature : comboFeatures) {
					instance.put(feature,  row.get(classNO).get(feature));
				}
				if(nearestneighborND(instance, classNO)==classNO) {
					correct = correct + 1;
				}
			}
		}

		return correct/new Double(numInstance);
	}
	
	public int nearestneighborND(Map<Integer, Double> instance, int classNo) {
		double nearestDistance = 5000;
		int nearestClass = 0;
		
		for( Map < Integer, Map< Integer, Double>> row : data) {
			for(Integer classNO : row.keySet()) {	//class No

				double sum = 0.0;
				for(Integer feature: instance.keySet()) {
					sum = sum + distanceCalc(instance.get(feature), row.get(classNO).get(feature));
				}
				double distance = Math.sqrt(sum);
				if(distance < nearestDistance && distance!=0) {
					nearestDistance = distance;
					nearestClass = classNO;
				}
			}
		}	
		return nearestClass;
	}
	
	//return the class of nearestNeighbor
	public int nearestNeighbor1D(double featureVal, int featureNo,  int classNo) {
		double nearestDistance = 5000;
		int nearestClass =0;
		for( Map < Integer, Map< Integer, Double>> row : data) {
			for(Integer classNO : row.keySet()) {	//class No
				double distance = distanceCalc(featureVal, row.get(classNO).get(featureNo));
				if(distance < nearestDistance && distance!=0) {
					nearestDistance = distance;
					nearestClass = classNO;
				}
			}
		}
		return nearestClass;
	}	
	
	public double getFeatureScore1D(int feature) {
		int correct = 0;
				
		for( Map < Integer, Map< Integer, Double>> row : data) {
			
			for(Integer classNO : row.keySet()) {	
				if(nearestNeighbor1D(row.get(classNO).get(feature), feature, classNO) == classNO) {
					correct = correct + 1;
				}
			}
		}
		return correct/new Double(numInstance);
	}
	
	public List<Map<Integer, Map<Integer, Double>>> getData() {
		return data;
	}

	public void setData(List<Map<Integer, Map<Integer, Double>>> data) {
		this.data = data;
	}

	public int getNumFeatures() {
		return numFeatures;
	}

	public void setNumFeatures(int numFeatures) {
		this.numFeatures = numFeatures;
	}

	public NearestNeighborClassifier(List< Map < Integer, Map< Integer, Double>>> data) {
		this.data = data;
		this.numFeatures = calcNumFeatures();
		this.numInstance = data.size();
	}

	public int getNumInstance() {
		return numInstance;
	}

	public void setNumInstance(int numInstance) {
		this.numInstance = numInstance;
	}
	
	private int calcNumFeatures() {
		for( Map < Integer, Map< Integer, Double>> row : data) {
			for(Integer classNo : row.keySet()) {
				return row.get(classNo).keySet().size() ;
			}
		}
		return 0;
	}

}
