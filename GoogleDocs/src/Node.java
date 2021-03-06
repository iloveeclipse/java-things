import java.util.Stack;

public class Node {
    Node left;
    Node right;
    int value;
    public Node(int value) {
        this.value = value;
    }

    static void preOrderPrint(Node n) {
        System.out.print(n.value);
        if(n.left != null) {
            preOrderPrint(n.left);
        }
        if(n.right != null) {
            preOrderPrint(n.right);
        }
    }

    static void preOrderPrintNonRec(Node n) {
        Stack<Node> stack = new Stack<Node>();
        stack.push(n);
        while (!stack.isEmpty()) {
            n = stack.pop();
            System.out.print(n.value);
            if(n.right != null) {
                stack.push(n.right);
            }
            if(n.left != null) {
                stack.push(n.left);
            }
        }
    }



    static void inOrderPrint(Node n) {
        if(n.left != null) {
            inOrderPrint(n.left);
        }
        System.out.print(n.value);
        if(n.right != null) {
            inOrderPrint(n.right);
        }
    }

    static void inOrderPrintNonRec(Node n) {
        Stack<Node> stack = new Stack<Node>();
        stack.push(n);
        while (!stack.isEmpty()) {
            n = stack.pop();
            if(n.right != null) {
                stack.push(n.right);
            }
            if(n.left == null && n.right == null) {
                System.out.print(n.value);
            } else {
                stack.push(new Node(n.value));
            }
            if(n.left != null) {
                stack.push(n.left);
            }
        }
    }

    static void postOrderPrint(Node n) {
        if(n.left != null) {
            postOrderPrint(n.left);
        }
        if(n.right != null) {
            postOrderPrint(n.right);
        }
        System.out.print(n.value);
    }

    static void postOrderPrintNonRec(Node n) {
        Stack<Node> stack = new Stack<Node>();
        stack.push(n);
        while (!stack.isEmpty()) {
            n = stack.pop();
            if(n.left == null && n.right == null) {
                System.out.print(n.value);
            } else {
                stack.push(new Node(n.value));
            }
            if(n.right != null) {
                stack.push(n.right);
            }
            if(n.left != null) {
                stack.push(n.left);
            }
        }
    }

    static int leastCommonAncestor(Node n, int v1, int v2) {
        while(true) {
            if(v1 > n.value && v2 > n.value && n.right != null) {
                n = n.right;
            } else if (v1 < n.value && v2 < n.value && n.left != null) {
                n = n.left;
            } else {
                return n.value;
            }
        }
    }

    public static void main(String[] args) {
        Node n = new Node(5);
        n.left = new Node(3);
        n.right = new Node(7);

        n.left.left = new Node(2);
        n.left.right = new Node(4);

        n.right.left = new Node(6);
        n.right.right = new Node(8);

        n.left.left.left = new Node(1);
        n.right.right.right = new Node(9);

//		532147689  preorder
//		123456789  inorder
//		124369875  postorder

        System.out.print("Preorder: \t\t");
        preOrderPrint(n);
        System.out.print("\nPreorder (nr): \t\t");
        preOrderPrintNonRec(n);

        System.out.print("\nPostorder: \t\t");
        postOrderPrint(n);
        System.out.print("\nPostorder (nr):\t\t");
        postOrderPrintNonRec(n);

        System.out.print("\nInorder: \t\t");
        inOrderPrint(n);
        System.out.print("\nInorder (nr): \t\t");
        inOrderPrintNonRec(n);

        int first = 6;
        int second = 2;
        System.out.print("\nLCA of " + first + "&" + second + " is " + leastCommonAncestor(n, first, second));

        System.out.println();
    }
}
