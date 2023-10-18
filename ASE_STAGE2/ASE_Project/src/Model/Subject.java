package Model;

import java.util.Observer;

@SuppressWarnings("deprecation")
public interface Subject 
{
    public void attach(Observer o);
    public void detach(Observer o);
    public void notifyObservers(Object message);
}
