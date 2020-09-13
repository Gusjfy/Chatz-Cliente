/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 * @author Gustavo
 */
public class ChatClient {

    private final String serverName;
    private final int serverPort;
    private Socket socket;
    private ObjectInputStream serverIn;
    private ObjectOutputStream serverOut;
    private BufferedReader bufferedIn;

    public ChatClient(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }
    
    public boolean connect() {
        //Conecta com o servidor.
        try {
            this.socket = new Socket(serverName, serverPort);
            System.out.println("Client port is " + socket.getLocalPort());
            this.serverOut = new ObjectOutputStream(socket.getOutputStream());
            this.serverIn = new ObjectInputStream(socket.getInputStream());
            this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    /**
     *
     * @param email
     * @param password
     */
    public boolean login(String email, String password) throws IOException{
//        //Envia para o servidor o usuário e senha para login
//        
//        //case 2 : login
//        serverOut.writeUTF("2");
//        serverOut.writeUTF(email);
//        serverOut.writeUTF(password);
//        
//        String res = serverIn.readUTF();
//        System.out.println("Response line " + res);
//        
//        if (res.equals("login ok")) {
//            return true;
//        }else {
//            return false;
//        }

        return true;
    }
    
    public boolean register(String nickName, String email, String password, String birthDate) throws IOException {
        //Envia para o servidor os dados para o singup
        
        //case 1: register
        serverOut.writeUTF("1");
        serverOut.writeUTF(nickName);
        serverOut.writeUTF(email);
        serverOut.writeUTF(password);
        serverOut.writeUTF(birthDate);
        
        String res = serverIn.readUTF();
        System.out.println("Response line " + res);
        
        if (res.equals("register ok")) {
            return true;
        }else {
            return false;
        }
    }
    
}
