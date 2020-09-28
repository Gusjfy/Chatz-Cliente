/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Model.Arquivo;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
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
                    case 4:// Recebimento de arquivo
                        friendId = entrada.readInt();
                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                byte[] arquivoByte = null;
                                try {
                                    arquivoByte = new byte[conn.getReceiveBufferSize()];
                                } catch (SocketException ex) {
                                    Logger.getLogger(GetConnections.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                BufferedInputStream bf = null;
                                try {
                                    bf = new BufferedInputStream(conn.getInputStream());
                                    bf.read(arquivoByte);
                                } catch (IOException ex) {
                                    Logger.getLogger(GetConnections.class.getName()).log(Level.SEVERE, null, ex);
                                }

                                Arquivo arquivo = (Arquivo) getObjectFromByte(arquivoByte);

                                String dir = arquivo.getDiretorioDestino().endsWith("/") ? arquivo
                                        .getDiretorioDestino() + arquivo.getNome() : arquivo
                                        .getDiretorioDestino() + "/" + arquivo.getNome();
                                System.out.println("Recebendo arquivo de usuario com id: " + friendId + "\t" + arquivo + "\t" + dir);
                                FileOutputStream fos = null;
                                try {
                                    fos = new FileOutputStream(dir);
                                } catch (FileNotFoundException ex) {
                                    Logger.getLogger(GetConnections.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                try {
                                    fos.write(arquivo.getConteudo());
                                    fos.close();
                                } catch (IOException ex) {
                                    Logger.getLogger(GetConnections.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        });
                        t.start();

                        break;

                }

                conn.close();
                // entrada.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static Object getObjectFromByte(byte[] objectAsByte) {
        Object obj = null;
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        try {
            bis = new ByteArrayInputStream(objectAsByte);
            ois = new ObjectInputStream(bis);
            obj = ois.readObject();

            bis.close();
            ois.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return obj;

    }

}
