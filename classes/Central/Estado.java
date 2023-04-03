package Central;

import IA.Energia.*;
import java.util.*;
import java.util.ArrayList;

import static IA.Energia.VEnergia.getCosteMarcha;


public class Estado {
    private static Clientes clients;
    private static Centrales centrals;
    private int[] Represent;
    private double [] produccion;
    private int[] central_activa;

    private double beneficio;

    private double produccion_disponible;

    private int cli_garantitzats;

    //Constructoria Principal
    public Estado(int [] cent,int ncl, double[] propc, double propg, int seed) throws Exception {
        //Constructora dels centrals
        this.centrals = new Centrales(cent,seed);
        //Constructora del clients
        this.clients = new Clientes(ncl,propc,propg,seed);

        //La nostra "solucio" on l'index representa el client i el valor la central
        this.Represent = new int[ncl];

        this.cli_garantitzats = 0;

        //Inicialitzo que tots els clients no estan assignats a cap central i li donem valor -1
        for (int i = 0; i < ncl; ++i) {
            this.Represent[i] = -1;
            if (clients.get(i).getContrato() == Cliente.GARANTIZADO) ++cli_garantitzats;
        }

        //Vector de produccions on cada posicio guarda la produccio de cada central
        this.produccion = new double[centrals.size()];
        this.central_activa = new int[centrals.size()];

        this.produccion_disponible = 0.0;

        //Inicialitzo el vector central amb la produccio dels centrals corresponents
        for (int i = 0; i < produccion.length; ++i) {
            produccion[i] = centrals.get(i).getProduccion();
            this.produccion_disponible += centrals.get(i).getProduccion();
            central_activa[i] = 0;
        }

        this.beneficio = 0.0;



    }

    //Funcion que replica un estado lo vamos a necesitar cuando se le aplique un operador al estado actual
    public Estado(Estado estat){
        this.clients = estat.getClients();
        this.centrals = estat.getCentrals();

        this.produccion = estat.getProduccion().clone();

        this.central_activa = estat.getCentral_activa().clone();

        this.beneficio = estat.getBeneficio();

        this.produccion_disponible = estat.getProduccion_disponible();

        this.Represent = estat.getRepresent().clone();

    }

    public Centrales getCentrals() {
        return centrals;
    }

    public Clientes getClients() {
        return clients;
    }

    public int getNCentrals() {
        return centrals.size();
    }

    public int getNClients() {
        return clients.size();
    }

    public int ncentrals () { return centrals.size();}

    public int[] getCentral_activa() {
        return central_activa;
    }

    public double[] getProduccion() {
        return produccion;
    }

    public int[] getRepresent(){

        return Represent;
    }

    public double getBeneficio() {
        return beneficio;
    }

    public double getProduccion_disponible() { return produccion_disponible; }

    public int getNumClients() {
        return clients.size();
    }
    public int getNumCentrals(){
        return centrals.size();
    }

    public boolean backtracking(int index_client, int n) throws Exception {
        if (n == cli_garantitzats) {
            calcular_benefici();
            return true;
        }
        else if (index_client < Represent.length) {
            Cliente cli = clients.get(index_client);
            if (cli.getContrato() == Cliente.NOGARANTIZADO) { return backtracking(index_client+1,n); }
            else {
                for (int j = 0; j < centrals.size(); ++j) {
                    double perc = perc(centrals.get(j), cli);
                    if (produccion[j] >= cli.getConsumo() * (1 + perc)) {
                        Represent[index_client] = j;
                        ++central_activa[j];
                        produccion[j] -= cli.getConsumo()*(1 + perc);
                        produccion_disponible -= cli.getConsumo()*(1+perc);
                        //beneficio += cli.getConsumo()*VEnergia.getTarifaClienteGarantizada(cli.getTipo());
                        if (backtracking(index_client+1,n+1)) return true;
                        --central_activa[j];
                        produccion[j] += cli.getConsumo()*(1 + perc);
                        produccion_disponible += cli.getConsumo()*(1+perc);
                        //beneficio -= cli.getConsumo()*VEnergia.getTarifaClienteGarantizada(cli.getTipo());
                    }
                }
                return false;
            }
        }
        else return false;
    }

