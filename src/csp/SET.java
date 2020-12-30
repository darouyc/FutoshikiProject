package csp;

import java.util.Collection;
import java.util.TreeSet;
import java.util.Iterator;

public class SET<Key> implements Iterable<Key> {
    private TreeSet<Key> st = new TreeSet<Key>();

    
    public TreeSet<Key> getSet()
    {
        return this.st;
    }
    
    public SET(TreeSet<Key> st) 
    {
        super();
        Iterator<Key> itr = st.iterator();
        while(itr.hasNext()) {
                this.st.add((Key) itr.next());
        }
    }
    
    public SET() 
    {
        super();
    }
    
    public void add(Key key)          { st.add(key);               }
    public boolean contains(Key key)  { return st.contains(key);   }
    public void remove(Key key)       { st.remove(key);            }
    public int size()                 { return st.size();          }
    public Iterator<Key> iterator()   { return st.iterator();      }


    // return a set containing the union of the elements in this SET a and b
    public SET<Key> union(SET<Key> b) {
        SET<Key> a = this;
        SET<Key> c = new SET<Key>();
        for (Key x : a) c.add(x);
        for (Key x : b) c.add(x);
        return c;
    }

    // return a set containing the intersection of the elements in this SET a and b
    public SET<Key> intersects(SET<Key> b) {
        SET<Key> a = this;
        SET<Key> c = new SET<Key>();
        if (a.size() < b.size()) {
            for (Key x : a) {
                if (b.contains(x)) c.add(x);
            }
        }
        else {
            for (Key x : b) {
                if (a.contains(x)) c.add(x);
            }
        }
        return c;
    }

    // return a string representation of this set
    public String toString() {
        String s = "";
        for (Key key : st) {
            s += key + " ";
        }
        return s;
    }

}
