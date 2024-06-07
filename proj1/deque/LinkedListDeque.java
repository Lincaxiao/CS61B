package deque;

public class LinkedListDeque<T> {
    private class Node {
        public T item;
        public Node prev;
        public Node next;

        public Node () {
            this.next = null;
            this.prev = null;
        }

        public Node (T item) {
            this.item = item;
            this.next = null;
            this.prev = null;
        }

        public Node (T item, Node prev, Node next) {
            this.prev = prev;
            this.next = next;
            this.item =item;
        }
    }

    private int size;
    private final Node sentinel;

    public LinkedListDeque () {
        this.sentinel = new Node();
        size = 0;
    }

    public boolean isEmpty () {
        return size == 0;
    }

    public void addFirst (T item) {
        Node addone = new Node(item, sentinel, sentinel.next);
        /* if originally empty */
        if (isEmpty()) {
            size++;
            addone.next = sentinel;
            sentinel.next = addone;
            sentinel.prev = addone;
            return;
        } else {
            size++;
            sentinel.next = addone;
            addone.next.prev = addone;
        }
    }

    public void addLast(T item) {
        Node addone = new Node(item, sentinel.prev, sentinel);
        /* if originally empty */
        if (isEmpty()) {
            size++;
            addone.prev = sentinel.prev;
            sentinel.prev = addone;
            sentinel.next = addone;
            return;
        } else {
            size++;
            sentinel.prev = addone;
            addone.prev.next = addone;
        }
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        Node current = sentinel;
        int j = 0;
        for (int i = 0; i < size; i++, j++) {
            System.out.print(current.next.item);
            if (j < size - 1) {
                System.out.print(" ");
            }
            current = current.next;
        }
        System.out.print("\n");
    }

    public T removeFirst() {
        if (size() == 0) {
            return null;
        } else if (size() == 1) {
            sentinel.prev = null;
            T val = sentinel.next.item;
            size--;
            sentinel.next = null;
            return  val;
        } else {
            T val = sentinel.next.item;
            sentinel.next = sentinel.next.next;
            sentinel.next.prev = sentinel;
            size--;
            return val;
        }
    }

    public T removeLast() {
        if (size() == 0) {
            return null;
        } else if (size() == 1) {
            sentinel.prev = null;
            T val = sentinel.next.item;
            size--;
            sentinel.next = null;
            return  val;
        } else {
            T val = sentinel.prev.item;
            sentinel.prev = sentinel.prev.prev;
            sentinel.prev.next = sentinel;
            size--;
            return val;
        }
    }

    public T get(int index) {
        if (index >= size) {
            return null;
        }
        Node current = sentinel;
        for (int i = 0; i <= index; i++) {
            current = current.next;
        }
        return current.item;
    }

    private T getRecursiveHelper(Node node, int index) {
        if (index == 0) {
            return node.item;
        } else {
            return getRecursiveHelper(node.next, index - 1);
        }
    }
    
    public T getRecursive(int index) {
        if (index >= size) {
            return null;
        } else {
            return getRecursiveHelper(sentinel.next, index);
        }
    }

    public boolean equals(Object object) {
        return false;
    }
}
