package Central;


import aima.search.framework.SuccessorFunction;
import aima.search.framework.Successor;
import java.util.ArrayList;
import java.util.List;

public class Sucessor3 implements SuccessorFunction {
    @Override
    public List getSuccessors(Object o) {
        ArrayList retval = new ArrayList();
        Estado estat = (Estado) o;

        //Estado new_state;
        for (int i = 0; i < estat.getNumClients(); ++i) {
            for (int j = 0; j < estat.getNumCentrals(); ++j) {
                Estado new_state = new Estado(estat);
                try {
                    if (new_state.anadirCliente(i, j)) {
                        StringBuffer S = new StringBuffer();
                        S.append("anadido cliente " + i + " a central " + j + " benefici: " + new_state.getBeneficio() + "\n");
                        retval.add(new Successor(S.toString(), new_state));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            Estado new_state = new Estado(estat);
            try {
                if (new_state.quitarCliente(i)) {
                    StringBuffer S = new StringBuffer();
                    S.append("quitado cliente " + i +" benefici: " + new_state.getBeneficio() + "\n");
                    retval.add(new Successor(S.toString(), new_state));
                }
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return retval;
    }
}

