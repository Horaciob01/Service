package userservice;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
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

    private static Queue<Estrategia> colaEstrategias;
    private static Conexion conexion;

    private static void respaldo(Estrategia estrategia) throws IOException {
        Date fecha = new Date();
        Process p = Runtime.getRuntime().exec("cmd.exe");
        BufferedWriter p_stdin
                = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
        try {
            p_stdin.write("rman target/");
            p_stdin.newLine();
            p_stdin.flush();
            p_stdin.write(estrategia.getSentencia());
            p_stdin.newLine();
            p_stdin.flush();
            p_stdin.write("exit;");
            p_stdin.newLine();
            p_stdin.flush();
            p_stdin.close();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = reader.readLine();
            String evidencia = "";
            while (line != null) {
                //System.out.println(line);
                evidencia = evidencia + line;
                line = reader.readLine();
            }
            try {
                conexion.setEvidencia(evidencia, fecha, estrategia);
            } catch (SQLException ex) {
                Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) throws ParseException {
        conexion = new Conexion();
        conexion.conectar();
        ArrayList<Estrategia> estrategias;
        Calendar cA, cC;
        Estrategia estrategia;
        colaEstrategias = new LinkedList();
        (new Thread(new Runnable() {
            @Override
            public void run() {
                Estrategia estrategia;
                while (true) {
                    estrategia = colaEstrategias.poll();
                    if (estrategia != null) {
                        try {
                            respaldo(estrategia);
                        } catch (IOException ex) {
                            Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    try {
                        //Sleep de 5 segundos
                        TimeUnit.SECONDS.sleep(5);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        })).start();
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
                        colaEstrategias.add(estrategia);
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
