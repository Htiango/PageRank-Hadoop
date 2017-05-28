package mapred.pagerank;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class PageRankReducer extends Reducer<Text, Text, Text, Text> {

	/**
	 * We here have two kinds of inputs to the reducer:
	 * 1. the value it self. (In order to know the adjacency nodes in reducer)
	 *    The format is the same as the input:
	 *    (node the link from)  (pageRank value)  ((link to 1);(link to 2);...)
	 * 2. all the pagerank value to nodes it links to 
	 *    The format is the as follows:
	 *    (node the link to)  (pageRank value the node spreads to) 
	 *
	 * The output format is linke below:
	 * (node the link from)  (pageRank value)  ((link to 1);(link to 2);...)
	 * For sink nodes, the last part is '$'
	 * 
	 */

	@Override
	protected void reduce(Text key, Iterable<Text> value,
			Context context)
			throws IOException, InterruptedException {
		int cur_iter = context.getConfiguration().getInt("cur_iter", 0);
		int total_iter = context.getConfiguration().getInt("total_iter", 0);
		String nodesTo = "";
		double pageRankValue = 0.15;
		for(Text line : value){
			String[] vec = line.toString().split("\\s+");
			if(vec.length == 2)
				nodesTo = vec[1];
			else{
				pageRankValue += Double.parseDouble(vec[0]);
			}	
		}
		String newValue = null;
		if(cur_iter != total_iter - 1) newValue = pageRankValue + "  " + nodesTo;
		else newValue = Double.toString(pageRankValue);
		context.write(key, new Text(newValue));
	}
}
