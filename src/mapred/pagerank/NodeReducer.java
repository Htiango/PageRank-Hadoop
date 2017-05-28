package mapred.pagerank;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class NodeReducer extends Reducer<Text, Text, Text, Text> {

	/**
	 * Here the input format is like below: 
	 * (node the link from) ((node the link to) or '$')
	 * 
	 * For those sink nodes, the values are all equal '$' and we regare it as 
	 * the out way. 
	 *
	 * The output format is linke below:
	 * (node the link from)  (pageRank value)  ((link to 1);(link to 2);...)
	 * For sink nodes, the last part is '$'
	 */

	@Override
	protected void reduce(Text key, Iterable<Text> value,
			Context context)
			throws IOException, InterruptedException {

		boolean ifSink = true;
		String pageRankValue = 1.0 + "  ";
		String outNode = "";
		for (Text node: value) {
			if(!node.toString().equals("$")){
				ifSink = false;
				outNode += node.toString() + ";";
			}
		}

		if(!ifSink)
			context.write(key, new Text(pageRankValue + outNode));
		else
			context.write(key, new Text(pageRankValue + "$"));
	}
}
