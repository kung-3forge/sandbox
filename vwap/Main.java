
import com.f1.ami.client.*;
import com.f1.ami.client.AmiClient;

import java.time.LocalDateTime;
import java.util.Random;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //final String[] transactionTypes = {"B","A","T"};
        final String[] transactionTypes = {"T"};
        final String symbolPrefix = "A_TYO";

        AmiClient client = new AmiClient();
        AmiClientListener listener = new AmiListener();
        client.addListener(listener);
      
        //AMI On Desktop 
        //client.start("localhost", 3289, "demo", 514);

        // AMI server
        client.start("blade", 42889, "demo", 514);

        client.setAutoFlushBufferMillis(100);
        final int symbolLimit = 40000;
        final int transactionLimit = 500;

        Map<String,Double[]> exitingSymbol = new HashMap<>();
        //Map<String,Long> symbolLastUpdateTime = new HashMap<>();

        System.out.println("Start:"+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.nnnnnnnnn")));

        if (client.waitForLogin(1000)) {
            System.out.println("All is well");
            double minPrice = 100.0;
            double maxPrice = 150.0;
            int minVolume = 100;
            int maxVolume = 10000;
            int stepVolume = 100;
            double maxPctChange = 0.001; //percentage price change
            double minPctChange = maxPctChange * -1; //percentage price change
            Random random = new Random();
            Long transId = 0L;


            //For symbol
            for (int i = 0 ; i < symbolLimit ; i++){

                for (int j = 0 ; j < transactionLimit ; j++){
                    //Random the symbol
                    String symbol = String.format("%s%04d", symbolPrefix, random.nextInt(symbolLimit)+1);
                    if (!exitingSymbol.containsKey(symbol)) {  //New symbol , add bid, ask and trade price
                        double trdprc = Math.round((minPrice + random.nextDouble() * (maxPrice - minPrice)) * 100.0) / 100.0;

                        double bid = Math.round((1-random.nextDouble()*maxPctChange) * trdprc * 100)/100.0;
                        double ask = Math.round((1+random.nextDouble()*maxPctChange) * trdprc * 100)/100.0 ;
                        exitingSymbol.put(symbol, new Double[]{bid,ask,trdprc}); //It has to be in the same order as the transactionTypes
                        //System.out.println("Symbol:" + symbol + " Bid:" + bid + " Ask:" +ask + " TradePrice:" + trdprc);
                    }
                    int randomTypeIndex = random.nextInt(transactionTypes.length); //Bid , Ask or Trade
                    String type = transactionTypes[randomTypeIndex];
                    double price = exitingSymbol.get(symbol)[randomTypeIndex] * (1+(minPctChange + (maxPctChange - minPctChange) *  random.nextDouble()) )  ; //Based on the last value +/- by maxPctChange
                    price = Math.round(price*100) / 100.0; //round to two decimals*/
                    //System.out.println("Symbol:" + symbol + " type:" + type + " price:" + price );

                    int numSteps = (maxVolume - minVolume) / stepVolume + 1;
                    int volume = minVolume + (random.nextInt(numSteps) * stepVolume);
                    //long updateTime = symbolLastUpdateTime.computeIfAbsent(symbol,k-> System.currentTimeMillis());
                    //updateTime = updateTime + (random.nextInt(1000)+1); //timestamp by current time plus a random value between 1 to 1000 milliseconds
                    long updateTime = System.currentTimeMillis();
                    transId++;
                    sendMessage(client,symbol,transId.toString()+updateTime+symbol,type,price,volume,updateTime);
                    exitingSymbol.get(symbol)[randomTypeIndex] = price; //Update the last bid or ask or trade

                    //for slowing down the update
                    if ( j % 100  == 0 ){
                        try{
                            Thread.sleep(5);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                //Each transaction

                //for bid
            }
            System.out.println("Finish:"+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.nnnnnnnnn")));

            while (true){
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.

    public static void sendMessage(AmiClient client, String symbol, String id, String type, double price, int volume, long updateTime) {
        String tableName;
        //Work out the table name, by symbol
        //String tableName = symbol + "_" ;
        //tableName += (type.equals("B") || type.equals("A")) ? "Q" : "T" ; // Use Q for Bid and ASK , otherwise T for table name suffix

        tableName = "A_Transactions";
        tableName += (type.equals("B") || type.equals("A")) ? "_Q" : "_T" ; // Use Q for Bid and ASK , otherwise T for table name suffix


        client.startObjectMessage(tableName,id);

        client.addMessageParamString("symbol", symbol);

        client.addMessageParamDouble("price", price);
        client.addMessageParamInt("volume", volume);
        client.addMessageParamLong("transTime",updateTime);
        client.addMessageParamString("type", type);
        client.addMessageParamString("flag", type);

        //client.sendMessageAndFlush();
        client.sendMessage();




    }

}
