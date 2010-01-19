package da;

public class Util {
	public static int[] intArray(String[] items) {
		int[] nums = new int[items.length];
		for (int i = 0; i < nums.length; ++i)
			nums[i] = Integer.parseInt(items[i]);
		return nums;
	}

}
