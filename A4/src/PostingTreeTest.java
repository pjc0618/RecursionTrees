import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static common.JUnitUtil.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.junit.BeforeClass;
import org.junit.Test;

public class PostingTreeTest {

    private static Network n;
    private static Person[] people;
    private static Person personA;
    private static Person personB;
    private static Person personC;
    private static Person personD;
    private static Person personE;
    private static Person personF;
    private static Person personG;
    private static Person personH;
    private static Person personI;
    private static Person personJ;
    private static Person personK;
    private static Person personL;

    @BeforeClass
    public static void setup() {
        n= new Network();
        people= new Person[]{new Person("A", n, 0),
                new Person("B", n, 0), new Person("C", n, 0),
                new Person("D", n, 0), new Person("E", n, 0), new Person("F", n, 0),
                new Person("G", n, 0), new Person("H", n, 0), new Person("I", n, 0),
                new Person("J", n, 0), new Person("K", n, 0), new Person("L", n, 0)
        };
        personA= people[0];
        personB= people[1];
        personC= people[2];
        personD= people[3];
        personE= people[4];
        personF= people[5];
        personG= people[6];
        personH= people[7];
        personI= people[8];
        personJ= people[9];
        personK= people[10];
        personL= people[11];
    }

    @Test
    public void testBuiltInGetters() {
        PostingTree st= new PostingTree(personB);
        assertEquals("B", toStringBrief(st));
    }
    
    
    /** Create a PostingTree with structure A[B[D E F[G[H[I]]]] C]
     * Doesn't rely on method add(..) of PostingTree. */ 
    private PostingTree makeTree1() {
        PostingTree dt = new PostingTree(personA); // A
        dt.insert(personB, personA); // A, B
        dt.insert(personC, personA); // A, C
        dt.insert(personD, personB); // B, D
        dt.insert(personE, personB); // B, E
        dt.insert(personF, personB); // B, F
        dt.insert(personG, personF); // F, G
        dt.insert(personH, personG); // G, H
        dt.insert(personI, personH); // H, I
        return new PostingTree(dt);
    }
    
    @Test
    public void testMakeTree1() {
        PostingTree dt= makeTree1();
        assertEquals("A[B[D E F[G[H[I]]]] C]", toStringBrief(dt)); 
    }
    
    @Test
    public void testgetSharedAncestor() {
        PostingTree st= makeTree1();
        // A.testSharedAncestorOf(A, A) is A
        assertEquals(personA, st.getSharedAncestor(personA, personA));
        
        // A.testSharedAncestorOf(A, B) is A
        assertEquals(personA, st.getSharedAncestor(personA, personB));
        
        // A.testSharedAncestorOf(E, B) is B
        assertEquals(personB, st.getSharedAncestor(personE, personB));
        
        // A.testSharedAncestorOf(D, I) is B
        assertEquals(personB, st.getSharedAncestor(personD, personI));
        // A.testSharedAncestorOf(I, D) is B
        assertEquals(personB, st.getSharedAncestor(personI, personD));
        
    }

    @Test
    public void test1Insert() {
        PostingTree st= new PostingTree(personB); 

        //Test add to root
        PostingTree dt2= st.insert(personC, personB);
        assertEquals("B[C]", toStringBrief(st)); // test tree
        assertEquals(people[2], dt2.getRoot());  // test return value

        //Test add to non-root
        PostingTree dt3= st.insert(personD, personC);
        assertEquals("B[C[D]]", toStringBrief(st)); // test tree
        assertEquals(people[3], dt3.getRoot());  // test return value
        
        assertThrows(IllegalArgumentException.class,()->{st.insert(null, null);});
        assertThrows(IllegalArgumentException.class,()->{st.insert(null, personB);});
        assertThrows(IllegalArgumentException.class,()->{st.insert(personA, null);});
        assertThrows(IllegalArgumentException.class,()->{st.insert(personA, personG);});
        assertThrows(IllegalArgumentException.class,()->{st.insert(personD, personB);});

        //Test add second child
        PostingTree dt0= st.insert(personA, personC);
        assertEquals("B[C[A D]]", toStringBrief(st)); // test tree
        assertEquals(personA, dt0.getRoot());  // test return value
        
        //Test add child to child's child
        PostingTree dt6= st.insert(personG, personA);
        assertEquals("B[C[A[G] D]]", toStringBrief(st)); // test tree
        assertEquals(personG, dt6.getRoot());  // test return value
        
        //Test add to child's tree
        PostingTree dt7= st.insert(personH, personG);
        assertEquals("B[C[A[G[H]] D]]", toStringBrief(st)); // test tree
        assertEquals(people[7], dt7.getRoot());  // test return value
    }
    
