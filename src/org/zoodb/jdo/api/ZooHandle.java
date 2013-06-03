/*
 * Copyright 2009-2013 Tilmann Zaeschke. All rights reserved.
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

import java.util.Date;

public interface ZooHandle {

	public abstract long getOid();

	public abstract void setOid(long oid);

	public abstract byte getAttrByte(String attrName);

	public abstract boolean getAttrBool(String attrName);

	public abstract short getAttrShort(String attrName);

	public abstract int getAttrInt(String attrName);

	public abstract long getAttrLong(String attrName);

	public abstract char getAttrChar(String attrName);

	public abstract float getAttrFloat(String attrName);

	public abstract double getAttrDouble(String attrName);

	public abstract String getAttrString(String attrName);

	public abstract Date getAttrDate(String attrName);

	public abstract ZooHandle getAttrRefHandle(String attrName);

	public abstract long getAttrRefOid(String attrName);

	public abstract void remove();
	
	public abstract ZooClass getType();
	
	public abstract Object getJavaObject();
	
}