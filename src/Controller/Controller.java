package Controller;

import Controller.Observer.LoginObserver;
import Controller.Observer.MainObserver;
import Controller.Observer.SignUpObserver;
import Controller.Observer.UpdateObserver;
import Controller.Observer.AddContactObserver;
import Model.Usuario;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Leonardo Steinke
 */
public class Controller {

    private Socket socket;

    private List<Usuario> friendList = new ArrayList<>();

    public List<Usuario> getFriendList() {
        return friendList;
    }

    public void setFriendList(List<Usuario> friendList) {
        this.friendList = friendList;
    }

    private int PORT;

    public int getPORT() {
        return PORT;
    }

    public void setPORT(int PORT) {
        this.PORT = PORT;
    }

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
                    PORT = (u.getId() + 5555);
                    notifySignIn();
                    if (entrada.readUTF().equalsIgnoreCase("true")) {
                        System.out.println(entrada.readInt());
                        System.out.println(entrada.readUTF());
                    }
                } else {

                }
            }
        } catch (IOException ex) {
            socket.close();
        }

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {

                    try {
                        Thread.sleep(10000);
                        fetchData();
                        System.out.println("Atualizou lista de contatos");
                    } catch (IOException ex) {
                        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }

            }
        });
        t.start();

        GetConnections conections = new GetConnections();
        conections.start();

    }

    public void LogOut() {
        PORT = 0;
        u = new Usuario();
        friendList = new ArrayList<>();
        notifyLogOut();
    }

    public void addContact(String userEmail) throws IOException {
        ObjectOutputStream saida;
        try {
            socket = new Socket("localhost", 5555);
            saida = new ObjectOutputStream(socket.getOutputStream());
            saida.writeInt(5);
            saida.writeInt(u.getId());
            saida.writeUTF(userEmail);
            saida.flush();
            ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
            String line;
            while ((line = entrada.readUTF()) != null) {
                if (line.equalsIgnoreCase("true")) {
                    notifyAddContact(true);
                } else {
                    notifyAddContact(false);
                }
            }
        } catch (IOException ex) {
            socket.close();
        }
    }

    public void fetchData() throws IOException {
        try {
            socket = new Socket("localhost", 5555);
            ObjectOutputStream saida = new ObjectOutputStream(socket.getOutputStream());

            saida.writeInt(6);
            saida.writeInt(u.getId());
            saida.flush();
            ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
            String line;
            while ((line = entrada.readUTF()) != null) {
                if (line.equalsIgnoreCase("true")) {
                    int id = entrada.readInt();
                    String apelido = entrada.readUTF();
                    int online = entrada.readInt();
                    friendList.add(new Usuario(id, apelido, online));
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
    private List<AddContactObserver> addContactObservers = new ArrayList<>();

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

    public void attach(AddContactObserver obs) {
        this.addContactObservers.add(obs);
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

    private void notifyAddContact(Boolean deuBoa) {
        for (AddContactObserver addContactObserver : addContactObservers) {
            addContactObserver.addContact(deuBoa);
        }
    }

}
