package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V>  implements Map61B<K, V>{
    private class Node {
        private final K key;
        private V value;
        private Node left;
        private Node right;

        private Node(K key, V value) {
            this.key = key;
            this.value = value;
        }

    }
    private Node root;
    private int size;

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    public BSTMap() {
        root = null;
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        if (root == null) {
            return false;
        }
        return containsKeyHelper(root, key);
    }

    private boolean containsKeyHelper(Node n, K key) {
        if (n == null) {
            return false;
        }
        if (n.key.equals(key)) {
            return true;
        } else if (n.key.compareTo(n.key) > 0) {
            return containsKeyHelper(n.left, key);
        } else {
            return containsKeyHelper(n.right, key);
        }
    }

    @Override
    public V get(K key) {
        if (root == null) {
            return null;
        }
        return getHelper(root, key);
    }

    private V getHelper(Node n, K key) {
        if (n == null) {
            return null;
        }
        if (n.key.equals(key)) {
            return n.value;
        } else if (n.key.compareTo(n.key) > 0) {
            return getHelper(n.left, key);
        } else {
            return getHelper(n.right, key);
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        if (root == null) {
            root = new Node(key, value);
            size++;
        } else {
            putHelper(root, key, value);
        }
    }

    private void putHelper(Node n, K key, V value) {
        if (n.key.equals(key)) {
            n.value = value;
        } else if (n.key.compareTo(n.key) > 0) {
            if (n.left == null) {
                n.left = new Node(key, value);
                size++;
            } else {
                putHelper(n.left, key, value);
            }
        } else {
            if (n.right == null) {
                n.right = new Node(key, value);
                size++;
            } else {
                putHelper(n.right, key, value);
            }
        }
    }

    @Override
    public Set<K> keySet() {
        /* throw an UnsupportedOperationException */
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key) {
        /* throw an UnsupportedOperationException */
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        /* throw an UnsupportedOperationException */
        throw new UnsupportedOperationException();
    }

    public void inOrderPrint() {
        /* throw an UnsupportedOperationException */
        throw new UnsupportedOperationException();
    }
    @Override
    public Iterator<K> iterator() {
        /* throw an UnsupportedOperationException */
        throw new UnsupportedOperationException();
    }
}
