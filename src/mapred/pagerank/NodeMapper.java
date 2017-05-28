package mapred.pagerank;


import java.io.IOException;

import mapred.util.Tokenizer;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class NodeMapper extends Mapper<LongWritable, Text, Text, Text> {

	/**
	 * Here the input format is like below: 
	 * (node the link from) (node the link to)
	 * 
	 * And we get the node value and emit it to reducer. 
	 * But we also add a sinkSign and emit it to reducer, so that we can know
	 * if a node have no out ways, which is known as sink node. They only have
	 * sinkSign '$'
	 */

	@Override
	protected void map(LongWritable key, Text value,Context context)
			throws IOException, InterruptedException {
		String line = value.toString();
		String[] words = line.split("\\s+");

		if (!(words[0].startsWith("#"))){
			context.write(new Text(words[0]), new Text(words[1]));
			context.write(new Text(words[1]), new Text("$"));
		}
	}
}