    @Test
    public void test2Size() {
        PostingTree st= new PostingTree(personB); 
        PostingTree dt2= st.insert(personC, personB);
        assertEquals(2, st.size());
        assertEquals(1,dt2.size());
        st.insert(personD, personB);
        st.insert(personE, personC);
        st.insert(personF, personC);
        PostingTree dt3=st.insert(personG, personD);
        assertEquals(6,st.size());
        assertEquals(1,dt3.size());
        assertEquals(3,dt2.size());
        PostingTree tree1=makeTree1();
        assertEquals(9,tree1.size());

    }
    
    @Test
    public void test3Depth() {
        PostingTree st= new PostingTree(personB); 
        PostingTree dt2= st.insert(personC, personB);
        assertEquals(0, st.depth(personB));
        assertEquals(0,dt2.depth(personC));
        assertEquals(1, st.depth(personC));
        assertEquals(-1, st.depth(personA));
        st.insert(personA, personC);
        st.insert(personD, personB);
        assertEquals(0,st.depth(personB));
        assertEquals(1,st.depth(personD));
        assertEquals(2,st.depth(personA));
        assertEquals(1,dt2.depth(personA));
        PostingTree tree1=makeTree1();
        assertEquals(5,tree1.depth(personI));
    }

    @Test
    public void test4WidthAtDepth() {
        PostingTree st= new PostingTree(personB); 
        assertEquals(1,st.widthAtDepth(0));
        assertThrows(IllegalArgumentException.class,()->{st.widthAtDepth(-1);});
        PostingTree dt2= st.insert(personC, personB);
        assertEquals(1, st.widthAtDepth(1));
        dt2=st.insert(personA, personB);
        assertEquals(2, st.widthAtDepth(1));
        st.insert(personD, personA);
        st.insert(personE, personA);
        st.insert(personF, personA);
        st.insert(personG, personC);
        st.insert(personH, personC);
        assertEquals(5, st.widthAtDepth(2));
        assertEquals(3,dt2.widthAtDepth(1));
        st.insert(personI, personH);
        assertEquals(1,st.widthAtDepth(3));
        assertEquals(0,st.widthAtDepth(4));
        assertEquals(0,st.widthAtDepth(5));
        PostingTree tree=new PostingTree(personA);
        tree.insert(personB, personA);tree.insert(personC, personA);
        tree.insert(personD, personB);tree.insert(personE, personC);
        tree.insert(personF, personC);tree.insert(personG, personF);
        assertEquals(1,tree.widthAtDepth(0));
        assertEquals(2,tree.widthAtDepth(1));
        assertEquals(3,tree.widthAtDepth(2));
        assertEquals(1,tree.widthAtDepth(3));
        assertEquals(0,tree.widthAtDepth(4));
        PostingTree tre=tree.getTree(personC);
        assertEquals(1,tre.widthAtDepth(0));
        assertEquals(2,tre.widthAtDepth(1));
        assertEquals(1,tre.widthAtDepth(2));
    }
    
    @Test
    public void test5getPostingRoute() {
        PostingTree st= new PostingTree(personB);
        List l=st.getPostingRoute(personB);
        assertEquals("[B]", getNames(l));
        PostingTree dt2= st.insert(personC, personB);
        List route= st.getPostingRoute(personC);
        assertEquals("[B, C]", getNames(route));
        dt2= st.insert(personD, personC);
        route= st.getPostingRoute(personD);
        assertEquals("[B, C, D]", getNames(route));
        dt2=st.insert(personE, personB);
        route=st.getPostingRoute(personE);
        assertEquals("[B, E]", getNames(route));
        PostingTree tree1=makeTree1();
        route=tree1.getPostingRoute(personI);
        assertEquals("[A, B, F, G, H, I]", getNames(route));
        route=st.getPostingRoute(personI);
        assertEquals(null, route);
        dt2=tree1.getTree(personC);
        assertEquals(null, dt2.getPostingRoute(personA));
        assertEquals(null, dt2.getPostingRoute(personB));
        assertEquals(null, dt2.getPostingRoute(personD));
        assertEquals(null, dt2.getPostingRoute(personI));
        
    }
    
    /** Return the names of Persons in sp, separated by ", " and delimited by [ ].
     *  Precondition: No name is the empty string. */
    private String getNames(List<Person> sp) {
        String res= "[";
        for (Person p : sp) {
            if (res.length() > 1) res= res + ", ";
            res= res + p.getName();
        }
        return res + "]";
    }
    
