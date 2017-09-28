package org.zoodb.profiling.acticvity1;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;

import org.zoodb.profiling.model1.Author;
import org.zoodb.profiling.model1.Publication;
import org.zoodb.profiling.simulator.AbstractAction;

public class DuplicateAction extends AbstractAction {

	@Override
	public Object executeAction(PersistenceManager pm) {
		//DBLPQueries queries = new DBLPQueries(pm, Author.class, ConferenceSeries.class);
		
		int max = 500;
		
		beforeOptimized(pm,max);
		//afterOptimized(pm,max);
		
		return null;
	}

	private void beforeOptimized(PersistenceManager pm, int max) {
		pm.currentTransaction().begin();
		Extent<Author> allAuthors = pm.getExtent(Author.class);
		int i=0;
		for (Author a : allAuthors) {
			i++;
			
			if (i==max) break;
			a.getName();
			
			for (Publication p : a.getSourceA()) {
				
							
				if (p.getConference() != null) {
					p.getConference().getIssue();
				} 
			}
		}
		pm.currentTransaction().commit();
	}
	
//	private void afterOptimized(PersistenceManager pm, int max) {
//		pm.currentTransaction().begin();
//		Extent<Author> allAuthors = pm.getExtent(Author.class);
//		int i=0;
//		for (Author a : allAuthors) {
//			i++;
//			
//			if (i==max) break;
//			a.getName();
//			for (Publication p : a.getSourceA()) {
//				p.getConferenceIssue();
//			}
//		}
//		pm.currentTransaction().commit();
//	}

}
