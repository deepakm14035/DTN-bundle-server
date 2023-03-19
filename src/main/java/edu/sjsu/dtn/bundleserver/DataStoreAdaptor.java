package edu.sjsu.dtn.bundleserver;

import edu.sjsu.model.ADU;
import edu.sjsu.storage.FileStoreHelper;
import edu.sjsu.storage.MySQLConnection;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.FileSystems;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DataStoreAdaptor {
    private FileStoreHelper sendFileStoreHelper;
    private FileStoreHelper receiveFileStoreHelper;

    public DataStoreAdaptor(String appRootDataDirectory){
        sendFileStoreHelper = new FileStoreHelper(appRootDataDirectory + "/send");
        receiveFileStoreHelper = new FileStoreHelper(appRootDataDirectory + "/receive");
    }

    public void deleteADUs(String clientId, String appId, Long aduIdEnd) {
        sendFileStoreHelper.deleteAllFilesUpTo(clientId, appId, aduIdEnd);
        System.out.println("[DSA] Deleted ADUs for application " + appId + " with id upto " + aduIdEnd);
    }

    //send data to app adapter
    public void persistADUForServer(String clientId, ADU adu) {
        sendFileStoreHelper.AddFile(adu.getAppId(), clientId, sendFileStoreHelper.getDataFromFile(adu.getSource()));
        DTNAdapterClient client = new DTNAdapterClient("localhost", 8080);
        List<byte[]> dataList=new ArrayList<>();
        dataList.add(sendFileStoreHelper.getDataFromFile(adu.getSource()));
        client.SendData(clientId, dataList);
        System.out.println(
                "[DSA] Stored ADU for application "
                        + adu.getAppId()
                        + " for client "
                        + clientId);
    }

    private ADU fetchADU(String clientId, String appId, long aduId) {
        try {
            File file = sendFileStoreHelper.getADUFile(clientId, appId, aduId + "");
            FileInputStream fis = new FileInputStream(file);
            int fileSize = fis.available();
            ADU adu = new ADU(file, appId, aduId, fileSize, clientId);
            return adu;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    //check if there is adapter
    //create GRPC connection to adapter and ask for data for the client
    public void fetchADUsForApp(String clientId, String appId){
        try {
            MySQLConnection mysql = new MySQLConnection();
            Connection con = mysql.GetConnection();
            Statement stmt = con.createStatement();

            ResultSet rs = stmt.executeQuery("select address from registered_app_adapter_table where app_name='"+appId+"';");
            String adapterAddress="";
            while(rs.next()) {
                System.out.println("max value for app- "+rs.getInt(1) );
                adapterAddress = rs.getString(1);
            }

            if(adapterAddress.isEmpty()){
                System.out.println("no adapter registered for application - "+appId);
            }
            String ipAddress = adapterAddress.split(":")[0];
            int port = Integer.parseInt(adapterAddress.split(":")[0]);
            DTNAdapterClient appAdapterClient = new DTNAdapterClient(ipAddress, port);
            List<byte[]> dataList = appAdapterClient.RequestForADUs(clientId);
            for(int i=0;i<dataList.size();i++){
                sendFileStoreHelper.AddFile(appId, clientId, dataList.get(i));
            }
            con.close();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public List<ADU> fetchADUs(String clientId, String appId, Long aduIdStart) {
        ADU adu;
        List<ADU> ret = new ArrayList<>();
        long aduId = aduIdStart;
        while ((adu = this.fetchADU(clientId, appId, aduId)) != null) {
            ret.add(adu);
            aduId++;
        }
        return ret;
    }
}