    public void SolucionIni() throws Exception {
        if (!backtracking(0,0)) {
            System.out.println("\nNo és possible trobar cap solució inicial vàlida.");
            System.exit(1);
        }
    }

    public boolean backtracking2(int index_client, int n, ArrayList<Integer> clientes_noG) throws Exception {
        if (n == cli_garantitzats && index_client == Represent.length) {
            return true;
        }
        else if (index_client < Represent.length) {
            Cliente cli = clients.get(index_client);
            if (cli.getContrato() == Cliente.NOGARANTIZADO) {
                clientes_noG.add(index_client);
                return backtracking2(index_client+1,n,clientes_noG);
            }
            else {
                for (int j = 0; j < centrals.size(); ++j) {
                    double perc = perc(centrals.get(j), cli);
                    if (produccion[j] >= cli.getConsumo() * (1 + perc)) {
                        Represent[index_client] = j;
                        ++central_activa[j];
                        produccion[j] -= cli.getConsumo()*(1 + perc);
                        produccion_disponible -= cli.getConsumo()*(1+perc);
                        //beneficio += cli.getConsumo()*VEnergia.getTarifaClienteGarantizada(cli.getTipo());
                        if (backtracking2(index_client+1,n+1,clientes_noG)) return true;
                        --central_activa[j];
                        produccion[j] += cli.getConsumo()*(1 + perc);
                        produccion_disponible += cli.getConsumo()*(1+perc);
                        //beneficio -= cli.getConsumo()*VEnergia.getTarifaClienteGarantizada(cli.getTipo());
                    }
                }
                return false;
            }
        }
        else return false;
    }

    public void SolucionIni2() throws Exception {
        ArrayList<Integer> clientes_noG = new ArrayList<Integer>();;
        if (!backtracking2(0,0,clientes_noG)) {
            System.out.println("\nNo és possible trobar cap solució inicial vàlida.");
            System.exit(1);
        }
        else {
            int index_cent = 0;
            for (int i = 0; i < clientes_noG.size() && !central_all_used(index_cent); ++i) {
                Cliente cli = clients.get(clientes_noG.get(i));
                double perc = perc(centrals.get(index_cent),cli);
                if (produccion[index_cent] >= cli.getConsumo()*(1+perc)) {
                    Represent[i] = index_cent;
                    ++central_activa[index_cent];
                    produccion[index_cent] -= cli.getConsumo()*(1 + perc);
                    produccion_disponible -= cli.getConsumo()*(1+perc);
                    //beneficio += cli.getConsumo()*VEnergia.getTarifaClienteGarantizada(cli.getTipo());
                }
                else {
                    ++index_cent;
                    --i;
                }
            }
        }
        calcular_benefici();
    }

    public boolean backtracking3(int index_client, int n) throws Exception {
        if (n == cli_garantitzats) {
            calcular_benefici();
            return true;
        }
        else if (index_client < Represent.length) {
            Cliente cli = clients.get(index_client);
            int x = n%centrals.size();
            //System.out.println(x);
            if (cli.getContrato() == Cliente.NOGARANTIZADO) { return backtracking3(index_client+1,n); }
            else {
                int j = x;
                do {
                    double perc = perc(centrals.get(j), cli);
                    if (produccion[j] >= cli.getConsumo() * (1 + perc)) {
                        Represent[index_client] = j;
                        ++central_activa[j];
                        produccion[j] -= cli.getConsumo() * (1 + perc);
                        produccion_disponible -= cli.getConsumo() * (1 + perc);
                        if (backtracking3(index_client + 1, n + 1)) return true;
                        --central_activa[j];
                        produccion[j] += cli.getConsumo() * (1 + perc);
                        produccion_disponible += cli.getConsumo() * (1 + perc);
                    }
                    ++j;
                    if (j == centrals.size()) j = 0;
                } while (j != x);
                return false;
            }
        }
        else return false;
    }

