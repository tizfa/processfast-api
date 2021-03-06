/*
 *
 * ****************
 * Copyright 2015 Tiziano Fagni (tiziano.fagni@isti.cnr.it)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ******************
 */

package it.cnr.isti.hlt.processfast.data;

import java.io.Serializable;
import java.util.Iterator;

import it.cnr.isti.hlt.processfast.core.TaskDataContext;
import it.cnr.isti.hlt.processfast.utils.Pair;
import it.cnr.isti.hlt.processfast.utils.Procedure3;

/**
 * A partitionable dataset which store items characterized by a key and
 * a value.
 * 
 * @author Tiziano Fagni (tiziano.fagni@isti.cnr.it)
 * @since 1.0.0
 *
 * @param <K> The type of the key.
 * @param <V> The type of the value.
 */
public interface PairPartitionableDataset<K extends Serializable, V extends Serializable> extends PartitionableDataset<Pair<K,V>> {
	/**
	 * Returns a dataset of (K, V) pairs
	 * where the values for each key are aggregated using the given reduce function.
	 * 
	 * @param func The reduce function.
	 * @return This partitionable dataset containing (K, V) pairs
	 * where the values for each key are aggregated using the given reduce function.
	 */
	PairPartitionableDataset<K, V> reduceByKey(PDFunction2<V, V, V> func);
	
	
	/**
	 * Sort the items in the collection by ordering them by using its key value. The sort
	 * order is determined by "ascending" flag. The key must implements {@link java.lang.Comparable}
     * interface.
	 * 
	 * @param ascending True if the items must be ordered in ascending order, false in
	 * descendant order.
	 * @return This partitionable dataset containing with items ordered in the specified way.
	 */
	PairPartitionableDataset<K, V> sortByKey(boolean ascending);
	
	
	/**
	 * Return a collection by grouping elements by key. Each group consists of a key and a sequence of elements mapping to that key.
	 * 
	 * @return This partitionable dataset containing items grouped by key.
	 */
	PairPartitionableDataset<K,DataIterable<V>> groupByKey();
	
	
	
	/**
	 * Join this [key, value] collection with the specified [key,value] collection by
	 * joining items using their key values.
	 * 
	 * @param dataset The dataset to join.
	 * @return This partitionable dataset containing joined items.
	 */
	<T extends Serializable> PairPartitionableDataset<K, Pair<V, T>> join(PairPartitionableDataset<K, T> dataset);


	
	@Override
	PairPartitionableDataset<K,V> cache(CacheType cacheType);
	
	@Override
	PairPartitionableDataset<K,V> saveOnStorageManager(Procedure3<TaskDataContext,StorageManager, Pair<K,V>> func);
	
	@Override
	PairPartitionableDataset<K,V> enableLocalComputation(boolean enable);


	/**
	 * Return a RDD containing the just the original pair values.
	 *
	 * @return A new RDD containing the just the original pair values.
	 */
	PartitionableDataset<V> values();


	/**
	 * Pass each value in the key-value pair PD through a map function without changing the keys.
	 *
	 * @param func The map function to be applied to each pair value.
	 * @param <T>  The resulting type of the map function.
	 * @return A new RDD containing the changed pair values.
	 */
	<T extends Serializable> PairPartitionableDataset<K, T> mapValues(PDFunction<V, T> func);

	PairPartitionableDataset<K, V> distinct();

	/**
	 * Suggest to the runtime how to size each partition of the dataset. Each partition
	 * will be processed in RAM by a set of processors.
	 *
	 * @param partitionSize The total number of items assigned to a partition.
	 * @return A new partitionable dataset.
	 */
	PairPartitionableDataset<K, V> withPartitionSize(int partitionSize);

	/**
	 * Return a new data collection containing the union of this dataset with the specified
	 * dataset. The resulting dataset may contains duplicate items. The equality between items is
	 * checked according to equals() and hashCode() methods.
	 *
	 * @param dataset The dataset to merge with.
	 * @return A new partitionable dataset containing the union of this dataset with the specified
	 * dataset.
	 */
	PairPartitionableDataset<K, V> union(PartitionableDataset<Pair<K, V>> dataset);
}
