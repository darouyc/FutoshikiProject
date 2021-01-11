/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csp;

import java.util.TreeMap;
import java.util.Iterator;
/**
 *
 * @author DAROUYc
 */


public class ST<Key extends Comparable<Key>, Val> implements Iterable<Key> {
    private TreeMap<Key, Val> st;

    public ST() {
        st = new TreeMap<Key, Val>();
    }
    
    public TreeMap<Key, Val> getST()
    {
        return this.st;
    }
    
    public void put(Key key, Val val) {
        if (val == null) st.remove(key);
        else             st.put(key, val);
    }
    public Val get(Key key)             { return st.get(key);            }
    public Val remove(Key key)          { return st.remove(key);         }
    public boolean contains(Key key)    { return st.containsKey(key);    }
    public int size()                   { return st.size();              }
    public Iterator<Key> iterator()     { return st.keySet().iterator(); }
    
}