    public void SolucionIni3() throws Exception {
        if (!backtracking3(0,0)) {
            System.out.println("\nNo és possible trobar cap solució inicial vàlida.");
            System.exit(1);
        }
    }


    public boolean isGoalState() {
        return false;
    }


    public boolean central_all_used(int i) {
        return i >= centrals.size();
    }

    private double perc(Central c, Cliente cli) {
        int central_x = c.getCoordX();
        int central_y = c.getCoordY();

        int client_x = cli.getCoordX();
        int client_y = cli.getCoordY();

        double dis = Math.sqrt((central_x-client_x)*(central_x-client_x) + (central_y-client_y)*(central_y-client_y));
        double perc = VEnergia.getPerdida(dis);

        return perc;
    }

    private double getCosteMarchaTotal(Central c) throws Exception {
        return c.getProduccion() * VEnergia.getCosteProduccionMW(c.getTipo()) + VEnergia.getCosteMarcha(c.getTipo());
    }

    //han d'estar els dos clients a una central
    public boolean permutarClientes(int c1, int c2) throws Exception {
        int index_cent1 = Represent[c1];
        int index_cent2 = Represent[c2];
        if (index_cent1 == index_cent2) return false;
        if (index_cent1 == -1) {
            Cliente cli2 = clients.get(c2);
            if (clients.get(c2).getContrato() == Cliente.GARANTIZADO) return false;
            Cliente cli1 = clients.get(c1);
            double perc_ce2_cl2 = perc(centrals.get(index_cent2), cli2);
            double perc_ce2_cl1 = perc(centrals.get(index_cent2), cli1);
            double cons_ce2_cl2 = cli2.getConsumo()*(1 + perc_ce2_cl2);
            double cons_ce2_cl1 = cli1.getConsumo() * (1 + perc_ce2_cl1);
            double produccio2 = produccion[index_cent2]+cons_ce2_cl2;
            if (produccio2 < cons_ce2_cl1) return false;
            Represent[c1] = index_cent2;
            Represent[c2] = index_cent1;
            produccion[index_cent2] = produccio2 - cons_ce2_cl1;
            produccion_disponible += cons_ce2_cl2-cons_ce2_cl1;
            beneficio += cli1.getConsumo()*VEnergia.getTarifaClienteNoGarantizada(cli1.getTipo()) - cli2.getConsumo()*VEnergia.getTarifaClienteNoGarantizada(cli2.getTipo());
            return true;
        }
        else if (index_cent2 == -1) {
            Cliente cli1 = clients.get(c1);
            if (clients.get(c1).getContrato() == Cliente.GARANTIZADO) return false;
            Cliente cli2 = clients.get(c2);
            double perc_ce1_cl2 = perc(centrals.get(index_cent1), cli2);
            double perc_ce1_cl1 = perc(centrals.get(index_cent1), cli1);
            double cons_ce1_cl2 = cli2.getConsumo()*(1 + perc_ce1_cl2);
            double cons_ce1_cl1 = cli1.getConsumo() * (1 + perc_ce1_cl1);
            double produccio1 = produccion[index_cent1]+cons_ce1_cl1;
            if (produccio1 < cons_ce1_cl2) return false;
            Represent[c1] = index_cent2;
            Represent[c2] = index_cent1;
            produccion[index_cent1] = produccio1 - cons_ce1_cl2;
            produccion_disponible += cons_ce1_cl1-cons_ce1_cl2;
            beneficio += cli2.getConsumo()*VEnergia.getTarifaClienteNoGarantizada(cli2.getTipo()) - cli1.getConsumo()*VEnergia.getTarifaClienteNoGarantizada(cli1.getTipo());
            return true;
        }
        Cliente cli1 = clients.get(c1);
        Cliente cli2 = clients.get(c2);
        double perc_ce1_cl1 = perc(centrals.get(index_cent1), cli1);
        double perc_ce2_cl2 = perc(centrals.get(index_cent2), cli2);
        double perc_ce2_cl1 = perc(centrals.get(index_cent2), cli1);
        double perc_ce1_cl2 = perc(centrals.get(index_cent1), cli2);
        double produccio1 = produccion[index_cent1]+cli1.getConsumo()*(1 + perc_ce1_cl1);
        double produccio2 = produccion[index_cent2]+cli2.getConsumo()*(1 + perc_ce2_cl2);
        if (produccio1 >= cli2.getConsumo()*(1+perc_ce1_cl2) && produccio2 >= cli1.getConsumo() * (1 + perc_ce2_cl1)){
            Represent[c1] = index_cent2;
            Represent[c2] = index_cent1;
            produccion[index_cent1] = produccio1 - cli2.getConsumo()*(1 + perc_ce1_cl2);
            produccion[index_cent2] = produccio2 - cli1.getConsumo()*(1 + perc_ce2_cl1);
            produccion_disponible += cli1.getConsumo()*(1 + perc_ce1_cl1) + cli2.getConsumo()*(1 + perc_ce2_cl2) - cli2.getConsumo()*(1 + perc_ce1_cl2) - cli1.getConsumo()*(1 + perc_ce2_cl1);
            return true;
        }
        return false;
    }


