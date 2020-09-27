/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Leonardo Steinke
 */
public class GetConnections extends Thread {

    private ServerSocket server;
    ObjectOutputStream saida;
    int friendId;
    Controller con;

    public GetConnections() throws IOException {
        this.con = Controller.getIntance();
    }

    @Override
    public void run() {
        try {
            server = new ServerSocket(con.getPORT());
        } catch (IOException ex) {
            Logger.getLogger(GetConnections.class.getName()).log(Level.SEVERE, null, ex);
        }
        while (true) {

            try (Socket conn = server.accept();) {
                ObjectInputStream entrada = new ObjectInputStream(conn.getInputStream());
                int code = entrada.readInt(); //Código de Operação

                switch (code) {
                    case 1://KeepAlive
                        System.out.println("Servidor conectou");
                        saida = new ObjectOutputStream(conn.getOutputStream());
                        saida.writeUTF("true");
                        saida.flush();
                        break;
                    case 2://Recebe Solicitação para chat

                        friendId = entrada.readInt();

                        saida = new ObjectOutputStream(conn.getOutputStream());
                        saida.writeUTF("true");
                        saida.flush();
                        con.StartNewChat(friendId);
                        break;
                    case 3:// Mensagem CLIENTE -> CLIENTE
                        friendId = entrada.readInt();
                        String mensagem = entrada.readUTF();
                        con.displayMessage(friendId, mensagem);
                        break;
                }

                conn.close();
                // entrada.close();
            } catch (Exception e) {

            }
        }
    }

}
