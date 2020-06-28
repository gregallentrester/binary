package net.greg.examples.mockito;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.*;



@RunWith(MockitoJUnitRunner.class)
public class MockitoInjectIntoSpyUnitTest {

  @Before
  public void init() {

    MockitoAnnotations.initMocks(this);
    spyDic = Mockito.spy(new MyDictionary(wordMap));
  }

  @Mock
  private Map<String, String> wordMap;

  @InjectMocks
  private MyDictionary dic = new MyDictionary();

  private MyDictionary spyDic;

  @Test
  public void whenUseInjectMocksAnnotation_thenCorrect() {

    Mockito.when(wordMap.get("aWord")).thenReturn("aMeaning");

    assertEquals("aMeaning", spyDic.getMeaning("aWord"));
  }
}
