import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Fredrik on 2015-07-06.
 */
public class MissionCompleteCallback implements MqttCallback{
    private MqttClient client;
    private HashMap<String, MissionInfo> clientToInfo;
    private HashSet<String> subscriptions;

    public MissionCompleteCallback(MqttClient client){
        this.client = client;
        clientToInfo = new HashMap<>();
        subscriptions = new HashSet<>();
    }

    @Override
    public void connectionLost(Throwable throwable) {
        System.out.println("MissionComplete lost connection!");
    }


    //nu timestampar vi här, vi borde skicka med timestampet, men då kanske vi måste ändra i databasen, vi kan göra det imorgon
    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        String message = mqttMessage.toString();
        System.out.println(message);


       if(topic.equals("telemetry/snapshot")){
           String carID = message.split(";")[0].split("carID:")[1];
           System.out.println(carID);
           //subscribe till alla snapshots??
           if(clientToInfo.containsKey(carID)){
               double longitude = Double.parseDouble(message.split("longitude:")[1].split(";")[0]);
               System.out.println(longitude);
               double latitude = Double.parseDouble(message.split("latitude:")[1].split(";")[0]);
               System.out.println(latitude);
               MissionInfo missionInfo = clientToInfo.get(carID);
               missionInfo.setCurrentLong(longitude);
               missionInfo.setCurrentLat(latitude);
               boolean done = missionInfo.checkIfDone();
               if (done){
                   System.out.println("car id är: " + carID);
                   new PublishThread(client,carID+"/message","message;messageText:Mission completed! DeviceID: " + carID).start();
               }
           }
           else{
               String subscriptionTopic = carID+"/message";
               if(!subscriptions.contains(subscriptionTopic)) {
                   System.out.println("Nu ska jag subscribe:a");
                   new SubscribeThread(client, carID + "/message").start();
               }
           }
       }
       else if (topic.contains("/message")) {
           //måste förbättras!
           if (message.contains("mission;")) {
               System.out.println("Nu kom det ett mission!!");
               String carID = topic.split("/message")[0];
               System.out.println("carID: "+carID);
               double latitude = Double.parseDouble(message.split("location:")[1].split(",")[0]);
               System.out.println(latitude);
               double longitude = Double.parseDouble(message.split("location:")[1].split(",")[1].split(";")[0]);
               System.out.println(longitude);
               int radius = 10;
               //int radius = Integer.parseInt(message.split("geofence#")[1].split("\n")[0]);
               System.out.println("radius: "+radius);
               MissionInfo missionInfo = new MissionInfo(radius,latitude,longitude);
               clientToInfo.put(carID, missionInfo);
           }
       }
       else{
           System.out.println("Unknown topic:\""+topic+"\"");
       }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
