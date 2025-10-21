package by.it.group410971.shevchenko.lesson12;

import java.util.*;

public class MyRbMap implements SortedMap<Integer, String> {

    private Node root;
    private int size;
    private final Node NULL = new Node();

    public MyRbMap() {
        NULL.color = Color.BLACK;
        root = NULL;
    }

    private enum Color {
        RED, BLACK
    }

    private class Node {
        Integer key;
        String value;
        Node left, right, parent;
        Color color = Color.BLACK;

        Node() {}

        Node(Integer key, String value) {
            this.key = key;
            this.value = value;
            this.left = NULL;
            this.right = NULL;
            this.parent = NULL;
            this.color = Color.RED;
        }
    }

    @Override
    public String toString() {
        if (root == NULL) return "{}";
        StringBuilder sb = new StringBuilder("{");
        inOrderTraversal(root, sb);
        if (sb.length() > 1) {
            sb.setLength(sb.length() - 2);
        }
        sb.append("}");
        return sb.toString();
    }

    private void inOrderTraversal(Node node, StringBuilder sb) {
        if (node != NULL) {
            inOrderTraversal(node.left, sb);
            sb.append(node.key).append("=").append(node.value).append(", ");
            inOrderTraversal(node.right, sb);
        }
    }

    @Override
    public String put(Integer key, String value) {
        Node newNode = new Node(key, value);
        Node parent = NULL;
        Node current = root;

        while (current != NULL) {
            parent = current;
            int cmp = key.compareTo(current.key);
            if (cmp < 0) {
                current = current.left;
            } else if (cmp > 0) {
                current = current.right;
            } else {
                String oldValue = current.value;
                current.value = value;
                return oldValue;
            }
        }

        newNode.parent = parent;

        if (parent == NULL) {
            root = newNode;
        } else if (key.compareTo(parent.key) < 0) {
            parent.left = newNode;
        } else {
            parent.right = newNode;
        }

        size++;
        fixInsert(newNode);
        return null;
    }

    private void fixInsert(Node node) {
        while (node.parent != NULL && node.parent.color == Color.RED) {
            if (node.parent == node.parent.parent.left) {
                Node uncle = node.parent.parent.right;
                if (uncle != NULL && uncle.color == Color.RED) {
                    node.parent.color = Color.BLACK;
                    uncle.color = Color.BLACK;
                    node.parent.parent.color = Color.RED;
                    node = node.parent.parent;
                } else {
                    if (node == node.parent.right) {
                        node = node.parent;
                        rotateLeft(node);
                    }
                    node.parent.color = Color.BLACK;
                    node.parent.parent.color = Color.RED;
                    rotateRight(node.parent.parent);
                }
            } else {
                Node uncle = node.parent.parent.left;
                if (uncle != NULL && uncle.color == Color.RED) {
                    node.parent.color = Color.BLACK;
                    uncle.color = Color.BLACK;
                    node.parent.parent.color = Color.RED;
                    node = node.parent.parent;
                } else {
                    if (node == node.parent.left) {
                        node = node.parent;
                        rotateRight(node);
                    }
                    node.parent.color = Color.BLACK;
                    node.parent.parent.color = Color.RED;
                    rotateLeft(node.parent.parent);
                }
            }
            if (node == root) break;
        }
        root.color = Color.BLACK;
    }

    private void rotateLeft(Node x) {
        Node y = x.right;
        x.right = y.left;
        if (y.left != NULL) {
            y.left.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == NULL) {
            root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }
        y.left = x;
        x.parent = y;
    }

    private void rotateRight(Node x) {
        Node y = x.left;
        x.left = y.right;
        if (y.right != NULL) {
            y.right.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == NULL) {
            root = y;
        } else if (x == x.parent.right) {
            x.parent.right = y;
        } else {
            x.parent.left = y;
        }
        y.right = x;
        x.parent = y;
    }

