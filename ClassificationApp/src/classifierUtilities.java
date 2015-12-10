import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;


public class classifierUtilities {
	public String readFile(String filename) {
	    try {
	    	StringBuilder content = new StringBuilder();
	    	BufferedReader br = new BufferedReader(new FileReader(filename));
	    	String line = "";
	    	while(( line = br.readLine())!=null) 
	    		content.append(line).append("\n");
	    	br.close();
	    	return content.toString();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return "";
	}
	
	public List<String> TokenizeToListByLine(String content) {
		List <String> tokens = new LinkedList<String> ();
		
		StringTokenizer st = new StringTokenizer(content, "\n");
		
		while(st.hasMoreElements()) {
			tokens.add(st.nextElement().toString());
		}
		return tokens;
	}
	
	public List<String> TokenizeToListBySpace(String content) {
		List <String> tokens = new LinkedList<String> ();
		
		StringTokenizer st = new StringTokenizer(content, " ");
		
		while(st.hasMoreElements()) {
			tokens.add(st.nextElement().toString());
		}
		return tokens;
	}
	
	public List< Map < Integer, Map< Integer, Double>>>  getFeatureMap(String content) {
		//  List < Map < classNo , Map <featureNo, featureVal> > >
		List< Map < Integer, Map< Integer, Double>>> featureMap = new LinkedList< Map < Integer, Map< Integer, Double>>> ();
		
		List<String> rows = TokenizeToListByLine(content);
		for(String row : rows ) {
			StringTokenizer st = new StringTokenizer(row, " ");
			int classNo = new BigDecimal(st.nextElement().toString()).intValue();
			
			int featureNo = 1;
			Map<Integer, Double> featureSet = new HashMap<Integer, Double> ();
			while(st.hasMoreElements()) {
				featureSet.put(featureNo, new BigDecimal(st.nextElement().toString()).doubleValue());
				featureNo = featureNo + 1;
			}
			Map < Integer, Map< Integer, Double>> newRow = new HashMap < Integer, Map< Integer, Double>>();
			newRow.put(classNo, featureSet);
			featureMap.add(newRow);
		}
		return featureMap;
	}
}
