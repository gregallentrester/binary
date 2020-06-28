package net.greg.examples.mockito.callbacks;

public interface Callback<T> {
  void reply(T response);
}
