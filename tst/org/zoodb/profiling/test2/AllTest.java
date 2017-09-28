package org.zoodb.profiling.test2;

import org.zoodb.profiling.ProfUtil;
import org.zoodb.profiling.acticvity2.AggregationAction;
import org.zoodb.profiling.acticvity2.AuthorMergeTest;
import org.zoodb.profiling.acticvity2.LOBTestAction;
import org.zoodb.profiling.acticvity2.MistakeAction;
import org.zoodb.profiling.acticvity2.SplitAction;
import org.zoodb.profiling.api.impl.ProfilingManager;
import org.zoodb.profiling.simulator.ActionArchive;
import org.zoodb.profiling.simulator.ZooDBSimulator;

public class AllTest {

	static final String DB_NAME = "dblpV2b";
	static final int N_TESTS = 1;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ZooDBSimulator us = new ZooDBSimulator(1,false,DB_NAME);
		
		//build action archive
		ActionArchive actions = new ActionArchive();
		//actions.addAction(new ShortcutAction(), 0.16d);
		actions.addAction(new AuthorMergeTest(), 0.16d);
		actions.addAction(new LOBTestAction(), 0.16d);
		actions.addAction(new AggregationAction(), 0.16d);
		//actions.addAction(new DuplicateAction(), 0.16d);
		actions.addAction(new SplitAction(),0.16d);
		actions.addAction(new MistakeAction(),0.16d);
		
		us.setActions(actions);
		us.run();

		
		ProfUtil.listSuggestions(ProfilingManager.getInstance().internalGetSuggestions());
	}

}
