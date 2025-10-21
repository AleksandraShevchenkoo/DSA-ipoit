package by.it.group410971.shevchenko.lesson12;

import java.util.*;

public class MySplayMap implements NavigableMap<Integer, String> {

    private static class Node {
        Integer key;
        String value;
        Node left, right, parent;

        Node(Integer key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    private Node root;
    private int size;

    public MySplayMap() {
        root = null;
        size = 0;
    }

    /////////////////////////////////////////////////////////////////////////
    //////               Обязательные к реализации методы             ///////
    /////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        if (size == 0) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        inOrderTraversal(root, sb);
        if (sb.length() > 1) {
            sb.setLength(sb.length() - 2);
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String put(Integer key, String value) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }

        String[] oldValue = new String[1];
        root = insert(root, key, value, oldValue);
        if (oldValue[0] == null) {
            size++;
        }
        return oldValue[0];
    }

    @Override
    public String remove(Object key) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }
        if (!(key instanceof Integer)) {
            return null;
        }

        String[] removedValue = new String[1];
        root = delete(root, (Integer) key, removedValue);
        if (removedValue[0] != null) {
            size--;
        }
        return removedValue[0];
    }

    @Override
    public String get(Object key) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }
        if (!(key instanceof Integer)) {
            return null;
        }

        root = splay(root, (Integer) key);
        return (root != null && root.key.equals(key)) ? root.value : null;
    }

    @Override
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        if (value == null) {
            throw new NullPointerException("Value cannot be null");
        }
        if (!(value instanceof String)) {
            return false;
        }
        return containsValue(root, (String) value);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public SortedMap<Integer, String> headMap(Integer toKey) {
        MySplayMap result = new MySplayMap();
        headMap(root, toKey, result);
        return result;
    }

    @Override
    public SortedMap<Integer, String> tailMap(Integer fromKey) {
        MySplayMap result = new MySplayMap();
        tailMap(root, fromKey, result);
        return result;
    }

    @Override
    public Integer firstKey() {
        if (root == null) {
            throw new NoSuchElementException();
        }
        Node min = findMin(root);
        root = splay(root, min.key);
        return min.key;
    }

    @Override
    public Integer lastKey() {
        if (root == null) {
            throw new NoSuchElementException();
        }
        Node max = findMax(root);
        root = splay(root, max.key);
        return max.key;
    }

    @Override
    public Integer lowerKey(Integer key) {
        Node node = lowerNode(root, key);
        if (node != null) {
            root = splay(root, node.key);
            return node.key;
        }
        return null;
    }

    @Override
    public Integer floorKey(Integer key) {
        Node node = floorNode(root, key);
        if (node != null) {
            root = splay(root, node.key);
            return node.key;
        }
        return null;
    }

    @Override
    public Integer ceilingKey(Integer key) {
        Node node = ceilingNode(root, key);
        if (node != null) {
            root = splay(root, node.key);
            return node.key;
        }
        return null;
    }

    @Override
    public Integer higherKey(Integer key) {
        Node node = higherNode(root, key);
        if (node != null) {
            root = splay(root, node.key);
            return node.key;
        }
        return null;
    }

    // Вспомогательные методы splay-дерева

    private Node rotateRight(Node x) {
        Node y = x.left;
        x.left = y.right;
        y.right = x;
        return y;
    }

    private Node rotateLeft(Node x) {
        Node y = x.right;
        x.right = y.left;
        y.left = x;
        return y;
    }

    private Node splay(Node root, Integer key) {
        if (root == null || root.key.equals(key)) {
            return root;
        }

        if (key < root.key) {
            if (root.left == null) {
                return root;
            }

            // Zig-Zig (Left Left)
            if (key < root.left.key) {
                root.left.left = splay(root.left.left, key);
                root = rotateRight(root);
            }
            // Zig-Zag (Left Right)
            else if (key > root.left.key) {
                root.left.right = splay(root.left.right, key);
                if (root.left.right != null) {
                    root.left = rotateLeft(root.left);
                }
            }

            return (root.left == null) ? root : rotateRight(root);
        } else {
            if (root.right == null) {
                return root;
            }

            // Zag-Zig (Right Left)
            if (key < root.right.key) {
                root.right.left = splay(root.right.left, key);
                if (root.right.left != null) {
                    root.right = rotateRight(root.right);
                }
            }
            // Zag-Zag (Right Right)
            else if (key > root.right.key) {
                root.right.right = splay(root.right.right, key);
                root = rotateLeft(root);
            }

            return (root.right == null) ? root : rotateLeft(root);
        }
    }

    private Node insert(Node root, Integer key, String value, String[] oldValue) {
        if (root == null) {
            return new Node(key, value);
        }

        root = splay(root, key);

        if (root.key.equals(key)) {
            oldValue[0] = root.value;
            root.value = value;
            return root;
        }

        Node newNode = new Node(key, value);
        if (key < root.key) {
            newNode.right = root;
            newNode.left = root.left;
            root.left = null;
        } else {
            newNode.left = root;
            newNode.right = root.right;
            root.right = null;
        }

        return newNode;
    }

    private Node delete(Node root, Integer key, String[] removedValue) {
        if (root == null) {
            return null;
        }

        root = splay(root, key);

        if (!root.key.equals(key)) {
            return root;
        }

        removedValue[0] = root.value;

        if (root.left == null) {
            return root.right;
        }

        Node temp = root.right;
        root = splay(root.left, key);
        root.right = temp;

        return root;
    }

    private boolean containsValue(Node node, String value) {
        if (node == null) {
            return false;
        }
        if (value.equals(node.value)) {
            return true;
        }
        return containsValue(node.left, value) || containsValue(node.right, value);
    }

    private Node findMin(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    private Node findMax(Node node) {
        while (node.right != null) {
            node = node.right;
        }
        return node;
    }

    private void inOrderTraversal(Node node, StringBuilder sb) {
        if (node != null) {
            inOrderTraversal(node.left, sb);
            sb.append(node.key).append("=").append(node.value).append(", ");
            inOrderTraversal(node.right, sb);
        }
    }

    private void headMap(Node node, Integer toKey, MySplayMap result) {
        if (node != null) {
            headMap(node.left, toKey, result);
            if (node.key < toKey) {
                result.put(node.key, node.value);
            }
            headMap(node.right, toKey, result);
        }
    }

    private void tailMap(Node node, Integer fromKey, MySplayMap result) {
        if (node != null) {
            tailMap(node.left, fromKey, result);
            if (node.key >= fromKey) {
                result.put(node.key, node.value);
            }
            tailMap(node.right, fromKey, result);
        }
    }

    private Node lowerNode(Node node, Integer key) {
        if (node == null) {
            return null;
        }

        if (key <= node.key) {
            return lowerNode(node.left, key);
        } else {
            Node right = lowerNode(node.right, key);
            return (right != null) ? right : node;
        }
    }

    private Node floorNode(Node node, Integer key) {
        if (node == null) {
            return null;
        }

        if (key < node.key) {
            return floorNode(node.left, key);
        } else if (key.equals(node.key)) {
            return node;
        } else {
            Node right = floorNode(node.right, key);
            return (right != null) ? right : node;
        }
    }

    private Node ceilingNode(Node node, Integer key) {
        if (node == null) {
            return null;
        }

        if (key > node.key) {
            return ceilingNode(node.right, key);
        } else if (key.equals(node.key)) {
            return node;
        } else {
            Node left = ceilingNode(node.left, key);
            return (left != null) ? left : node;
        }
    }

    private Node higherNode(Node node, Integer key) {
        if (node == null) {
            return null;
        }

        if (key >= node.key) {
            return higherNode(node.right, key);
        } else {
            Node left = higherNode(node.left, key);
            return (left != null) ? left : node;
        }
    }

    /////////////////////////////////////////////////////////////////////////
    //////        Остальные методы NavigableMap - можно оставить пустыми /////
    /////////////////////////////////////////////////////////////////////////

    @Override
    public Entry<Integer, String> lowerEntry(Integer key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Entry<Integer, String> floorEntry(Integer key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Entry<Integer, String> ceilingEntry(Integer key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Entry<Integer, String> higherEntry(Integer key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Entry<Integer, String> firstEntry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Entry<Integer, String> lastEntry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Entry<Integer, String> pollFirstEntry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Entry<Integer, String> pollLastEntry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableMap<Integer, String> descendingMap() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableSet<Integer> navigableKeySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableSet<Integer> descendingKeySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableMap<Integer, String> subMap(Integer fromKey, boolean fromInclusive, Integer toKey, boolean toInclusive) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableMap<Integer, String> headMap(Integer toKey, boolean inclusive) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableMap<Integer, String> tailMap(Integer fromKey, boolean inclusive) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Comparator<? super Integer> comparator() {
        return null;
    }

    @Override
    public SortedMap<Integer, String> subMap(Integer fromKey, Integer toKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends String> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Integer> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<String> values() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Entry<Integer, String>> entrySet() {
        throw new UnsupportedOperationException();
    }
}