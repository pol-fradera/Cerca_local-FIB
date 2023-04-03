package Central;


import aima.search.framework.SuccessorFunction;
import aima.search.framework.Successor;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;



public class SucessorSA implements SuccessorFunction {
    @Override
    public List getSuccessors(Object o) {
        ArrayList retval = new ArrayList();
        Estado estat = (Estado) o;
        Random myRandom = new Random();
        int ncli = estat.getNClients();
        int ncen = estat.getNCentrals();
        int espacio_mover = ncli * ncen;
        int espacio_permutar = ncli * ncli;
        int espacio_total = espacio_mover+espacio_permutar;
        int op = abs(myRandom.nextInt())%espacio_total;

        if (op < espacio_mover) {

            int i = abs(myRandom.nextInt())%ncli;
            int j = abs(myRandom.nextInt())%ncen;

            while (true){
                try {
                    if (estat.moverCliente(i, j)) {
                        StringBuffer S = new StringBuffer();
                        S.append("movido cliente " + i + " a central " + j + " benefici: " + estat.getBeneficio() + "\n");
                        retval.add(new Successor(S.toString(), estat));
                        break;
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                i = abs(myRandom.nextInt())%ncli;
                j = abs(myRandom.nextInt())%ncen;
            }

        }

        else {

            int i = abs(myRandom.nextInt())%ncli;
            int j = abs(myRandom.nextInt())%ncli;

            while (true){
                try {
                    if (estat.permutarClientes(i, j)) {
                        StringBuffer S = new StringBuffer();
                        S.append("permutados clientes " + i + " " + j + " benefici: " + estat.getBeneficio() + "\n");
                        retval.add(new Successor(S.toString(), estat));
                        break;
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                i = abs(myRandom.nextInt())%ncli;
                j = abs(myRandom.nextInt())%ncli;
            }
        }
        return retval;
    }

    private int abs(int x) {
        if (x < 0) x = -x;
        return x;
    }
}
