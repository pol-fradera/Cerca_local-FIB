package Central;

import aima.search.framework.*;
import aima.search.informed.AStarSearch;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.SimulatedAnnealingSearch;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import java.util.List;

import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        double[] propc = {0.25, 0.3, 0.45};
        int[] cent = {5, 10, 25};

        long inicio = System.currentTimeMillis();

        Estado es = new Estado(cent, 10000, propc, 0.75, 1234);


        es.SolucionIni3();

        System.out.println(es.toStringInicial());

        Problem p = new Problem(es, new SucessorSA(), new goalTest(), new Heuristica());


        //Search alg = new HillClimbingSearch();
        Search alg = new SimulatedAnnealingSearch(1000,10,1,0.01);

        // Instantiate the SearchAgent object
        SearchAgent agent = new SearchAgent(p, alg);

        //printActions(agent.getActions());
        printInstrumentation(agent.getInstrumentation());



        Estado es2 = (Estado)alg.getGoalState();
        System.out.println(es2.toStringFinal());

        long fin = System.currentTimeMillis();

        double tiempo = (double) ((fin - inicio));

        System.out.println("Tiempo de ejecucion: " +tiempo +" milisegundos");
        System.out.println("Tiempo de ejecucion: " +tiempo/1000 +" segundos");
    }


    private static void printActions(List actions) {
        for (int i = 0; i < actions.size(); i++) {
            String action = (String) actions.get(i);
            System.out.println(action);
        }
    }

    private static void printInstrumentation(Properties properties) {
        Iterator keys = properties.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String property = properties.getProperty(key);
            System.out.println(key + " : " + property);
        }

    }

}