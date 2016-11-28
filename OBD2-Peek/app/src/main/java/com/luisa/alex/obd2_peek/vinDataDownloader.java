package com.luisa.alex.obd2_peek;


import android.os.AsyncTask;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by alex on 2016-11-21.
 */

public class vinDataDownloader extends AsyncTask<String, Integer, String> {
    private static final String VIN_BASE_URL = "https://vpic.nhtsa.dot.gov/api/vehicles/decodevin/";

    ArrayList<Tuple> data;
    ConnectionHandler listener;

    public vinDataDownloader(ConnectionHandler listener) {
        this.data = new ArrayList<>();
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String[] vin) {
        final String METHOD = "doInBackground";
        Log.d(METHOD, "called");

        try {
            String fullUrlStr = VIN_BASE_URL + vin[0];
            //fullUrlStr = "https://vpic.nhtsa.dot.gov/api/vehicles/decodevin/KMHHxxxDx3Uxxxxxx"; //TEMP

            URL url = new URL(fullUrlStr);
            Log.d(METHOD, "Vin = "  + url.toString());

            //parse the definiton out of the XML
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(url.openStream());
            doc.getDocumentElement().normalize(); //cleans up the document to remove unnecessary nodes

            //search through the document
            NodeList nodeList = doc.getElementsByTagName("DecodedVariable");
            //if(nodeList.getLength() > 0 ){
            for(int i = 0; i < nodeList.getLength(); i++){
                //Node node = nodeList.item(0);
                Element ele = (Element)nodeList.item(i);
                NodeList variableNL = ele.getElementsByTagName("Variable");
                NodeList valueNL = ele.getElementsByTagName("Value");
                if(variableNL.getLength() > 0 && valueNL.getLength() > 0 ) {
                    String variable = variableNL.item(0).getTextContent().toString();
                    String value = valueNL.item(0).getTextContent().toString();
                    if((!variable.toLowerCase().equals("error code")) &&
                            (!variable.toLowerCase().equals("additional error text")) &&
                            (!variable.toLowerCase().equals("possible values")) &&
                            (!variable.toLowerCase().equals("suggested vin")) &&
                            (!variable.toLowerCase().equals("note"))
                            ){
                        this.data.add(new Tuple(variable, value));
                        //Log.d(METHOD, "variable=" + variable + ", value=" + value);
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        final String METHOD = "onProgressUpdate";
        Log.d(METHOD, "called");

    }

    @Override
    protected void onPostExecute(String fileContent){
        final String METHOD = "onPostExecute";
        Log.d(METHOD, "called");

        //DEBUG
        /*
        //for(String ele : this.data){
            Log.d(METHOD, ele);
        }
         */

        //Start the activity with all the data
        this.listener.showCarDataList(this.data);
    }
}

