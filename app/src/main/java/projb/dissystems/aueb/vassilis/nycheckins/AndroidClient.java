package projb.dissystems.aueb.vassilis.nycheckins;

/**
 * Created by Vassilis on 6/5/2016.
 */
public interface AndroidClient {

    public void distributeToMappers();

    public void waitForMappers();

    public void ackToReducers();

    public void collectDataFromReducers();
}
