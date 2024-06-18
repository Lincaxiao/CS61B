package hashmap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author Zhang, Xiaochen
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    private int size;
    private int initialSize;
    private double loadFactor;
    private int bucketSize;
    private Set<K> keys;
    /** MAX_LOAD is the maximum load factor of the hash table */
    private static final double MAX_LOAD = 0.75;
    /** INITIAL_SIZE is the initial bucket size of the hash table */
    private static final int INITIAL_SIZE = 16;


    /** Constructors */
    public MyHashMap() {
        this(INITIAL_SIZE, MAX_LOAD);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, MAX_LOAD);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.initialSize = initialSize;
        this.loadFactor = maxLoad;
        this.size = 0;
        this.bucketSize = initialSize;
        buckets = createTable(bucketSize);
        for (int i = 0; i < bucketSize; i++) {
            buckets[i] = createBucket();
        }
        keys = new HashSet<>();
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new HashSet<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }

    /**
     * Uses the key to find the index of the bucket that would contain the key
     * @param key the key to hash
     * @return the index of the bucket that would contain the key
     */
    private int hashIndex(K key) {
        int h = key.hashCode();
        return Math.floorMod(h, bucketSize);
    }

    @Override
    public void clear() {
        for (int i = 0; i < bucketSize; i++) {
            buckets[i].clear();
        }
        keys.clear();
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        int index = hashIndex(key);
        for (Node node : buckets[index]) {
            if (node.key.equals(key)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public V get(K key) {
        int index = hashIndex(key);
        for (Node node : buckets[index]) {
            if (node.key.equals(key)) {
                return node.value;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        resize();
        int index = hashIndex(key);
        for (Node node : buckets[index]) {
            if (node.key.equals(key)) {
                node.value = value;
                return;
            }
        }
        buckets[index].add(createNode(key, value));
        keys.add(key);
        size++;
    }

    /**
     * Check if the size of the hash table exceeds the load factor
     * If so, double the size of the hash table and rehash all the keys
     */
    private void resize() {
        if ((double) size / bucketSize <= loadFactor) {
            return;
        }
        int newSize = bucketSize * 2;
        Collection<Node>[] newBuckets = createTable(newSize);
        for (int i = 0; i < newSize; i++) {
            newBuckets[i] = createBucket();
        }
        for (Collection<Node> bucket : buckets) {
            for (Node node : bucket) {
                int newIndex = (node.key.hashCode() & 0x7fffffff) % newSize;
                newBuckets[newIndex].add(node);
            }
        }
        buckets = newBuckets;
        bucketSize = newSize;
    }

    @Override
    public V remove(K key) {
        return remove(key, get(key));
    }

    @Override
    public V remove(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        int index = hashIndex(key);
        for (Node node : buckets[index]) {
            if (node.key.equals(key) && node.value.equals(value)) {
                buckets[index].remove(node);
                keys.remove(key);
                size--;
                return value;
            }
        }
        return null;
    }

    @Override
    public Set<K> keySet() {
        return keys;
    }

    @Override
    public Iterator<K> iterator() {
        return keys.iterator();
    }
}
