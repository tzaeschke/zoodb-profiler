package org.zoodb.profiling.acticvity1;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;

import org.zoodb.profiling.model1.Conference;
import org.zoodb.profiling.model1.ConferenceSeries;
import org.zoodb.profiling.model1.Publication;
import org.zoodb.profiling.simulator.AbstractAction;

public class MistakeAction extends AbstractAction {

	@Override
	public Object executeAction(PersistenceManager pm) {
		
		//task will be executed for 500 conferenceseries
		int max = 500; 
		//int max = Integer.MAX_VALUE;
		
		repeatXMax(pm,max);
		//repeatXMaxShortcutImplemented(pm,max);
		return null;
	}

	/**
	 * We execute the same action 'max' times
	 * @param pm
	 */
	private void repeatXMax(PersistenceManager pm,int max) {
		int count = 0;
		
		pm.currentTransaction().begin();

		Extent<ConferenceSeries> conferenceSeries = pm.getExtent(ConferenceSeries.class);
		
		for (ConferenceSeries cs : conferenceSeries) {
			count++;
			
			if (count >= max) {
				break;
			}
			
			cs.getDBLPkey();
			cs.getName();
			
			for (Publication p: cs.getPublications()) {
				Conference c= p.getConference();
				c.getIssue();
				c.getYear();
				c.getLocation();
			}
		}
		
		pm.currentTransaction().commit();
	
	}
	
//	private void repeatXMaxShortcutImplemented(PersistenceManager pm,int max) {
//		int count = 0;
//		
//		pm.currentTransaction().begin();
//
//		Extent<ConferenceSeries> conferenceSeries = pm.getExtent(ConferenceSeries.class);
//		
//		for (ConferenceSeries cs : conferenceSeries) {
//			count++;
//			
//			if (count >= max) {
//				break;
//			}
//			
//			for (Conference c : cs.getConferences()) {
//				
//				c.getIssue();
//				c.getYear();
//				
//				Author keynoteAuthor = c.getKeynoteAuthor();
//				
//				if (keynoteAuthor != null) {
//					keynoteAuthor.getName();
//				}
//			}			
//		}
//		
//		pm.currentTransaction().commit();
//	
//	}
	

}
