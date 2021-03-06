package org.zoodb.profiling.analyzer;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.globis.profiling.commons.suggestion.FieldCount;

public class TrxGroup {
	
	private List<String> fields;
	
	private List<String> trxIds;
	
	private List<int[]> accessVectors;
	
	private FieldCount[] fieldCounts;
	
	private int splitIndex;
	
	private double gain;
	private double cost;
	
	private Class<?> c;
	
	private SplitCostCalculator scc;
	
	public TrxGroup(List<String> fields, Class<?> c) {
		this.fields = fields;
		this.c = c;
		
		trxIds = new LinkedList<String>();
		accessVectors = new LinkedList<int[]>();
		fieldCounts = new FieldCount[fields.size()];
	}
	
	/**
	 * Adds a transaction to this transaction group
	 * @param trxId
	 * @param accessVectors
	 */
	public void addTrx(String trxId, int[] accessVector) {
		trxIds.add(trxId);
		accessVectors.add(accessVector);
	}

	public boolean calculateSplit() {
		/*
		 * aggregate accesses per field
		 * outsource the fieldcount to a separate class so we can use the comparable interfaces
		 * and do not have to permute/sort 2 arrays with same order!
		 */
		int fieldsCount = fields.size();
		String fName = null;
		
		for (int i=0;i<fieldsCount;i++) {
			fName = fields.get(i);
			int aggrCount = 0;
			for (int[] av : accessVectors) {
				aggrCount += av[i];
			}
			fieldCounts[i] = new FieldCount(fName,aggrCount);
		}
		
		/*
		 * Sort field counts descending by field accesses
		 */
		Arrays.sort(fieldCounts);
		
		SplitStrategyAdvisor ssa = new SplitStrategyAdvisor(new SimpleSplitStrategy());
		splitIndex = ssa.checkForSplit(fieldCounts,c);
		
		if (splitIndex > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public void calculateSplitCost() {
		//tell split cost calculator  that only activations for this trx-set should be consireded!
		scc  = new SplitCostCalculator(this);
		scc.calculateCost(c, fieldCounts, splitIndex);
		
		this.cost = scc.getCost();
		this.gain = scc.getGain();
		
	}
	
	public List<String> getFields() {
		return fields;
	}
	public List<String> getTrxIds() {
		return trxIds;
	}
	public List<int[]> getAccessVectors() {
		return accessVectors;
	}
	public int getSplitIndex() {
		return splitIndex;
	}
	public FieldCount[] getFieldCounts() {
		return fieldCounts;
	}
	public long getGain() {
		return Math.round(gain);
	}
	public long getCost() {
		return Math.round(cost);
	}
	public Collection<String> getSplittedFields() {
		Collection<String> result = new LinkedList<String>();
		for (int i=splitIndex;i<fieldCounts.length;i++) {
			result.add(fieldCounts[i].getName());
		}
		return result;
	}

	public SplitCostCalculator getSplitCostCalculator() {
		return scc;
	}
	

}