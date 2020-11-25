package com.simple.xrcraft.common.utils.common;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Semaphore;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @description:
 * @author pthahnil
 * @date 2020/4/8 15:08
 */
public class CommonUtils {

	/**
	 * 掩码，头尾各保留一段
	 * @param str
	 * @param head
	 * @param tail
	 * @return
	 */
	public static String maskInfo(String str, int head, int tail) {
		return maskInfo(str, head, tail, "*");
	}


	public static String maskInfo(String str, int head, int tail, String seprator) {
		if(StringUtils.isBlank(str)){
			return str;
		}
		if(str.length() < head + tail) {
			return str;
		}

		int startCt = str.length() - head - tail;
		String stars = IntStream.range(0, startCt).boxed().map(i -> seprator).collect(Collectors.joining());

		return str.substring(0, head) + stars + str.substring(str.length() - tail, str.length());
	}

	/**
	 * list按批次拆分
	 * @param list
	 * @param batchSize 子list大小
	 * @param <T>
	 * @return
	 */
	public static <T> List<List<T>> splitList(List<T> list, int batchSize) {

		List<List<T>> retList = new ArrayList<>();
		if(CollectionUtils.isEmpty(list) || batchSize <= 0) {
			return retList;
		}
		int size = list.size();
		int listNum = size % batchSize == 0 ? size / batchSize : size / batchSize + 1;

		for (int i = 0; i < listNum; i++) {
			int startIndex = i * batchSize;
			int endIndex = (startIndex + batchSize > size) ? size : startIndex + batchSize;
			List<T> subList = list.subList(startIndex, endIndex);
			retList.add(subList);
		}
		return retList;
	}

	/**
	 * 当前文件夹目录
	 * @return
	 * @throws Exception
	 */
	public static File getFilePath() throws Exception {
		String path = CommonUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		File file = new File(path);
		return file.getParentFile();
	}

	/**
	 * 异步转换
	 * @param list
	 * @param function
	 * @param executor
	 * @param bucketSize
	 * @param <C>
	 * @param <T>
	 * @return
	 */
	public static <C,T> List<T> asyncConvert(List<C> list, Function<C, T> function, Executor executor, int bucketSize){
		if(CollectionUtils.isEmpty(list)){
			return null;
		}
		CountDownLatch latch = new CountDownLatch(list.size());
		Semaphore semaphore = new Semaphore(bucketSize);

		List<FutureTask<T>> tasks = new ArrayList<>();
		for (C c : list) {
			FutureTask<T> task = new FutureTask<T>(() -> {
				try {
					semaphore.acquire();

					return function.apply(c);
				} catch (Exception e){} finally {
					semaphore.release();
					latch.countDown();
				}
				return null;
			});

			executor.execute(task);
			tasks.add(task);
		}
		try {
			latch.await();
		} catch (Exception e){}

		return tasks.stream()
				.map(task -> {
					try {
						return task.get();
					} catch (Exception e) { }
					return null;
				})
				.filter(t -> null != t)
				.collect(Collectors.toList());
	}
}
