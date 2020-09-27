package Model;

/**
 *
 * @author Leonardo Steinke
 */
public class Usuario {

    private int id;
    private String apelido;
    private String senha;
    private String email;
    private String dataNascimento;
    private int online;

    public Usuario() {
    }

    public Usuario(int id, String apelido, int online) {
        this.id = id;
        this.apelido = apelido;
        this.online = online;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getApelido() {
        return apelido;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }
    
}
