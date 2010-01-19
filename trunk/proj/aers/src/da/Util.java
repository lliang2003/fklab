package da;
/**
 * Utility class.
 * @author fankai
 */
public class Util {
	/**
	 * Convert a String array to a integer array.
	 */
	public static int[] intArray(String[] tokens) {
		int[] nums = new int[tokens.length];
		for (int i = 0; i < nums.length; ++i)
			nums[i] = Integer.parseInt(tokens[i]);
		return nums;
	}
}
