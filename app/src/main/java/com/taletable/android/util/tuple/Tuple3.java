
package com.taletable.android.util.tuple;


/**
 * Three element tuple
 */
public class Tuple3<A,B,C>
{
    private A item1;
    private B item2;
    private C item3;

    public Tuple3(A item1, B item2, C item3)
    {
        this.item1 = item1;
        this.item2 = item2;
        this.item3 = item3;
    }

    public A getItem1()
    {
        return item1;
    }

    public B getItem2()
    {
        return item2;
    }

    public C getItem3()
    {
        return item3;
    }
}
