package Controller;

import Controller.Observer.LoginObserver;
import Controller.Observer.MainObserver;
import Controller.Observer.SignUpObserver;
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

    private List<Usuario> friendList = new ArrayList<>();

    private int PORT;

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

            ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
            String line;
            while ((line = entrada.readUTF()) != null) {
                if (line.equalsIgnoreCase("true")) {
                    notifyRegister("Deu certo!! Faça login com seu E-Mail e Senha Cadastrados");
                } else {
                    System.out.println(line);
                    notifyRegister(line);

                }
            }

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
                    PORT = (u.getId() + 5555);
                    u.setId(entrada.readInt());
                    u.setApelido(entrada.readUTF());
                    u.setEmail(entrada.readUTF());
                    u.setSenha(entrada.readUTF());
                    u.setDataNascimento(entrada.readUTF());
                    System.out.println("LOGADO COM SUCESSO");
                    notifySignIn();
                    if (entrada.readUTF().equalsIgnoreCase("true")) {

                    }
                } else {

                }
            }
        } catch (IOException ex) {
            socket.close();
        }
    }

    public void LogOut() {
        PORT = 0;
        u = null;
        friendList = null;
        notifyLogOut();
    }

    public void fetchData() throws IOException {
        try {
            socket = new Socket("localhost", 5555);
            ObjectOutputStream saida = new ObjectOutputStream(socket.getOutputStream());

            System.out.println("Client port is " + socket.getLocalPort());
            saida.writeInt(6);
            saida.writeInt(u.getId());
            saida.flush();
            ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
            String line;
            while ((line = entrada.readUTF()) != null) {
                if (line.equalsIgnoreCase("true")) {
                    int id = entrada.readInt();
                    String apelido = entrada.readUTF();
                    System.out.println(apelido);
                    friendList.add(new Usuario(id, apelido));
                }
            }
        } catch (IOException ex) {
            socket.close();
        }

    }

    //Observers
    private List<LoginObserver> LoginObservers = new ArrayList<>();
    private List<SignUpObserver> SignUpObservers = new ArrayList<>();
    private List<UpdateObserver> updateObservers = new ArrayList<>();
    private List<MainObserver> mainObservers = new ArrayList<>();

    public void attach(LoginObserver obs) {
        this.LoginObservers.add(obs);
    }

    public void detach(LoginObserver obs) {
        this.LoginObservers.remove(obs);
    }

    public void attach(SignUpObserver obs) {
        this.SignUpObservers.add(obs);
    }

    public void detach(SignUpObserver obs) {
        this.SignUpObservers.remove(obs);
    }

    public void attach(UpdateObserver obs) {
        this.updateObservers.add(obs);
    }

    public void detach(UpdateObserver obs) {
        this.updateObservers.remove(obs);
    }

    public void attach(MainObserver obs) {
        this.mainObservers.add(obs);
    }

    public void detach(MainObserver obs) {
        this.mainObservers.remove(obs);
    }

    private void notifySignIn() {
        for (LoginObserver LoginObserver : LoginObservers) {
            LoginObserver.signIn();
        }
    }

    private void notifyUpdateSuccessful() {
        for (UpdateObserver updateObserver : updateObservers) {
            updateObserver.updateSuccessful();
        }
    }

    private void notifyRegister(String string) {
        for (SignUpObserver signUpObservers : SignUpObservers) {
            signUpObservers.signUp(string);
        }

    }

    private void notifyLogOut() {
        for (MainObserver mainObserver : mainObservers) {
            mainObserver.LogOut();
        }
    }

}
