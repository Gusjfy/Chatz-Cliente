package Controller;

import Controller.Observer.LoginObserver;
import Controller.Observer.UpdateObserver;
import Model.Usuario;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Leonardo Steinke
 */
public class Controller {

    private Socket socket;

    private Usuario u;

    public Usuario getU() {
        return u;
    }

    private Controller() {

    }

    private static Controller instance = null;

    public static Controller getIntance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    public void register(String apelido, String email, String senha, String dataNascimento) {
        try {
            socket = new Socket("localhost", 5555);
            ObjectOutputStream saida = new ObjectOutputStream(socket.getOutputStream());
            saida.writeInt(1);
            saida.writeUTF(apelido);
            saida.writeUTF(email);
            saida.writeUTF(senha);
            saida.writeUTF(dataNascimento);
            saida.flush();
            saida.close();

        } catch (IOException ex) {

        }
    }

    public void updateUser(String apelido, String senha, String dataNascimento) throws IOException {
        u.setApelido(apelido);
        u.setSenha(senha);
        u.setDataNascimento(dataNascimento);
        try {
            socket = new Socket("localhost", 5555);
            ObjectOutputStream saida = new ObjectOutputStream(socket.getOutputStream());

            System.out.println("Client port is " + socket.getLocalPort());
            saida.writeInt(3);
            saida.writeUTF(u.getApelido());
            saida.writeUTF(u.getEmail());
            saida.writeUTF(u.getSenha());
            saida.writeUTF(u.getDataNascimento());
            saida.flush();
            ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
            String line;
            while ((line = entrada.readUTF()) != null) {
                if (line.equalsIgnoreCase("true")) {
                    notifyUpdateSuccessful();
                } else {

                }
            }
        } catch (IOException ex) {
            socket.close();
        }
    }

    public void login(String email, String senha) throws IOException {
        try {
            socket = new Socket("localhost", 5555);
            ObjectOutputStream saida = new ObjectOutputStream(socket.getOutputStream());

            System.out.println("Client port is " + socket.getLocalPort());
            saida.writeInt(2);
            saida.writeUTF(email);
            saida.writeUTF(senha);
            saida.flush();
            ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
            String line;
            while ((line = entrada.readUTF()) != null) {
                if (line.equalsIgnoreCase("true")) {
                    u = new Usuario();
                    u.setId(entrada.readInt());
                    u.setApelido(entrada.readUTF());
                    u.setEmail(entrada.readUTF());
                    u.setSenha(entrada.readUTF());
                    u.setDataNascimento(entrada.readUTF());
                    System.out.println("LOGADO COM SUCESSO");
                    notifySignIn();
                } else {
                    notifyLoginFailed();
                }
            }
        } catch (IOException ex) {
            socket.close();
        }
    }

    public void switchToSignUp() {
        notifySignUp();
    }

    public void switchToSignIn() {
        notifySignIn();
    }

    private List<LoginObserver> LoginObservers = new ArrayList<>();

    public void attach(LoginObserver obs) {
        this.LoginObservers.add(obs);
    }

    public void detach(LoginObserver obs) {
        this.LoginObservers.remove(obs);
    }

    private void notifySignUp() {

        for (LoginObserver LoginObserver : LoginObservers) {
            LoginObserver.signUp();
        }
    }

    private void notifySignIn() {
        for (LoginObserver LoginObserver : LoginObservers) {
            LoginObserver.signIn();
        }
    }

    private void notifyLoginFailed() {
        for (LoginObserver LoginObserver : LoginObservers) {
            LoginObserver.loginFailed();
        }
    }

    private List<UpdateObserver> updateObservers = new ArrayList<>();

    public void attach(UpdateObserver obs) {
        this.updateObservers.add(obs);
    }

    public void detach(UpdateObserver obs) {
        this.updateObservers.remove(obs);
    }

    private void notifyUpdateSuccessful() {
        for (UpdateObserver updateObserver : updateObservers) {
            updateObserver.updateSuccessful();
        }
    }

}
