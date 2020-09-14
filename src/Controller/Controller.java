package Controller;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 * @author Leonardo Steinke
 */
public class Controller {

    Socket socket;

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

    public void login(String email, String senha) {
        try {
            socket = new Socket("localhost", 5555);
            ObjectOutputStream saida = new ObjectOutputStream(socket.getOutputStream());
            saida.writeInt(2);
            saida.writeUTF(email);
            saida.writeUTF(senha);
            saida.flush();
            saida.close();

        } catch (IOException ex) {

        }
    }
}
