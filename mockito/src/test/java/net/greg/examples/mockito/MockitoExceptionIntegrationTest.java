package net.greg.examples.mockito;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.mockito.Mockito;


public class MockitoExceptionIntegrationTest {

  @Test(expected = NullPointerException.class)
  public void whenConfigNonVoidRetunMethodToThrowEx_thenExIsThrown() {

    MyDictionary mock =
      mock(MyDictionary.class);

    when(mock.getMeaning(anyString())).thenThrow(NullPointerException.class);

    mock.getMeaning("word");
  }

  @Test(expected = IllegalStateException.class)
  public void whenConfigVoidRetunMethodToThrowEx_thenExIsThrown() {

    MyDictionary mock =
      mock(MyDictionary.class);

    doThrow(IllegalStateException.class).when(mock)
        .add(anyString(), anyString());

    mock.add("word", "meaning");
  }

  @Test(expected = NullPointerException.class)
  public void whenConfigNonVoidRetunMethodToThrowExWithNewExObj_thenExIsThrown() {

    MyDictionary mock =
      mock(MyDictionary.class);

    when(mock.getMeaning(anyString())).thenThrow(new NullPointerException("Error occurred"));

    mock.getMeaning("word");
  }

  @Test(expected = IllegalStateException.class)
  public void whenConfigVoidRetunMethodToThrowExWithNewExObj_thenExIsThrown() {

    MyDictionary mock =
      mock(MyDictionary.class);

    doThrow(new IllegalStateException("Error occurred")).when(mock)
        .add(anyString(), anyString());

    mock.add("word", "meaning");
  }

  @Test(expected = NullPointerException.class)
  public void givenSpy_whenConfigNonVoidRetunMethodToThrowEx_thenExIsThrown() {

    MyDictionary dict =
      new MyDictionary();

    MyDictionary spy =
      Mockito.spy(dict);

    when(spy.getMeaning(anyString())).thenThrow(NullPointerException.class);

    spy.getMeaning("word");
  }
}
