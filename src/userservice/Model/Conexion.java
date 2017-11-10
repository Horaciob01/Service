package userservice.Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author cesar
 */
public class Conexion {

    //parametros de configuracion de usuario
    //Descargar ojdbc6.jar e incluirlo en la libreria
    private Connection conexion;
    static String url = "jdbc:oracle:thin:@localhost:1521/XE";
    static String user = "SYSTEM";
    static String password = "root";

    /*Metodos*/
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        Conexion.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        Conexion.password = password;
    }

    public Connection getConexion() {
        return conexion;
    }

    public void setConexion(Connection conexion) {
        this.conexion = conexion;
    }

    public void conectar() {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try {
            conexion = DriverManager.getConnection(url, user, password);
            System.out.println("Conectado");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    public ArrayList<Estrategia> getEstrategias() throws SQLException {
        ArrayList<Estrategia> vec = new ArrayList<>();
        Statement stm = null;
        ResultSet rs;
        String nombre, sentencia;
        Boolean estado;
        int frecuencia, dias, hora_inicio, hora_final;
        Timestamp proxima_ejecucion;
        try {
            stm = conexion.createStatement();
            rs = stm.executeQuery("select * from sys.estrategias");
            while (rs.next()) {
                nombre = rs.getString("nombre");
                sentencia = rs.getString("sentencia");
                estado = rs.getBoolean("estado");
                frecuencia = rs.getInt("frecuencia");
                hora_inicio = rs.getInt("hora_inicio");
                hora_final = rs.getInt("hora_final");
                dias = rs.getInt("dias");
                proxima_ejecucion = rs.getTimestamp("proxima_ejecucion");
                vec.add(new Estrategia(nombre, sentencia, estado, frecuencia, dias, hora_inicio, hora_final, proxima_ejecucion));
            }
            stm.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
        return vec;
    }

    public void setProximaEjecucion(Estrategia estrategia) throws SQLException {
        Boolean caso = true;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(estrategia.getProxima_ejecucion());
        calendar.setTimeInMillis(calendar.getTimeInMillis() + (estrategia.getFrecuencia() * 60000));
        Date proxima_fecha = calendar.getTime();
        Timestamp proxima_ejecucion = new Timestamp(proxima_fecha.getTime());
        int dia = (calendar.get(Calendar.DAY_OF_WEEK) - 1);
        int dias = estrategia.getDias();
        int verificar = dias >> dia;
        int cont = 0;
        if ((verificar & 1) == 1) {
            if (((calendar.get(Calendar.HOUR_OF_DAY) * 60) + calendar.get(Calendar.MINUTE)) > estrategia.getHora_final()) {
                caso = false;
                dia++;
                cont++;
            }
        } else {
            caso = false;
        }
        if (!caso) {
            while (true) {
                if (dia == 7) {
                    dia = 0;
                }
                verificar = dias >> dia;
                if ((verificar & 1) == 1) {
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.setTimeInMillis(calendar.getTimeInMillis() + (((cont * 24) * 60) + estrategia.getHora_inicio()) * 60000);
                    proxima_fecha = calendar.getTime();
                    proxima_ejecucion = new Timestamp(proxima_fecha.getTime());
                    break;
                }
                dia++;
                cont++;
            }
        }
        estrategia.setProxima_ejecucion(proxima_ejecucion);
        String sql
                = "UPDATE SYS.ESTRATEGIAS "
                + "SET proxima_ejecucion = ?"
                + " WHERE nombre = ?";
        PreparedStatement pstmt = conexion.prepareStatement(sql);

        pstmt.setTimestamp(
                1, estrategia.getProxima_ejecucion());
        pstmt.setString(
                2, estrategia.getNombre());
        try {
            pstmt.executeUpdate();
            pstmt.close();
            //conexion.commit();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
