package Central;

import aima.search.framework.SuccessorFunction;
import aima.search.framework.Successor;
import java.util.ArrayList;
import java.util.List;
public class Sucessor implements SuccessorFunction {
    @Override
    public List getSuccessors(Object o) {
        ArrayList retval = new ArrayList();
        Estado estat = (Estado) o;

        //Estado new_state;
        for (int i = 0; i < estat.getNumClients(); ++i) {

            for (int j = 0; j < estat.getNumCentrals(); ++j) {
                Estado new_state = new Estado(estat);
                try {
                    if (new_state.moverCliente(i, j)) {
                        StringBuffer S = new StringBuffer();
                        S.append("movido cliente " + i + " a central " + j + " benefici: " +new_state.getBeneficio()+ "\n");
                        retval.add(new Successor(S.toString(), new_state));
                        //System.out.println(S);

                        //System.out.println("Estat actual = ");
                        //int rep_estat [] = new_state.getRepresent();
                        //for (int k = 0; k < rep_estat.length; k++) {
                          //  System.out.println(" Client "+k+ " a central "+ rep_estat[k]);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            /*
            int cli = i+1;
            while (cli < estat.getNumClients()) {
                Estado new_state = new Estado(estat);
                try {
                    if (new_state.permutarClientes(i,cli)) {
                        StringBuffer S = new StringBuffer();
                        S.append("permutados clientes " + i + " y " + cli + "\n");
                        retval.add(new Successor(S.toString(), new_state));
                        //System.out.println(S);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                cli++;
            }*/
        }
        return retval;
    }
}