    @Override
    public String remove(Object key) {
        if (!(key instanceof Integer)) return null;
        Integer k = (Integer) key;
        Node z = findNode(k);
        if (z == NULL) return null;

        String oldValue = z.value;
        deleteNode(z);
        size--;
        return oldValue;
    }

    private Node findNode(Integer key) {
        Node current = root;
        while (current != NULL) {
            int cmp = key.compareTo(current.key);
            if (cmp < 0) {
                current = current.left;
            } else if (cmp > 0) {
                current = current.right;
            } else {
                return current;
            }
        }
        return NULL;
    }

    private void deleteNode(Node z) {
        Node y = z;
        Node x;
        Color yOriginalColor = y.color;

        if (z.left == NULL) {
            x = z.right;
            transplant(z, z.right);
        } else if (z.right == NULL) {
            x = z.left;
            transplant(z, z.left);
        } else {
            y = minimum(z.right);
            yOriginalColor = y.color;
            x = y.right;
            if (y.parent == z) {
                if (x != NULL) x.parent = y;
            } else {
                transplant(y, y.right);
                y.right = z.right;
                y.right.parent = y;
            }
            transplant(z, y);
            y.left = z.left;
            y.left.parent = y;
            y.color = z.color;
        }

        if (yOriginalColor == Color.BLACK) {
            fixDelete(x);
        }
    }

    private void transplant(Node u, Node v) {
        if (u.parent == NULL) {
            root = v;
        } else if (u == u.parent.left) {
            u.parent.left = v;
        } else {
            u.parent.right = v;
        }
        if (v != NULL) {
            v.parent = u.parent;
        }
    }

    private Node minimum(Node node) {
        while (node.left != NULL) {
            node = node.left;
        }
        return node;
    }

    private void fixDelete(Node x) {
        while (x != root && x != NULL && x.color == Color.BLACK) {
            if (x == x.parent.left) {
                Node w = x.parent.right;
                if (w.color == Color.RED) {
                    w.color = Color.BLACK;
                    x.parent.color = Color.RED;
                    rotateLeft(x.parent);
                    w = x.parent.right;
                }
                if (w.left.color == Color.BLACK && w.right.color == Color.BLACK) {
                    w.color = Color.RED;
                    x = x.parent;
                } else {
                    if (w.right.color == Color.BLACK) {
                        w.left.color = Color.BLACK;
                        w.color = Color.RED;
                        rotateRight(w);
                        w = x.parent.right;
                    }
                    w.color = x.parent.color;
                    x.parent.color = Color.BLACK;
                    w.right.color = Color.BLACK;
                    rotateLeft(x.parent);
                    x = root;
                }
            } else {
                Node w = x.parent.left;
                if (w.color == Color.RED) {
                    w.color = Color.BLACK;
                    x.parent.color = Color.RED;
                    rotateRight(x.parent);
                    w = x.parent.left;
                }
                if (w.right.color == Color.BLACK && w.left.color == Color.BLACK) {
                    w.color = Color.RED;
                    x = x.parent;
                } else {
                    if (w.left.color == Color.BLACK) {
                        w.right.color = Color.BLACK;
                        w.color = Color.RED;
                        rotateLeft(w);
                        w = x.parent.left;
                    }
                    w.color = x.parent.color;
                    x.parent.color = Color.BLACK;
                    w.left.color = Color.BLACK;
                    rotateRight(x.parent);
                    x = root;
                }
            }
        }
        if (x != NULL) x.color = Color.BLACK;
    }

    @Override
    public String get(Object key) {
        if (!(key instanceof Integer)) return null;
        Node node = findNode((Integer) key);
        return node != NULL ? node.value : null;
    }

    @Override
    public boolean containsKey(Object key) {
        if (!(key instanceof Integer)) return false;
        return findNode((Integer) key) != NULL;
    }

    @Override
    public boolean containsValue(Object value) {
        return containsValueRecursive(root, value);
    }

