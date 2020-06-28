package net.greg.examples.mockito;

import java.util.List;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class MockitoAnnotationsUninitializedUnitTest {

  @Mock
  List<String> mockedList;

  @Test(expected = NullPointerException.class)
  public void whenMockitoAnnotationsUninitialized_thenNPEThrown() {

    Mockito.when(mockedList.size()).thenReturn(1);
  }
}
