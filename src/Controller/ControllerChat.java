/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Model.Usuario;
import View.JanelaChat;
import java.io.IOException;
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

    void sendMessage(String mensagem) {
        janela.newMessage(mensagem);

    }

    void receiveMessage(String mensagem) {
        janela.newMessage(friend.getApelido(), mensagem);

    }

    public void attatch(JanelaChat janela) {
        this.janela = janela;
    }

}
