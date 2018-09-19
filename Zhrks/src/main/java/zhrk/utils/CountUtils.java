package zhrk.utils;

import java.util.Random;

public class CountUtils {

	public static final int ARRAY_SIZE = 20;
	public static final int RANDOM_MAX_SIZE = 100;

	public static void main(String[] args) {
		int[] array = new int[ARRAY_SIZE];
		Random r = new Random();
		Integer flag = r.nextInt(RANDOM_MAX_SIZE);
		System.out.println(flag%2);
		for (int i = 0; i < array.length; i++) {
			array[i] = r.nextInt(RANDOM_MAX_SIZE);
			System.out.print(array[i] + " ");
		}
		bubbleSort(array);
		System.out.println();
		for (int i = 0; i < array.length; i++) {
			System.out.print(array[i] + " ");
		}
	}

	/**
	 * 随机生成100以内的某个数，判断奇偶
	 * @param array
	 * 2018年8月1日 下午3:24:46
	 */
	public static boolean getCount() {
		Random r = new Random();
		Integer flag = r.nextInt(RANDOM_MAX_SIZE);
		if(flag%2 == 0)
			return true;
		return false;
	}
	
	/**
	 * 0-100随机生成一个数字
	 * @return
	 * 2018年8月7日 下午5:35:23
	 */
	public static Integer getInt() {
		Random r = new Random();
		return r.nextInt(RANDOM_MAX_SIZE);
	}
	
	private static void bubbleSort(int[] array) {
		for (int i = 0; i < array.length - 1; i++) {
			for (int j = 0; j < array.length - i - 1; j++){
				if (array[j] < array[j + 1]) {
					int temp = array[j];
					array[j] = array[j + 1];
					array[j + 1] = temp;
				}
			}
		}
     }
}
