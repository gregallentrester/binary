package net.greg.examples.mockito;

import java.util.HashMap;
import java.util.Map;

class MyDictionary {

  private Map<String, String> wordMap;

  MyDictionary() {
    wordMap = new HashMap<>();
  }

  MyDictionary(Map<String, String> value) {
    wordMap = value;
  }

  public void add(final String word, final String meaning) {
    wordMap.put(word, meaning);
  }

  String getMeaning(final String word) {
    return wordMap.get(word);
  }
}
