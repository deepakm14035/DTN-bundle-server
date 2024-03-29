package edu.sjsu.dtn.bundleserver;

import edu.sjsu.model.ADU;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataStoreAdaptorTest {
    private static final String ROOT_DIRECTORY = "C:\\Users\\dmuna\\Documents\\java\\DTN-bundle-server\\FileStore";

    private static void fetchADUsTest(DataStoreAdaptor adaptor, String clientId, String appId){
        List<ADU> adus = new ArrayList<>();
        adus.add(new ADU(
                new File(ROOT_DIRECTORY+"\\DataFromTransport\\2.txt"),
                appId,
                4,
                0,
                clientId));
        adaptor.persistADUsForServer(clientId, appId, adus);
    }



    public static void main(String[] args){
        String clientId="m.deepak";
        String appId = "com.android.mysignal";
        DataStoreAdaptor adaptor = new DataStoreAdaptor(ROOT_DIRECTORY);
        fetchADUsTest(adaptor, clientId, appId);
    }
}
