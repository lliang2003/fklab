package trec;

import java.util.HashMap;

import org.apache.hadoop.mapreduce.Partitioner;

public class TrecPartitioner<K, V> extends Partitioner<K, V> {

	@Override
	public int getPartition(K key, V value, int numReduceTasks) {
		String[] parts = key.toString().split(":");
		int p = nmap.get(parts[0])*10+Integer.parseInt(parts[1])%10;
		System.out.println("part "+p);
		return 1;
	}

	HashMap<String, Integer> nmap = new HashMap<String, Integer>();

	TrecPartitioner() {
		for (int i = 0; i < 10; ++i) {
			nmap.put("build" + (i + 1), i);
			nmap.put("index" + (i + 1), 10 + i);
			if (i < 5) {
				nmap.put("maze6" + (i + 1), 20 + i);
				nmap.put("jupiter" + i, 25 + i);
			}
		}
		nmap.put("maze4", 23);
	}
}
