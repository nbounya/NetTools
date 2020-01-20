package com.example.nettools.tools;

import android.net.InetAddresses;
import android.os.AsyncTask;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tools {

    public static String ping(String address, int packets, int ttl, double waitTime, TextView view) throws Exception{
        String format = "ping -c %d -t %d -i %f %s";
        String command = String.format(format, packets, ttl, waitTime, address);
        Process process = Runtime.getRuntime().exec(command);

        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
        // Grab the results
        StringBuilder log = new StringBuilder();
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            log.append(line + "\n");
            if(view != null){
            view.setText(log.toString());
            }
        }

        return log.toString();
    }

//    public static void traceroute(String address, int max_ttl, int first_ttl, boolean resolve, int probes, TextView view) throws Exception{
//        String[][] hopProbeTimes = new String[max_ttl][probes];
//        String[] hopAddress = new String[max_ttl];
//        StringBuilder log = new StringBuilder();
//
//        String format = "traceroute to %s (%s), %d hops max, 60 byte packets\n";
//        String line = String.format(format, address, address, max_ttl);
//        log.append(line);
//
//        for(int i = first_ttl - 1; i < max_ttl; i++) {
//            int remaining_ping = 3;
//            String hopPingOutput = ping(address, 1, i + 1, 0.201, null);
//            hopAddress[i] = parseHopIp(hopPingOutput);
//            format = " %d  %s (%<s)";
//            line = String.format(format, i + 1, hopAddress[i]);
//            log.append(line);
//
//            while (remaining_ping != 0) {
//                String probePingOutput = ping(hopAddress[i], 1, 30, 0.201, null);
//                hopProbeTimes[i][remaining_ping - 1] = parseHopPingTimes(probePingOutput);
//                if (hopProbeTimes[i][remaining_ping - 1].equals("*")) {
//                    line = "       " + hopProbeTimes[i][remaining_ping-1] + "       ";
//                    } else {
//                    line = "  " + hopProbeTimes[i][remaining_ping-1] + " ms";
//                    }
//                log.append(line);
//                remaining_ping--;
//            }
//
//            format = "\n";
//            line = String.format(format);
//            log.append(line);
//
//            if(hopAddress[i].equals(address)){
//                view.setText(log.toString());
//                break;
//            }
//        }
//        view.setText(log.toString());
//    }

    static String parseHopIp(String hopPingOutput, boolean isInputIP){
        String hopIp;
        hopPingOutput = hopPingOutput.replaceAll("[\\t\\n\\r]+"," ");
        String[] hopPingOutputArray = hopPingOutput.split(" ");
        if(isInputIP) {
            hopIp = hopPingOutputArray[8].substring(0, hopPingOutputArray[8].length() - 1);
            if (hopIp.equals("byte")) {
                hopIp = hopPingOutputArray[10].substring(0, hopPingOutputArray[10].length() - 1);
            }
        } else {
            hopIp = hopPingOutputArray[9].substring(1, hopPingOutputArray[9].length() - 2);
            if (hopIp.equals("byte")) {
                hopIp = hopPingOutputArray[11].substring(1, hopPingOutputArray[11].length() - 2);
            }
        }
        return hopIp;
    }

    static String parseHopPingTimes(String probePingOutput){
        String hopPingTime;
        probePingOutput = probePingOutput.replaceAll("[\\t\\n\\r]+"," ");
        String[] probePingOutputArray = probePingOutput.split(" ");
        if (probePingOutputArray[13].equals("packets")) {
            hopPingTime = "*";
            } else {
                hopPingTime = probePingOutputArray[13].substring(5);
            }
        return hopPingTime;
    }

    static String parseHopHostname(String probePingOutput){
        String hopPingTime;
        probePingOutput = probePingOutput.replaceAll("[\\t\\n\\r]+"," ");
        String[] probePingOutputArray = probePingOutput.split(" ");
        if (probePingOutputArray[13].equals("packets")) {
            hopPingTime = "*";
            } else {
                hopPingTime = probePingOutputArray[13].substring(5);
            }
        return hopPingTime;
    }

    static boolean checkifIP(String input){
        Pattern pattern = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }
    static String parseWhoisRefer(String whoisOutput){
        String refer;
        whoisOutput = whoisOutput.replaceAll("[\\t\\n\\r]+"," ");
        String[] whoisOutputArray = whoisOutput.split(" ");
        if (whoisOutputArray[18].equals("refer:")) {
            refer = whoisOutputArray[26];
        } else {
            refer = "";
        }
        return refer;
    }

    public static class pingTask extends AsyncTask<Void, String, Void>{
        String address;
        int packets;
        int ttl;
        double waitTime;
        TextView view;
        String error;

        public pingTask(String address, int packets, int ttl, double waitTime, TextView view){
            this.address = address;
            this.packets = packets;
            this.ttl = ttl;
            this.waitTime = waitTime;
            this.view = view;
        }

        @Override
        protected Void doInBackground(Void... params){
            String format = "ping -c %d -t %d -i %f %s";
            String command = String.format(format, packets, ttl, waitTime, address);
            try {
                Process process = Runtime.getRuntime().exec(command);

                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));
                // Grab the results
                StringBuilder log = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    log.append(line + "\n");
                    if (view != null) {
                        publishProgress(log.toString());
                    }
                }
            } catch(Exception e){
                error = e.getMessage();
                cancel(true);
            }
        return null;
        }

        @Override
        protected void onProgressUpdate(String... args){
            view.setText(args[0]);
        }

        @Override
        protected void onCancelled(){
            super.onCancelled();
            view.setText(error);
        }
    }

    public static class traceRouteTask extends AsyncTask<Void, String, Void>{
        String address;
        int max_ttl;
        int first_ttl;
        boolean resolve;
        int probes;
        TextView view;
        String error;

        public traceRouteTask(String address, int max_ttl, int first_ttl, boolean resolve, int probes, TextView view){
            this.address = address;
            this.max_ttl = max_ttl;
            this.first_ttl = first_ttl;
            this.resolve = resolve;
            this.probes = probes;
            this.view = view;
        }

        @Override
        protected Void doInBackground(Void... params){
            String[][] hopProbeTimes = new String[max_ttl][probes];
            String[] hopAddress = new String[max_ttl];
            String[] hopHosts = new String[max_ttl];
            StringBuilder log = new StringBuilder();
            boolean isInputIP;

            try {
                isInputIP = checkifIP(address);

                String format = "traceroute to %s (%s), %d hops max, 60 byte packets\n";
                String line;
                if(resolve){
                    String tracerouteToHostname;
                    try {
                        tracerouteToHostname = InetAddress.getByName(address).getHostName();
                    } catch (UnknownHostException e){
                        tracerouteToHostname = address;
                    }
                    line = String.format(format, tracerouteToHostname, address, max_ttl);
                } else {
                    line = String.format(format, address, address, max_ttl);
                }
                log.append(line);

                for (int i = first_ttl - 1; i < max_ttl; i++) {
                    isInputIP = true;
                    int remaining_ping = probes;
                    String hopPingOutput = ping(address, 1, i + 1, 0.201, null);
                    hopAddress[i] = parseHopIp(hopPingOutput, isInputIP);
                    format = " %d  %s (%s)";

                    if(resolve){
                        try {
                            hopHosts[i] = InetAddress.getByName(hopAddress[i]).getHostName();
                        } catch (UnknownHostException e){
                            hopHosts[i] = hopAddress[i];
                        }
                        line = String.format(format, i + 1, hopHosts[i], hopAddress[i]);
                    } else {
                        format = " %d  %s (%<s)";
                        line = String.format(format, i + 1, hopAddress[i]);
                    }
                    log.append(line);

                    while (remaining_ping != 0) {
                        String probePingOutput = ping(hopAddress[i], 1, 30, 0.201, null);
                        hopProbeTimes[i][remaining_ping - 1] = parseHopPingTimes(probePingOutput);
                        if (hopProbeTimes[i][remaining_ping - 1].equals("*")) {
                            line = "       " + hopProbeTimes[i][remaining_ping - 1] + "       ";
                        } else {
                            line = "  " + hopProbeTimes[i][remaining_ping - 1] + " ms";
                        }
                        log.append(line);
                        remaining_ping--;
                    }

                    format = "\n";
                    line = String.format(format);
                    log.append(line);
                    publishProgress(log.toString());

                    if (hopAddress[i].equals(address)) {
                        error = "";
                        cancel(true);
                    }
                }
            } catch (Exception e){
                error = e.getMessage();
                cancel(true);
            }
        return null;
        }

        @Override
        protected void onProgressUpdate(String... args){
            view.setText(args[0]);
        }

        @Override
        protected void onCancelled(){
            super.onCancelled();
            if(!error.equals("")) {
                view.setText(error);
            }
            view.setText(view.getText() + "\n Success!");
        }
    }

    public static class whoisTask extends AsyncTask<Void, String, Void> {
        String address;
        TextView view;
        String whoisServerName = "whois.iana.org";
        int whoisPort = 43;
        String ianaWhoisResult = "";
        String referWhoisResult = "";
        String error;

        public whoisTask(String address, TextView view) {
            this.address = address;
            this.view = view;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Socket theSocket = new Socket(whoisServerName, whoisPort, true);
                Writer out = new OutputStreamWriter(theSocket.getOutputStream());
                out.write("" + address + "\r\n");
                out.flush();
                DataInputStream WhoisStream;
                WhoisStream = new DataInputStream(theSocket.getInputStream());
                String s;
                while ((s = WhoisStream.readLine()) != null) {
                    ianaWhoisResult = ianaWhoisResult + s + "\n";
                }

                String referWhoisServerName = parseWhoisRefer(ianaWhoisResult);
                if (referWhoisServerName.equals("")) {
                    publishProgress(ianaWhoisResult);
                } else {
                    theSocket = new Socket(referWhoisServerName, whoisPort, true);
                    out = new OutputStreamWriter(theSocket.getOutputStream());
                    out.write("" + address + "\r\n");
                    out.flush();
                    WhoisStream = new DataInputStream(theSocket.getInputStream());
                    while ((s = WhoisStream.readLine()) != null) {
                        referWhoisResult = referWhoisResult + s + "\n";
                    }
                    publishProgress(referWhoisResult);
                }
            }
            catch (Exception e) {
                error = e.getMessage();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... args){
            view.setText(args[0]);
        }

        @Override
        protected void onCancelled(){
            super.onCancelled();
            if(!error.equals("")) {
                view.setText(error);
            }
        }
    }
}