    public boolean moverCliente(int index_client, int index_cent) throws Exception {
        Cliente cli = clients.get(index_client);
        Central cent = centrals.get(index_cent);
        double perc = perc(cent, cli);
        int index_cent_antiga = Represent[index_client];
        if (index_cent_antiga == index_cent || produccion[index_cent] < cli.getConsumo() * (1 + perc)) return false;
        //el client no està a cap central
        if (Represent[index_client] == -1) {
            Represent[index_client] = index_cent;
            produccion[index_cent] -= cli.getConsumo() * (1 + perc);
            produccion_disponible -= cli.getConsumo() * ( 1 + perc);
            ++central_activa[index_cent];
            beneficio += cli.getConsumo()*VEnergia.getTarifaClienteNoGarantizada(cli.getTipo());
            beneficio += VEnergia.getTarifaClientePenalizacion(cli.getTipo())*cli.getConsumo();
            if (central_activa[index_cent] == 1) beneficio -= getCosteMarchaTotal(cent) - VEnergia.getCosteParada(cent.getTipo());
            return true;
        }
        //el client ja està a una central
        Central cent_antiga = centrals.get(index_cent_antiga);
        double perc_antic = perc(cent_antiga, cli);
        produccion[index_cent_antiga] += cli.getConsumo()*(1 + perc_antic);
        produccion_disponible += cli.getConsumo()*(1+perc_antic);
        --central_activa[index_cent_antiga];
        Represent[index_client] = index_cent;
        produccion[index_cent] -= cli.getConsumo()*(1 + perc);
        produccion_disponible -= cli.getConsumo()*( 1 + perc);
        ++central_activa[index_cent];
        if (central_activa[index_cent] == 1) beneficio -= getCosteMarchaTotal(cent) - VEnergia.getCosteParada(cent.getTipo());
        if (central_activa[index_cent_antiga] == 0) beneficio += getCosteMarchaTotal(cent_antiga) - VEnergia.getCosteParada(cent_antiga.getTipo());
        return true;
    }

    public boolean anadirCliente(int index_client, int index_cent) throws Exception {
        if (Represent[index_client] != -1) return false;
        Cliente cli = clients.get(index_client);
        Central cent = centrals.get(index_cent);
        double perc = perc(cent, cli);
        if (produccion[index_cent] >= cli.getConsumo() * (1 + perc)) {
            Represent[index_client] = index_cent;
            produccion[index_cent] -= cli.getConsumo()*(1 + perc);
            produccion_disponible -= cli.getConsumo()*( 1 + perc);
            ++central_activa[index_cent];
            beneficio += cli.getConsumo()*VEnergia.getTarifaClienteNoGarantizada(cli.getTipo());
            beneficio += VEnergia.getTarifaClientePenalizacion(cli.getTipo())*cli.getConsumo();
            if (central_activa[index_cent] == 1) beneficio -= getCosteMarchaTotal(cent) - VEnergia.getCosteParada(cent.getTipo());
            return true;
        }
        return false;
    }

