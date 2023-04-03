package Central;

import aima.search.framework.HeuristicFunction;

public class Heuristica implements HeuristicFunction {
    public double getHeuristicValue(Object o) {
        //300 és la tarifa mínima, que multiplicat per la produccio disponible
        //return -(((Estado) o).getBeneficio()+300.0*((Estado) o).getProduccion_disponible());
        return -(((Estado) o).getBeneficio()+300.0*((Estado) o).getProduccion_disponible()/1.6);
        //return -((Estado) o).getBeneficio();
    }
}
