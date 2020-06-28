package net.greg.examples.mockito.callbacks;

public class Response {

  private Data data;
  public Data getData() { return data; }
  public void setData(Data value) { data = value; }

  private boolean isValid = true;
  public boolean isValid() { return isValid; }
  public void setIsValid(boolean value) { isValid = value; }
}