    @Test
    public void test6getSharedAncestor() {
        PostingTree st= new PostingTree(personB); 
        PostingTree dt2= st.insert(personC, personB);
        Person p= st.getSharedAncestor(personC, personC);
        assertEquals(personC, p);
        dt2=st.insert(personD, personB);
        p=st.getSharedAncestor(personC,personD);
        assertEquals(personB,p);
        dt2=st.insert(personE, personC);
        st.insert(personF, personE);
        st.insert(personG, personF);
        p=st.getSharedAncestor(personG, personD);
        assertEquals(personB,p);
        p=st.getSharedAncestor(personG, null);
        assertEquals(null,p);
        p=st.getSharedAncestor(null, personG);
        assertEquals(null,p);
        p=st.getSharedAncestor(personG, null);
        assertEquals(null,p);
        p=dt2.getSharedAncestor(personG,personF);
        assertEquals(personF,p);
        p=dt2.getSharedAncestor(personG, personD);
        assertEquals(null,p);
        p=dt2.getSharedAncestor(personB, personF);
        assertEquals(null,p);
        
    }
    
    @Test
    public void test7equals() {
        PostingTree tree1= new PostingTree(personB);
        PostingTree tree2= new PostingTree(personB); 
        assertEquals(true, tree1.equals(tree2));
        assertFalse(tree1.equals(personB));
        tree1.insert(personC, personB);
        tree2.insert(personC, personB);
        assertEquals(true, tree1.equals(tree2));
        tree1.insert(personD, personB);
        tree2.insert(personD, personB);
        assertEquals(true, tree1.equals(tree2));
        tree1.insert(personF, personB);
        tree2.insert(personF, personB);
        tree1.insert(personA, personB);
        tree2.insert(personA, personB);
        assertEquals(true, tree1.equals(tree2));
        tree1.insert(personE, personC);
        tree2.insert(personE, personC);
        assertEquals(true, tree1.equals(tree2));
        tree2.insert(personG, personB);
        assertEquals(false, tree1.equals(tree2));
        tree1.insert(personG, personB);
        assertEquals(true, tree1.equals(tree2));
        tree1.insert(personH, personE);
        tree2.insert(personI, personE);
        assertEquals(false, tree1.equals(tree2));
        PostingTree tree3= new PostingTree(personC);
        tree3.insert(personE, personC);
        assertFalse(tree1.equals(tree3));
        int tree4= 3;
        assertFalse(tree1.equals(tree4));
        assertFalse(tree1.equals(personD));
        tree2.insert(personH, personE);
        tree1.insert(personI, personE);
    }
    
    /** Return a representation of this tree. This representation is:
     * (1) the name of the Person at the root, followed by
     * (2) the representations of the children (in alphabetical
     *     order of the children's names).
     * There are two cases concerning the children.
     *
     * No children? Their representation is the empty string.
     * Children? Their representation is the representation of each child, with
     * a blank between adjacent ones and delimited by "[" and "]".
     * Examples:
     * One-node tree: "A"
     * root A with children B, C, D: "A[B C D]"
     * root A with children B, C, D and B has a child F: "A[B[F] C D]"
     */
    public static String toStringBrief(PostingTree t) {
        String res= t.getRoot().getName();

        Object[] childs= t.getChildren().toArray();
        if (childs.length == 0) return res;
        res= res + "[";
        selectionSort1(childs);

        for (int k= 0; k < childs.length; k= k+1) {
            if (k > 0) res= res + " ";
            res= res + toStringBrief(((PostingTree)childs[k]));
        }
        return res + "]";
    }

    /** Sort b --put its elements in ascending order.
     * Sort on the name of the Person at the root of each SharingTree
     * Throw a cast-class exception if b's elements are not SharingTrees */
    public static void selectionSort1(Object[] b) {
        int j= 0;
        // {inv P: b[0..j-1] is sorted and b[0..j-1] <= b[j..]}
        // 0---------------j--------------- b.length
        // inv : b | sorted, <= | >= |
        // --------------------------------
        while (j != b.length) {
            // Put into p the index of smallest element in b[j..]
            int p= j;
            for (int i= j+1; i != b.length; i++) {
                String bi= ((PostingTree)b[i]).getRoot().getName();
                String bp= ((PostingTree)b[p]).getRoot().getName();
                if (bi.compareTo(bp) < 0) {
                    p= i;

                }
            }
            // Swap b[j] and b[p]
            Object t= b[j]; b[j]= b[p]; b[p]= t;
            j= j+1;
        }
    }

}
