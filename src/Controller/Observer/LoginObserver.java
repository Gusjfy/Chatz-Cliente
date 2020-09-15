/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller.Observer;

/**
 *
 * @author leona
 */
public interface LoginObserver {

    void signUp();

    void signIn();

    public void loginFailed();

}
