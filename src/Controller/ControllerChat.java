/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Model.Arquivo;
import Model.Usuario;
import View.JanelaChat;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

/**
 *
 * @author Leonardo Steinke
 */
public class ControllerChat extends Thread {

    private int friendId;
    private Usuario friend;

    public Usuario getFriend() {
        return friend;
    }

    private JanelaChat janela;
    Controller controller;

    public ControllerChat(int friendId) throws IOException {
        this.friendId = friendId;
        this.controller = Controller.getIntance();
        List<Usuario> friendList = controller.getFriendList();
        for (Usuario u : friendList) {
            if (u.getId() == friendId) {
                friend = u;
                break;
            }
        }
    }

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;

    }

    @Override
    public void run() {

    }
    private Socket socket;

    public void sendMessage(String mensagem) throws IOException {

        try {
            socket = new Socket("localhost", (5555 + friendId));
            ObjectOutputStream saida = new ObjectOutputStream(socket.getOutputStream());
            saida.writeInt(3);
            saida.writeInt(controller.getU().getId());
            saida.writeUTF(mensagem);
            saida.flush();
            janela.newMessage(mensagem);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        socket.close();

    }

    public void sendFile(Arquivo arquivo) throws IOException {
        try {
            socket = new Socket("localhost", (5555 + friendId));
            ObjectOutputStream saida = new ObjectOutputStream(socket.getOutputStream());
            saida.writeInt(4);
            saida.writeInt(controller.getU().getId());
            saida.flush();
            BufferedOutputStream bf = new BufferedOutputStream(socket.getOutputStream());

            byte[] bytea = serializarArquivo(arquivo);
            bf.write(bytea);
            bf.flush();
            bf.close();
            socket.close();

        } catch (IOException ex) {
            ex.printStackTrace();
            socket.close();
        }
    }

    public void receiveMessage(String mensagem) {
        janela.newMessage(friend.getApelido(), mensagem);

    }

    public void attatch(JanelaChat janela) {
        this.janela = janela;
    }

    private byte[] serializarArquivo(Arquivo arquivo) {
        try {
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            ObjectOutputStream ous;
            ous = new ObjectOutputStream(bao);
            ous.writeObject(arquivo);
            return bao.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

}
