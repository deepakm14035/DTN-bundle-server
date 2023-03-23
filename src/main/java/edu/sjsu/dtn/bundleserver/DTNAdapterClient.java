package edu.sjsu.dtn.bundleserver;

import com.google.protobuf.ByteString;
import edu.sjsu.dtn.adapter.communicationservice.ClientData;
import edu.sjsu.dtn.adapter.communicationservice.DTNAdapterGrpc;
import edu.sjsu.dtn.adapter.communicationservice.AppData;
import edu.sjsu.dtn.adapter.communicationservice.Status;
import edu.sjsu.model.ADU;
import edu.sjsu.storage.FileStoreHelper;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.ArrayList;
import java.util.List;

public class DTNAdapterClient {
    public String ipAddress;
    public int port;
    private DTNAdapterGrpc.DTNAdapterBlockingStub blockingStub;
    private DTNAdapterGrpc.DTNAdapterStub asyncStub;
    public DTNAdapterClient(String ipAddress, int port){
        this.ipAddress = ipAddress;
        this.port = port;
        Channel channel = ManagedChannelBuilder.forAddress(ipAddress, port)
                .usePlaintext()
                .build();
        blockingStub = DTNAdapterGrpc.newBlockingStub(channel);
        asyncStub = DTNAdapterGrpc.newStub(channel);
    }

    public AppData SendData(String clientId, List<byte[]> dataList){
        DTNAdapterClient client = new DTNAdapterClient(ipAddress, port);
        try {
            List<ByteString> dataListConverted = new ArrayList<>();
            for (int i=0;i<dataList.size();i++){
                dataListConverted.add(ByteString.copyFrom(dataList.get(i)));
            }
            AppData data = AppData.newBuilder()
                    .setClientId(clientId)
                    .addAllData(dataListConverted)
                    .build();
            AppData appData = client.blockingStub.saveData(data);
            System.out.println("[DTNAdapterClient.SendData] response: appData.getDataCount()- " + appData.getDataCount());
            return appData;
        } catch (StatusRuntimeException e) {
            e.printStackTrace();
        }

        return null;
    }
}