    private boolean containsValueRecursive(Node node, Object value) {
        if (node == NULL) return false;
        if (Objects.equals(node.value, value)) return true;
        return containsValueRecursive(node.left, value) || containsValueRecursive(node.right, value);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        root = NULL;
        size = 0;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public Integer firstKey() {
        if (root == NULL) throw new NoSuchElementException();
        return minimum(root).key;
    }

    @Override
    public Integer lastKey() {
        if (root == NULL) throw new NoSuchElementException();
        return maximum(root).key;
    }

    private Node maximum(Node node) {
        while (node.right != NULL) {
            node = node.right;
        }
        return node;
    }

    @Override
    public SortedMap<Integer, String> headMap(Integer toKey) {
        MyRbMap result = new MyRbMap();
        buildHeadMap(root, toKey, result);
        return result;
    }

    private void buildHeadMap(Node node, Integer toKey, MyRbMap result) {
        if (node == NULL) return;
        if (node.key.compareTo(toKey) < 0) {
            buildHeadMap(node.left, toKey, result);
            result.put(node.key, node.value);
            buildHeadMap(node.right, toKey, result);
        } else {
            buildHeadMap(node.left, toKey, result);
        }
    }

    @Override
    public SortedMap<Integer, String> tailMap(Integer fromKey) {
        MyRbMap result = new MyRbMap();
        buildTailMap(root, fromKey, result);
        return result;
    }

    private void buildTailMap(Node node, Integer fromKey, MyRbMap result) {
        if (node == NULL) return;
        if (node.key.compareTo(fromKey) >= 0) {
            buildTailMap(node.left, fromKey, result);
            result.put(node.key, node.value);
            buildTailMap(node.right, fromKey, result);
        } else {
            buildTailMap(node.right, fromKey, result);
        }
    }

    // Not implemented methods
    @Override
    public Comparator<? super Integer> comparator() {
        return null;
    }

    @Override
    public SortedMap<Integer, String> subMap(Integer fromKey, Integer toKey) {
        MyRbMap result = new MyRbMap();
        buildSubMap(root, fromKey, toKey, result);
        return result;
    }

    private void buildSubMap(Node node, Integer fromKey, Integer toKey, MyRbMap result) {
        if (node == NULL) return;
        if (node.key.compareTo(fromKey) >= 0 && node.key.compareTo(toKey) < 0) {
            buildSubMap(node.left, fromKey, toKey, result);
            result.put(node.key, node.value);
            buildSubMap(node.right, fromKey, toKey, result);
        } else if (node.key.compareTo(fromKey) < 0) {
            buildSubMap(node.right, fromKey, toKey, result);
        } else {
            buildSubMap(node.left, fromKey, toKey, result);
        }
    }

    @Override
    public Set<Integer> keySet() {
        Set<Integer> keys = new TreeSet<>();
        buildKeySet(root, keys);
        return keys;
    }

    private void buildKeySet(Node node, Set<Integer> keys) {
        if (node == NULL) return;
        buildKeySet(node.left, keys);
        keys.add(node.key);
        buildKeySet(node.right, keys);
    }

    @Override
    public Collection<String> values() {
        List<String> values = new ArrayList<>();
        buildValues(root, values);
        return values;
    }

    private void buildValues(Node node, List<String> values) {
        if (node == NULL) return;
        buildValues(node.left, values);
        values.add(node.value);
        buildValues(node.right, values);
    }

    @Override
    public Set<Entry<Integer, String>> entrySet() {
        Set<Entry<Integer, String>> entries = new TreeSet<>(Comparator.comparing(Entry::getKey));
        buildEntrySet(root, entries);
        return entries;
    }

    private void buildEntrySet(Node node, Set<Entry<Integer, String>> entries) {
        if (node == NULL) return;
        buildEntrySet(node.left, entries);
        entries.add(new AbstractMap.SimpleEntry<>(node.key, node.value));
        buildEntrySet(node.right, entries);
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends String> m) {
        for (Entry<? extends Integer, ? extends String> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }
}