package edu.sjsu.dtn.bundleserver;

import edu.sjsu.model.ADU;

import java.io.File;

public class DataStoreAdaptorTest {
    private static final String ROOT_DIRECTORY = "C:\\Users\\dmuna\\Documents\\java\\DTN-bundle-server\\FileStore";

    public static void main(String[] args){
        String clientId="m.deepak";
        String appId = "com.example.contentprovidertest";
        DataStoreAdaptor adaptor = new DataStoreAdaptor(ROOT_DIRECTORY);
        adaptor.persistADUForServer(clientId,
                new ADU(
                        new File(ROOT_DIRECTORY+"\\DataFromTransport\\1.txt"),
                        appId,
                        4,
                        0,
                        clientId));
        adaptor.fetchADUsForApp(clientId, appId);
    }
}
