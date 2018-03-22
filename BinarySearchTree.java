import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;
import java.lang.Math;
import java.util.function.Consumer;
import java.util.ArrayList;


public class BinarySearchTree<K> implements Tree<K> {

    class Node implements Location<K>{

        protected K data;
        protected Node left, right;
        protected Node parent;
        protected int height;
        protected List<K> list;

        public Node(K key) {
            this(key, null, null);
        }

 
        public Node(K data, Node left, Node right) {
            this.data = data;
            this.left = left;
            this.right = right;
            this.height = 0;
        }

        public boolean isNodeBalanced(){
            return Math.abs(get_height(left) - get_height(right)) < 2 ;
        }

        public boolean pointsLeft(Node n){
            if(left.equals(n)){
                return true;
            }
            else{
                return false;
            }
        }



        @Override
        public K get() {
            return data;
        }

        protected boolean isLeaf() {
            return left == null && right == null;
        }

 
        protected boolean updateHeight() {
            int new_height = 1 + Math.max(get_height(left), get_height(right));
            if(new_height == height){
                return false;
            }
            else{
                height = new_height;
                return true;
            }
        }

        public Node getBefore() {
            if(left != null){
                return left.largest();
            }
            else{
                if(parent == null){
                    return null;
                }
                else return smallerAncestor(this);
            }
        }


        public Node getAfter() {
            if(right != null){
                return right.smallest();
            }
            else{
                if(parent == null){
                    return null;
                }
                else return greaterAncestor(this);
            }
        }

        private Node smallerAncestor(Node q) {
            if(q.parent == null){
                return null;
            }
            else if(lessThan.test(q.parent.data, q.data)){
                return q.parent;
            }
            else{
                return smallerAncestor(q.parent);
            }
        }

        private Node greaterAncestor(Node q) {
            if (q.parent == null){
                return null;
            }
            if(lessThan.test(q.data, q.parent.data)){
                return q.parent;
            }
            else{
                return greaterAncestor(q.parent);
            }
        }

        protected Node smallest() {
            if(left != null){
                return left.smallest();
            }
            else{
                return this;
            }
        }

        private Node largest() {
            if(right != null){
                return right.largest();
            }
            else{
                return this;
            }
        }

        private List<K> getKeys(Consumer<K> f){
            if(left != null) {
                left.getKeys(f);
            }
            f.accept(data);
            if(right != null) {
                right.getKeys(f);
            }
            return list;
        }


    }

    protected Node root;
    protected int numNodes;
    protected BiPredicate<K, K> lessThan;
    List<K> list;

    public BinarySearchTree(BiPredicate<K, K> lessThan) {
        this.lessThan = lessThan;
    }

    public Node search(K key) {
        return find_helper(root, key);
    }

    private Node find_helper(Node n, K key) {
        if (n == null) {
            return null;
        } else if (lessThan.test(key, n.data)) {
            return find_helper(n.left, key);
        } else if (lessThan.test(n.data, key)) {
            return find_helper(n.right, key);
        } else {
            return n;
        }
    }

    public int height() {
        return get_height(root);
    }

    protected int get_height(Node n) {
        if(n == null){
            return -1;
        }
        else{
            return n.height;
        }
    }


    public void clear() {
        root = null;
        numNodes = 0;
    }


    public int size() {
        return numNodes;
    }


     */
    public Node insert(K key) {
        if(search(key) == null) {
            numNodes = numNodes + 1;
        }
        root = insert_helper(root, root, key);

        return search(key);
    }

    private Node insert_helper(Node n, Node p, K key) {
        if (n == null) {
            Node temp = new Node(key, null, null);
            temp.parent = p;
            return temp;
        } else if (lessThan.test(key, n.data)) {
            n.left = insert_helper(n.left, n, key);
            n.updateHeight();
            return n;
        } else if (lessThan.test(n.data, key)) {
            n.right = insert_helper(n.right, n, key);
            n.updateHeight();
            return n;
        } else { // no need to insert, already there
            return n;
        }
    }

    private void add_height(Node n){
        n.height = n.height + 1;
        if(n.parent !=null){
            add_height(n.parent);
        }
    }

    public boolean contains(K key) {
        Node p = search(key);
        return p != null;
    }


    public void remove(K key) {

        if(search(key) != null) {
            numNodes = numNodes - 1;
            Node test = search(key).parent;


            Node temp = remove_helper(root, key);
            root = temp;

            if (test != null) {
                updateRestofTree(test);
            }
        }
    }

    private Node remove_helper(Node n, K key) {
        if (n == null) {
            return null;
        } else if (lessThan.test(key, n.data)) { // remove in left subtree
            n.left = remove_helper(n.left, key);
            return n;
        } else if (lessThan.test(n.data, key)) { // remove in right subtree
            n.right = remove_helper(n.right, key);
            return n;
        } else { // remove this node
            if (n.left == null) {
                if (n.right != null) {
                    if (n.right.parent != null) {
                        n.right.parent = n.parent;
                    }
                }


                return n.right;

            } else if (n.right == null) {
                if(n.left != null) {
                    if (n.left.parent != null) {
                        n.left.parent = n.parent;
                    }
                }


                return n.left;
            } else { // two children, replace this with min of right subtree
                Node min = get_min(n.right);
                n.data = min.data;
                n.right = delete_min(n.right);
                return n;
            }
        }
    }

    private Node delete_min(Node n) {
        if (n.left == null) {
            return n.right;
        } else {
            n.left = delete_min(n.left);
            return n;
        }
    }

    private Node get_min(Node n) {
        if (n.left == null) {
            return n;
        }
        else {
            return get_min(n.left);
        }
    }

    public void updateRestofTree(Node n){
        n.updateHeight();
        if(n.parent!=null){
            updateRestofTree(n.parent);
        }
    }


    public List<K> keys() {
        List<K> B = new ArrayList<>();
        if(root == null){
            return B;
        }
        else{
            root.getKeys((K k) -> {
                B.add(k);
            });
            return B;
        }


    }

}
