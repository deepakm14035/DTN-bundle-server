package edu.sjsu.dtn.bundleserver;


import edu.sjsu.dtn.server.communicationservice.ConnectionData;
import edu.sjsu.dtn.server.communicationservice.DTNCommunicationGrpc;
import edu.sjsu.dtn.server.communicationservice.ResponseStatus;
import io.grpc.stub.StreamObserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DTNCommunicationService extends DTNCommunicationGrpc.DTNCommunicationImplBase{

    @Override
    public void registerAdapter(ConnectionData connectionData,
                                StreamObserver<ResponseStatus> responseObserver){

    }

    @Override
    public void saveData(edu.sjsu.dtn.server.communicationservice.AppData request,
                         StreamObserver<ResponseStatus> responseObserver) {

    }

    public static void main(String args[]){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con= DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/sonoo","root","root");
//here sonoo is database name, root is username and password
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select * from emp");
            while(rs.next())
                System.out.println(rs.getInt(1)+"  "+rs.getString(2)+"  "+rs.getString(3));
            con.close();
        }catch(Exception e){ System.out.println(e);}
    }
}
