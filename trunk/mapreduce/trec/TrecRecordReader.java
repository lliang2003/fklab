/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package trec;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.util.LineReader;

/**
 * Treats keys as offset in file and value as line.
 */
public class TrecRecordReader extends RecordReader<LongWritable, Text> {

	private CompressionCodecFactory compressionCodecs = null;
	private long start;
	private long pos;
	private long end;
	private LineReader in;
	private LongWritable key = null;
	private Text value = null;

	public void initialize(InputSplit genericSplit, TaskAttemptContext context)
			throws IOException {
		FileSplit split = (FileSplit) genericSplit;
		Configuration job = context.getConfiguration();
		start = split.getStart();
		end = start + split.getLength();
		final Path file = split.getPath();
		compressionCodecs = new CompressionCodecFactory(job);
		final CompressionCodec codec = compressionCodecs.getCodec(file);

		// open the file and seek to the start of the split
		FileSystem fs = file.getFileSystem(job);
		FSDataInputStream fileIn = fs.open(split.getPath());
		boolean skipFirstLine = false;
		if (codec != null) {
			in = new LineReader(codec.createInputStream(fileIn), job);
			end = Long.MAX_VALUE;
		} else {
			if (start != 0) {
				skipFirstLine = true;
				--start;
				fileIn.seek(start);
			}
			in = new LineReader(fileIn, job);
		}
		if (skipFirstLine) { // skip first line and re-establish "start".
			start += in.readLine(new Text(), 0, (int) Math.min(
					(long) Integer.MAX_VALUE, end - start));
		}
		this.pos = start;
	}

	public int nextLine() throws IOException {
		int size = in.readLine(line, Integer.MAX_VALUE, Integer.MAX_VALUE);
		pos += size;
		return size;
	}

	Text headline = null;
	Text line = new Text();

	public boolean nextKeyValue() throws IOException {
		if (pos >= end) {
			return false;
		}
		if (key == null) {
			key = new LongWritable();
		}
		key.set(pos);
		if (value == null) {
			value = new Text();
		}
		String recordStr = new String();
		if (headline == null) {
			while (!recordStr.startsWith("###")) {
				if (pos >= end) {
					return false;
				}
				int size = nextLine();
				if (size == 0) {
					key = null;
					value = null;
					return false;
				}
				recordStr = line.toString();
			}
		} else {
			recordStr = headline.toString();
			headline = null;
		}
		while (true) {
			int size = nextLine();
			if (line.toString().startsWith("###"))
				headline = new Text(line);
			if (size == 0 || line.toString().startsWith("###")) {
				break;
			}
			recordStr += "\n" + line;
		}
		value.set(recordStr);
		return true;
	}

	@Override
	public LongWritable getCurrentKey() {
		return key;
	}

	@Override
	public Text getCurrentValue() {
		return value;
	}

	/**
	 * Get the progress within the split
	 */
	public float getProgress() {
		if (start == end) {
			return 0.0f;
		} else {
			return Math.min(1.0f, (pos - start) / (float) (end - start));
		}
	}

	public synchronized void close() throws IOException {
		if (in != null) {
			in.close();
		}
	}
}
