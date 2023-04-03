package Central;

import aima.search.framework.GoalTest;

public class goalTest implements GoalTest {
    @Override
    public boolean isGoalState(Object o) {

        return ((Estado) o).isGoalState();
    }
}
