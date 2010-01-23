package da;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * Copy contents of files of this group from the old directory to a file in new
 * directory.
 */
public class ReorgnizeMapper extends Mapper<Object, Text, ItemSet, IntWritable> {
	public static Log log = LogFactory.getLog(ReorgnizeMapper.class);

	public void run(Context context) throws IOException, InterruptedException {
		int id = context.getTaskAttemptID().getTaskID().getId();
		log.info("id:" + id);
		log.info("attempt id:" + context.getTaskAttemptID());
		Configuration conf = context.getConfiguration();
		FileSystem fs = FileSystem.get(conf);
		String odir = conf.get("oldtreedir");
		String ndir = conf.get("newtreedir");
		String[] files = conf.get("treefiles").split(";");
		log.info("old dir:" + odir);
		log.info("new dir:" + ndir);
		log.info("tree files:" + conf.get("treefiles"));
		if (id >= files.length)
			return;
		String[] myfiles = files[id].split(",");
		log.info("my files:" + Arrays.toString(myfiles));

		log.info("create file:" + ndir + "/" + myfiles[0]);
		OutputStream os = fs.create(new Path(ndir + "/" + myfiles[0]));
		for (int i = 0; i < myfiles.length; ++i) {
			long len = fs.getFileStatus(new Path(odir + "/" + myfiles[i]))
					.getLen();
			log.info("copy file content:" + myfiles[i] + "\tlen=" + len);
			InputStream is = fs.open(new Path(odir + "/" + myfiles[i]));
			IOUtils.copyBytes(is, os, conf, false);
			is.close();
		}
		os.close();

		for (FileStatus status : fs.listStatus(new Path(ndir))) {
			System.out.println(status.getPath() + "\t" + status.getLen());
		}
	}
}
