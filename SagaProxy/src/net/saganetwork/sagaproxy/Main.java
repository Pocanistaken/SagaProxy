
package net.saganetwork.sagaproxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import net.saganetwork.sagaproxy.detectors.BotDetection;
import net.saganetwork.sagaproxy.detectors.CountryDetection;

public class Main {

    
    public static void main(String[] args) {
        try {
            Scanner scan = new Scanner(System.in);
            System.out.println("[SagaProxy] Lütfen ana oyun sunucusunun IP adresini giriniz (Örnek: localhost)");
            String GAME_IP = scan.nextLine();
            System.out.println("[SagaProxy] Lütfen proxy sunucusunun Port adresini giriniz (Örnek: 25565)");
            int PROXY_PORT = scan.nextInt();
            System.out.println("[SagaProxy] Lütfen ana oyun sunucusunun giriş Port adresini giriniz (Örnek: 25566)");
            int GAME_PORT = scan.nextInt();

            ServerSocket serverSocket = new ServerSocket(PROXY_PORT);
            System.out.println("-----------------------------------------------");
            System.out.println("[SagaProxy] Proxy sunucusu başlatıldı... (v1.0.0)");
            System.out.println("[SagaProxy] İletişim için (Discordc: aliozturk_)");
            System.out.println("-----------------------------------------------");

            while (true) {
                Socket clientSocket = serverSocket.accept();

                Socket minecraftServerSocket = new Socket(GAME_IP, GAME_PORT);
                ProxyThread clientToServerThread = new ProxyThread(clientSocket, minecraftServerSocket, GAME_IP, 1000);
                ProxyThread serverToClientThread = new ProxyThread(minecraftServerSocket, clientSocket, GAME_IP, 1000);

                clientToServerThread.start();
                serverToClientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ProxyThread extends Thread {
    private final Socket inputSocket;
    private final Socket outputSocket;
    private final String gameIP;
    private final long timeInterval;
    private final AtomicInteger totalBytesSent;
    private final AtomicInteger packetCount;
    private static final int MAX_PACKET_COUNT = 2000;
    private static final int MAX_BYTES_SENT = 1048576; // 1MB
    public ProxyThread(Socket inputSocket, Socket outputSocket, String gameIP, long timeInterval) {
        this.inputSocket = inputSocket;
        this.outputSocket = outputSocket;
        this.gameIP = gameIP;
        this.timeInterval = timeInterval;
        this.totalBytesSent = new AtomicInteger(0);
        this.packetCount = new AtomicInteger(0);
    }

    public void run() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int currentPacketCount = packetCount.getAndSet(0);
                int currentTotalBytesSent = totalBytesSent.getAndSet(0);
                SimpleDateFormat date = new SimpleDateFormat("HH:mm:ss");
                String text = date.format(new Date());
                //System.out.println(text + " Paket Sayısı: " + currentPacketCount);
                //System.out.println(text + " Toplam Gönderilen Byte: " + currentTotalBytesSent);

                // Eşik değerine göre aşırı yük kontrolü yapabilirsiniz
                if (currentPacketCount > MAX_PACKET_COUNT || currentTotalBytesSent > MAX_BYTES_SENT) {
                    System.out.println("Aşırı yük tespit edildi. Bağlantı kapatılıyor...");
                    try {
                        inputSocket.close();
                        outputSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, timeInterval, timeInterval);

        try {
            String clientAddress = inputSocket.getInetAddress().getHostAddress();
            int clientPort = inputSocket.getPort();
            if (!clientAddress.equals("127.0.0.1") && !clientAddress.equals("::1") && !clientAddress.equals(gameIP)) { // Fixing Nullpoint exception , gameIP exception
                BotDetection botDetection = new BotDetection();
                CountryDetection countryDetection = new CountryDetection();
                ArrayList<String> list = countryDetection.getResultList(countryDetection.getIPInfo(clientAddress));
                System.out.println("[SagaProxy] Yeni bağlantı: " + clientAddress + ":" + clientPort + " - (" + list.get(0) + ")" + " VPN=" + list.get(1) + " HOSTING=" + list.get(2));
                if (!list.get(0).contains("TR") || !list.get(1).contains("false") ||!list.get(2).contains("false") || botDetection.isBot(clientAddress)) { // Country - Vpn - Hosting - Bot
                    inputSocket.close();
                    outputSocket.close();
                }
            }   

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputSocket.getInputStream().read(buffer)) != -1) {
                outputSocket.getOutputStream().write(buffer, 0, bytesRead);
                outputSocket.getOutputStream().flush();
                
                
                totalBytesSent.addAndGet(bytesRead);
                packetCount.incrementAndGet();
              
                
                /*
                
                SimpleDateFormat date = new SimpleDateFormat("HH:mm:ss");
                String text = date.format(new Date());
                
               // System.out.println(text + " Toplam Gönderilen byte =" + totalBytesSent.get() + " Paket =" + packetCount.get());
                
                
                if (totalBytesSent.get() > MAX_BYTES_SENT || packetCount.get() > MAX_PACKET_COUNT) {
                    inputSocket.close();
                    outputSocket.close();
                    break;
                */
                
                
            }
        } catch (IOException e) {
            //e.printStackTrace();
            // I don't want to fuck console
        } finally {
            try {
                inputSocket.close();
                outputSocket.close();
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
    }
}