    public boolean quitarCliente(int index_client) throws Exception {
        Cliente cli = clients.get(index_client);
        if (cli.getContrato() == Cliente.NOGARANTIZADO && Represent[index_client] != -1) {
            int index_cent = Represent[index_client];
            Central cent = centrals.get(index_cent);
            double perc = perc(cent, cli);
            produccion[index_cent] += cli.getConsumo()*(1 + perc);
            produccion_disponible += cli.getConsumo()*( 1 + perc);
            --central_activa[index_cent];
            Represent[index_client] = -1;
            beneficio -= cli.getConsumo()*VEnergia.getTarifaClienteNoGarantizada(cli.getTipo());
            beneficio -= VEnergia.getTarifaClientePenalizacion(cli.getTipo())*cli.getConsumo();
            if (central_activa[index_cent] == 0) {
                beneficio += getCosteMarchaTotal(cent) - VEnergia.getCosteParada(cent.getTipo());
            }
            return true;
        }
        return false;
    }

    public void calcular_benefici() throws Exception {
        //double b = 0;
        for (int i = 0; i < Represent.length; ++i) {
            Cliente cli = clients.get(i);
            if (Represent[i] != -1) {
                if (cli.getContrato() == Cliente.GARANTIZADO) beneficio += cli.getConsumo()*VEnergia.getTarifaClienteGarantizada(cli.getTipo());
                else if(cli.getContrato() == Cliente.NOGARANTIZADO) beneficio += cli.getConsumo()*VEnergia.getTarifaClienteNoGarantizada(cli.getTipo());
            }
            else beneficio -= VEnergia.getTarifaClientePenalizacion(cli.getTipo())*cli.getConsumo();
        }
        for (int i = 0; i < centrals.size(); ++i) {
            Central cent = centrals.get(i);
            if (central_activa[i] != 0) beneficio -= getCosteMarchaTotal(cent);
            else beneficio -= VEnergia.getCosteParada(cent.getTipo());
        }
    }



    public String toStringInicial() {
        int clients_servits = 0;
        for (int i = 0; i< Represent.length; i++) {
            if (Represent[i] != -1) ++clients_servits;
        }
        int clients_garantitzats = 0;
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getContrato() == Cliente.GARANTIZADO) ++clients_garantitzats;
        }

        int centrals_actives = 0;
        for (int i = 0; i < centrals.size(); i++) {
            if (central_activa[i] > 0) ++centrals_actives;
        }

        String str = "N Clientes: " + this.clients.size() + " N Centrales: " + this.centrals.size()+ "\n";
        str += "Beneficio inicial: " + beneficio + "\n";
        str += "Produccio disponible inicial: " + produccion_disponible + "\n";
        str += "Clients garantitzats: " + clients_garantitzats + "\n";
        str += "Clients servits inicials: " + clients_servits + "\n";
        str += "Centrals actives: " + centrals_actives + "\n";

        return str;
    }

    public String toStringFinal() {
        int clients_servits = 0;
        for (int i = 0; i< Represent.length; i++) {
            if (Represent[i] != -1) ++clients_servits;
        }

        int centrals_actives = 0;
        for (int i = 0; i < centrals.size(); i++) {
            if (central_activa[i] > 0) ++centrals_actives;
        }
        String str = "Estat final:"+"\n";
        str += "Beneficio final: " + beneficio + "\n";
        str += "Produccio disponible final: " + produccion_disponible + "\n";
        str += "Clients servits finals: " + clients_servits + "\n";
        str += "Centrals actives: " + centrals_actives + "\n";
        return str;
    }


}