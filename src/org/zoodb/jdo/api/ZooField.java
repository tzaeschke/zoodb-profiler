/*
 * Copyright 2009-2011 Tilmann Z�schke. All rights reserved.
 * 
 * This file is part of ZooDB.
 * 
 * ZooDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ZooDB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ZooDB.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * See the README and COPYING files for further information. 
 */
package org.zoodb.jdo.api;



/**
 * Public interface to manage database schema class fields.
 * 
 * @author ztilmann
 */
public abstract class ZooField {

	protected Class<?> cls;

	protected ZooField(Class<?> cls) {
		this.cls = cls;
	}

	public Class<?> getJavaClass() {
		checkInvalid();
		return cls;
	}

	@Override
	public String toString() {
		checkInvalid();
		return "Class schema field: " + cls.getName();
	}

	public abstract void remove();

	protected abstract void checkInvalid();

//	public abstract void defineIndex(String fieldName, boolean isUnique);
//
//	public abstract boolean removeIndex(String fieldName);
//
//	public abstract boolean isIndexDefined(String fieldName);
//
//	public abstract boolean isIndexUnique(String fieldName);


	public abstract void rename(String name);

	/**
	 * 
	 * @return The name of the Java class of this schema.
	 */
	public abstract String getFieldName();

}