package mapred.pagerank;

import java.io.IOException;

import mapred.util.Tokenizer;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class PageRankMapper extends Mapper<LongWritable, Text, Text, Text> {

	/**
	 *
	 * The input format is linke below:
	 * (node the link from)  (pageRank value)  ((link to 1);(link to 2);...)
	 * For sink nodes, the last part is '$'
	 *
	 * We here emit two kinds of things to the reducer:
	 * 1. the value it self. (In order to know the adjacency nodes in reducer)
	 *    The format is the same as the input:
	 *    (node the link from)  (pageRank value)  ((link to 1);(link to 2);...)
	 * 2. all the pagerank value to nodes it links to 
	 *    The format is the as follows:
	 *    (node the link to)  (pageRank value the node spreads to) 
	 */


	@Override
	protected void map(LongWritable key, Text value,Context context)
			throws IOException, InterruptedException {
		
		String line = value.toString();
		String[] pageRankFeature = line.split("\\s+");

		context.write(new Text(pageRankFeature[0]), 
			new Text(pageRankFeature[1] + "  " + pageRankFeature[2]));

		String vec = pageRankFeature[2];
		if(!vec.startsWith("$")){
			String[] nodes = vec.split(";");
			int size = nodes.length;

			double pageRankValue = Double.parseDouble(pageRankFeature[1]);
			pageRankValue = pageRankValue * 0.85 / size;
			for (String node : nodes) {
				context.write(new Text(node), new Text(Double.toString(pageRankValue)));
			}
		}
	}
}
