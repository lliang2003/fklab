package da;

import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;

/** 
 * Make split size as large as possible.
 * This class is used to set split size dynamically in programs, user need to 
 * call MaxInputFormat.setMaxInputSplitSize to specify split size.
 * 
 * Just like using TextInputFormat and setting both max and min split size.
 * 
 * @author fankai
 */
public class MaxInputFormat extends TextInputFormat {
	protected long computeSplitSize(long blockSize, long minSize,
			long maxSize) {
		return maxSize;
	}
}
