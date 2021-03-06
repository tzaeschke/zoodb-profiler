package org.zoodb.profiling.api.impl;

import java.util.IdentityHashMap;
import java.util.Iterator;

import org.zoodb.internal.ZooClassDef;
import org.zoodb.profiling.api.AbstractActivation;
import org.zoodb.profiling.api.IPathManager;


public class PathManagerTreeV2 implements IPathManager {
	
	private IdentityHashMap<Class<?>,ActivationArchive> classArchives;
	
	public PathManagerTreeV2() {
		//classArchives = new HashMap<Class<?>,ActivationArchive>();
		//the identity hashmap brings about 1second performance improvement 
		//when executing the AuthorMergeTest!
		classArchives= new IdentityHashMap<Class<?>,ActivationArchive>();
	}
	
	/**
	 * This is only for the initial initialization with all valid classes. Classes without 
	 * Activations are useful for recognizing unused classes.
	 * @param classDef
	 */
	@Override
	public void addClass(ZooClassDef classDef) {
		Class<?> cls = classDef.getJavaClass();
		ActivationArchive aa = classArchives.get(cls);
		
		if (aa == null) {
			aa = new ActivationArchive(classDef);
			classArchives.put(cls, aa);
		}
	}

	@Override
	public void add(AbstractActivation a, ZooClassDef classDef) {
		ActivationArchive aa = classArchives.get(a.getClazz());
		
		if (aa == null) {
			aa = new ActivationArchive(classDef);
			classArchives.put(a.getClazz(), aa);
		}
		aa.addItem(a);
	}

	
	
	@Override
	public ActivationArchive getArchive(Class<?> c) {
		return classArchives.get(c);
	}

	@Override
	public Iterator<Class<?>> getClassIterator() {
		return classArchives.keySet().iterator();
	}

}
