package CircleTSP.algo.sorting;

import CircleTSP.entities.Point;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BucketSort implements PointSorter {

	@Override
	public List<Point> sort(List<Point> points) {
		int length = (int) Math.ceil(((double)points.size()) / 4.0);
		return sort(points, length, 1);
	}

    public List<Point> sort(final List<Point> points, int numBuckets) {
    	return sort(points, numBuckets, 1);
	}

	// Reference:https://reader.uni-mainz.de/WiSe2016-17/08-079-060-00/Lists/DocumentLib/Vorlesungsfolien/03_randomisierung_ann.pdf
	public List<Point> sort(final List<Point> points, final int numBuckets, final int numThreads) {
		PointSorter insertionSort = new InsertionSort();
		PointSorter mergeSort = new MergeSort();

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
							buckets.set(bucketIndex, insertionSort.sort(currentBucket));
						else
                            buckets.set(bucketIndex, mergeSort.sort(currentBucket));
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
}
