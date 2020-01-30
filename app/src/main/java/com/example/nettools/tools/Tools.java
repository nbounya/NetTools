package com.example.nettools.tools;

import android.os.AsyncTask;
import android.os.Build;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

public class Tools {

    public static String ping(String address, int packets, int ttl, double waitTime, TextView view) throws Exception{
        String format = "ping -n -c %d -t %d -i %f %s";
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

    public static void traceroute(String address, int max_ttl, int first_ttl, boolean resolve, int probes, TextView view) throws Exception{
        String[][] hopProbeTimes = new String[max_ttl][probes];
        String[] hopAddress = new String[max_ttl];
        StringBuilder log = new StringBuilder();

        String format = "traceroute to %s (%s), %d hops max, 60 byte packets\n";
        String line = String.format(format, address, address, max_ttl);
        log.append(line);

        for(int i = first_ttl - 1; i < max_ttl; i++) {
            int remaining_ping = 3;
            String hopPingOutput = ping(address, 1, i + 1, 0.201, null);
            hopAddress[i] = parseHopIp(hopPingOutput);
            format = " %d  %s (%<s)";
            line = String.format(format, i + 1, hopAddress[i]);
            log.append(line);

            while (remaining_ping != 0) {
                String probePingOutput = ping(hopAddress[i], 1, 30, 0.201, null);
                hopProbeTimes[i][remaining_ping - 1] = parseHopPingTimes(probePingOutput);
                if (hopProbeTimes[i][remaining_ping - 1].equals("*")) {
                    line = "       " + hopProbeTimes[i][remaining_ping-1] + "       ";
                    } else {
                    line = "  " + hopProbeTimes[i][remaining_ping-1] + " ms";
                    }
                log.append(line);
                remaining_ping--;
            }

            format = "\n";
            line = String.format(format);
            log.append(line);

            if(hopAddress[i].equals(address)){
                view.setText(log.toString());
                break;
            }
        }
        view.setText(log.toString());
    }

    static String parseHopIp(String hopPingOutput){
        String hopIp;
        hopPingOutput = hopPingOutput.replaceAll("[\\t\\n\\r]+"," ");
        String[] hopPingOutputArray = hopPingOutput.split(" ");
        hopIp = hopPingOutputArray[8].substring(0, hopPingOutputArray[8].length() - 1);
        if (hopIp.equals("byte")) {
            hopIp = hopPingOutputArray[10].substring(0, hopPingOutputArray[10].length() - 1);
        }
        if (hopPingOutputArray[7].equals("---")) {
            hopIp = "";
        }
        return hopIp;
    }

    static String parseHopPingTimes(String probePingOutput){
        String hopPingTime;
        probePingOutput = probePingOutput.replaceAll("[\\t\\n\\r]+"," ");
        String[] probePingOutputArray = probePingOutput.split(" ");
        if (probePingOutputArray[13].equals("packets") || probePingOutputArray[13].equals("exceeded")) {
            hopPingTime = "*";
            } else {
                hopPingTime = probePingOutputArray[13].substring(5);
            }
        return hopPingTime;
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

        public pingTask(String address, int packets, int ttl, double waitTime, boolean isNumericOnly, TextView view){
            this.address = address;
            this.packets = packets;
            this.ttl = ttl;
            this.waitTime = waitTime;
            this.view = view;
            view.setText("$ ping -c " + packets + " -t " + ttl + " -i " + waitTime + " " + address);
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
                log.append("$ ping -c " + packets + " -t " + ttl + " -i " + waitTime + " " + address + "\n");
                while ((line = bufferedReader.readLine()) != null) {
                    if(this.isCancelled()) break;
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
            view.setText("$ traceroute -f " + first_ttl + " -m " + max_ttl + " -q " + probes + " " + address);
        }

        @Override
        protected Void doInBackground(Void... params){
            StringBuilder log = new StringBuilder();
            try {
                String format = "traceroute to %s (%s), %d hops max, 60 byte packets\n";
                String line;

                if (resolve) {
                    String tracerouteToHostname;
                    String tracerouteToIP;
                    try {
                        tracerouteToHostname = InetAddress.getByName(address).getHostName();
                        tracerouteToIP = InetAddress.getByName(address).getHostAddress();
                    } catch (UnknownHostException e) {
                        tracerouteToHostname = address;
                        tracerouteToIP = address;
                    }
                    line = String.format(format, tracerouteToHostname, tracerouteToIP, max_ttl);
                } else {
                    line = String.format(format, address, address, max_ttl);
                }

                log.append("$ traceroute -f " + first_ttl + " -m " + max_ttl + " -q " + probes + " " + address + "\n");
                log.append(line);
            } catch(Exception e) {
                error = "ERROR";
                cancel(true);
            }

            try{
                String[][] hopProbeTimes = new String[max_ttl][probes];
                String[] hopAddress = new String[max_ttl];
                String[] hopHostname = new String[max_ttl];
                for (int i = first_ttl - 1; i < max_ttl; i++) {
                    if(this.isCancelled()) break;
                    String format, line;
                    int remaining_ping = probes;
                    String hopPingOutput = ping(InetAddress.getByName(address).getHostAddress(), 1, i + 1, 0.201, null);
                    hopAddress[i] = parseHopIp(hopPingOutput);

                    if(hopAddress[i].equals("")){
                        format = " %d  ";
                    } else {
                        format = " %d  %s (%s)";
                    }

                    if(hopAddress[i].equals("")) {
                        line = String.format(format, i + 1);
                    } else {
                        if (resolve) {
                            try {
                                hopHostname[i] = InetAddress.getByName(hopAddress[i]).getHostName();
                            } catch (UnknownHostException e) {
                                hopHostname[i] = hopAddress[i];
                            }
                            line = String.format(format, i + 1, hopHostname[i], hopAddress[i]);
                        } else {
                            format = " %d  %s (%<s)";
                            line = String.format(format, i + 1, hopAddress[i]);
                        }
                    }
                    log.append(line);

                    try{
                    if(!hopAddress[i].equals("")) {
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
                    } else {
                        hopProbeTimes[i][0] = "*";
                        hopProbeTimes[i][1] = "*";
                        hopProbeTimes[i][2] = "*";
                        line = "       " + hopProbeTimes[i][0] + "       " + hopProbeTimes[i][1] + "       "+ hopProbeTimes[i][2] + "       ";
                        log.append(line);
                    }
                    }catch(Exception e){
                        error = "ERROR";
                        cancel(true);
                    }

                    line = "\n";
                    log.append(line);
                    publishProgress(log.toString());
                    String lastAddressCheck = InetAddress.getByName(address).getHostAddress();
                    if (hopAddress[i].equals(lastAddressCheck)) {
                        error = "";
                        return null;
                    }
                }
            } catch (Exception e){
                error = "ERROR";
                cancel(true);
            }
        return null;
        }

        @Override
        protected void onProgressUpdate(String... args){
            view.setText(args[0]);
        }

        @Override
        protected void onPostExecute(Void params){
            view.setText(view.getText() + "\n Traceroute over.");
        }

        @Override
        protected void onCancelled(){
            super.onCancelled();
            if(error != null) {
                if (!error.equals("")) {
                    view.setText(error);
                }
            }
        }
    }

    public static class whoisTask extends AsyncTask<Void, String, Void> {
        String address;
        TextView view;
        String whoisServerName = "whois.iana.org";
        int whoisPort = 43;
        String ianaWhoisResult = "";
        String referWhoisResult = "";
        String error = "";

        public whoisTask(String address, TextView view) {
            this.address = address;
            this.view = view;
            view.setText("");
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
                    if(this.isCancelled()) break;
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
                error = "ERROR";
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
        }
    }

    public static class HttpStatusTask extends AsyncTask<Void, Void, Void>{
        TextView view;
        String urlText;
        String error = "";
        int code;
        String httpMessage;

        public HttpStatusTask(String url, TextView view) {
            this.urlText = url;
            this.view = view;
            view.setText("");
        }

        @Override
        protected  void onPreExecute(){
                view.setText("Connecting...");
        }

        @Override
        protected Void doInBackground(Void... params) {
            try{
                URL url = new URL("http://" + urlText);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                code = connection.getResponseCode();
                httpMessage = connection.getResponseMessage();
            } catch (Exception e) {
                error = "ERROR";
                cancel(true);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void params){
            view.setText("HTTP Status: " + code + " " +  httpMessage);
        }

        @Override
        protected void onCancelled(){
            super.onCancelled();
            if(!error.equals("")) {
                view.setText(error);
            }
        }
    }

    public static class ifconfigTask extends AsyncTask<Void, String, Void> {
        TextView view;
        String error = "";

        public ifconfigTask(TextView view) {
            this.view = view;
            view.setText("");
        }

        @Override
        protected Void doInBackground(Void... params) {
            String command;
            String message = "";
            if(Build.VERSION.SDK_INT > 23) {
                message = "$ ifconfig\n";
                command = "ifconfig";
            } else {
                message = "\"ifconfig\" not available, using \"netcfg\" instead.\n\n$ netcfg\n";
                command = "netcfg";
            }
            try {
                Process process = Runtime.getRuntime().exec(command);

                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));
                // Grab the results
                StringBuilder log = new StringBuilder();
                String line;
                log.append(message);
                while ((line = bufferedReader.readLine()) != null) {
                    if(this.isCancelled()) break;
                    log.append(line + "\n");
                    if (view != null) {
                        publishProgress(log.toString());
                    }
                }
            } catch(Exception e){
                error = "ERROR";
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
        }
    }

}