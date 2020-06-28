package net.greg.examples.mockito;

import java.util.AbstractList;


public class MyList extends AbstractList<String> {

  final public int finalMethod() { return 0; }

  @Override
  public String get(final int value) { return null; }

  @Override
  public int size() { return 1; }

  @Override
  public void add(int index, String element) { /* no-op*/ }
}
