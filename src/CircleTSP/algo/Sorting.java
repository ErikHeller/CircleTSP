package CircleTSP.algo;

import CircleTSP.entities.Point;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Sorting {

	public static List<Point> bucketSort(List<Point> points) {
		int length = (int) Math.ceil(((double)points.size()) / 4.0);
		return bucketSort(points, length, 1);
	}

    public static List<Point> bucketSort(final List<Point> points, int length) {
    	return bucketSort(points, length, 1);
	}

	// Reference 1
	public static List<Point> bucketSort(final List<Point> points, final int numBuckets, final int numThreads) {
    	if (numBuckets < 1)
    		throw new IllegalArgumentException("Length can't be 0");

		// Check if values are in interval [0,1)
		for (Point p : points) {
			double val = p.getAngle();
			if (val < 0 || val >= 1)
				throw new IllegalArgumentException("The values have to be" +
						" double floating point numbers between 0 and 1");
		}

        List<Point> result = new LinkedList<>();

		if (points.size() > 1) {
			List<List<Point>> buckets = new ArrayList<>(numBuckets);
			for (int i = 0; i < numBuckets; i++) {
				buckets.add(new LinkedList<>());
			}
			for (Point point : points) {
				List<Point> bucket = buckets.get((int) Math.floor(numBuckets * point.getAngle()));
				bucket.add(point);
			}

			Thread[] threads = new Thread[numThreads];
			for (int i = 0; i < numThreads; i++) {
				final int threadID = i;
				threads[threadID] = new Thread(() -> {
					for (int j = 0; j < Math.floorDiv(numBuckets,numThreads); j++) {
						int bucketIndex = (j*numThreads)+threadID;
						List<Point> currentBucket = buckets.get(bucketIndex);
						if (currentBucket.size() <= 1)
						    continue;
						if (currentBucket.size() < 20)
							buckets.set(bucketIndex, insertionSort(currentBucket));
						else
                            buckets.set(bucketIndex, mergeSort(currentBucket));
					}
				});
				threads[threadID].start();
			}
			for (Thread t : threads) {
				try {
					t.join();
				} catch (InterruptedException eArg) {
					eArg.printStackTrace();
				}
			}

			// Concatenate buckets
            // TODO: Find more efficient way to concatenate buckets (addAll() iterates over all list entries)
			for (int i = 0; i < numBuckets; i++) {
			    List<Point> currentBucket = buckets.get(i);
			    if (currentBucket.size() > 0)
			        result.addAll(currentBucket);
			}
		}
		return result;
	}

	// Collection.sort() uses a variant of MergeSort
	private static List<Point> mergeSort(final List<Point> sort) {
    	sort.sort((p1, p2) -> (int) Math.signum(p1.getAngle() - p2.getAngle()));
    	return sort;
	}

	// Taken from Reference 2, rewritten to match Types, ignoring cases with
	// Length 0 and 1
	private static List<Point> insertionSort(final List<Point> sort) {
		if (sort.size() > 1) {
			Point temp;
			for (int i = 1; i < sort.size(); i++) {
				temp = sort.get(i);
				int j = i;
				while (j > 0 && sort.get(j - 1).getAngle() > temp.getAngle()) {
					sort.set(j, sort.get(j - 1));
					j--;
				}
				sort.set(j, temp);
			}
		}
		return sort;
	}
}

// Reference 1:
// https://reader.uni-mainz.de/WiSe2016-17/08-079-060-00/Lists/DocumentLib/Vorlesungsfolien/03_randomisierung_ann.pdf
// Reference 2: http://www.java-programmieren.com/insertionsort-java.php