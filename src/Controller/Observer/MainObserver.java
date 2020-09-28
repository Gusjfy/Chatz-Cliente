/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller.Observer;

/**
 *
 * @author Leonardo Steinke
 */
public interface MainObserver {

    void LogOut();

    public void updateFriendList();

    public void newChat(int friendId);

    public void removeContact(Boolean deuBoa);

}
