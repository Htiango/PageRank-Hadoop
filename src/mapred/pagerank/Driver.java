package mapred.pagerank;

import java.util.HashSet;
import java.io.IOException;
import mapred.job.Optimizedjob;
import mapred.util.FileUtil;
import mapred.util.SimpleParser;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

public class Driver {
	private final static int iteration_time = 30;
	public static HashSet<String> nodeSet = new HashSet<String>();
	public final static double paraD = 0.85;
	public final static String sinkSign = "$";

	public static void main(String args[]) throws Exception {
		SimpleParser parser = new SimpleParser(args);

		String input = parser.get("input");
		String output = parser.get("output");
		String tmpdir = parser.get("tmpdir");

		getNodeState(input, tmpdir + "/iter-0/output");

		//Read/Write files in every iterations.
		for(int i = 0; i < iteration_time; i++){
			String dir = tmpdir + "/iter-" + (i+1);
			String dirPrev = tmpdir + "/iter-" + i;
			if (i == iteration_time - 1){
				getPageRankValue(dir + "/tmp", dirPrev + "/output", output, i);
			}
			else{
				getPageRankValue(dir + "/tmp", dirPrev + "/output", dir + "/output", i);
			}
		}
	}

	private static void getNodeState(String input, String output)
			throws Exception {
		Optimizedjob job = new Optimizedjob(new Configuration(), input, output,
				"Get all the node state vector");
		job.setClasses(NodeMapper.class, NodeReducer.class, null);
		job.setMapOutputClasses(Text.class, Text.class);

		job.run();
	}

	private static void getPageRankValue(String tmpDir, 
		String input, String output, int iter) throws IOException,
		ClassNotFoundException, InterruptedException {

		Configuration conf = new Configuration();
		conf.set("tmpDir", tmpDir);
		conf.setInt("total_iter", iteration_time);
		conf.setInt("cur_iter", iter);
		Optimizedjob job = new Optimizedjob(conf, input, output,
				"Iterative get the page rank value");
		job.setClasses(PageRankMapper.class, PageRankReducer.class, null);
		job.setMapOutputClasses(Text.class, Text.class);
		job.run();
	}



}
