package userservice;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import userservice.Model.Conexion;
import userservice.Model.Estrategia;

/**
 *
 * @author Horacio
 */
public class UserService {

    public static void main(String[] args) throws ParseException, IOException {
        Conexion conexion = new Conexion();
        conexion.conectar();
        ArrayList<Estrategia> estrategias;
        Calendar cA, cC;
        Estrategia estrategia;
        while (true) {
            try {
                estrategias = conexion.getEstrategias();
                cA = Calendar.getInstance();
                cC = Calendar.getInstance();
                for (int i = 0; i < estrategias.size(); i++) {
                    estrategia = estrategias.get(i);
                    cC.setTime(estrategia.getProxima_ejecucion());
                    if (estrategia.getEstado()
                            && ((cA.get(Calendar.HOUR_OF_DAY) * 60) + cA.get(Calendar.MINUTE)) >= estrategia.getHora_inicio()
                            && ((cA.get(Calendar.HOUR_OF_DAY) * 60) + cA.get(Calendar.MINUTE)) <= estrategia.getHora_final()
                            && cA.get(Calendar.DAY_OF_YEAR) == cC.get(Calendar.DAY_OF_YEAR)
                            && cA.get(Calendar.YEAR) == cC.get(Calendar.YEAR)
                            && cA.get(Calendar.HOUR_OF_DAY) == cC.get(Calendar.HOUR_OF_DAY)
                            && cA.get(Calendar.MINUTE) == cC.get(Calendar.MINUTE)) {
                        conexion.setProximaEjecucion(estrategias.get(i));
                        //Meter aqui el codigo para que el rman haga el respaldo
                        //el codigo del comando lo tiene estrategia con getSentencia se saca
                        // basicamente estrategia.getSentencia();
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                //Sleep de 5 segundos
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException ex) {
                Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
