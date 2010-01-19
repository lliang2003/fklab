package da;

import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;

// Make split size as large as possible.
public class MaxInputFormat extends TextInputFormat {
	protected long computeSplitSize(long blockSize, long minSize,
			long maxSize) {
		return maxSize;
	}
}
